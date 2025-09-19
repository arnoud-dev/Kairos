package domein;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Subject {
    private String name;
    private final List<Task> tasks;

    public Subject(String name) {
        setName(name);
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(task);
    }

    public boolean removeTask(Task task) {
        if (task == null) return false;
        return tasks.remove(task);
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
                    task.getDueDate(),
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
    }
}
