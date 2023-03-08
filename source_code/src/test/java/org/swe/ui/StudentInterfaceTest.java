package org.swe.ui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.swe.server.student;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class StudentInterfaceTest {
    static Connection con = null;
    static studentInterface std1;
    static student std_1;

    @BeforeAll
    static void initDb() throws ClassNotFoundException, SQLException {

        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection(url, username, password);

        std1 = new studentInterface("student1@iitrpr.ac.in", con);
        std_1 = new student("student1@iitrpr.ac.in", con);
    }

    @Test
    void studentProcedures() throws SQLException {
        String input1 = "\n4\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        System.setOut(ps);
        String ret1 = std1.studentProcedures();
        assertEquals("BACK", ret1);

        String input2 = "\n5\n";
        in = new ByteArrayInputStream(input2.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret2 = std1.studentProcedures();
        assertEquals("Invalid! Enter Valid Input as mentioned.", ret2);

        String input3 = "\n3\n";
        in = new ByteArrayInputStream(input3.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret3 = std1.studentProcedures();
        assertEquals(" ", ret3);

        String input4 = "\n2\n";
        in = new ByteArrayInputStream(input4.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret4 = std1.studentProcedures();
        assertEquals(" ", ret4);

        String input5 = "\n0\n100\n";
        in = new ByteArrayInputStream(input5.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret5 = std1.studentProcedures();
        assertEquals(" ", ret5);

        String input6 = "\n1\n100\n";
        in = new ByteArrayInputStream(input6.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret6 = std1.studentProcedures();
        assertEquals(" ", ret6);


        std_1.enrollCourse(34);
        String input7 = "\n1\n34\n";
        in = new ByteArrayInputStream(input7.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret7 = std1.studentProcedures();
        assertEquals(" ", ret7);

    }

    @AfterAll
    static void originalDbState() throws SQLException {
        con.close();
    }

}