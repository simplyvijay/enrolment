package com.cognizant.enrolment.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Student {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String dob;
    private final String location;
    private Integer courseId;
    private final String courseName;

    public Student(String email, String firstName, String lastName, String dob, String location, Integer courseId, String courseName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.location = location;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public Student(String email, String firstName, String lastName, String dob, String location, Integer courseId) {
        this(email, firstName, lastName, dob, location, courseId, null);
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public String getLocation() {
        return location;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s, %s", courseName, courseId, firstName, lastName, dob, location);
    }

    public static boolean isValidDob(String dob) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            dateTimeFormatter.parse(dob, LocalDate::from);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
