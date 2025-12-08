# AirportManagement – Simulation concurrente d’un aéroport

## 1. Description

Ce projet simule la gestion d’un aéroport en Java avec plusieurs avions (threads) qui se partagent des ressources limitées :

- 2 pistes d’atterrissage / décollage
- 4 portes d’embarquement

L’objectif est de comparer deux mécanismes de synchronisation :

- Version **Sémaphore** (package `semaphoreversion`)
- Version **Moniteur** (package `monitorversion`)

L’interface graphique Swing (package `gui`) est commune aux deux versions.

## 2. Organisation du code

Le projet Java contient plusieurs packages :

- `core`  
  - classes communes : `Avion`, `GestionAeroport`,`GestionAeroportAbstrait`, `AeroportLogger`.
- `semaphoreversion`  
  - implémentation avec `GestionAeroportSemaphore`
- `monitorversion`  
  - implémentation avec `GestionAeroportMoniteur`
- `gui`  
  - interface graphique : `MainWindow`.

## 3. Exécution dans Eclipse

1. Ouvrir le projet dans Eclipse.
2. Lancer la classe `gui.MainWindow` :
   - Clic droit sur `MainWindow.java`  
   - **Run As > Java Application**
3. Dans la fenêtre :
   - Choisir la méthode de synchronisation dans la combo (Sémaphore ou Moniteur).
   - Cliquer sur **Démarrer**.
   - Cliquer sur **Ajouter avion** pour lancer des avions.

## 4. Fonctionnalités

L’interface affiche :

- L’état des pistes et des portes (vert = libre, rouge = occupée).
- La file d’attente des avions.
- Un journal des événements (arrivées, portes, décollages).
- Des métriques de performance :
  - Temps moyen d’attente pour une piste.
  - Temps moyen d’attente pour une porte.
  - Taux d’utilisation des pistes et des portes.
  - Temps moyen de séjour d’un avion.

## 5. Scénario de comparaison

Pour comparer Sémaphore et Moniteur, on utilise par exemple :

- 2 pistes, 4 portes
- 30 avions créés progressivement
- Durées :
  - Atterrissage : 3 000 ms
  - Porte : 5 000 ms
  - Décollage : 3 000 ms

On exécute la simulation avec chaque méthode et on compare les métriques affichées.
