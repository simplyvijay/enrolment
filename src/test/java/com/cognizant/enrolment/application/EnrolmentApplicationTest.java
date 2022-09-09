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
                .next(1001)
                .next("John")
                .next("Travolta")
                .next("12/11/2002")
                .next("example@abc.com")
                .next("NY")
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains("Your enrolment is confirmed"));

        input = Input.create()
                .next("ab")
                .next(1)
                .next(1010)
                .next(1001)
                .next("John")
                .next("Travolta")
                .next("12-33-2002")
                .next("12/11/2002")
                .next("example@abc.com")
                .next("NY")
                .next(5)
                .toString();
        output = run(input);
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("Not a valid course id"));
        assertTrue(output.contains("Date of birth should be valid with format DD/MM/YYYY"));
        assertTrue(output.contains("Student with the given email already exists"));
    }

    @Test
    @Order(2)
    void view() {
        String input = Input.create()
                .next(2)
                .next("example@abc.com")
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains("My enrolment details - " +
                new Student("example@abc.com", "John", "Travolta", "12/11/2002", "NY", 1001, "Cloud")));
    }

    @Test
    @Order(3)
    void update() {
        String input = Input.create()
                .next(3)
                .next("example@abc.com")
                .next(1002)
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains("Your enrolment is updated"));

        input = Input.create()
                .next(2)
                .next("example@abc.com")
                .next(5)
                .toString();
        output = run(input);
        assertTrue(output.contains("My enrolment details - " +
                new Student("example@abc.com", "John", "Travolta", "12/11/2002", "NY", 1002, "Java")));

        input = Input.create()
                .next(3)
                .next("example1@abc.com")
                .next(3)
                .next("example@abc.com")
                .next(1010)
                .next(1002)
                .next(5)
                .toString();
        output = run(input);
        assertTrue(output.contains("No enrolment exist for the given email: example1@abc.com"));
        assertTrue(output.contains("Not a valid course id") && output.contains("No update required"));
    }

    @Test
    @Order(4)
    void delete() {
        String input = Input.create()
                .next(4)
                .next("example@abc.com")
                .next(5)
                .toString();
        String output = run(input);
        assertTrue(output.contains("Your enrolment is removed"));
        output = run(input);
        assertTrue(output.contains("No enrolment exist for the given email: example@abc.com"));
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
        private final StringBuilder input;
        private Input() {
            input = new StringBuilder();
        }
        public static Input create() {
            return new Input();
        }
        public Input next(String n) {
            input.append(n).append(System.lineSeparator());
            return this;
        }
        public Input next(int n) {
            input.append(n).append(System.lineSeparator());
            return this;
        }
        @Override
        public String toString() {
            return input.toString();
        }
    }
}
