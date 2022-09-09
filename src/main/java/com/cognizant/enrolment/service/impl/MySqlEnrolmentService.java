package com.cognizant.enrolment.service.impl;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.EnrolmentException;
import com.cognizant.enrolment.model.Student;
import com.cognizant.enrolment.service.EnrolmentService;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class MySqlEnrolmentService implements EnrolmentService {

    private static final String ENROLMENT_URL = "enrolment_url";
    private static final String ENROLMENT_USER = "enrolment_user";
    private static final String ENROLMENT_PASSWORD = "enrolment_password";

    private static final String DDL_FILE = "create.sql";
    private static final String PROPERTIES_FILE = "application.properties";
    private static final String GET_COURSES = "SELECT * FROM COURSE";
    private static final String INSERT_STUDENT = "INSERT INTO STUDENT VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_STUDENT = "SELECT email FROM STUDENT WHERE email = ? LIMIT 1";
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
            PreparedStatement statement = connection.prepareStatement(SELECT_STUDENT);
            statement.setString(1, student.getEmail());
            if(statement.executeQuery().next()) {
                throw new EnrolmentException("Student with the given email already exists");
            }

            statement = connection.prepareStatement(INSERT_STUDENT);
            statement.setString(1, student.getEmail());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getDob());
            statement.setString(5, student.getLocation());
            statement.setInt(6, student.getCourseId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new EnrolmentException("Exception while inserting student: " + e.getErrorCode() + " Message: " + e.getMessage());
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
                throw new EnrolmentException("No enrolment exist for the given email: " + student.getEmail());
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
                throw new EnrolmentException("No enrolment exist for the given email: " + email);
            }
        } catch (SQLException e) {
            throw new EnrolmentException("Exception while deleting student: " + e.getErrorCode());
        }
    }

    public static EnrolmentService create() throws EnrolmentException {
        var p = getProperties();
        String url = getProperty(p, ENROLMENT_URL);
        String user = getProperty(p, ENROLMENT_USER);
        String password = getProperty(p, ENROLMENT_PASSWORD);
        try(Connection connection = DriverManager.getConnection(url, user, password);
            InputStream is = EnrolmentService.class.getClassLoader().getResourceAsStream(DDL_FILE);
            Reader iReader = new InputStreamReader(is)) {
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setLogWriter(null);
            runner.runScript(iReader);
            return new MySqlEnrolmentService(url, user, password);
        } catch (SQLException | IOException e) {
            throw new EnrolmentException("Exception during creating EnrolmentService: " + e.getMessage());
        }
    }

    private static Properties getProperties() throws EnrolmentException {
        try(InputStream is = EnrolmentService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            var p = new Properties();
            p.load(is);
            return p;
        } catch (IOException e) {
            throw new EnrolmentException("Cannot read properties file: " + PROPERTIES_FILE);
        }
    }

    private static String getProperty(Properties properties, String property) {
        return (String) properties.getOrDefault(property, System.getenv(property));
    }
}
