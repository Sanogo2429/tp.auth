package com.tp.auth.ui;

import java.awt.*;
import javax.swing.*;
import com.tp.auth.model.Contact;
import com.tp.auth.model.User;
import com.tp.auth.service.ContactService;

public class ContactDialog extends JDialog {

    private final JTextField nomField = new JTextField(20);
    private final JTextField prenomField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField telephoneField = new JTextField(20);
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);

    private final ContactService service = new ContactService();
    private final User user;
    private final Contact contact; // null = ajout, sinon modification
    private boolean saved = false;

    public ContactDialog(Frame owner, User user, Contact contact) {
        super(owner, contact == null ? "Ajouter un contact" : "Modifier un contact", true);
        this.user = user;
        this.contact = contact;

        if (contact != null) {
            nomField.setText(contact.getNom());
            prenomField.setText(contact.getPrenom());
            emailField.setText(contact.getEmail());
            telephoneField.setText(contact.getTelephone());
        }

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Nom :"));
        form.add(nomField);
        form.add(new JLabel("Prénom :"));
        form.add(prenomField);
        form.add(new JLabel("Email :"));
        form.add(emailField);
        form.add(new JLabel("Téléphone :"));
        form.add(telephoneField);

        errorLabel.setForeground(Color.RED);

        JButton saveBtn = new JButton("Enregistrer");
        JButton cancelBtn = new JButton("Annuler");
        saveBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(saveBtn);

        JPanel buttons = new JPanel();
        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.add(errorLabel, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.add(form, BorderLayout.CENTER);
        content.add(south, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void save() {
        try {
            int id = (contact == null) ? 0 : contact.getId();
            // user_id vient de l'utilisateur connecté, jamais d'une saisie
            Contact c = new Contact(id, user.getId(),
                    nomField.getText(), prenomField.getText(),
                    emailField.getText(), telephoneField.getText());

            String error = (contact == null) ? service.add(c) : service.update(c);
            if (error == null) {
                saved = true;
                dispose();
            } else {
                errorLabel.setText(error); // le formulaire reste ouvert
            }
        } catch (Exception ex) {
            errorLabel.setText("Erreur : " + ex.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}