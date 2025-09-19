package repo;

import domein.Category;
import domein.Subject;
import domein.Task;
import domein.Difficulty;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static final String FILE_PATH = "Category.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // --- Save categories ---
    public void saveCategories(List<Category> categories) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Category category : categories) {
                writer.write("#Category:" + category.getName());
                writer.newLine();
                for (Subject subject : category.getSubjects()) {
                    writer.write("##Subject:" + subject.getName());
                    writer.newLine();
                    for (Task task : subject.getTasks()) {
                        String dueDateString = (task.getDueDate() != null)
                                ? task.getDueDate().format(formatter)
                                : ""; // store empty if no due date

                        writer.write(String.format("###Task:%s|%s|%s|%s|%b|%s",
                                task.getName(),
                                task.getDescription().replace("|", "/"),
                                task.getDifficulty(),
                                dueDateString,
                                task.isCompleted(),
                                task.getLastAccessed().format(formatter)
                        ));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Load categories ---
    public List<Category> loadCategories() {
        List<Category> loaded = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return loaded;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Category currentCategory = null;
            Subject currentSubject = null;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#Category:")) {
                    String name = line.substring(10);
                    currentCategory = new Category(name);
                    loaded.add(currentCategory);
                } else if (line.startsWith("##Subject:")) {
                    String name = line.substring(10);
                    if (currentCategory != null) {
                        currentSubject = new Subject(name);
                        currentCategory.addSubject(currentSubject);
                    }
                } else if (line.startsWith("###Task:")) {
                    if (currentSubject != null) {
                        String data = line.substring(8);
                        String[] parts = data.split("\\|");
                        if (parts.length == 6) {
                            String taskName = parts[0];
                            String description = parts[1].replace("/", "|");
                            Difficulty difficulty = Difficulty.valueOf(parts[2]);

                            LocalDateTime dueDate = parts[3].isBlank()
                                    ? null
                                    : LocalDateTime.parse(parts[3], formatter);

                            boolean completed = Boolean.parseBoolean(parts[4]);
                            LocalDateTime lastAccessed = LocalDateTime.parse(parts[5], formatter);

                            Task task = new Task(taskName, description, difficulty, dueDate);
                            task.setCompleted(completed);
                            task.setLastAccessed(lastAccessed);
                            currentSubject.addTask(task);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loaded;
    }
}
