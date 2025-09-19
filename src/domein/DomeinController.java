package domein;

import java.time.LocalDateTime;

public class DomeinController {
	private CategoryManager catMan;
	
	public DomeinController() {
		catMan = new CategoryManager();
	}
	
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
	
	public String tasksToString(String categoryName, String subjectName) {
		return catMan.tasksToString(categoryName, subjectName);
	}
	
	public void addTask(String categoryName, String subjectName, String taskName,  String description, Difficulty difficulty, LocalDateTime dueDate) {
		catMan.addTask(categoryName, subjectName, new Task(taskName, description, difficulty, dueDate));
	}

	public void editTask(String categoryName, String subjectName, String oldName, String newName, String newDescription,
			Difficulty newDifficulty, LocalDateTime newDueDate) {
		catMan.editTask(categoryName, subjectName, oldName, new Task(newName, newDescription, newDifficulty, newDueDate));
	}

	public void removeTask(String categoryName, String subjectName, String taskName) {
		catMan.removeTask(categoryName, subjectName, taskName);
	}
}
