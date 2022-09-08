package model;

import java.util.Map;
import java.util.TreeMap;

public class CourseList {
    private Map<Integer, String> courses;

    public void add(Integer courseId, String courseName) {
        if(courses == null) {
            courses = new TreeMap<>();
        }
        courses.put(courseId, courseName);
    }

    public boolean contains(Integer courseId) {
        return courses != null && courses.containsKey(courseId);
    }

    @Override
    public String toString() {
        return String.join("\n", courses.entrySet().stream().map(e -> e.getKey() + " - " + e.getValue()).toList());
    }
}
