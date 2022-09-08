import model.CourseList;
import model.EnrolmentException;
import model.Student;
import service.EnrolmentService;
import service.impl.MySqlEnrolmentService;

import java.util.Scanner;

public class EnrolmentApplication {
    private static final String DEFAULT_URL = "";
    private static final String DEFAULT_USER = "";
    private static final String DEFAULT_PASSWORD = "";
    public static void main(String[] args) {
        System.out.println("Welcome to Enrolment service");
        EnrolmentService enrolmentService;
        try {
            enrolmentService = MySqlEnrolmentService.create(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASSWORD);
        } catch (EnrolmentException e) {
            System.out.println(e.getMessage());
            return;
        }
        boolean iterate = true;
        CourseList courseList = null;
        while(iterate) {
            try {
                System.out.print("\n1. Course Enrolment\n2. View Enrolment\n3. Update Enrolment\n4. Delete Enrolment\n5. Exit\nEnter Option: ");
                Scanner scanner = new Scanner(System.in);
                switch (scanner.nextInt()) {
                    case 1 -> {
                        if(courseList == null) {
                            courseList = enrolmentService.getCourseList();
                        }
                        System.out.println(courseList);
                        int courseId;
                        while(true) {
                            System.out.print("Enter course id: ");
                            courseId = scanner.nextInt();
                            if(courseList.contains(courseId)) break;
                            System.out.println("Not a valid course id");
                        }
                        System.out.print("Enter First Name: ");
                        String firstName = scanner.nextLine();
                        System.out.print("Enter Last Name: ");
                        String lastName = scanner.nextLine();
                        String dob;
                        while(true) {
                            System.out.print("Enter DOB (DD/MM/YYYY): ");
                            dob = scanner.nextLine();
                            if(Student.isValidDob(dob)) break;
                            System.out.println("Date of birth should be valid with format DD/MM/YYYY");
                        }
                        System.out.print("Enter Email id: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter Location: ");
                        String location = scanner.nextLine();
                        enrolmentService.add(new Student(email, firstName, lastName, dob, location, courseId));
                        System.out.println("Your enrolment is confirmed");
                    }
                    case 2 -> {
                        System.out.println("Enter email id: ");
                        System.out.println("My enrolment details - " + enrolmentService.view(scanner.nextLine()));
                    }
                    case 3 -> {
                        System.out.println("Enter email id: ");
                        String email = scanner.nextLine();
                        Student student = enrolmentService.view(email);
                        System.out.println("My enrolment details - " + student);
                        System.out.println("Available courses:");
                        if(courseList == null) {
                            courseList = enrolmentService.getCourseList();
                        }
                        System.out.println(courseList);
                        int courseId;
                        while(true) {
                            System.out.print("Enter course id: ");
                            courseId = scanner.nextInt();
                            if(courseList.contains(courseId)) break;
                            System.out.println("Not a valid course id");
                        }
                        student.setCourseId(courseId);
                        enrolmentService.update(student);
                        System.out.println("Your enrolment is updated");
                    }
                    case 4 -> {
                        System.out.println("Enter email id: ");
                        enrolmentService.delete(scanner.nextLine());
                        System.out.println("Your enrolment is removed");
                    }
                    default -> iterate = false;
                }
            } catch (EnrolmentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
