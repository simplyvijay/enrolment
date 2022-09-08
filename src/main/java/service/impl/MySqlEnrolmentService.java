package service.impl;

import com.mysql.cj.exceptions.MysqlErrorNumbers;
import model.CourseList;
import model.EnrolmentException;
import model.Student;
import service.EnrolmentService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlEnrolmentService implements EnrolmentService {

    private static final String GET_COURSES = "SELECT * FROM COURSE";
    private static final String INSERT_STUDENT = "INSERT INTO STUDENT VALUES (?, ?, ?, ?, ?, ?)";
    private static final String VIEW_STUDENT = "SELECT s.*, c.name FROM STUDENT s INNER JOIN COURSE c ON s.course_id = c.id WHERE s.email = ?";
    private static final String UPDATE_STUDENT = "UPDATE STUDENT SET course_id = ? WHERE email = ?";
    private static final String DELETE_STUDENT = "DELETE FROM STUDENT WHERE email = ?";
    private final String url;
    private final String user;
    private final String password;

    private MySqlEnrolmentService(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public CourseList getCourseList() throws EnrolmentException {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();
            var resultSet = statement.executeQuery(GET_COURSES);
            CourseList list = new CourseList();
            while(resultSet.next()) {
                list.add(resultSet.getInt(1), resultSet.getString(2));
            }
            return list;
        } catch (SQLException e) {
            throw new EnrolmentException("Exception while getting course list: " + e.getErrorCode());
        }
    }

    @Override
    public void add(Student student) throws EnrolmentException {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement statement = connection.prepareStatement(INSERT_STUDENT);
            statement.setString(1, student.getEmail());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getDob());
            statement.setString(5, student.getLocation());
            statement.setInt(6, student.getCourseId());
            statement.executeUpdate();
        } catch (SQLException e) {
            if(e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY_WITH_KEY_NAME) {
                throw new EnrolmentException("Student with the given email already exists");
            }
            throw new EnrolmentException("Exception while inserting student: " + e.getErrorCode());
        }
    }

    @Override
    public Student view(String email) throws EnrolmentException {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement statement = connection.prepareStatement(VIEW_STUDENT);
            statement.setString(1, email);
            var resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new Student(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getInt(6),
                        resultSet.getString(7)
                );
            } else {
                throw new EnrolmentException("No enrolment exist for the given email: " + email);
            }
        } catch (SQLException e) {
            throw new EnrolmentException("Exception while fetching student: " + e.getErrorCode());
        }
    }

    @Override
    public void update(Student student) throws EnrolmentException {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT);
            statement.setInt(1, student.getCourseId());
            statement.setString(2, student.getEmail());
            if(statement.executeUpdate() == 0) {
                throw new EnrolmentException("New enrolment exists for the email: " + student.getEmail());
            }
        } catch (SQLException e) {
            throw new EnrolmentException("Exception while updating student: " + e.getErrorCode());
        }
    }

    @Override
    public void delete(String email) throws EnrolmentException {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT);
            statement.setString(1, email);
            if(statement.executeUpdate() == 0) {
                throw new EnrolmentException("New enrolment exists for the email: " + email);
            }
        } catch (SQLException e) {
            throw new EnrolmentException("Exception while updating student: " + e.getErrorCode());
        }
    }

    public static MySqlEnrolmentService create(String url, String user, String password) throws EnrolmentException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new EnrolmentException("MySQL driver not found.");
        }
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            return new MySqlEnrolmentService(url, user, password);
        } catch (SQLException e) {
            throw new EnrolmentException("Cannot create MySQL DB connection with url: " + url);
        }
    }
}
