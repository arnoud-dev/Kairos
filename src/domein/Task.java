package domein;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Difficulty difficulty;
    private boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime lastAccessed;

    public Task(String name, String description, Difficulty difficulty, LocalDateTime dueDate) {
        setName(name);
        setDescription(description);
        setDifficulty(difficulty);
        setDueDate(dueDate);
        this.completed = false;
        this.lastAccessed = LocalDateTime.now();
    }

    public void open() {
        lastAccessed = LocalDateTime.now();
    }

    public void markCompleted() {
        if (!completed) {
            completed = true;
        }
    }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be null or blank.");
        }
        this.name = name;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null.");
        }
        this.description = description;
    }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null.");
        }
        this.difficulty = difficulty;
    }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past.");
        }
        this.dueDate = dueDate;
    }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) {
        if (lastAccessed == null) {
            throw new IllegalArgumentException("Last accessed cannot be null.");
        }
        this.lastAccessed = lastAccessed;
    }

    @Override
    public String toString() {
        String due = (dueDate == null) ? "none" : dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"));
        return String.format("Task[name='%s', difficulty=%s, due=%s, completed=%b]",
                name, difficulty, due, completed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
               Objects.equals(dueDate, task.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dueDate);
    }
}
