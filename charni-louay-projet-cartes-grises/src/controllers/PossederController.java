package controllers;

import database.DatabaseConnection;
import models.Posseder;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PossederController {

    public List<Posseder> getAllPosseder() {
        List<Posseder> possederList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT p.id_proprietaire, p.prenom, p.nom, v.id_vehicule, v.matricule, pos.date_debut_propriete, pos.date_fin_propriete " +
                        "FROM proprietaire p " +
                        "JOIN posseder pos ON p.id_proprietaire = pos.id_proprietaire " +
                        "JOIN vehicule v ON pos.id_vehicule = v.id_vehicule")) {

            while (rs.next()) {
                possederList.add(new Posseder(
                    rs.getInt("id_proprietaire"),
                    rs.getInt("id_vehicule"),
                    rs.getString("prenom"),
                    rs.getString("nom"),
                    rs.getString("matricule"),
                    rs.getDate("date_debut_propriete"),
                    rs.getDate("date_fin_propriete"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return possederList;
    }

    public void updatePossession(
        String newPrenom, String newNom, String newMatricule,
        Date newDateFin, String oldPrenom, String oldNom, String oldMatricule) {

        int oldIdProprietaire = getProprietaireIdByName(oldPrenom, oldNom);
        int oldIdVehicule = getVehiculeIdByMatricule(oldMatricule);

        if (oldIdProprietaire == -1 || oldIdVehicule == -1) {
            JOptionPane.showMessageDialog(null, "Ancien propriétaire ou véhicule introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newIdProprietaire = getProprietaireIdByName(newPrenom, newNom);
        int newIdVehicule = getVehiculeIdByMatricule(newMatricule);

        if (newIdProprietaire == -1 || newIdVehicule == -1) {
            JOptionPane.showMessageDialog(null, "Nouveau propriétaire ou véhicule introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existsPossession(newIdProprietaire, newIdVehicule)) {
            JOptionPane.showMessageDialog(null, "Erreur : Cette relation existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String updateSQL = "UPDATE posseder SET id_proprietaire = ?, id_vehicule = ?, date_fin_propriete = ? WHERE id_proprietaire = ? AND id_vehicule = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setInt(1, newIdProprietaire);
                updateStmt.setInt(2, newIdVehicule);
                updateStmt.setDate(3, newDateFin);
                updateStmt.setInt(4, oldIdProprietaire);
                updateStmt.setInt(5, oldIdVehicule);
                int rows = updateStmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Modification réussie !");
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPossession(String prenom, String nom, String matricule, Date dateDebut) {
        int idProprietaire = getProprietaireIdByName(prenom, nom);
        int idVehicule = getVehiculeIdByMatricule(matricule);

        if (idProprietaire == -1 || idVehicule == -1) {
            JOptionPane.showMessageDialog(null, "Propriétaire ou véhicule introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existsPossession(idProprietaire, idVehicule)) {
            JOptionPane.showMessageDialog(null, "Cette relation existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO posseder (id_proprietaire, id_vehicule, date_debut_propriete) VALUES (?, ?, ?)") ) {
            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);
            ps.setDate(3, dateDebut);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Possession ajoutée avec succès.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePossession(int idProprietaire, int idVehicule) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM posseder WHERE id_proprietaire = ? AND id_vehicule = ?")) {

            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Possession supprimée avec succès !");
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getVehiculeIdByMatricule(String matricule) {
        String query = "SELECT id_vehicule FROM VEHICULE WHERE matricule = ?";
        return executeQueryForInt(query, new Object[]{matricule}, "id_vehicule");
    }

    public String getVehiculeMatriculeById(int idVehicule) {
        String query = "SELECT matricule FROM VEHICULE WHERE id_vehicule = ?";
        return executeQueryForString(query, new Object[]{idVehicule}, "matricule");
    }

    public String getProprietaireNomPrenomById(int idProprietaire) {
        String query = "SELECT nom, prenom FROM PROPRIETAIRE WHERE id_proprietaire = ?";
        String nom = executeQueryForString(query, new Object[]{idProprietaire}, "nom");
        String prenom = executeQueryForString(query, new Object[]{idProprietaire}, "prenom");
        return (nom != null && prenom != null) ? nom + " " + prenom : null;
    }

    public int getProprietaireIdByName(String prenom, String nom) {
        String query = "SELECT id_proprietaire FROM PROPRIETAIRE WHERE prenom = ? AND nom = ?";
        return executeQueryForInt(query, new Object[]{prenom, nom}, "id_proprietaire");
    }

    private boolean existsPossession(int idProprietaire, int idVehicule) {
        String query = "SELECT COUNT(*) FROM posseder WHERE id_proprietaire = ? AND id_vehicule = ?";
        return executeQueryForInt(query, new Object[]{idProprietaire, idVehicule}, "count") > 0;
    }

    private int executeQueryForInt(String query, Object[] params, String column) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String executeQueryForString(String query, Object[] params, String column) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setParameters(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
