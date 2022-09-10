package com.cognizant.enrolment.service.impl;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.Student;
import com.cognizant.enrolment.service.EnrolmentService;
import com.cognizant.enrolment.service.EnrolmentStatus;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;

public class MySqlEnrolmentService implements EnrolmentService {

    private static final String ENROLMENT_URL = "enrolment_url";
    private static final String ENROLMENT_USER = "enrolment_user";
    private static final String ENROLMENT_PASSWORD = "enrolment_password";

    private static final String DDL_FILE = "create.sql";
    private static final String PROPERTIES_FILE = "application.properties";
    private static final String GET_COURSES = "SELECT * FROM COURSE";
    private static final String INSERT_STUDENT = "INSERT INTO STUDENT (email, first_name, last_name, dob, location, course_id) VALUES (?, ?, ?, ?, ?, ?)";

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
    public CourseList getCourseList() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(GET_COURSES)) {
            CourseList list = new CourseList();
            while (resultSet.next()) {
                list.add(resultSet.getInt(1), resultSet.getString(2));
            }
            return list;
        }
    }

    @Override
    public EnrolmentStatus add(Student student) throws SQLException {
        ResultSet resultSet = null;
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement1 = connection.prepareStatement(SELECT_STUDENT);
            PreparedStatement statement2 = connection.prepareStatement(INSERT_STUDENT)) {

            statement1.setString(1, student.getEmail());
            resultSet = statement1.executeQuery();
            if(resultSet.next()) {
                return EnrolmentStatus.EXISTS;
            }

            statement2.setString(1, student.getEmail());
            statement2.setString(2, student.getFirstName());
            statement2.setString(3, student.getLastName());
            statement2.setString(4, student.getDob());
            statement2.setString(5, student.getLocation());
            statement2.setInt(6, student.getCourseId());
            statement2.executeUpdate();
            return EnrolmentStatus.SUCCESS;
        } finally {
            if(resultSet != null) {
                resultSet.close();
            }
        }
    }

    @Override
    public Optional<Student> fetch(String email) throws SQLException {
        ResultSet resultSet = null;
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(VIEW_STUDENT)) {
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(new Student(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getInt(6),
                        resultSet.getString(7)
                ));
            } else {
                return Optional.empty();
            }
        } finally {
            if(resultSet != null) {
                resultSet.close();
            }
        }
    }

    @Override
    public EnrolmentStatus update(Student student) throws SQLException {
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT)) {
            statement.setInt(1, student.getCourseId());
            statement.setString(2, student.getEmail());
            return (statement.executeUpdate() == 0)? EnrolmentStatus.NOT_EXISTS : EnrolmentStatus.SUCCESS;
        }
    }

    @Override
    public EnrolmentStatus delete(String email) throws SQLException {
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT)) {
            statement.setString(1, email);
            return (statement.executeUpdate() == 0)? EnrolmentStatus.NOT_EXISTS : EnrolmentStatus.SUCCESS;
        }
    }

    public static EnrolmentService create() throws IOException, SQLException {
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
        }
    }

    private static Properties getProperties() throws IOException {
        try(InputStream is = EnrolmentService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            var p = new Properties();
            p.load(is);
            return p;
        }
    }

    private static String getProperty(Properties properties, String property) {
        return (String) properties.getOrDefault(property, System.getenv(property));
    }
}
