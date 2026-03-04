package com.example;

import com.example.repository.*;
import com.example.service.*;
import com.example.test.TestScenarios;
import com.example.util.DataInitializer;
import com.example.util.PerformanceReport;
import com.example.util.DatabaseMigrationTool;


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println(" APPLICATION DE RÉSERVATION DE SALLES ");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestion-reservations");

        try {
            SalleRepository salleRepository = new SalleRepositoryImpl(emf);
            SalleService salleService = new SalleServiceImpl(salleRepository);

            ReservationRepository reservationRepository = new ReservationRepositoryImpl(emf);
            ReservationService reservationService = new ReservationServiceImpl(emf, reservationRepository);

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║   APPLICATION RÉSERVATION DE SALLES  ║");
                System.out.println("╠══════════════════════════════════════╣");
                System.out.println("║  1. 📦 Initialiser les données       ║");
                System.out.println("║  2. 🧪 Exécuter les scénarios        ║");
                System.out.println("║  3. 📊 Rapport de performance        ║");
                System.out.println("║  4. 🚪 Quitter                       ║");
                System.out.println("╚══════════════════════════════════════╝");
                System.out.print("  👉 Votre choix : ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> new DataInitializer(emf).initializeData();
                    case 2 -> new TestScenarios(emf, salleService, reservationService).runAllTests();
                    case 3 -> new PerformanceReport(emf).runPerformanceTests();
                    case 4 -> {
                        exit = true;
                        System.out.println("\n👋 Au revoir !");
                    }
                    default -> System.out.println("⚠️  Choix invalide. Entrez 1, 2, 3 ou 4.");
                }
            }
        } finally {
            emf.close();
        }
    }
}