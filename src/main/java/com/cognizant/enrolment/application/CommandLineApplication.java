package com.cognizant.enrolment.application;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.Student;
import com.cognizant.enrolment.service.EnrolmentException;
import com.cognizant.enrolment.service.EnrolmentService;
import com.cognizant.enrolment.service.EnrolmentServiceFactory;
import com.cognizant.enrolment.service.EnrolmentStatus;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

public class CommandLineApplication {

    public static final String ENROLMENT_WELCOME_MESSAGE = "Welcome to Student Enrolment System";
    public static final String ENROLMENT_OPTIONS = "\n1. Course Enrolment\n2. View Enrolment\n3. Update Enrolment\n4. Delete Enrolment\n5. Exit\nEnter Option: ";
    public static final String ENTER_COURSE = "Enter course id: ";
    public static final String NOT_VALID_COURSE = "Not a valid course id";
    public static final String ENTER_FIRST_NAME = "Enter First Name: ";
    public static final String ENTER_LAST_NAME = "Enter Last Name: ";
    public static final String ENTER_DOB = "Enter DOB (DD/MM/YYYY): ";
    public static final String NOT_VALID_DOB = "Date of birth should be valid with format DD/MM/YYYY";
    public static final String ENTER_EMAIL = "Enter Email id: ";
    public static final String ENTER_LOCATION = "Enter Location: ";
    public static final String ENROLMENT_CONFIRMATION = "Your enrolment is confirmed";
    public static final String UPDATE_CONFIRMATION = "Your enrolment is updated";
    public static final String DELETE_CONFIRMATION = "Your enrolment is removed";
    public static final String NO_UPDATE_REQUIRED = "No update required";
    public static final String ENROLMENT_DETAILS_PREFIX = "My enrolment details - ";
    public static final String AVAILABLE_COURSES = "Available courses:";
    public static final String INVALID_INPUT = "Invalid input, Enter again: ";
    public static final String NO_ENROLMENT_EXISTS = "No enrolment exist for the given email: ";
    public static final String STUDENT_EXISTS = "Student with the given email already exists";
    private EnrolmentService enrolmentService;
    private CourseList courseList;
    private Scanner scanner;
    private PrintStream printStream;

    public void start(InputStream inputStream, PrintStream printStream) {
        try {
            scanner = new Scanner(inputStream);
            this.printStream = printStream;
            println(ENROLMENT_WELCOME_MESSAGE);
            if(enrolmentService == null) {
                enrolmentService = EnrolmentServiceFactory.getService();
            }
            boolean iterate = true;
            while(iterate) {
                printStream.print(ENROLMENT_OPTIONS);
                switch (nextInt()) {
                    case 1 -> enrol();
                    case 2 -> view();
                    case 3 -> update();
                    case 4 -> delete();
                    default -> iterate = false;
                }
            }
        } catch (EnrolmentException e) {
            printStream.println("Exception occurred with the enrolment process: " + e.getMessage());
        }
    }

    private void enrol() throws EnrolmentException {
        println(AVAILABLE_COURSES);
        printStream.println(getCourseList());
        int courseId = getCourseId();
        printStream.print(ENTER_FIRST_NAME);
        String firstName = scanner.nextLine();
        printStream.print(ENTER_LAST_NAME);
        String lastName = scanner.nextLine();
        String dob;
        while(true) {
            printStream.print(ENTER_DOB);
            dob = scanner.nextLine();
            if(Student.isValidDob(dob)) break;
            printStream.println(NOT_VALID_DOB);
        }
        printStream.print(ENTER_EMAIL);
        String email = scanner.nextLine();
        printStream.print(ENTER_LOCATION);
        String location = scanner.nextLine();
        var status = enrolmentService.add(new Student(email, firstName, lastName, dob, location, courseId));
        println(status.equals(EnrolmentStatus.SUCCESS)? ENROLMENT_CONFIRMATION : STUDENT_EXISTS);
    }

    private void view() throws EnrolmentException {
        printStream.print(ENTER_EMAIL);
        var email = scanner.nextLine();
        println(enrolmentService.fetch(email).map(CommandLineApplication::getEnrolmentDetails).orElseGet(() -> NO_ENROLMENT_EXISTS + email));
    }

    private void update() throws EnrolmentException {
        printStream.print(ENTER_EMAIL);
        String email = scanner.nextLine();
        var optStudent = enrolmentService.fetch(email);
        if(optStudent.isEmpty()) {
            println(NO_ENROLMENT_EXISTS + email);
            return;
        }
        var student = optStudent.get();
        println(getEnrolmentDetails(student));
        println(AVAILABLE_COURSES);
        printStream.println(getCourseList());
        int courseId = getCourseId();
        if(Objects.equals(student.getCourseId(), courseId)) {
            println(NO_UPDATE_REQUIRED);
        } else {
            student.setCourseId(courseId);
            enrolmentService.update(student);
            println(UPDATE_CONFIRMATION);
        }
    }

    private void delete() throws EnrolmentException {
        printStream.print(ENTER_EMAIL);
        var email = scanner.nextLine();
        var status = enrolmentService.delete(email);
        println(status.equals(EnrolmentStatus.SUCCESS)? DELETE_CONFIRMATION : NO_ENROLMENT_EXISTS + email);
    }

    public static String getEnrolmentDetails(Student student) {
        return ENROLMENT_DETAILS_PREFIX + student;
    }

    private int getCourseId() throws EnrolmentException {
        while(true) {
            printStream.print(ENTER_COURSE);
            int courseId = nextInt();
            if(getCourseList().contains(courseId)) return courseId;
            printStream.println(NOT_VALID_COURSE);
        }
    }

    private int nextInt() {
        while(true) {
            String str = scanner.nextLine();
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ex) {
                printStream.print(INVALID_INPUT);
            }
        }
    }

    private CourseList getCourseList() throws EnrolmentException {
        if(courseList == null) {
            courseList = enrolmentService.getCourseList();
        }
        return courseList;
    }

    private void println(String message) {
        printStream.println("\n" + message);
    }
}
