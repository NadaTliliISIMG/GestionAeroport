package GUI;

import javax.swing.*;

import MonitorVersion.GestionAeroportMoniteur;
import SemaphoreVersion.GestionAeroportSemaphore;
import core.AeroportLogger;
import core.Avion;
import core.GestionAeroportAbstrait;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicInteger;

public class MainWindow extends JFrame implements AeroportLogger {

	private static final long serialVersionUID = 1L;

	private GestionAeroportAbstrait gestion;

    private JLabel[] pistesLabels;
    private JLabel[] portesLabels;

    private JLabel lblTPiste, lblTPorte, lblUPiste, lblUPorte,lblTSejour;

    private DefaultListModel<String> fileAttenteModel;
    private JTextArea journal;

    private JComboBox<String> comboMethode;
    private JButton btnAddAvion;

    private AtomicInteger nextId = new AtomicInteger(1);

    public MainWindow() {
        setTitle("Gestion Aéroport");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // in the TOP
        JPanel top = new JPanel();
		        comboMethode = new JComboBox<>(new String[]{"Sémaphore", "Moniteur"});
		        JButton btnStart = new JButton("Démarrer");
		        top.add(new JLabel("Méthode : "));
		        top.add(comboMethode);
		        top.add(btnStart);
        add(top, BorderLayout.NORTH);

        // in the CENTER
        JPanel center = new JPanel(new GridLayout(1, 3));

		        // 1) les Piste libre
		        JPanel p1 = new JPanel(new GridLayout(2, 1));
		        p1.setBorder(BorderFactory.createTitledBorder("Pistes"));
		        pistesLabels = new JLabel[2];
		        for (int i = 0; i < 2; i++) {
		            pistesLabels[i] = new JLabel("Piste " + (i + 1) + " : Libre");
		            pistesLabels[i].setOpaque(true);
		            pistesLabels[i].setBackground(Color.GREEN);
		            p1.add(pistesLabels[i]);
		        }
		        center.add(p1);
		
		        // 2) les Porte libre
		        JPanel p2 = new JPanel(new GridLayout(4, 1));
		        p2.setBorder(BorderFactory.createTitledBorder("Portes"));
		        portesLabels = new JLabel[4];
		        for (int i = 0; i < 4; i++) {
		            portesLabels[i] = new JLabel("Porte " + (i + 1) + " : Libre");
		            portesLabels[i].setOpaque(true);
		            portesLabels[i].setBackground(Color.GREEN);
		            p2.add(portesLabels[i]);
		        }
		        center.add(p2);
		
		        // 3) la File d'attete
		        JPanel p3 = new JPanel(new BorderLayout());
		        p3.setBorder(BorderFactory.createTitledBorder("File d’attente"));
		        fileAttenteModel = new DefaultListModel<>();
		        p3.add(new JScrollPane(new JList<>(fileAttenteModel)));
		        center.add(p3);

        
        add(center, BorderLayout.CENTER);

        // in the BOTTOM
        JPanel bottom = new JPanel();
		        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		
		        btnAddAvion = new JButton("Ajouter avion");
		        btnAddAvion.setEnabled(false);
		        bottom.add(btnAddAvion);

		        lblTPiste = new JLabel("Temps moyen piste = 0 ms");
		        lblTPorte = new JLabel("Temps moyen porte = 0 ms");
		        lblUPiste = new JLabel("Utilisation pistes = 0%");
		        lblUPorte = new JLabel("Utilisation portes = 0%");
		        lblTSejour= new JLabel("Temps moyen séjour = 0%");
		        bottom.add(lblTPiste);
		        bottom.add(lblTPorte);
		        bottom.add(lblUPiste);
		        bottom.add(lblUPorte);
		        bottom.add(lblTSejour);
		
		        journal = new JTextArea(8, 50);
		        journal.setEditable(false);
		        bottom.add(new JScrollPane(journal));
        add(bottom, BorderLayout.SOUTH);

        // les actions sur les boutons 
        btnStart.addActionListener(this::demarrer);
        btnAddAvion.addActionListener(this::ajouterAvion);

        // Metriques s'affichent auto: Timer(int delay, ActionListener listener)
        new Timer(500, e -> afficherMetriques()).start();

        setVisible(true);
    }
    
    
    //les methodes des actions 
    private void demarrer(ActionEvent e) {
        if (comboMethode.getSelectedItem().equals("Sémaphore"))
            gestion = new GestionAeroportSemaphore(pistesLabels.length, portesLabels.length, this);
        else
            gestion = new GestionAeroportMoniteur(pistesLabels.length, portesLabels.length, this);

        btnAddAvion.setEnabled(true);
        log("Simulation démarrée ! Méthode : " + comboMethode.getSelectedItem());
    }

    private void ajouterAvion(ActionEvent e) {
        if (gestion == null) return;
        int id = nextId.getAndIncrement();

        // ajouter en file d'attente visuelle
        SwingUtilities.invokeLater(() -> fileAttenteModel.addElement("Avion " + id + " en attente"));
        

        // lancer le thread avion qui utilisera la gestion (atterrir -> porte -> décoller)
        Avion a = new Avion(id, gestion);
        a.start();

        log("Avion " + id + " ajouté.");
    }

    private void afficherMetriques() {
        if (gestion == null) 
        	return;

        if (gestion.getNbAvionsActifs() == 0) {
            //on ne recalcule plus.
            return;
        }
        
        lblTPiste.setText("Temps moyen piste = " + String.format("%.1f", gestion.getTempsMoyenAttentePisteMs()) + " ms");
        lblTPorte.setText("Temps moyen porte = " + String.format("%.1f", gestion.getTempsMoyenAttentePorteMs()) + " ms");
        lblUPiste.setText("Utilisation pistes = " + String.format("%.1f", gestion.getTauxUtilisationPistes() * 100) + "%");
        lblUPorte.setText("Utilisation portes = " + String.format("%.1f", gestion.getTauxUtilisationPortes() * 100) + "%");
        lblTSejour.setText("Temps moyen séjour = " + String.format("%.1f", gestion.getTempsMoyenSejourMs()) + " ms");
    }

    @Override
    public void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            journal.append(msg + "\n");
            
        });
    }

    @Override
    public void setPisteEtat(int index, boolean occupe) {
        SwingUtilities.invokeLater(() -> {
            if (index < 0 || index >= pistesLabels.length) 
            	return;
            pistesLabels[index].setBackground(occupe ? Color.RED : Color.GREEN);
            pistesLabels[index].setText("Piste " + (index + 1) + " : " + (occupe ? "Occupée" : "Libre"));
            // si une piste est occupee par un avion on peut retirer le premier element de la file
            if (occupe && fileAttenteModel.size() > 0) {
                // retirer l'element le plus ancien (le premier) considerant que c'était pour cette piste
                fileAttenteModel.remove(0);
            }
        });
    }

    @Override
    public void setPorteEtat(int index, boolean occupe) {
        SwingUtilities.invokeLater(() -> {
            if (index < 0 || index >= portesLabels.length) 
            	return;
            portesLabels[index].setBackground(occupe ? Color.RED : Color.GREEN);
            portesLabels[index].setText("Porte " + (index + 1) + " : " + (occupe ? "Occupée" : "Libre"));
        });
    }
    
    @Override
    public void setAvionEtat(int index, boolean waiting) {
        SwingUtilities.invokeLater(() -> {
            if (index < 0) 
            	return;
            // ajouté la file visuelle
            if (waiting) {
            	fileAttenteModel.addElement("Avion " + index + " en attente");
            }
        });
    }

    
    //main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
