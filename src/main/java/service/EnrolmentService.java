package service;

import model.CourseList;
import model.EnrolmentException;
import model.Student;

public interface EnrolmentService {
    CourseList getCourseList() throws EnrolmentException;
    void add(Student student) throws EnrolmentException;
    Student view(String email) throws EnrolmentException;
    void update(Student student) throws EnrolmentException;
    void delete(String email) throws EnrolmentException;
}
