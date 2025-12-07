package core;

public class Avion extends Thread {
    private final int id;
    private final GestionAeroport gestion;
    
    private long debut;
    private long fin;

    public Avion(int id,GestionAeroport gestion) {
        this.id = id;
        this.gestion = gestion;
        this.setName("Avion-" + id);
    }
	@Override
    public void run() {
        try {
        	gestion.avionCommence();
        	debut = System.currentTimeMillis();
        	gestion.aterrir(id);//demander une piste 
        	fin = System.currentTimeMillis();
            long duree = fin - debut;
            gestion.enregistrerTempsSejour(duree);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            gestion.avionTermine();// avion totalement terminé
        }
    }
}
