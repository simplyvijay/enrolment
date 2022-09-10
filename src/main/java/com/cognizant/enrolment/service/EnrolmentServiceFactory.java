package com.cognizant.enrolment.service;

import com.cognizant.enrolment.service.impl.MySqlEnrolmentService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class EnrolmentServiceFactory {

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String ENROLMENT_URL = "enrolment_url";
    private static final String ENROLMENT_USER = "enrolment_user";
    private static final String ENROLMENT_PWD = "enrolment_pwd";
    private static final String ENROLMENT_SERVICE = "enrolmentservice";
    private static final String MYSQL_SERVICE = "mysql";
    private static final String DDL_FILE = "ddl_file";

    private EnrolmentServiceFactory() {
    }

    public static EnrolmentService getService() throws EnrolmentException {
        Properties applicationProperties;
        try {
            applicationProperties = getProperties();
        } catch (IOException e) {
            throw new EnrolmentException("Unable to open the properties file: " + PROPERTIES_FILE, e);
        }

        if(getProperty(applicationProperties, ENROLMENT_SERVICE).equals(MYSQL_SERVICE)) {
            String url = getProperty(applicationProperties, ENROLMENT_URL);
            String user = getProperty(applicationProperties, ENROLMENT_USER);
            String password = getProperty(applicationProperties, ENROLMENT_PWD);
            String ddlFile = getProperty(applicationProperties, DDL_FILE);
            return MySqlEnrolmentService.create(url, user, password, ddlFile);
        }
        throw new EnrolmentException("No enrolment service configured", null);
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
