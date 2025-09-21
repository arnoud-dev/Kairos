package domein;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import repo.CategoryRepository;

public class CategoryManager {

    private final CategoryRepository repo;
    private final List<Category> categories;

    public CategoryManager() {
        repo = new CategoryRepository();
        categories = repo.loadCategories();
    }

    private void validateName(String name, String type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(type + " name cannot be empty");
        }
    }

    // --- Category operations ---
    public void addCategory(Category category) {
        validateName(category.getName(), "Category");
        if (getCategoryByName(category.getName()) != null)
            throw new IllegalArgumentException("Category already exists: " + category.getName());
        categories.add(category);
        save();
    }

    public void removeCategory(String name) {
        validateName(name, "Category");
        categories.removeIf(c -> c.getName().equals(name));
        save();
    }

    public void editCategory(String oldName, String newName) {
        validateName(oldName, "Category");
        validateName(newName, "Category");
        Category cat = getCategoryByName(oldName);
        if (cat != null) {
            if (getCategoryByName(newName) != null)
                throw new IllegalArgumentException("Category already exists: " + newName);
            cat.setName(newName);
            save();
        }
    }

    public Category getCategoryByName(String name) {
        for (Category c : categories) {
            if (c.getName().equals(name)) return c;
        }
        return null;
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    // --- Subject operations ---
    public void addSubject(String categoryName, Subject subject) {
        validateName(categoryName, "Category");
        validateName(subject.getName(), "Subject");
        Category cat = getCategoryByName(categoryName);
        if (cat == null) throw new IllegalArgumentException("Category not found: " + categoryName);
        for (Subject s : cat.getSubjects()) {
            if (s.getName().equals(subject.getName()))
                throw new IllegalArgumentException("Subject already exists: " + subject.getName());
        }
        cat.addSubject(subject);
        save();
    }

    public void removeSubject(String categoryName, String subjectName) {
        validateName(categoryName, "Category");
        validateName(subjectName, "Subject");
        Category cat = getCategoryByName(categoryName);
        if (cat == null) return;

        try {
            boolean removed = cat.getSubjects().removeIf(s -> s.getName().equals(subjectName));
            if (removed) {
                save();
                return;
            }
        } catch (UnsupportedOperationException ignored) {
    }

        int idx = categories.indexOf(cat);
        if (idx >= 0) {
            Category rebuilt = new Category(cat.getName());
            for (Subject s : cat.getSubjects()) {
                if (!s.getName().equals(subjectName)) {
                    rebuilt.addSubject(s);
                }
            }
            categories.set(idx, rebuilt);
            save();
        }
    }

    public void editSubject(String categoryName, String oldName, String newName) {
        validateName(categoryName, "Category");
        validateName(oldName, "Subject");
        validateName(newName, "Subject");
        Category cat = getCategoryByName(categoryName);
        if (cat != null) {
            Subject target = null;
            for (Subject s : cat.getSubjects()) {
                if (s.getName().equals(oldName)) target = s;
                if (s.getName().equals(newName))
                    throw new IllegalArgumentException("Subject already exists: " + newName);
            }
            if (target != null) {
                target.setName(newName);
                save();
            }
        }
    }

    public Subject getSubjectByName(String categoryName, String subjectName) {
        Category cat = getCategoryByName(categoryName);
        if (cat != null) {
            for (Subject s : cat.getSubjects()) {
                if (s.getName().equals(subjectName)) return s;
            }
        }
        return null;
    }

    // --- Task operations ---

    public void addTask(String categoryName, String subjectName, Task task) {
        validateName(categoryName, "Category");
        validateName(subjectName, "Subject");
        validateName(task.getName(), "Task");
        Subject subj = getSubjectByName(categoryName, subjectName);
        if (subj == null) throw new IllegalArgumentException("Subject not found: " + subjectName);
        for (Task t : subj.getTasks()) {
            if (t.getName().equals(task.getName()))
                throw new IllegalArgumentException("Task already exists: " + task.getName());
        }
        subj.addTask(task);
        save();
    }

    public void removeTask(String categoryName, String subjectName, String taskName) {
        validateName(categoryName, "Category");
        validateName(subjectName, "Subject");
        validateName(taskName, "Task");
        Subject subj = getSubjectByName(categoryName, subjectName);
        if (subj != null) {
            Task toRemove = null;
            for (Task t : subj.getTasks()) {
                if (t.getName().equals(taskName)) {
                    toRemove = t;
                    break;
                }
            }
            if (toRemove != null) {
                subj.removeTask(toRemove);
                save();
            }
        }
    }

    public void editTask(String categoryName, String subjectName, String oldName, Task newTask) {
        validateName(categoryName, "Category");
        validateName(subjectName, "Subject");
        validateName(oldName, "Task");
        validateName(newTask.getName(), "Task");

        Subject subj = getSubjectByName(categoryName, subjectName);
        if (subj != null) {
            Task target = null;
            for (Task t : subj.getTasks()) {
                if (t.getName().equals(oldName)) {
                    target = t;
                    break;
                }
            }
            if (target == null) return;

            if (!oldName.equals(newTask.getName())) {
                for (Task x : subj.getTasks()) {
                    if (x.getName().equals(newTask.getName()))
                        throw new IllegalArgumentException("Task already exists: " + newTask.getName());
                }
            }
            target.setName(newTask.getName());
            target.setDescription(newTask.getDescription());
            target.setDifficulty(newTask.getDifficulty());
            target.setDueDate(newTask.getDueDate());
            target.setCompleted(newTask.isCompleted());
            target.setLastAccessed(newTask.getLastAccessed());

            save();
        }
    }

    public Task getTaskByName(String categoryName, String subjectName, String taskName) {
        Subject subj = getSubjectByName(categoryName, subjectName);
        if (subj != null) {
            for (Task t : subj.getTasks()) {
                if (t.getName().equals(taskName)) return t;
            }
        }
        return null;
    }

    // --- Search ---
    public List<Task> getTasksByDifficulty(Difficulty difficulty) {
        List<Task> result = new ArrayList<>();
        for (Category c : categories) {
            for (Subject s : c.getSubjects()) {
                for (Task t : s.getTasks()) {
                    if (t.getDifficulty() == difficulty) result.add(t);
                }
            }
        }
        return result;
    }

    public List<Task> getOverdueTasks() {
        List<Task> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Category c : categories) {
            for (Subject s : c.getSubjects()) {
                for (Task t : s.getTasks()) {
                    if (!t.isCompleted() && t.getDueDate() != null && t.getDueDate().isBefore(now)) result.add(t);
                }
            }
        }
        return result;
    }

    public List<Task> getTasksForCategory(String categoryName) {
        List<Task> result = new ArrayList<>();
        Category c = getCategoryByName(categoryName);
        if (c != null) {
            for (Subject s : c.getSubjects()) {
                result.addAll(s.getTasks());
            }
        }
        return result;
    }

    public List<Task> getTasksForSubject(String categoryName, String subjectName) {
        Subject s = getSubjectByName(categoryName, subjectName);
        return s != null ? new ArrayList<>(s.getTasks()) : new ArrayList<>();
    }

    public String categoriesToString() {
        StringBuilder sb = new StringBuilder();
        for (Category c : categories) {
            sb.append(c.getName()).append(" (Subjects: ").append(c.getSubjects().size()).append(")").append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String subjectsToString(String categoryName) {
        Category c = getCategoryByName(categoryName);
        if (c == null) return "Category not found: " + categoryName;
        StringBuilder sb = new StringBuilder();
        for (Subject s : c.getSubjects()) {
            sb.append(s.getName()).append(" (Tasks: ").append(s.getTasks().size()).append(")").append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String tasksToString(String categoryName, String subjectName) {
        Subject s = getSubjectByName(categoryName, subjectName);
        if (s == null) return "Subject not found: " + subjectName;

        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Task t : s.getTasks()) {
            sb.append(index++)
              .append(". ")
              .append(t.toString())
              .append(System.lineSeparator());
        }
        return sb.toString();
    }

    // --- Save all ---
    public void save() {
        repo.saveCategories(categories);
    }
}
