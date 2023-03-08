package org.swe.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    static Connection con = null;
    static student std1;
    static student std2;
    static student std3;
    static student std7;
    static student std8;
    static student std9;
    static student std10;
    static student std11;
    static student std12;
    static acadOffice acad;


    @BeforeAll
    static void initDb() throws ClassNotFoundException, SQLException {

        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection(url, username, password);
        std1 = new student("student1@iitrpr.ac.in", con);
        std2 = new student("student2@iitrpr.ac.in", con);
        std3 = new student("student3@iitrpr.ac.in", con);
        std7 = new student("student7@iitrpr.ac.in", con);
        std8 = new student("student8@iitrpr.ac.in", con);
        std9 = new student("student9@iitrpr.ac.in", con);
        std10 = new student("student10@iitrpr.ac.in", con);
        std11 = new student("student11@iitrpr.ac.in", con);
        std12 = new student("student12@iitrpr.ac.in", con);


        acad = new acadOffice("acadoffice@iitrpr.ac.in", con);
    }


    @Test
    void enrollDropCourse() throws SQLException {
        int invalidId = std1.enrollCourse(11);
        assertEquals(0, invalidId);
        int checkCgpa = std11.enrollCourse(34);
        assertEquals(0, checkCgpa);
        int alreadyTaken = std1.enrollCourse(1);
        assertEquals(0, alreadyTaken);
        int prereqFail = std9.enrollCourse(34);
        assertEquals(0, prereqFail);
        int enrollOk = std1.enrollCourse(34);
        assertEquals(1, enrollOk);

        int enrollNotOk = std1.enrollCourse(34);
        assertEquals(0, enrollNotOk);

        acad.changeDeadlines(1);
        int enrollDead = std1.enrollCourse(34);
        assertEquals(0, enrollDead);
        int dropDead = std1.dropCourse(34);
        assertEquals(0, dropDead);
        acad.changeDeadlines(1);

        int dropFalse = std1.dropCourse(1);
        assertEquals(0, dropFalse);
        int dropTrue = std1.dropCourse(34);
        assertEquals(1, dropTrue);

        int dropNotPoss = std1.dropCourse(2);
        assertEquals(0, dropNotPoss);

    }

//    @Test
//    void dropCourse() {
//    }

    @Test
    void calculateCGPA() throws SQLException {
        double cgpa1 = std1.calculateCGPA();
        assertEquals(10.0, cgpa1);
        double cgpa2 = std2.calculateCGPA();
        assertEquals(9.0, cgpa2);
        double cgpa7 = std7.calculateCGPA();
        assertEquals(8.0, cgpa7);
        double cgpa8 = std8.calculateCGPA();
        assertEquals(5.0, cgpa8);
        double cgpa9 = std9.calculateCGPA();
        assertEquals(6.5, cgpa9);
        double cgpa10 = std10.calculateCGPA();
        assertEquals(5.0, cgpa10);
        double cgpa11 = std11.calculateCGPA();
        assertEquals(4.0, cgpa11);
        double cgpa12 = std12.calculateCGPA();
        assertEquals(0.0, cgpa12);
        double cgpa3 = std3.calculateCGPA();
        assertEquals(0, cgpa3);
    }

    @Test
    void viewGrades() throws SQLException {
        int ch = std1.viewGrades();
        assertEquals(1, ch);
    }

    @AfterAll
    static void originalDbState() throws SQLException {
         con.close();
    }
}