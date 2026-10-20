package com.tp.auth.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.tp.auth.db.Database;
import com.tp.auth.model.Contact;

public class ContactDao {

    public List<Contact> findByUser(int userId) throws Exception {
        String sql = "SELECT id, user_id, nom, prenom, email, telephone "
                   + "FROM contacts WHERE user_id = ? ORDER BY nom, prenom";
        List<Contact> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public void insert(Contact c) throws Exception {
        String sql = "INSERT INTO contacts (user_id, nom, prenom, email, telephone) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getNom());
            ps.setString(3, c.getPrenom());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getTelephone());
            ps.executeUpdate();
        }
    }

    // user_id dans le WHERE : un utilisateur ne peut modifier que ses contacts
    public void update(Contact c) throws Exception {
        String sql = "UPDATE contacts SET nom = ?, prenom = ?, email = ?, telephone = ? "
                   + "WHERE id = ? AND user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelephone());
            ps.setInt(5, c.getId());
            ps.setInt(6, c.getUserId());
            ps.executeUpdate();
        }
    }

    public void delete(int id, int userId) throws Exception {
        String sql = "DELETE FROM contacts WHERE id = ? AND user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public List<Contact> search(int userId, String text) throws Exception {
        String sql = "SELECT id, user_id, nom, prenom, email, telephone FROM contacts "
                   + "WHERE user_id = ? AND (nom LIKE ? OR prenom LIKE ? OR email LIKE ?) "
                   + "ORDER BY nom, prenom";
        String pattern = "%" + text + "%";
        List<Contact> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private Contact map(ResultSet rs) throws SQLException {
        return new Contact(rs.getInt("id"),
                           rs.getInt("user_id"),
                           rs.getString("nom"),
                           rs.getString("prenom"),
                           nullToEmpty(rs.getString("email")),
                           nullToEmpty(rs.getString("telephone")));
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}