package views;

import controllers.PossederController;
import models.Posseder;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class PossederView extends JFrame {
    private PossederController controller;

    public PossederView() {
        controller = new PossederController();

        setTitle("Gestion des Possessions");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        List<Posseder> possessions = controller.getAllPosseder();
        for (Posseder possession : possessions) {
            JPanel possessionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel possessionLabel = new JLabel(
                    "Propriétaire: " + possession.getPrenom() + " " + possession.getNom() +
                            ", Véhicule: " + possession.getMatricule() +
                            ", Début: " + possession.getDateDebutPropriete() +
                            ", Fin: " + (possession.getDateFinPropriete() != null ? possession.getDateFinPropriete() : "Actuel")
            );

            JButton modifyButton = new JButton("Modifier la possession");
            modifyButton.addActionListener(e -> {
                JTextField newPrenomField = new JTextField(possession.getPrenom());
                JTextField newNomField = new JTextField(possession.getNom());
                JTextField newMatriculeField = new JTextField(possession.getMatricule());
                JTextField dateFinField = new JTextField(possession.getDateFinPropriete() != null ? possession.getDateFinPropriete().toString() : "");

                Object[] message = {
                        "Nouveau prénom du propriétaire:", newPrenomField,
                        "Nouveau nom du propriétaire:", newNomField,
                        "Nouveau matricule du véhicule:", newMatriculeField,
                        "Nouvelle date de fin (YYYY-MM-DD):", dateFinField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Modifier une possession", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String newPrenom = newPrenomField.getText();
                        String newNom = newNomField.getText();
                        String newMatricule = newMatriculeField.getText();
                        Date newDateFin = dateFinField.getText().trim().isEmpty() ? null : Date.valueOf(dateFinField.getText().trim());

                        controller.updatePossession(newPrenom, newNom, newMatricule, newDateFin, possession.getPrenom(), possession.getNom(), possession.getMatricule());
                        refreshView();
                    } catch (Exception ex) {
                        showErrorMessage("Format de date invalide ou erreur lors de la mise à jour.");
                    }
                }
            });

            JButton deleteButton = new JButton("Supprimer");
            deleteButton.addActionListener(e -> {
                int confirmation = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette possession ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
                        controller.deletePossession(possession.getIdProprietaire(), possession.getIdVehicule());
                        refreshView();
                    } catch (Exception ex) {
                        showErrorMessage("Erreur lors de la suppression de la possession.");
                    }
                }
            });

            possessionPanel.add(possessionLabel);
            possessionPanel.add(modifyButton);
            possessionPanel.add(deleteButton);
            panel.add(possessionPanel);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addButton = new JButton("Ajouter une Possession");
        addButton.addActionListener(e -> {
            JTextField prenomField = new JTextField();
            JTextField nomField = new JTextField();
            JTextField matriculeField = new JTextField();
            JTextField dateDebutField = new JTextField();

            Object[] message = {
                    "Prénom du propriétaire:", prenomField,
                    "Nom du propriétaire:", nomField,
                    "Matricule du véhicule:", matriculeField,
                    "Date de début (YYYY-MM-DD):", dateDebutField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter une Possession", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String prenom = prenomField.getText().trim();
                    String nom = nomField.getText().trim();
                    String matricule = matriculeField.getText().trim();
                    Date dateDebut = Date.valueOf(dateDebutField.getText().trim());

                    if (prenom.isEmpty() || nom.isEmpty() || matricule.isEmpty()) {
                        showErrorMessage("Tous les champs doivent être remplis.");
                        return;
                    }

                    controller.addPossession(prenom, nom, matricule, dateDebut);
                    refreshView();
                } catch (Exception ex) {
                    showErrorMessage("Données invalides ou erreur lors de l'ajout.");
                }
            }
        });

        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);

        setVisible(true);
    }

    private void refreshView() {
        dispose();
        new PossederView();
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new PossederView();
    }
}
