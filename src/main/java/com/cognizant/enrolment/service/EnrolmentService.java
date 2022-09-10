package com.cognizant.enrolment.service;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.Student;

import java.util.Optional;

public interface EnrolmentService {
    CourseList getCourseList() throws Exception;
    EnrolmentStatus add(Student student) throws Exception;
    Optional<Student> fetch(String email) throws Exception;
    EnrolmentStatus update(Student student) throws Exception;
    EnrolmentStatus delete(String email) throws Exception;
}
