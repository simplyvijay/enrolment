package com.cognizant.enrolment.application;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.EnrolmentException;
import com.cognizant.enrolment.model.Student;
import com.cognizant.enrolment.service.EnrolmentService;
import com.cognizant.enrolment.service.impl.MySqlEnrolmentService;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

public class EnrolmentApplication {

    private EnrolmentService enrolmentService;
    private CourseList courseList;

    public void start(InputStream in, PrintStream out) {
        out.println("Welcome to Enrolment service");
        try {
            if(enrolmentService == null) {
                enrolmentService = MySqlEnrolmentService.create();
            }
        } catch (EnrolmentException e) {
            out.println(e.getMessage());
            return;
        }
        boolean iterate = true;
        Scanner scanner = new Scanner(in);
        while(iterate) {
            try {
                out.print("\n1. Course Enrolment\n2. View Enrolment\n3. Update Enrolment\n4. Delete Enrolment\n5. Exit\nEnter Option: ");
                switch (nextInt(scanner)) {
                    case 1 -> {
                        out.println(getCourseList());
                        int courseId;
                        while(true) {
                            out.print("Enter course id: ");
                            courseId = nextInt(scanner);
                            if(courseList.contains(courseId)) break;
                            out.println("Not a valid course id");
                        }
                        out.print("Enter First Name: ");
                        String firstName = scanner.nextLine();
                        out.print("Enter Last Name: ");
                        String lastName = scanner.nextLine();
                        String dob;
                        while(true) {
                            out.print("Enter DOB (DD/MM/YYYY): ");
                            dob = scanner.nextLine();
                            if(Student.isValidDob(dob)) break;
                            out.println("Date of birth should be valid with format DD/MM/YYYY");
                        }
                        out.print("Enter Email id: ");
                        String email = scanner.nextLine();
                        out.print("Enter Location: ");
                        String location = scanner.nextLine();
                        enrolmentService.add(new Student(email, firstName, lastName, dob, location, courseId));
                        out.println("\nYour enrolment is confirmed");
                    }
                    case 2 -> {
                        out.print("Enter email id: ");
                        out.println("\nMy enrolment details - " + enrolmentService.view(scanner.nextLine()));
                    }
                    case 3 -> {
                        out.print("Enter email id: ");
                        String email = scanner.nextLine();
                        Student student = enrolmentService.view(email);
                        out.println("\nMy enrolment details - " + student);
                        out.println("\nAvailable courses:");
                        out.println(getCourseList());
                        int courseId;
                        while(true) {
                            out.print("Enter course id: ");
                            courseId = nextInt(scanner);
                            if(courseList.contains(courseId)) break;
                            out.println("Not a valid course id");
                        }
                        if(!Objects.equals(student.getCourseId(), courseId)) {
                            student.setCourseId(courseId);
                            enrolmentService.update(student);
                            out.println("\nYour enrolment is updated");
                        } else {
                            out.println("\nNo update required");
                        }
                    }
                    case 4 -> {
                        out.print("Enter email id: ");
                        enrolmentService.delete(scanner.nextLine());
                        out.println("\nYour enrolment is removed");
                    }
                    default -> iterate = false;
                }
            } catch (EnrolmentException e) {
                out.println("\n" + e.getMessage());
            }
        }
    }
    
    private int nextInt(Scanner scanner) throws EnrolmentException {
        String str = scanner.nextLine();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            throw new EnrolmentException("Invalid input");
        }
    }

    private CourseList getCourseList() throws EnrolmentException {
        if(courseList == null) {
            courseList = enrolmentService.getCourseList();
        }
        return courseList;
    }
}
