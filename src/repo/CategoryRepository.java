package repo;

import domein.Category;
import domein.Subject;
import domein.Task;
import domein.Difficulty;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static final String FILE_PATH = "Category.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void saveCategories(List<Category> categories) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Category category : categories) {
                writer.write("#Category:" + escapeSimple(category.getName()));
                writer.newLine();

                for (Subject subject : category.getSubjects()) {
                    writer.write("##Subject:" + escapeSimple(subject.getName()));
                    writer.newLine();

                    for (Task task : subject.getTasks()) {
                        String dueDateString = (task.getDueDate() != null)
                                ? task.getDueDate().format(formatter)
                                : "";

                        String descEncoded = URLEncoder.encode(task.getDescription(), StandardCharsets.UTF_8);

                        StringBuilder linksString = new StringBuilder();
                        task.getLinks().forEach((name, url) -> {
                            String k = URLEncoder.encode(name, StandardCharsets.UTF_8);
                            String v = URLEncoder.encode(url, StandardCharsets.UTF_8);
                            linksString.append(k).append("->").append(v).append(",");
                        });
                        if (linksString.length() > 0) linksString.setLength(linksString.length() - 1);

                        writer.write(String.format("###Task:%s|%s|%s|%s|%b|%s|%s|%s|%s",
                                escapeSimple(task.getName()),
                                descEncoded,
                                task.getDifficulty(),
                                dueDateString,
                                task.isCompleted(),
                                task.getLastAccessed().format(formatter),
                                linksString.toString(),
                                escapeSimple(task.getOriginalCategoryName()),
                                escapeSimple(task.getOriginalSubjectName())
                        ));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                    String name = unescapeSimple(line.substring(10));
                    currentCategory = new Category(name);
                    loaded.add(currentCategory);
                } else if (line.startsWith("##Subject:")) {
                    String name = unescapeSimple(line.substring(10));
                    if (currentCategory != null) {
                        currentSubject = new Subject(name);
                        currentCategory.addSubject(currentSubject);
                    }
                } else if (line.startsWith("###Task:")) {
                    if (currentSubject != null) {
                        String data = line.substring(8);
                        String[] parts = data.split("\\|", -1);
                        if (parts.length >= 6) {
                            String taskName = unescapeSimple(parts[0]);

                            String description;
                            try {
                                description = parts[1].isEmpty() ? "" :
                                        URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                            } catch (IllegalArgumentException ex) {
                                description = parts[1].replace("/", "|");
                            }

                            Difficulty difficulty;
                            try {
                                difficulty = Difficulty.valueOf(parts[2]);
                            } catch (Exception ex) {
                                difficulty = Difficulty.EASY;
                            }

                            LocalDateTime dueDate = parts[3].isBlank() ? null : LocalDateTime.parse(parts[3], formatter);
                            boolean completed = Boolean.parseBoolean(parts[4]);
                            LocalDateTime lastAccessed = LocalDateTime.parse(parts[5], formatter);

                            Task task = new Task(taskName, description, difficulty, dueDate);
                            task.setCompleted(completed);
                            task.setLastAccessed(lastAccessed);

                            if (parts.length >= 7 && !parts[6].isBlank()) {
                                String linksPart = parts[6];
                                String[] linkPairs = linksPart.split(",", -1);
                                for (String pair : linkPairs) {
                                    if (pair.isBlank()) continue;
                                    String[] kv = pair.split("->", 2);
                                    if (kv.length == 2) {
                                        try {
                                            task.addLink(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                                                    URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                                        } catch (IllegalArgumentException ignored) {}
                                    }
                                }
                            }
                            task.setOriginalCategoryName(currentCategory != null ? currentCategory.getName() : null);
                            task.setOriginalSubjectName(currentSubject != null ? currentSubject.getName() : null);
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

    private String escapeSimple(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").trim();
    }

    private String unescapeSimple(String s) {
        if (s == null) return "";
        return s;
    }
}
