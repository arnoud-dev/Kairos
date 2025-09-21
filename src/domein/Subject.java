package domein;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Subject {
    private String name;
    private final List<Task> tasks;

    public Subject(String name) {
        this.tasks = new ArrayList<>();
        setName(name);
    }

    public void addTask(Task task) {
        if (task == null) throw new IllegalArgumentException("Task cannot be null.");
        try {
            task.setOriginalSubjectName(this.name);
        } catch (Exception ignored) {}
        tasks.add(task);
    }

    public boolean removeTask(Task task) {
        if (task == null) return false;
        return tasks.remove(task);
    }

    public boolean removeTaskByName(String taskName) {
        if (taskName == null || taskName.isBlank()) return false;
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (taskName.equals(t.getName())) {
                tasks.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void showTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available for subject: " + name);
            return;
        }
        System.out.println("Tasks for subject: " + name);
        for (Task task : tasks) {
            System.out.printf("- %s (Due: %s, Completed: %b)%n",
                    task.getName(),
                    task.getDueDate() != null ? task.getDueDate() : "none",
                    task.isCompleted());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Subject name cannot be null or blank.");
        }
        this.name = name;
        for (Task t : tasks) {
            try {
                t.setOriginalSubjectName(name);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        Subject subject = (Subject) o;
        return Objects.equals(name, subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("Subject[name='%s', tasks=%d]", name, tasks.size());
    }
}
