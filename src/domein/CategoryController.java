package domein;

import java.time.LocalDateTime;

public class CategoryController {
    private CategoryManager catMan;

    public CategoryController() {
        catMan = new CategoryManager();
    }

    // --- Category operations ---
    public String categoriesToString() {
        return catMan.categoriesToString();
    }

    public void addCategory(String categoryName) {
        catMan.addCategory(new Category(categoryName));
    }

    public void editCategory(String categoryOldName, String categoryNewName) {
        catMan.editCategory(categoryOldName, categoryNewName);
    }

    public void removeCategory(String categoryName) {
        catMan.removeCategory(categoryName);
    }

    // --- Subject operations ---
    public String subjectsToString(String categoryName) {
        return catMan.subjectsToString(categoryName);
    }

    public void addSubject(String categoryName, String subjectName) {
        catMan.addSubject(categoryName, new Subject(subjectName));
    }

    public void editSubject(String categoryName, String oldName, String newName) {
        catMan.editSubject(categoryName, oldName, newName);
    }

    public void removeSubject(String categoryName, String subjectName) {
        catMan.removeSubject(categoryName, subjectName);
    }

    // --- Task operations ---
    public String tasksToString(String categoryName, String subjectName) {
        return catMan.tasksToString(categoryName, subjectName);
    }

    public void addTask(String categoryName, String subjectName, String taskName, String description, Difficulty difficulty, LocalDateTime dueDate) {
        catMan.addTask(categoryName, subjectName, new Task(taskName, description, difficulty, dueDate));
    }

    public void editTask(String categoryName, String subjectName, String oldName, String newName, String newDescription,
                         Difficulty newDifficulty, LocalDateTime newDueDate) {
        catMan.editTask(categoryName, subjectName, oldName, new Task(newName, newDescription, newDifficulty, newDueDate));
    }

    public void removeTask(String categoryName, String subjectName, String taskName) {
        catMan.removeTask(categoryName, subjectName, taskName);
    }
    
    public Task getRandomTask() {
		return catMan.getRandomTask();
	}

    // --- Link operations ---
    public void addTaskLink(String categoryName, String subjectName, String taskName, String linkName, String url) {
        if (linkName == null || linkName.isBlank() || url == null || url.isBlank()) {
            throw new IllegalArgumentException("Link name and URL cannot be blank.");
        }
        Task task = catMan.getTaskByName(categoryName, subjectName, taskName);
        if (task != null) {
            task.addLink(linkName, url);
            catMan.save();
        }
    }

    public void removeTaskLink(String categoryName, String subjectName, String taskName, String linkName) {
        Task task = catMan.getTaskByName(categoryName, subjectName, taskName);
        if (task != null) {
            task.removeLink(linkName);
            catMan.save();
        }
    }

    public String getTaskLinks(String categoryName, String subjectName, String taskName) {
        Task task = catMan.getTaskByName(categoryName, subjectName, taskName);
        if (task == null || task.getLinks().isEmpty()) return "No links for this task.";

        StringBuilder sb = new StringBuilder();
        task.getLinks().forEach((name, url) -> sb.append(name).append(" -> ").append(url).append(System.lineSeparator()));
        return sb.toString();
    }
}
