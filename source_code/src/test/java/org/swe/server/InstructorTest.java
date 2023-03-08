package org.swe.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class InstructorTest {
    static Connection con = null;
    static instructor inst1;
    static instructor inst3;
    static instructor inst7;
    static instructor inst8;
    static instructor inst9;
    static acadOffice acad;

    @BeforeAll
    static void initDb() throws ClassNotFoundException, SQLException {

        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection(url, username, password);
        inst1 = new instructor("inst1@iitrpr.ac.in", con);
        inst3 = new instructor("inst3@iitrpr.ac.in", con);
        inst7 = new instructor("inst7@iitrpr.ac.in", con);
        inst8 = new instructor("inst8@iitrpr.ac.in", con);
        inst9 = new instructor("inst9@iitrpr.ac.in", con);

        acad = new acadOffice("acadoffice@iitrpr.ac.in", con);
    }

    @Test
    void viewGrades() throws SQLException {
        int g = inst1.viewGrades(1);
        assertEquals(1, g);
    }

    @Test
    void uploadGrades() throws SQLException, IOException {
        int uploadFalse1 = inst8.uploadGrades("grades_41", "41");
        assertEquals(0, uploadFalse1);

        int uploadFalse2 = inst3.uploadGrades("grades_34", "34");
        assertEquals(0, uploadFalse2);

        int uploadTrue = inst8.uploadGrades("grades_42", "42");
        assertEquals(1, uploadTrue);

        int uploadFalse3 = inst9.uploadGrades("grades_45", "45");
        assertEquals(0, uploadFalse3);

        acad.changeDeadlines(2);
        int uploadFalse4 = inst8.uploadGrades("grades_42", "42");
        assertEquals(0, uploadFalse4);
        acad.changeDeadlines(2);
    }

    @Test
    void downloadStudentDetails() throws SQLException, IOException {
        int offerFalse = inst1.downloadStudentDetails(2);
        assertEquals(0, offerFalse);

        int offerTrue = inst1.downloadStudentDetails(1);
        assertEquals(1, offerTrue);
    }

    @Test
    void offerRemoveCourse() throws SQLException {
        int offerFalse1 = inst7.offerCourse("EE401", 6.0F);
        assertEquals(0, offerFalse1);
        int offerTrue = inst7.offerCourse("EE101", 0F);
        assertEquals(1, offerTrue);
        int offerFalse2 = inst7.offerCourse("EE101", 0F);
        assertEquals(0, offerFalse2);

        int remTrue = inst7.removeOffer("EE101");
        assertEquals(1, remTrue);
        int remFalse1 = inst7.removeOffer("EE101");
        assertEquals(0, remFalse1);

        acad.changeDeadlines(0);
        int offerFalse3 = inst7.offerCourse("EE101", 0F);
        assertEquals(0, offerFalse3);
        int remFalse2 = inst7.removeOffer("EE101");
        assertEquals(0, remFalse2);
        acad.changeDeadlines(0);
    }

//    @Test
//    void removeOffer() {
//    }

    @AfterAll
    static void originalDbState() throws SQLException {
        // upload grades
        String grade0 = "DELETE FROM grades WHERE offer_id=42";
        String grade1 = "UPDATE offered SET status=0 WHERE offer_id=42";
        Statement stg = con.createStatement();
        stg.executeUpdate(grade0);
        stg.executeUpdate(grade1);

        con.close();
    }
}