package cui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import domein.Difficulty;
import domein.CategoryController;

public class KairosApplication {
    private final CategoryController dc;
    private final Scanner input;

    public KairosApplication(CategoryController dc) {
        this.dc = dc;
        this.input = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            int choice = mainMenu();

            switch (choice) {
                case 1 -> categoryMenu();
                case 2 -> subjectMenu();
                case 3 -> taskMenu();
                case 4 -> {
                    System.out.println("Exiting...");
                    running = false;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    // -------- MENUS --------
    private int mainMenu() {
        String[] options = {
            "Manage categories",
            "Manage subjects",
            "Manage tasks",
            "Back"
        };
        return makeChoice("Main Menu", options);
    }

    private void categoryMenu() {
        String[] options = {"Show categories", "Add category", "Edit category", "Remove category", "Back"};
        while (true) {
            int choice = makeChoice("Category Menu", options);
            switch (choice) {
                case 1 -> showCategories();
                case 2 -> makeNewCategory();
                case 3 -> editCategory();
                case 4 -> removeCategory();
                case 5 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void subjectMenu() {
        String[] options = {"Show subjects", "Add subject", "Edit subject", "Remove subject", "Back"};
        while (true) {
            int choice = makeChoice("Subject Menu", options);
            switch (choice) {
                case 1 -> showSubjects();
                case 2 -> makeNewSubject();
                case 3 -> editSubject();
                case 4 -> removeSubject();
                case 5 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void taskMenu() {
        String[] options = {"Show tasks", "Add task", "Edit task", "Remove task", "Manage task links", "Back"};
        while (true) {
            int choice = makeChoice("Task Menu", options);
            switch (choice) {
                case 1 -> showTasks();
                case 2 -> makeNewTask();
                case 3 -> editTask();
                case 4 -> removeTask();
                case 5 -> manageTaskLinks();
                case 6 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ------------- TASK LINK FUNCTIONS -------------
    private void manageTaskLinks() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        String subjectName = chooseSubject(categoryName);
        if (subjectName == null) return;
        String taskName = chooseTask(categoryName, subjectName);
        if (taskName == null) return;

        while (true) {
            String[] options = {"Show links", "Add link", "Remove link", "Back"};
            int choice = makeChoice("Manage Links for '" + taskName + "'", options);

            switch (choice) {
                case 1 -> showTaskLinks(categoryName, subjectName, taskName);
                case 2 -> addTaskLink(categoryName, subjectName, taskName);
                case 3 -> removeTaskLink(categoryName, subjectName, taskName);
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void showTaskLinks(String categoryName, String subjectName, String taskName) {
        try {
            String links = dc.getTaskLinks(categoryName, subjectName, taskName);
            System.out.println("\nLinks for task '" + taskName + "':");
            System.out.println(links);
        } catch (Exception e) {
            System.out.println("Error retrieving links: " + e.getMessage());
        }
    }

    private void addTaskLink(String categoryName, String subjectName, String taskName) {
        System.out.print("Enter link name: ");
        String linkName = input.nextLine().trim();
        System.out.print("Enter URL: ");
        String url = input.nextLine().trim();
        try {
            dc.addTaskLink(categoryName, subjectName, taskName, linkName, url);
            System.out.println("Link added successfully.");
            System.out.println("Saved links now:");
            System.out.println(dc.getTaskLinks(categoryName, subjectName, taskName));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while adding link: " + e.getMessage());
        }
    }

    private void removeTaskLink(String categoryName, String subjectName, String taskName) {
        System.out.print("Enter link name to remove: ");
        String linkName = input.nextLine().trim();
        try {
            dc.removeTaskLink(categoryName, subjectName, taskName, linkName);
            System.out.println("Link removed successfully.");
            System.out.println("Saved links now:");
            System.out.println(dc.getTaskLinks(categoryName, subjectName, taskName));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while removing link: " + e.getMessage());
        }
    }

    // -------- CHOICE HELPER --------
    private int makeChoice(String title, String[] options) {
        System.out.println("\n=== " + title + " ===");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", i + 1, options[i]);
        }
        System.out.print("Choose an option: ");

        while (!input.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            input.next();
        }
        int choice = input.nextInt();
        input.nextLine();
        return choice;
    }

    private String[] toLines(String display) {
        if (display == null || display.isBlank()) return new String[0];
        String[] raw = display.split("\\R");
        List<String> lines = new ArrayList<>();
        for (String s : raw) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) lines.add(trimmed);
        }
        return lines.toArray(new String[0]);
    }

    private String chooseDisplayLine(String title, String displayLines) {
        String[] items = toLines(displayLines);
        if (items.length == 0) {
            System.out.println("No " + title.toLowerCase() + " available.");
            return null;
        }

        System.out.println("\n=== " + title + " ===");
        for (int i = 0; i < items.length; i++) {
            System.out.printf("%d. %s%n", i + 1, items[i]);
        }
        System.out.print("Choose a number: ");

        while (!input.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            input.next();
        }
        int choice = input.nextInt();
        input.nextLine();

        if (choice < 1 || choice > items.length) {
            System.out.println("Invalid choice.");
            return null;
        }
        return items[choice - 1];
    }

    private String extractNameFromDisplay(String displayLine) {
        if (displayLine == null) return null;
        String line = displayLine.trim();
        int idx = indexOfFirst(line, " (", " [", " | ");
        return idx == -1 ? line : line.substring(0, idx);
    }

    private String extractTaskNameFromDisplay(String displayLine) {
        if (displayLine == null) return null;

        int nameIndex = displayLine.indexOf("name='");
        if (nameIndex != -1) {
            int start = nameIndex + "name='".length();
            int end = displayLine.indexOf("'", start);
            if (end > start) return displayLine.substring(start, end);
        }
        String trimmed = displayLine.trim();
        if (trimmed.matches("^\\d+\\.\\s+.*$")) {
            int dot = trimmed.indexOf('.');
            String remainder = trimmed.substring(dot + 1).trim();

            int idx2 = remainder.indexOf("name='");
            if (idx2 != -1) {
                int start = idx2 + "name='".length();
                int end = remainder.indexOf("'", start);
                if (end > start) return remainder.substring(start, end);
            }

            int idx = indexOfFirst(remainder, " (", " [", " | ");
            return idx == -1 ? remainder : remainder.substring(0, idx).trim();
        }

        int idx = indexOfFirst(trimmed, " (", " [", " | ");
        return idx == -1 ? trimmed : trimmed.substring(0, idx).trim();
    }

    private int indexOfFirst(String s, String... patterns) {
        int min = -1;
        for (String p : patterns) {
            int i = s.indexOf(p);
            if (i != -1) {
                if (min == -1 || i < min) min = i;
            }
        }
        return min;
    }

    private String chooseCategory() {
        String selectedLine = chooseDisplayLine("Categories", dc.categoriesToString());
        return selectedLine == null ? null : extractNameFromDisplay(selectedLine);
    }

    private String chooseSubject(String categoryName) {
        String selectedLine = chooseDisplayLine("Subjects in " + categoryName, dc.subjectsToString(categoryName));
        return selectedLine == null ? null : extractNameFromDisplay(selectedLine);
    }

    private String chooseTask(String categoryName, String subjectName) {
        String selectedLine = chooseDisplayLine("Tasks in " + subjectName, dc.tasksToString(categoryName, subjectName));
        return selectedLine == null ? null : extractTaskNameFromDisplay(selectedLine);
    }

    // -------- CATEGORY FUNCTIONS --------
    private void showCategories() {
        System.out.println(dc.categoriesToString());
    }

    private void makeNewCategory() {
        System.out.print("Enter new category name: ");
        String name = input.nextLine();
        try {
            dc.addCategory(name);
            System.out.println("Category '" + name + "' added.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editCategory() {
        String oldName = chooseCategory();
        if (oldName == null) return;
        System.out.print("Enter new category name: ");
        String newName = input.nextLine();
        try {
            dc.editCategory(oldName, newName);
            System.out.println("Category renamed to '" + newName + "'.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeCategory() {
        String name = chooseCategory();
        if (name == null) return;
        try {
            dc.removeCategory(name);
            System.out.println("Category '" + name + "' removed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // -------- SUBJECT FUNCTIONS --------
    private void showSubjects() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;

        String subjectsDisplay = dc.subjectsToString(categoryName);
        String[] lines = toLines(subjectsDisplay);

        System.out.println("\n=== Subjects in '" + categoryName + "' ===");
        if (lines.length == 0) {
            System.out.println("No subjects in '" + categoryName + "'.");
            return;
        }
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private void makeNewSubject() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        System.out.print("Enter new subject name: ");
        String subjectName = input.nextLine();
        try {
            dc.addSubject(categoryName, subjectName);
            System.out.println("Subject '" + subjectName + "' added to '" + categoryName + "'.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editSubject() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        String oldName = chooseSubject(categoryName);
        if (oldName == null) return;
        System.out.print("Enter new subject name: ");
        String newName = input.nextLine();
        try {
            dc.editSubject(categoryName, oldName, newName);
            System.out.println("Subject renamed to '" + newName + "'.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeSubject() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        String subjectName = chooseSubject(categoryName);
        if (subjectName == null) return;
        try {
            dc.removeSubject(categoryName, subjectName);
            System.out.println("Subject '" + subjectName + "' removed from '" + categoryName + "'.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // -------- TASK FUNCTIONS --------
    private void showTasks() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        String subjectName = chooseSubject(categoryName);
        if (subjectName == null) return;

        String tasksDisplay = dc.tasksToString(categoryName, subjectName);
        String[] lines = toLines(tasksDisplay);

        System.out.println("\n=== Tasks in '" + subjectName + "' (Category: '" + categoryName + "') ===");
        if (lines.length == 0) {
            System.out.println("No tasks in '" + subjectName + "'.");
            return;
        }
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private void makeNewTask() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        String subjectName = chooseSubject(categoryName);
        if (subjectName == null) return;

        System.out.print("Enter task name: ");
        String taskName = input.nextLine();

        System.out.print("Enter description: ");
        String description = input.nextLine();

        System.out.print("Enter difficulty (EASY, MEDIUM, HARD): ");
        Difficulty difficulty;
        try {
            difficulty = Difficulty.valueOf(input.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid difficulty. Use EASY, MEDIUM or HARD.");
            return;
        }

        System.out.print("Enter due date (dd-MM-yy) or (dd-MM-yy HH:mm) — leave blank for no due date: ");
        String dueDateInput = input.nextLine().trim();

        LocalDateTime dueDate = null;
        if (!dueDateInput.isEmpty()) {
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");

                if (dueDateInput.contains(" ")) {
                    dueDate = LocalDateTime.parse(dueDateInput, dateTimeFormatter);
                } else {
                    dueDate = LocalDate.parse(dueDateInput, dateFormatter).atTime(23, 59);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-yy or dd-MM-yy HH:mm.");
                return;
            }
        }

        try {
            dc.addTask(categoryName, subjectName, taskName, description, difficulty, dueDate);
            String dueString = (dueDate != null)
                    ? dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"))
                    : "none";
            System.out.println("Task '" + taskName + "' added. Due: " + dueString);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editTask() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        String subjectName = chooseSubject(categoryName);
        if (subjectName == null) return;
        String oldName = chooseTask(categoryName, subjectName);
        if (oldName == null) return;

        System.out.print("Enter new task name: ");
        String newName = input.nextLine();

        System.out.print("Enter new description: ");
        String newDescription = input.nextLine();

        System.out.print("Enter new difficulty (EASY, MEDIUM, HARD): ");
        Difficulty newDifficulty;
        try {
            newDifficulty = Difficulty.valueOf(input.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid difficulty.");
            return;
        }

        System.out.print("Enter new due date (dd-MM-yy) or (dd-MM-yy HH:mm) — leave blank to keep current: ");
        String dueDateInput = input.nextLine().trim();

        LocalDateTime newDueDate = null;
        if (!dueDateInput.isEmpty()) {
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
                if (dueDateInput.contains(" ")) {
                    newDueDate = LocalDateTime.parse(dueDateInput, dateTimeFormatter);
                } else {
                    newDueDate = LocalDate.parse(dueDateInput, dateFormatter).atTime(23, 59);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
                return;
            }
        } else {
            newDueDate = LocalDate.now().atTime(23, 59);
        }

        try {
            dc.editTask(categoryName, subjectName, oldName, newName, newDescription, newDifficulty, newDueDate);
            System.out.println("Task updated.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeTask() {
        String categoryName = chooseCategory();
        if (categoryName == null) return;
        String subjectName = chooseSubject(categoryName);
        if (subjectName == null) return;
        String taskName = chooseTask(categoryName, subjectName);
        if (taskName == null) return;
        try {
            dc.removeTask(categoryName, subjectName, taskName);
            System.out.println("Task '" + taskName + "' removed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
