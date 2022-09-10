package com.cognizant.enrolment.service.impl;

import com.cognizant.enrolment.model.Student;
import com.cognizant.enrolment.service.EnrolmentService;
import com.cognizant.enrolment.service.EnrolmentStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static com.cognizant.enrolment.Constants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MySqlEnrolmentServiceTest {
    EnrolmentService service;

    @BeforeAll
    void init() {
        service = assertDoesNotThrow(MySqlEnrolmentService::create);
    }

    @Test
    @Order(1)
    void getCourseList() {
        var courseList = assertDoesNotThrow(() -> service.getCourseList());
        assertEquals(5, courseList.size());
    }

    @Test
    @Order(2)
    void addAndView() {
        assertEquals(EnrolmentStatus.SUCCESS, assertDoesNotThrow(() -> service.add(SAMPLE_STUDENT)));
        var optStudent = assertDoesNotThrow(() -> service.fetch(SAMPLE_STUDENT.getEmail()));
        assertTrue(optStudent.isPresent());
        var student2 = optStudent.get();
        assertEquals(SAMPLE_STUDENT.getEmail(), student2.getEmail());
        assertEquals(SAMPLE_STUDENT.getFirstName(), student2.getFirstName());
        assertEquals(SAMPLE_STUDENT.getLastName(), student2.getLastName());
        assertEquals(SAMPLE_STUDENT.getDob(), student2.getDob());
        assertEquals(SAMPLE_STUDENT.getLocation(), student2.getLocation());
        assertEquals(SAMPLE_STUDENT.getCourseId(), student2.getCourseId());
        assertEquals(SAMPLE_COURSE_NAME1, student2.getCourseName());
    }

    @Test
    @Order(3)
    void addExists() {
        assertEquals(EnrolmentStatus.EXISTS, assertDoesNotThrow(() -> service.add(SAMPLE_STUDENT)));
    }
    
    @Test
    @Order(4)
    void updateAndView() {
        Student student1 = new Student(SAMPLE_EMAIL1, SAMPLE_FIRST_NAME, SAMPLE_LAST_NAME, SAMPLE_DOB, SAMPLE_LOCATION, SAMPLE_COURSE_ID2);
        assertEquals(EnrolmentStatus.SUCCESS, assertDoesNotThrow(() -> service.update(student1)));
        var optStudent = assertDoesNotThrow(() -> service.fetch(student1.getEmail()));
        assertTrue(optStudent.isPresent());
        var student2 = optStudent.get();
        assertEquals(student1.getEmail(), student2.getEmail());
        assertEquals(student1.getFirstName(), student2.getFirstName());
        assertEquals(student1.getLastName(), student2.getLastName());
        assertEquals(student1.getDob(), student2.getDob());
        assertEquals(student1.getLocation(), student2.getLocation());
        assertEquals(student1.getCourseId(), student2.getCourseId());
        assertEquals(SAMPLE_COURSE_NAME2, student2.getCourseName());
    }

    @Test
    @Order(5)
    void updateNotExists() {
        Student student = new Student(SAMPLE_EMAIL2, SAMPLE_FIRST_NAME, SAMPLE_LAST_NAME, SAMPLE_DOB, SAMPLE_LOCATION, SAMPLE_COURSE_ID2);
        assertEquals(EnrolmentStatus.NOT_EXISTS, assertDoesNotThrow(() -> service.update(student)));
    }

    @Test
    @Order(6)
    void deleteAndView() {
        assertEquals(EnrolmentStatus.SUCCESS, assertDoesNotThrow(() -> service.delete(SAMPLE_EMAIL1)));
        assertTrue(assertDoesNotThrow(() -> service.fetch(SAMPLE_EMAIL1)).isEmpty());
    }

    @Test
    @Order(7)
    void deleteNotExists() {
        assertEquals(EnrolmentStatus.NOT_EXISTS, assertDoesNotThrow(() -> service.delete(SAMPLE_EMAIL1)));
    }
}
