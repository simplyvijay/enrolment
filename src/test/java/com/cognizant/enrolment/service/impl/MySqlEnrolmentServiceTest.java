package com.cognizant.enrolment.service.impl;

import com.cognizant.enrolment.model.EnrolmentException;
import com.cognizant.enrolment.model.Student;
import com.cognizant.enrolment.service.EnrolmentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Student student1 = new Student("example@abc.com", "John", "Trovolta", "12/11/2002", "NY", 1001);
        assertDoesNotThrow(() -> service.add(student1));
        Student student2 = assertDoesNotThrow(() -> service.view("example@abc.com"));
        assertEquals("example@abc.com", student2.getEmail());
        assertEquals("John", student2.getFirstName());
        assertEquals("Trovolta", student2.getLastName());
        assertEquals("12/11/2002", student2.getDob());
        assertEquals("NY", student2.getLocation());
        assertEquals(1001, student2.getCourseId());
        assertEquals("Cloud", student2.getCourseName());
    }

    @Test
    @Order(3)
    void addError() {
        Student student = new Student("example@abc.com", "John", "Trovolta", "12/11/2002", "NY", 1001);
        var ex = assertThrows(EnrolmentException.class, () -> service.add(student));
        assertEquals("Student with the given email already exists", ex.getMessage());
    }
    
    @Test
    @Order(4)
    void updateAndView() {
        Student student1 = new Student("example@abc.com", "John", "Trovolta", "12/11/2002", "NY", 1002);
        assertDoesNotThrow(() -> service.update(student1));
        Student student2 = assertDoesNotThrow(() -> service.view("example@abc.com"));
        assertEquals("example@abc.com", student2.getEmail());
        assertEquals("John", student2.getFirstName());
        assertEquals("Trovolta", student2.getLastName());
        assertEquals("12/11/2002", student2.getDob());
        assertEquals("NY", student2.getLocation());
        assertEquals(1002, student2.getCourseId());
        assertEquals("Java", student2.getCourseName());
    }

    @Test
    @Order(5)
    void updateError() {
        Student student = new Student("example1@abc.com", "John", "Trovolta", "12/11/2002", "NY", 1002);
        var ex = assertThrows(EnrolmentException.class, () -> service.update(student));
        assertEquals("No enrolment exist for the given email: example1@abc.com", ex.getMessage());
    }

    @Test
    @Order(6)
    void deleteAndView() {
        assertDoesNotThrow(() -> service.delete("example@abc.com"));
        var ex = assertThrows(EnrolmentException.class, () -> service.view("example@abc.com"));
        assertEquals("No enrolment exist for the given email: example@abc.com", ex.getMessage());
    }

    @Test
    @Order(7)
    void deleteError() {
        var ex = assertThrows(EnrolmentException.class, () -> service.delete("example@abc.com"));
        assertEquals("No enrolment exist for the given email: example@abc.com", ex.getMessage());
    }
}
