package com.cognizant.enrolment.service;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.Student;

import java.util.Optional;

public interface EnrolmentService {
    CourseList getCourseList() throws EnrolmentException;
    EnrolmentStatus add(Student student) throws EnrolmentException;
    Optional<Student> fetch(String email) throws EnrolmentException;
    EnrolmentStatus update(Student student) throws EnrolmentException;
    EnrolmentStatus delete(String email) throws EnrolmentException;
}
