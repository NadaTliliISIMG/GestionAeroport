package SemaphoreVersion;

import java.util.concurrent.Semaphore;

import core.AeroportLogger;
import core.GestionAeroportAbstrait;


public class GestionAeroportSemaphore extends GestionAeroportAbstrait {

    private final Semaphore semPistes;
    private final Semaphore semPortes;

    public GestionAeroportSemaphore(int nbPistes, int nbPortes, AeroportLogger logger) {
        super(nbPistes, nbPortes, logger);
        semPistes = new Semaphore(nbPistes, true);
        semPortes = new Semaphore(nbPortes, true);
    }

    @Override
    public void aterrir(int id) throws InterruptedException {
        long t0 = System.currentTimeMillis();
        semPistes.acquire();
        sommeAttentePisteMs += System.currentTimeMillis() - t0;
        nbDemandesPiste++;

        int piste = choisirPisteLibre();
        occuperPiste(piste);
        logger.log("[Semaphore] Avion " + id + " atterrit sur piste " + (piste+1));

        Thread.sleep(3000);
        //puisquil n'a pas une file d'attente pour les portes il va grader la semaphore
        //piste jusqu'a il avoir une semaphore porte
        occuperPorte(id,piste);
        
       

    }

    @Override
    public void decoller(int id) throws InterruptedException {
        long t0 = System.currentTimeMillis();
        ajouterAvionFileAttente(id);
        semPistes.acquire();
        sommeAttentePisteMs += System.currentTimeMillis() - t0;
        nbDemandesPiste++;

        int piste = choisirPisteLibre();
        occuperPiste(piste);
        logger.log("[Semaphore] Avion " + id + " décolle depuis piste " + (piste+1));

        
        Thread.sleep(3000);

        libererPiste(piste);
        semPistes.release();
    }

    @Override
    public void occuperPorte(int id,int piste) throws InterruptedException {
        long t0 = System.currentTimeMillis();
        semPortes.acquire();
        
        libererPiste(piste);
        semPistes.release();
        
        sommeAttentePorteMs += System.currentTimeMillis() - t0;
        nbDemandesPorte++;

        int porte = choisirPorteLibre();
        occupePorte(porte);
        logger.log("[Semaphore] Avion " + id + " occupe porte " + (porte+1));

        Thread.sleep(5000);

        libererPorte(porte);
        semPortes.release();

        decoller(id);
    }
}
