package com.tp.auth.service;

import java.util.List;
import com.tp.auth.dao.ContactDao;
import com.tp.auth.model.Contact;

public class ContactService {

    private final ContactDao dao = new ContactDao();

    public List<Contact> list(int userId) throws Exception {
        return dao.findByUser(userId);
    }

    public List<Contact> search(int userId, String text) throws Exception {
        if (text == null || text.isBlank()) {
            return dao.findByUser(userId);
        }
        return dao.search(userId, text.trim());
    }

    // Retourne null si tout va bien, sinon le message d'erreur
    public String add(Contact c) throws Exception {
        String error = validate(c);
        if (error != null) return error;
        dao.insert(c);
        return null;
    }

    public String update(Contact c) throws Exception {
        String error = validate(c);
        if (error != null) return error;
        dao.update(c);
        return null;
    }

    public void delete(int id, int userId) throws Exception {
        dao.delete(id, userId);
    }

    // Nettoie les champs puis vérifie les règles
    private String validate(Contact c) {
        c.setNom(clean(c.getNom()));
        c.setPrenom(clean(c.getPrenom()));
        c.setEmail(clean(c.getEmail()));
        c.setTelephone(clean(c.getTelephone()));

        if (c.getNom().isEmpty()) return "Le nom est obligatoire";
        if (c.getPrenom().isEmpty()) return "Le prénom est obligatoire";
        if (!c.getEmail().isEmpty()
                && !(c.getEmail().contains("@") && c.getEmail().contains("."))) {
            return "Email invalide";
        }
        if (!c.getTelephone().isEmpty() && !c.getTelephone().matches("\\+?[0-9 ]+")) {
            return "Téléphone invalide";
        }
        return null;
    }

    private String clean(String s) {
        return s == null ? "" : s.trim();
    }
}