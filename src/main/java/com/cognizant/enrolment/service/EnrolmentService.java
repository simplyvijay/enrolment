package com.cognizant.enrolment.service;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.EnrolmentException;
import com.cognizant.enrolment.model.Student;

public interface EnrolmentService {
    CourseList getCourseList() throws EnrolmentException;
    void add(Student student) throws EnrolmentException;
    Student view(String email) throws EnrolmentException;
    void update(Student student) throws EnrolmentException;
    void delete(String email) throws EnrolmentException;
}
