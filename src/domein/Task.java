package domein;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Difficulty difficulty;
    private boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime lastAccessed;
    private Map<String, String> links = new HashMap<>();

    private String originalCategoryName;
    private String originalSubjectName;

    public Task(String name, String description, Difficulty difficulty, LocalDateTime dueDate) {
        setName(name);
        setDescription(description);
        setDifficulty(difficulty);
        setDueDate(dueDate);
        this.completed = false;
        this.lastAccessed = LocalDateTime.now();
    }

    public void addLink(String linkName, String url) {
        if (linkName == null || linkName.isBlank()) {
            throw new IllegalArgumentException("Link name cannot be null or blank.");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank.");
        }
        links.put(linkName, url);
    }

    public Map<String, String> getLinks() { return links; }
    public String getLink(String linkName) { return links.get(linkName); }
    public void removeLink(String linkName) { links.remove(linkName); }

    public void open() { lastAccessed = LocalDateTime.now(); }
    public void markCompleted() { completed = true; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Task name cannot be null or blank.");
        this.name = name;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description == null) throw new IllegalArgumentException("Description cannot be null.");
        this.description = description;
    }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) throw new IllegalArgumentException("Difficulty cannot be null.");
        this.difficulty = difficulty;
    }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Due date cannot be in the past.");
        this.dueDate = dueDate;
    }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) {
        if (lastAccessed == null) throw new IllegalArgumentException("Last accessed cannot be null.");
        this.lastAccessed = lastAccessed;
    }

    public String getOriginalCategoryName() { return originalCategoryName; }
    public void setOriginalCategoryName(String originalCategoryName) { this.originalCategoryName = originalCategoryName; }

    public String getOriginalSubjectName() { return originalSubjectName; }
    public void setOriginalSubjectName(String originalSubjectName) { this.originalSubjectName = originalSubjectName; }

    @Override
    public String toString() {
        String due = (dueDate == null) ? "none" : dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"));

        StringBuilder linksStr = new StringBuilder();
        if (!links.isEmpty()) {
            linksStr.append("Links: ");
            links.forEach((k, v) -> linksStr.append(k).append(" -> ").append(v).append("; "));
        }

        return String.format(
            "Task[name='%s', description='%s', difficulty=%s, due=%s, completed=%b%s, category='%s', subject='%s']",
            name,
            description,
            difficulty,
            due,
            completed,
            linksStr.length() > 0 ? " | " + linksStr.toString() : "",
            originalCategoryName,
            originalSubjectName
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(dueDate, task.dueDate);
    }

    @Override
    public int hashCode() { return Objects.hash(name, dueDate); }
}
