import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    private static JPanel lecturePanel;
    private static JFrame frame2;

    public static void main(String[] args) {
        JFrame jf = new JFrame("Ders Seçme Penceresi");
        jf.setSize(400, 300);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel schoolNo = new JLabel("Okul numaranızı giriniz:");
        JTextField schNo = new JTextField();
        schoolNo.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(schoolNo);
        panel.add(schNo);

        JLabel label1 = new JLabel("İsminizi giriniz:");
        JTextField name = new JTextField();
        label1.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(label1);
        panel.add(name);

        JLabel label2 = new JLabel("Soyisminizi giriniz:");
        JTextField surname = new JTextField();
        label2.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(label2);
        panel.add(surname);

        JButton lecSelectLog = new JButton("Ders Seçim Ekranına Geç");
        panel.add(new JLabel());
        panel.add(lecSelectLog);

        JButton addNewLecture = new JButton("Sisteme Yeni Ders Ekle");
        panel.add(new JLabel());
        panel.add(addNewLecture);
        addNewLecture.setHorizontalAlignment(JButton.CENTER);

        JButton deleteLec = new JButton("Sistemden Ders Sil");
        panel.add(new JLabel());
        panel.add(deleteLec);
        deleteLec.setHorizontalAlignment(JButton.CENTER);

        deleteLec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteLectureDialog();
            }
        });

        jf.add(panel);

        lecSelectLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (name.getText().trim().isEmpty() || surname.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(jf, "İsminizi veya soyisminizi girmediniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                } else {
                    saveUser(schNo.getText(), name.getText(), surname.getText());
                    openLectureSelection();
                }
            }

            private void openLectureSelection() {
                frame2 = new JFrame("Ders Seçim Ekranı");
                frame2.setSize(400, 500);
                frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame2.setLocationRelativeTo(null);

                lecturePanel = new JPanel();
                lecturePanel.setLayout(new GridLayout(0, 1, 5, 5));
                lecturePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel titleLabel = new JLabel("Ders Seçim Ekranı");
                titleLabel.setHorizontalAlignment(JLabel.CENTER);
                lecturePanel.add(titleLabel);

                loadLectures(schNo.getText());

                JButton addLec = new JButton("Seçilen Dersleri Ekle");
                lecturePanel.add(addLec);
                JButton showLecture = new JButton("Seçtiğim Dersleri Listele");
                lecturePanel.add(showLecture);

                addLec.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (anyLectureSelected()) {

                            showLecture.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    StringBuilder selectedCourses = new StringBuilder();

                                    Component[] components = lecturePanel.getComponents();
                                    for (Component component : components) {
                                        if (component instanceof JCheckBox) {
                                            JCheckBox checkBox = (JCheckBox) component;
                                            if (checkBox.isSelected()) {
                                                selectedCourses.append(checkBox.getText()).append("\n");
                                            }
                                        }
                                    }

                                    if (selectedCourses.toString().isEmpty()) {
                                        JOptionPane.showMessageDialog(null, "Henüz ders seçmediniz.");
                                    } else {

                                        JOptionPane.showMessageDialog(null, "Seçtiğiniz dersler: \n" + selectedCourses.toString());
                                    }
                                }
                            });
                            Component[] components2 = lecturePanel.getComponents();
                            for (Component component : components2) {
                                if (component instanceof JCheckBox) {
                                    JCheckBox checkBox2 = (JCheckBox) component;
                                    if (checkBox2.isSelected()) {
                                        saveLectureToStudents(schNo.getText(), checkBox2.getText());
                                    }
                                }
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Henüz ders seçmediniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    private boolean anyLectureSelected() {
                        Component[] components = lecturePanel.getComponents();
                        for (Component component : components) {
                            if (component instanceof JCheckBox) {
                                if (((JCheckBox) component).isSelected()) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });

                frame2.add(lecturePanel);
                frame2.setVisible(true);
            }
        });

        addNewLecture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddLectureDialog();
            }
        });

        jf.setVisible(true);
    }

    private static void saveUser(String schoolNo, String name, String surname) {
        String url = "jdbc:mysql://127.0.0.1:3306/projectpl";
        String username = "root";
        String password = "261203ee";

        String selectQuery = "SELECT * FROM tb_user WHERE ogr_No = ?";
        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(selectQuery)) {

            statement.setInt(1, Integer.parseInt(schoolNo));

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                System.out.println("Bu öğrenci zaten var!");

            }
            else{
                String insertQuery = "INSERT INTO tb_user (ogr_No, ogr_Name, ogr_Surname) VALUES (?, ?, ?)";

                try (PreparedStatement statement2 = con.prepareStatement(insertQuery)) {

                    statement2.setInt(1, Integer.parseInt(schoolNo));
                    statement2.setString(2, name);
                    statement2.setString(3, surname);

                    int rowsInserted = statement2.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Yeni öğrenci başarıyla eklendi!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static void showDeleteLectureDialog(){

        JDialog dialog2 = new JDialog();
        dialog2.setTitle("Ders Sil");
        dialog2.setSize(300, 200);
        dialog2.setLocationRelativeTo(null);
        dialog2.setModal(true);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel courseNameLabel = new JLabel("Ders Adı:");
        JTextField courseNameField = new JTextField();
        panel.add(courseNameLabel);
        panel.add(courseNameField);

        JButton saveButton = new JButton("Kaydet");
        panel.add(new JLabel());
        panel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseName = courseNameField.getText().trim();
                if (!courseName.isEmpty()) {
                    deleteLecture(courseName);
                    dialog2.dispose();
                    if (frame2 != null && frame2.isVisible()) {
                        reloadLectures();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog2, "Ders adı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
    });
        dialog2.add(panel);
        dialog2.setVisible(true);
    }

    private static void showAddLectureDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Yeni Ders Ekle");
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel courseNameLabel = new JLabel("Ders Adı:");
        JTextField courseNameField = new JTextField();
        panel.add(courseNameLabel);
        panel.add(courseNameField);

        JButton saveButton = new JButton("Kaydet");
        panel.add(new JLabel());
        panel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseName = courseNameField.getText().trim();
                if (!courseName.isEmpty()) {
                    saveNewLecture(courseName);
                    dialog.dispose();
                    if (frame2 != null && frame2.isVisible()) {
                        reloadLectures();
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Ders adı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void saveNewLecture(String courseName) {
        String url = "jdbc:mysql://127.0.0.1:3306/projectpl";
        String username = "root";
        String password = "261203ee";

        String query = "INSERT INTO lectures (course_name) VALUES (?)";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, courseName);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Yeni ders başarıyla eklendi!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLecture(String courseName) {
        String url = "jdbc:mysql://127.0.0.1:3306/projectpl";
        String username = "root";
        String password = "261203ee";

        String query = "DELETE FROM lectures WHERE course_name = ?";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, courseName);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Ders başarıyla silindi: " + courseName);
            } else {
                System.out.println("Belirtilen ders bulunamadı: " + courseName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void loadLectures(String ogr_No) {
        String url = "jdbc:mysql://127.0.0.1:3306/projectpl";
        String username = "root";
        String password = "261203ee";

        String query = "SELECT course_name FROM lectures";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                JCheckBox lectureCheckBox = new JCheckBox(courseName);
                lectureCheckBox.setSelected(hasCourseAssignedtoStudent(courseName,ogr_No));
                lecturePanel.add(lectureCheckBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadLectures() {
        String url = "jdbc:mysql://127.0.0.1:3306/projectpl";
        String username = "root";
        String password = "261203ee";

        String query = "SELECT course_name FROM lectures";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                JCheckBox lectureCheckBox = new JCheckBox(courseName);
                lecturePanel.add(lectureCheckBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasCourseAssignedtoStudent(String course_name, String ogr_No){
        String url = "jdbc:mysql://127.0.0.1:3306/projectpl";
        String username = "root";
        String password = "261203ee";

        String query = "SELECT course_name FROM user_lecture WHERE ogr_No = ? AND course_name = ?";

        boolean courseAssigned = false;

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setInt(1, Integer.parseInt(ogr_No));
            statement.setString(2, course_name);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    courseAssigned = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseAssigned;
    }

    private static void reloadLectures() {
        lecturePanel.removeAll();
        loadLectures();
        lecturePanel.revalidate();
        lecturePanel.repaint();
    }

    private static void saveLectureToStudents(String ogr_No, String course_name) {
        String url = "jdbc:mysql://127.0.0.1:3306/projectpl";
        String username = "root";
        String password = "261203ee";

        String query = "INSERT INTO user_lecture (ogr_No, course_name) VALUES (?,?)";

        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setInt(1, Integer.parseInt(ogr_No));
            statement.setString(2, course_name);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Dersler öğrenciye atandı!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}