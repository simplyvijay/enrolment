package com.cognizant.enrolment.application;

import com.cognizant.enrolment.model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static com.cognizant.enrolment.Constants.*;
import static com.cognizant.enrolment.application.EnrolmentApplication.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnrolmentApplicationTest {
    EnrolmentApplication application;

    @BeforeAll
    void init() {
        application = new EnrolmentApplication();
    }

    @Test
    @Order(1)
    void enrol() {
        String input = Input.create()
                .next(1)
                .next(SAMPLE_COURSE_ID1)
                .next(SAMPLE_FIRST_NAME)
                .next(SAMPLE_LAST_NAME)
                .next(SAMPLE_DOB)
                .next(SAMPLE_EMAIL1)
                .next(SAMPLE_LOCATION)
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains(ENROLMENT_CONFIRMATION));

        input = Input.create()
                .next("ab")
                .next(1)
                .next(1010)
                .next(SAMPLE_COURSE_ID1)
                .next(SAMPLE_FIRST_NAME)
                .next(SAMPLE_LAST_NAME)
                .next("12-33-2002")
                .next(SAMPLE_DOB)
                .next(SAMPLE_EMAIL1)
                .next(SAMPLE_LOCATION)
                .next(5)
                .toString();
        output = run(input);
        assertTrue(output.contains(INVALID_INPUT));
        assertTrue(output.contains(NOT_VALID_COURSE));
        assertTrue(output.contains(NOT_VALID_DOB));
        assertTrue(output.contains(STUDENT_EXISTS));
    }

    @Test
    @Order(2)
    void view() {
        String input = Input.create()
                .next(2)
                .next(SAMPLE_EMAIL1)
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains(getEnrolmentDetails(SAMPLE_STUDENT)));
    }

    @Test
    @Order(3)
    void update() {
        String input = Input.create()
                .next(3)
                .next(SAMPLE_EMAIL1)
                .next(SAMPLE_COURSE_ID2)
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains(UPDATE_CONFIRMATION));

        input = Input.create()
                .next(2)
                .next(SAMPLE_EMAIL1)
                .next(5)
                .toString();
        output = run(input);
        assertTrue(output.contains(getEnrolmentDetails(
                new Student(SAMPLE_EMAIL1, SAMPLE_FIRST_NAME, SAMPLE_LAST_NAME, SAMPLE_DOB, SAMPLE_LOCATION, SAMPLE_COURSE_ID2, SAMPLE_COURSE_NAME2))));

        input = Input.create()
                .next(3)
                .next(SAMPLE_EMAIL2)
                .next(3)
                .next(SAMPLE_EMAIL1)
                .next(1010)
                .next(SAMPLE_COURSE_ID2)
                .next(5)
                .toString();
        output = run(input);
        assertTrue(output.contains(NO_ENROLMENT_EXISTS + SAMPLE_EMAIL2));
        assertTrue(output.contains(NOT_VALID_COURSE) && output.contains(NO_UPDATE_REQUIRED));
    }

    @Test
    @Order(4)
    void delete() {
        String input = Input.create()
                .next(4)
                .next(SAMPLE_EMAIL1)
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains(DELETE_CONFIRMATION));
        output = run(input);
        assertTrue(output.contains(NO_ENROLMENT_EXISTS + SAMPLE_EMAIL1));
    }

    private String run(String input) {
        String output = "";
        try(ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream pr = new PrintStream(out, true, StandardCharsets.UTF_8)) {
            application.start(in, pr);
            output = out.toString();
        } catch (IOException e) {
            fail();
        }
        return output;
    }

    private static class Input {
        private final StringBuilder buffer;
        private Input() {
            buffer = new StringBuilder();
        }
        public static Input create() {
            return new Input();
        }
        public Input next(String n) {
            buffer.append(n).append(System.lineSeparator());
            return this;
        }
        public Input next(int n) {
            buffer.append(n).append(System.lineSeparator());
            return this;
        }
        @Override
        public String toString() {
            return buffer.toString();
        }
    }
}
