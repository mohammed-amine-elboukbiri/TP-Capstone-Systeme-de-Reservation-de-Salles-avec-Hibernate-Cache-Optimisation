package com.example.util;

import com.example.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Random;

public class DataInitializer {

    private final EntityManagerFactory emf;
    private final Random random = new Random();

    public DataInitializer(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void initializeData() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // (Optionnel) si tu relances plusieurs fois : vider d'abord
            // clearAll(em);

            System.out.println("==> Création des équipements...");
            Equipement[] equipements = createEquipements(em);

            System.out.println("==> Création des utilisateurs...");
            Utilisateur[] utilisateurs = createUtilisateurs(em);

            System.out.println("==> Création des salles...");
            Salle[] salles = createSalles(em, equipements);

            System.out.println("==> Création des réservations...");
            createReservations(em, utilisateurs, salles);

            em.getTransaction().commit();
            System.out.println("✅ Jeu de données initialisé avec succès !");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("❌ Erreur lors de l'initialisation des données");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /* ------------------------------------------------------------
       1) EQUIPEMENTS
       ------------------------------------------------------------ */

    private Equipement[] createEquipements(EntityManager em) {
        Equipement[] equipements = new Equipement[10];

        // Assure-toi d’avoir : new Equipement(String nom, String description)
        equipements[0] = new Equipement("Projecteur HD", "Projecteur haute définition 4K");
        equipements[0].setReference("PROJ-4K-001");

        equipements[1] = new Equipement("Écran interactif", "Écran tactile 65 pouces");
        equipements[1].setReference("ECRAN-T-65");

        equipements[2] = new Equipement("Système de visioconférence", "Système complet avec caméra HD");
        equipements[2].setReference("VISIO-HD-100");

        equipements[3] = new Equipement("Tableau blanc", "Tableau blanc magnétique 2m x 1m");
        equipements[3].setReference("TB-MAG-2X1");

        equipements[4] = new Equipement("Système audio", "Système audio avec 4 haut-parleurs");
        equipements[4].setReference("AUDIO-4HP");

        equipements[5] = new Equipement("Microphones sans fil", "Set de 4 microphones sans fil");
        equipements[5].setReference("MIC-SF-4");

        equipements[6] = new Equipement("Ordinateur fixe", "PC avec Windows 11 et suite Office");
        equipements[6].setReference("PC-W11-OFF");

        equipements[7] = new Equipement("Connexion WiFi haut débit", "WiFi 6 jusqu'à 1 Gbps");
        equipements[7].setReference("WIFI-6-1G");

        equipements[8] = new Equipement("Système de climatisation", "Climatisation réglable");
        equipements[8].setReference("CLIM-REG");

        equipements[9] = new Equipement("Prises électriques multiples", "10 prises électriques réparties");
        equipements[9].setReference("PRISES-10");

        for (Equipement e : equipements) {
            em.persist(e);
        }

        return equipements;
    }

    /* ------------------------------------------------------------
       2) UTILISATEURS
       ------------------------------------------------------------ */

    private Utilisateur[] createUtilisateurs(EntityManager em) {
        Utilisateur[] utilisateurs = new Utilisateur[20];

        String[] noms = {
                "Martin", "Bernard", "Dubois", "Thomas", "Robert",
                "Richard", "Petit", "Durand", "Leroy", "Moreau",
                "Simon", "Laurent", "Lefebvre", "Michel", "Garcia",
                "David", "Bertrand", "Roux", "Vincent", "Fournier"
        };

        String[] prenoms = {
                "Jean", "Marie", "Pierre", "Sophie", "Thomas",
                "Catherine", "Nicolas", "Isabelle", "Philippe", "Nathalie",
                "Michel", "Françoise", "Patrick", "Monique", "René",
                "Sylvie", "Louis", "Anne", "Daniel", "Christine"
        };

        String[] departements = {
                "Ressources Humaines", "Informatique", "Finance", "Marketing", "Commercial",
                "Production", "Recherche et Développement", "Juridique", "Communication", "Direction"
        };

        for (int i = 0; i < utilisateurs.length; i++) {
            String nom = noms[i];
            String prenom = prenoms[i];
            String email = prenom.toLowerCase() + "." + nom.toLowerCase() + "@example.com";

            // Assure-toi d’avoir : new Utilisateur(String nom, String prenom, String email)
            Utilisateur u = new Utilisateur(nom, prenom, email);

            u.setTelephone("06" + (10000000 + random.nextInt(90000000)));
            u.setDepartement(departements[i % departements.length]);

            em.persist(u);
            utilisateurs[i] = u;
        }

        return utilisateurs;
    }

    /* ------------------------------------------------------------
       3) SALLES
       ------------------------------------------------------------ */

    private Salle[] createSalles(EntityManager em, Equipement[] equipements) {
        Salle[] salles = new Salle[15];

        // Assure-toi d’avoir : new Salle(String nom, Integer capacite)
        // + méthodes utilitaires addEquipement

        // Bâtiment A - Salles réunion standard (A1..A5)
        for (int i = 0; i < 5; i++) {
            Salle s = new Salle("Salle A" + (i + 1), 10 + i * 2);
            s.setDescription("Salle de réunion standard");
            s.setBatiment("Bâtiment A");
            s.setEtage((i % 3) + 1);
            s.setNumero("A" + (i + 1));

            // équipements de base
            s.addEquipement(equipements[3]); // tableau blanc
            s.addEquipement(equipements[7]); // wifi
            s.addEquipement(equipements[9]); // prises

            // optionnels
            if (i % 2 == 0) s.addEquipement(equipements[0]); // projecteur
            if (i % 3 == 0) s.addEquipement(equipements[4]); // audio

            em.persist(s);
            salles[i] = s;
        }

        // Bâtiment B - Salles formation (B1..B5)
        for (int i = 5; i < 10; i++) {
            Salle s = new Salle("Salle B" + (i - 4), 20 + (i - 5) * 5);
            s.setDescription("Salle de formation équipée");
            s.setBatiment("Bâtiment B");
            s.setEtage((i % 4) + 1);
            s.setNumero("B" + (i - 4));

            s.addEquipement(equipements[0]); // projecteur
            s.addEquipement(equipements[3]); // tableau
            s.addEquipement(equipements[6]); // PC
            s.addEquipement(equipements[7]); // wifi
            s.addEquipement(equipements[9]); // prises

            if (i % 2 == 0) s.addEquipement(equipements[1]); // écran interactif

            em.persist(s);
            salles[i] = s;
        }

        // Bâtiment C - Salles conférence (C1..C5)
        for (int i = 10; i < 15; i++) {
            Salle s = new Salle("Salle C" + (i - 9), 50 + (i - 10) * 20);
            s.setDescription("Salle de conférence haut de gamme");
            s.setBatiment("Bâtiment C");
            s.setEtage((i % 3) + 1);
            s.setNumero("C" + (i - 9));

            s.addEquipement(equipements[0]); // projecteur
            s.addEquipement(equipements[2]); // visio
            s.addEquipement(equipements[4]); // audio
            s.addEquipement(equipements[5]); // micros
            s.addEquipement(equipements[7]); // wifi
            s.addEquipement(equipements[8]); // clim
            s.addEquipement(equipements[9]); // prises

            em.persist(s);
            salles[i] = s;
        }

        return salles;
    }

    /* ------------------------------------------------------------
       4) RESERVATIONS (réaliste + évite chevauchement)
       ------------------------------------------------------------ */

    private void createReservations(EntityManager em, Utilisateur[] utilisateurs, Salle[] salles) {
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        String[] motifs = {
                "Réunion d'équipe", "Entretien", "Formation", "Présentation client",
                "Brainstorming", "Réunion de projet", "Conférence", "Atelier",
                "Séminaire", "Réunion de direction", "Démonstration produit"
        };

        int created = 0;
        int target = 100;

        // On essaie jusqu’à créer 100 réservations valides
        // (on limite les essais pour éviter boucle infinie si trop de conflits)
        int maxAttempts = 2000;
        int attempts = 0;

        while (created < target && attempts < maxAttempts) {
            attempts++;

            int jourOffset = random.nextInt(90);         // sur 90 jours
            int heureDebut = 8 + random.nextInt(9);      // 8h..16h
            int duree = 1 + random.nextInt(3);           // 1..3h

            LocalDateTime dateDebut = now.plusDays(jourOffset).withHour(heureDebut);
            LocalDateTime dateFin = dateDebut.plusHours(duree);

            Utilisateur utilisateur = utilisateurs[random.nextInt(utilisateurs.length)];
            Salle salle = salles[random.nextInt(salles.length)];

            // ✅ vérifier qu'il n'y a pas chevauchement sur la même salle
            if (hasConflict(em, salle.getId(), dateDebut, dateFin)) {
                continue;
            }

            // Assure-toi d’avoir : new Reservation(LocalDateTime debut, LocalDateTime fin, String motif)
            Reservation r = new Reservation(dateDebut, dateFin, motifs[random.nextInt(motifs.length)]);

            // Statut : 80% confirmées, 10% en attente, 10% annulées
            int statutRandom = random.nextInt(10);
            if (statutRandom < 8) r.setStatut(StatutReservation.CONFIRMEE);
            else if (statutRandom < 9) r.setStatut(StatutReservation.EN_ATTENTE);
            else r.setStatut(StatutReservation.ANNULEE);

            // ✅ relations (utilitaires)
            utilisateur.addReservation(r);
            salle.addReservation(r);

            em.persist(r);
            created++;
        }

        System.out.println("Réservations créées: " + created + " (tentatives: " + attempts + ")");

        if (created < target) {
            System.out.println("⚠️ Pas pu atteindre " + target + " réservations à cause des conflits (normal si planning chargé).");
        }
    }

    /**
     * Conflit si une réservation existante chevauche l'intervalle [debut, fin[
     * Condition overlap classique :
     * existingStart < fin AND existingEnd > debut
     */
    private boolean hasConflict(EntityManager em, Long salleId, LocalDateTime debut, LocalDateTime fin) {
        TypedQuery<Long> q = em.createQuery(
                "select count(r) " +
                        "from Reservation r " +
                        "where r.salle.id = :salleId " +
                        "and r.statut <> :annulee " +
                        "and r.dateDebut < :fin " +
                        "and r.dateFin > :debut",
                Long.class
        );

        Long count = q.setParameter("salleId", salleId)
                .setParameter("annulee", StatutReservation.ANNULEE)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getSingleResult();

        return count != null && count > 0;
    }

    /* ------------------------------------------------------------
       (Optionnel) Clear DB si tu veux rerun
       ------------------------------------------------------------ */

    @SuppressWarnings("unused")
    private void clearAll(EntityManager em) {
        // Ordre important à cause des FK
        em.createQuery("delete from Reservation").executeUpdate();
        em.createQuery("delete from Salle").executeUpdate();
        em.createQuery("delete from Utilisateur").executeUpdate();
        em.createQuery("delete from Equipement").executeUpdate();
    }
}