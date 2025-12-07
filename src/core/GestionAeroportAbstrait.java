package core;
public abstract class GestionAeroportAbstrait implements GestionAeroport {

    protected int nbPistes, nbPortes;
    protected AeroportLogger logger;
    protected boolean[] pisteOccupe;
    protected boolean[] porteOccupe;

    
    protected long sommeAttentePisteMs = 0;
    protected int nbDemandesPiste = 0;
    protected long sommeAttentePorteMs = 0;
    protected int nbDemandesPorte = 0;

    protected long debutSimulation = System.currentTimeMillis();
    protected long tempsOccupePistesMs = 0;
    protected long tempsOccupePortesMs = 0;

    protected long[] debutOccupationPiste;
    protected long[] debutOccupationPorte;
    
    protected long sommeTempsSejourMs = 0;
    protected int nbAvionsTermines = 0;
    
    protected int nbAvionsActifs = 0;

    
    public GestionAeroportAbstrait(int nbPistes, int nbPortes, AeroportLogger logger) {
        this.nbPistes = nbPistes;
        this.nbPortes = nbPortes;
        this.logger = logger;
        pisteOccupe = new boolean[nbPistes];
        porteOccupe = new boolean[nbPortes];
        debutOccupationPiste = new long[nbPistes];
        debutOccupationPorte = new long[nbPortes];

    }


    protected int choisirPisteLibre() {
        for (int i = 0; i < nbPistes; i++) {
            if (!pisteOccupe[i]) 
            	return i;
        }
        return -1;
    }

    protected int choisirPorteLibre() {
        for (int i = 0; i < nbPortes; i++) {
            if (!porteOccupe[i]) 
            	return i;
        }
        return -1;
    }

    protected void occuperPiste(int index) {
        pisteOccupe[index] = true;
        debutOccupationPiste[index] = System.currentTimeMillis();
        if (logger != null) 
        	logger.setPisteEtat(index, true);
    }

    protected void libererPiste(int index) {
        pisteOccupe[index] = false;
        long now = System.currentTimeMillis();
        tempsOccupePistesMs += (now - debutOccupationPiste[index]);
        if (logger != null) 
        	logger.setPisteEtat(index, false);
    }

    protected void occupePorte(int index) {
        porteOccupe[index] = true;
        debutOccupationPorte[index] = System.currentTimeMillis();
        if (logger != null) 
        	logger.setPorteEtat(index, true);
    }

    protected void libererPorte(int index) {
        porteOccupe[index] = false;
        long now = System.currentTimeMillis();
        tempsOccupePortesMs += (now - debutOccupationPorte[index]);
        if (logger != null) 
        	logger.setPorteEtat(index, false);
    }
    
    protected void ajouterAvionFileAttente(int idAvion) {
    	logger.setAvionEtat(idAvion, true);
    }


    
 // pour la metrique : Temps moyen piste/porte
    @Override
    public double getTempsMoyenAttentePisteMs() {
        return nbDemandesPiste == 0 ? 0 : (double) sommeAttentePisteMs / nbDemandesPiste;
    }

    @Override
    public double getTempsMoyenAttentePorteMs() {
        return nbDemandesPorte == 0 ? 0 : (double) sommeAttentePorteMs / nbDemandesPorte;
    }
    
    
    
    // pour la metrique : TauxUtilisation
    @Override
    public double getTauxUtilisationPistes() {
        long duree = System.currentTimeMillis() - debutSimulation;
        if (duree == 0) return 0;
        return (double) tempsOccupePistesMs / (duree * nbPistes);
    }

    @Override
    public double getTauxUtilisationPortes() {
        long duree = System.currentTimeMillis() - debutSimulation;
        if (duree == 0) return 0;
        return (double) tempsOccupePortesMs / (duree * nbPortes);
    }
    
    
    //pour la metrique : Temps moyen sejour 
    @Override
    public synchronized void enregistrerTempsSejour(long dureeMs) {
        sommeTempsSejourMs += dureeMs;
        nbAvionsTermines++;
    }

    @Override
    public double getTempsMoyenSejourMs() {
        return nbAvionsTermines == 0 ? 0 : (double) sommeTempsSejourMs / nbAvionsTermines;
    }
    
    
  //pour arreter les metriques de taux d'utilisation 
    @Override
    public synchronized void avionCommence() {
        nbAvionsActifs++;
    }

    @Override
    public synchronized void avionTermine() {
        nbAvionsActifs--;
    }

    @Override
    public synchronized int getNbAvionsActifs() {
        return nbAvionsActifs;
    }
}
