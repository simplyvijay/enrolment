package com.cognizant.enrolment;

import com.cognizant.enrolment.model.Student;

public class Constants {
    private Constants() {
    }

    public static final String SAMPLE_EMAIL1 = "example@abc.com";
    public static final String SAMPLE_EMAIL2 = "example1@abc.com";
    public static final String SAMPLE_FIRST_NAME = "John";
    public static final String SAMPLE_LAST_NAME = "Travolta";
    public static final String SAMPLE_DOB = "12/11/2002";
    public static final String SAMPLE_LOCATION = "NY";
    public static final int SAMPLE_COURSE_ID1 = 1001;
    public static final String SAMPLE_COURSE_NAME1 = "Cloud";
    public static final int SAMPLE_COURSE_ID2 = 1002;
    public static final String SAMPLE_COURSE_NAME2 = "Java";
    public static final Student SAMPLE_STUDENT = new Student(SAMPLE_EMAIL1, SAMPLE_FIRST_NAME, SAMPLE_LAST_NAME,
            SAMPLE_DOB, SAMPLE_LOCATION, SAMPLE_COURSE_ID1, SAMPLE_COURSE_NAME1);
}
