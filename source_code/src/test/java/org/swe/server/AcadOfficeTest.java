package org.swe.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class AcadOfficeTest {
    static Connection con = null;
    static acadOffice acad;
    @BeforeAll
    static void initDb() throws ClassNotFoundException, SQLException {
        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection(url, username, password);
        acad = new acadOffice("acadoffice@iitrpr.ac.in", con);
    }


    @Test
    void viewGrades() throws SQLException {
        assertEquals(1, acad.viewGrades());
    }

    @Test
    void addCourseCatalog() throws SQLException {
        // 1
        int numPrereq = 0;
        String[] preCode1 = new String[numPrereq];
        int[] preGrade1 = new int[numPrereq];
        int oldAdd = acad.addCourseCatalog("CS101", "cs course1", 3, 1, 1, 4, 3.0, "CSE", preCode1, preGrade1, numPrereq);
        assertEquals(0, oldAdd);

        // 2
        acad.deleteCourseCatalog("EE101");
        String[] preCode2 = new String[numPrereq];
        int[] preGrade2 = new int[numPrereq];
        int delAdd = acad.addCourseCatalog("EE101", "ee course1", 3, 1, 1,4, 3.0, "EE", preCode2, preGrade2, numPrereq);
        assertEquals(1, delAdd);

        // 3
        numPrereq = 1;
        String[] preCode = new String[numPrereq];
        preCode[0] = "CS101";
        int[] preGrade = new int[numPrereq];
        preGrade[0] = 0;
        int newAdd = acad.addCourseCatalog("CS401", "cs course test", 3, 1, 1, 4, 3.0, "CSE", preCode, preGrade, numPrereq);
        assertEquals(1, newAdd);
    }

    @Test
    void deleteCourseCatalog() throws SQLException {
        int checkDelete1 = acad.deleteCourseCatalog("HS401");
        assertEquals(1, checkDelete1);
        int checkDelete2 = acad.deleteCourseCatalog("HS401");
        assertEquals(0, checkDelete2);
    }

    @Test
    void readCourseCatalog() throws SQLException {
        assertEquals(1, acad.readCourseCatalog());
    }

//    @Test
//    void calculateCGPA() {
//    }

    @Test
    void generateTranscript() throws SQLException, IOException {
        assertEquals(1, acad.generateTranscript());
    }

    @Test
    void endSemester() throws SQLException {
        int checkEnd = acad.endSemester();
        assertEquals(0, checkEnd);

        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();
        ArrayList<Integer> statusId = new ArrayList<Integer>();
        String statusChange="SELECT * FROM offered WHERE status=0 AND sem=" + Integer.toString(curSem) +" AND year=" + Integer.toString(curYear) + ";";
        Statement stsc = con.createStatement();
        ResultSet rssc = stsc.executeQuery(statusChange);
        while (rssc.next()) {
            statusId.add(rssc.getInt("offer_id"));
        }
        String change = "UPDATE offered SET status=1 WHERE status=0 AND sem=" + Integer.toString(curSem) +" AND year=" + Integer.toString(curYear) + ";";
        Statement stc = con.createStatement();
        stc.executeUpdate(change);
        int checkEnd1 = acad.endSemester();
        assertEquals(1, checkEnd1);
        for (Integer id : statusId) {
            String revert = "UPDATE offered SET status=0 WHERE offer_id=" + Integer.toString(id) + ";";
            Statement str = con.createStatement();
            str.executeUpdate(revert);
        }

    }

    @Test
    void checkGraduation() throws SQLException {
        assertEquals(0, acad.checkGraduation("student1@iitrpr.ac.in"));
        assertEquals(0, acad.checkGraduation("student2@iitrpr.ac.in"));
    }

    @Test
    void changeDeadlines() throws SQLException {
        int offer1 = acad.changeDeadlines(0);
        assertEquals(1, offer1);
        int enroll1 = acad.changeDeadlines(1);
        assertEquals(1, enroll1);
        int grade1 = acad.changeDeadlines(2);
        assertEquals(1, grade1);
        int wrong = acad.changeDeadlines(3);
        assertEquals(0, wrong);
        int offer2 = acad.changeDeadlines(0);
        assertEquals(1, offer2);
        int enroll2 = acad.changeDeadlines(1);
        assertEquals(1, enroll2);
        int grade2 = acad.changeDeadlines(2);
        assertEquals(1, grade2);
    }

    @AfterAll
    static void originalDbState() throws SQLException {
        // delete course from catalog
        String deleteCourse = "UPDATE course SET status=1 WHERE code='HS401';";
        Statement stdc = con.createStatement();
        stdc.executeUpdate(deleteCourse);

        // end semester
        String oldTime = "UPDATE time_frame SET status=1 WHERE sem=2 AND year=2022;";
        String delNewTime = "DELETE FROM time_frame WHERE sem=1 AND year=2023";
        String delDeadline = "DELETE FROM deadlines WHERE sem=1 AND year=2023;";
        Statement stt = con.createStatement();
        stt.executeUpdate(oldTime);
        stt.executeUpdate(delNewTime);
        stt.executeUpdate(delDeadline);

        // course add to catalog
        String delPrereq = "DELETE FROM prereq WHERE course_code='CS401';";
        String delCourse = "DELETE FROM course WHERE code='CS401';";
        Statement stac = con.createStatement();
        stac.executeUpdate(delPrereq);
        stac.executeUpdate(delCourse);

        con.close();
    }
}