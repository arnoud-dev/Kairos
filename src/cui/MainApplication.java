package cui;

import java.util.Scanner;

import domein.CategoryController;
import domein.HabitController;

public class MainApplication {
    private KairosApplication taskApp;
    private HabitTrackerApplication habitApp;

    public MainApplication() {
        taskApp = new KairosApplication(new CategoryController());
        habitApp = new HabitTrackerApplication(new HabitController());
    }

    public void start() {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Task Manager");
            System.out.println("2. Habit Tracker");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1 -> taskApp.start();
                case 2 -> habitApp.start();
                case 3 -> {
                    System.out.println("Exiting...");
                    running = false;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
        input.close();
    }
}
