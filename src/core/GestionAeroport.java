package core;

public interface GestionAeroport {
    void aterrir(int idAvion) throws InterruptedException;
    void decoller(int idAvion) throws InterruptedException;
    void occuperPorte(int idAvion, int piste) throws InterruptedException;
    
    void avionCommence();
    void avionTermine();
    int getNbAvionsActifs();
    
 // metriques
    void enregistrerTempsSejour(long dureeMs);
    double getTempsMoyenSejourMs();
    double getTempsMoyenAttentePisteMs();
    double getTempsMoyenAttentePorteMs();
    double getTauxUtilisationPistes();  
    double getTauxUtilisationPortes();  
	
}
