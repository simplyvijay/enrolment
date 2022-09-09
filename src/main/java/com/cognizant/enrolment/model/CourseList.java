package com.cognizant.enrolment.model;

import java.util.Map;
import java.util.TreeMap;

public class CourseList {
    private final Map<Integer, String> courses;

    public CourseList() {
        courses = new TreeMap<>();
    }

    public void add(Integer courseId, String courseName) {
        courses.put(courseId, courseName);
    }

    public int size() {
        return courses.size();
    }

    public boolean contains(Integer courseId) {
        return courses.containsKey(courseId);
    }

    @Override
    public String toString() {
        return String.join("\n", courses.entrySet().stream().map(e -> e.getKey() + " - " + e.getValue()).toList());
    }
}
