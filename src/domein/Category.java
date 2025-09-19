package domein;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Category {
    private String name;
    private final List<Subject> subjects;

    public Category(String name) {
        setName(name);
        this.subjects = new ArrayList<>();
    }

    public void addSubject(Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null.");
        }
        subjects.add(subject);
    }

    public boolean removeSubject(Subject subject) {
        if (subject == null) return false;
        return subjects.remove(subject);
    }

    public List<Subject> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    public void showSubjects() {
        if (subjects.isEmpty()) {
            System.out.println("No subjects in category: " + name);
            return;
        }
        System.out.println("Subjects in category: " + name);
        for (Subject subject : subjects) {
            System.out.println("- " + subject.getName());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be null or blank.");
        }
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category[name='" + name + "', subjects=" + subjects.size() + "]";
    }
}
