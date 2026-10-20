package com.tp.auth.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.tp.auth.model.Contact;
import com.tp.auth.model.User;
import com.tp.auth.service.ContactService;

public class ContactFrame extends JFrame {

    private final User user;
    private final ContactService service = new ContactService();
    private final JTextField searchField = new JTextField(20);
    private List<Contact> contacts = new ArrayList<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[] { "Nom", "Prénom", "Email", "Téléphone" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public ContactFrame(User user) {
        super("Mes contacts");
        this.user = user;

        // Barre du haut : utilisateur connecté + déconnexion
        JButton logoutBtn = new JButton("Se déconnecter");
        logoutBtn.addActionListener(e -> logout());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(new JLabel("Connecté en tant que " + user.getUsername()), BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);

        // Recherche
        JButton searchBtn = new JButton("Rechercher");
        searchBtn.addActionListener(e -> refreshTable());
        searchField.addActionListener(e -> refreshTable()); // Entrée lance la recherche
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Rechercher :"));
        searchBar.add(searchField);
        searchBar.add(searchBtn);

        JPanel north = new JPanel(new BorderLayout(5, 5));
        north.add(topBar, BorderLayout.NORTH);
        north.add(searchBar, BorderLayout.SOUTH);

        // Boutons du bas
        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton deleteBtn = new JButton("Supprimer");
        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> edit());
        deleteBtn.addActionListener(e -> delete());
        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(north, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        refreshTable();
    }

    private void refreshTable() {
        try {
            contacts = service.search(user.getId(), searchField.getText());
            model.setRowCount(0);
            for (Contact c : contacts) {
                model.addRow(new Object[] {
                        c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Contact selectedContact() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un contact");
            return null;
        }
        return contacts.get(row);
    }

    private void openDialog(Contact contact) {
        ContactDialog dialog = new ContactDialog(this, user, contact);
        dialog.setVisible(true); // modal : le code attend la fermeture
        if (dialog.isSaved()) {
            refreshTable();
        }
    }

    private void edit() {
        Contact c = selectedContact();
        if (c != null) {
            openDialog(c);
        }
    }

    private void delete() {
        Contact c = selectedContact();
        if (c == null) return;
        int answer = JOptionPane.showConfirmDialog(this,
                "Supprimer " + c.getNom() + " " + c.getPrenom() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                service.delete(c.getId(), user.getId());
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}