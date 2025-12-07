package MonitorVersion;
import core.AeroportLogger;
import core.GestionAeroportAbstrait;

public class GestionAeroportMoniteur extends GestionAeroportAbstrait {

    private final Object lockPistes = new Object();
    private final Object lockPortes = new Object();

    private int pistesLibres;
    private int portesLibres;

    public GestionAeroportMoniteur(int nbPistes, int nbPortes, AeroportLogger logger) {
        super(nbPistes, nbPortes, logger);
        pistesLibres = nbPistes;
        portesLibres = nbPortes;
    }

    @Override
    public void aterrir(int id) throws InterruptedException {
        long t0 = System.currentTimeMillis();

        synchronized (lockPistes) {
            while (pistesLibres == 0) {
                lockPistes.wait();
            }
            pistesLibres--;
        }

        sommeAttentePisteMs += System.currentTimeMillis() - t0;
        nbDemandesPiste++;

        int piste = choisirPisteLibre();
        occuperPiste(piste);
        logger.log("[Moniteur] Avion " + id + " atterrit sur piste " + (piste+1));

        Thread.sleep(3000);

        //demander une porte (mais on garde la piste)
        occuperPorte(id, piste);
    }

    @Override
    public void decoller(int id) throws InterruptedException {
        long t0 = System.currentTimeMillis();

        ajouterAvionFileAttente(id);

        synchronized (lockPistes) {
            while (pistesLibres == 0) {
                lockPistes.wait();
            }
            pistesLibres--;
        }

        sommeAttentePisteMs += System.currentTimeMillis() - t0;
        nbDemandesPiste++;

        int piste = choisirPisteLibre();
        occuperPiste(piste);
        logger.log("[Moniteur] Avion " + id + " décolle depuis piste " + (piste+1));

        Thread.sleep(3000);

        libererPiste(piste);

        synchronized (lockPistes) {

            pistesLibres++;
            lockPistes.notifyAll();
        }
    }

    @Override
    public void occuperPorte(int id, int piste) throws InterruptedException {
        long t0 = System.currentTimeMillis();

        synchronized (lockPortes) {
            while (portesLibres == 0) {
                lockPortes.wait();
            }
            portesLibres--;
        }

        libererPiste(piste);
        synchronized (lockPistes) {
            pistesLibres++;
            lockPistes.notifyAll();
        }

        sommeAttentePorteMs += System.currentTimeMillis() - t0;
        nbDemandesPorte++;

        int porte = choisirPorteLibre();
        occupePorte(porte);
        logger.log("[Moniteur] Avion " + id + " occupe porte " + (porte+1));

        Thread.sleep(5000);

        libererPorte(porte);

        synchronized (lockPortes) {
            portesLibres++;
            lockPortes.notifyAll();
        }

        decoller(id);
    }
}
