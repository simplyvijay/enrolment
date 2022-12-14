package com.cognizant.enrolment.service.impl;

import com.cognizant.enrolment.model.CourseList;
import com.cognizant.enrolment.model.Student;
import com.cognizant.enrolment.service.EnrolmentException;
import com.cognizant.enrolment.service.EnrolmentService;
import com.cognizant.enrolment.service.EnrolmentStatus;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public final class MySqlEnrolmentService implements EnrolmentService {

    private static final String GET_COURSES = "SELECT * FROM COURSE";
    private static final String INSERT_STUDENT = "INSERT INTO STUDENT (email, first_name, last_name, dob, location, course_id) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_STUDENT = "SELECT email FROM STUDENT WHERE email = ? LIMIT 1";
    private static final String VIEW_STUDENT = "SELECT s.*, c.name FROM STUDENT s INNER JOIN COURSE c ON s.course_id = c.id WHERE s.email = ?";
    private static final String UPDATE_STUDENT = "UPDATE STUDENT SET course_id = ? WHERE email = ?";
    private static final String DELETE_STUDENT = "DELETE FROM STUDENT WHERE email = ?";
    private final String url;
    private final String user;
    private final String pwd;
    private Connection connection;

    private MySqlEnrolmentService(String url, String user, String pwd) {
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }

    @Override
    public CourseList getCourseList() throws EnrolmentException {
        try (var statement = getConnection().createStatement();
             var resultSet = statement.executeQuery(GET_COURSES)) {
            CourseList list = new CourseList();
            while (resultSet.next()) {
                list.add(resultSet.getInt(1), resultSet.getString(2));
            }
            return list;
        } catch (SQLException e) {
            throw new EnrolmentException("Unable to get course list: " + e.getMessage(), e);
        }
    }

    @Override
    public EnrolmentStatus add(Student student) throws EnrolmentException {
        try(var statement1 = getConnection().prepareStatement(SELECT_STUDENT);
            var statement2 = getConnection().prepareStatement(INSERT_STUDENT)) {

            statement1.setString(1, student.getEmail());

            try(var resultSet = statement1.executeQuery()) {
                if(resultSet.next()) {
                    return EnrolmentStatus.EXISTS;
                }
            }

            statement2.setString(1, student.getEmail());
            statement2.setString(2, student.getFirstName());
            statement2.setString(3, student.getLastName());
            statement2.setString(4, student.getDob());
            statement2.setString(5, student.getLocation());
            statement2.setInt(6, student.getCourseId());
            statement2.executeUpdate();
            return EnrolmentStatus.SUCCESS;
        } catch (SQLException e) {
            throw new EnrolmentException("Unable to add enrolment: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> fetch(String email) throws EnrolmentException {
        try(var statement = getConnection().prepareStatement(VIEW_STUDENT)) {
            statement.setString(1, email);
            try(var resultSet = statement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            throw new EnrolmentException("Unable to fetch the enrolment: " + e.getMessage(), e);
        }
    }

    @Override
    public EnrolmentStatus update(Student student) throws EnrolmentException {
        try(var statement = getConnection().prepareStatement(UPDATE_STUDENT)) {
            statement.setInt(1, student.getCourseId());
            statement.setString(2, student.getEmail());
            return (statement.executeUpdate() == 0)? EnrolmentStatus.NOT_EXISTS : EnrolmentStatus.SUCCESS;
        } catch (SQLException e) {
            throw new EnrolmentException("Unable to update enrolment: " + e.getMessage(), e);
        }
    }

    @Override
    public EnrolmentStatus delete(String email) throws EnrolmentException {
        try(var statement = getConnection().prepareStatement(DELETE_STUDENT)) {
            statement.setString(1, email);
            return (statement.executeUpdate() == 0)? EnrolmentStatus.NOT_EXISTS : EnrolmentStatus.SUCCESS;
        } catch (SQLException e) {
            throw new EnrolmentException("Unable to delete enrolment: " + e.getMessage(), e);
        }
    }

    private Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, pwd);
        }
        return connection;
    }

    public static EnrolmentService create(String url, String user, String pwd, String ddlFile) throws EnrolmentException {
        try {
            try(var connection = DriverManager.getConnection(url, user, pwd);
                var inputStream = EnrolmentService.class.getClassLoader().getResourceAsStream(ddlFile);
                var reader = new InputStreamReader(inputStream)) {
                ScriptRunner runner = new ScriptRunner(connection);
                runner.setLogWriter(null);
                runner.runScript(reader);
                return new MySqlEnrolmentService(url, user, pwd);
            }
        } catch (SQLException | IOException e) {
            throw new EnrolmentException("Unable to create enrolment service: " + e.getMessage(), e);
        }
    }
}
