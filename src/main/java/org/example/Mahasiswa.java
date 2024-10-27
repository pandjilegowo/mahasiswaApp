package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Mahasiswa {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnNilai;

    //disesuaikan
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Mahasiswa() {
        frame = new JFrame("Aplikasi Nilai Mahasiswa");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "NIM", "Nilai Tugas", "Nilai Kuis", "Nilai UTS", "Nilai UAS"}, 0);
        table = new JTable(tableModel);

        loadTableData();

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        btnAdd = new JButton("Tambah Data");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");
        btnNilai = new JButton("Hitung Nilai");

        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnNilai);
        frame.add(panel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addData());
        btnEdit.addActionListener(e -> editData());
        btnDelete.addActionListener(e -> deleteRecord());
        btnNilai.addActionListener(e -> showAverageDialog());

        frame.setVisible(true);
    }

    private void loadTableData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student ORDER BY id ASC")) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("nim"),
                        rs.getInt("nilai_tugas"),
                        rs.getInt("nilai_kuis"),
                        rs.getInt("nilai_uts"),
                        rs.getInt("nilai_uas")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Koneksi Gagal", "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void addData() {
        JTextField txtNama = new JTextField();
        JTextField txtNim = new JTextField();
        JTextField txtNilaiTugas = new JTextField();
        JTextField txtNilaiKuis = new JTextField();
        JTextField txtNilaiUts = new JTextField();
        JTextField txtNilaiUas = new JTextField();

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Nama:"));
        panel.add(txtNama);
        panel.add(new JLabel("NIM:"));
        panel.add(txtNim);
        panel.add(new JLabel("Nilai Tugas:"));
        panel.add(txtNilaiTugas);
        panel.add(new JLabel("Nilai Kuis:"));
        panel.add(txtNilaiKuis);
        panel.add(new JLabel("Nilai UTS:"));
        panel.add(txtNilaiUts);
        panel.add(new JLabel("Nilai UAS:"));
        panel.add(txtNilaiUas);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Tambahkan Data Baru", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO student (nama, nim, nilai_tugas, nilai_kuis, nilai_uts, nilai_uas) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtNama.getText());
                pstmt.setString(2, txtNim.getText());
                pstmt.setInt(3, Integer.parseInt(txtNilaiTugas.getText()));
                pstmt.setInt(4, Integer.parseInt(txtNilaiKuis.getText()));
                pstmt.setInt(5, Integer.parseInt(txtNilaiUts.getText()));
                pstmt.setInt(6, Integer.parseInt(txtNilaiUas.getText()));
                pstmt.executeUpdate();
                loadTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Gagal menyimpan data. Pastikan NIM tidak duplikat.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void editData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Pilih data yang akan diubah.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        JTextField txtNama = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField txtNim = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField txtNilaiTugas = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
        JTextField txtNilaiKuis = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
        JTextField txtNilaiUts = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 5)));
        JTextField txtNilaiUas = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 6)));

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Nama:"));
        panel.add(txtNama);
        panel.add(new JLabel("NIM:"));
        panel.add(txtNim);
        panel.add(new JLabel("Nilai Tugas:"));
        panel.add(txtNilaiTugas);
        panel.add(new JLabel("Nilai Kuis:"));
        panel.add(txtNilaiKuis);
        panel.add(new JLabel("Nilai UTS:"));
        panel.add(txtNilaiUts);
        panel.add(new JLabel("Nilai UAS:"));
        panel.add(txtNilaiUas);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Data", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = getConnection()) {
                String sql = "UPDATE student SET nama=?, nim=?, nilai_tugas=?, nilai_kuis=?, nilai_uts=?, nilai_uas=? WHERE id=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txtNama.getText());
                pstmt.setString(2, txtNim.getText());
                pstmt.setInt(3, Integer.parseInt(txtNilaiTugas.getText()));
                pstmt.setInt(4, Integer.parseInt(txtNilaiKuis.getText()));
                pstmt.setInt(5, Integer.parseInt(txtNilaiUts.getText()));
                pstmt.setInt(6, Integer.parseInt(txtNilaiUas.getText()));
                pstmt.setInt(7, id);
                pstmt.executeUpdate();
                loadTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Gagal menyimpan data. Pastikan NIM tidak duplikat.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Pilih data yang akan dihapus.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM student WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadTableData();
            JOptionPane.showMessageDialog(frame, "Berhasil menghapus data.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showAverageDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Pilih data untuk penghitungan nilai.");
            return;
        }

        String nama = (String) tableModel.getValueAt(selectedRow, 1);
        String nim = (String) tableModel.getValueAt(selectedRow, 2);
        int nilaiTugas = (int) tableModel.getValueAt(selectedRow, 3);
        int nilaiKuis = (int) tableModel.getValueAt(selectedRow, 4);
        int nilaiUts = (int) tableModel.getValueAt(selectedRow, 5);
        int nilaiUas = (int) tableModel.getValueAt(selectedRow, 6);

        double average = (nilaiTugas + nilaiKuis + nilaiUts + nilaiUas) / 4.0;
        
        String grade = null;
        if (average >= 95){
            grade = "A";
        } else if (average >= 85) {
            grade = "B";
        } else if (average >= 75) {
            grade = "C";
        } else if (average >= 65) {
            grade = "D";
        } else {
            grade = "F";
        }

        String keterangan = "Dinyatakan Lulus";
        if (grade.equals("F")){
            keterangan = "Dinyatakan Tidak Lulus";
        }
        JOptionPane.showMessageDialog(frame, "Nama : " + nama + "\nNIM : " + nim + "\nRata-rata : " + average + "\nNilai : " + grade + "\nKeterangan : " + keterangan);
    }

    public static void main(String[] args) {
        new Mahasiswa();
    }
}
