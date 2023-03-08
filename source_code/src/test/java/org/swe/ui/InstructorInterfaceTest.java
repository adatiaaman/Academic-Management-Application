package org.swe.ui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class InstructorInterfaceTest {
    static Connection con;
    static instructorInterface inst1;
    static instructorInterface inst7;
    static instructorInterface inst9;

    @BeforeAll
    static void initDb() throws ClassNotFoundException, SQLException {

        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection(url, username, password);

        inst1 = new instructorInterface("inst1@iitrpr.ac.in", con);
        inst7 = new instructorInterface("inst7@iitrpr.ac.in", con);
        inst9 = new instructorInterface("inst9@iitrpr.ac.in", con);
    }

    @Test
    void instProcedures() throws SQLException, IOException {
        String input1 = "\n5\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        System.setOut(ps);
        String ret1 = inst1.instProcedures();
        assertEquals("BACK", ret1);

        String input2 = "\n6\n";
        in = new ByteArrayInputStream(input2.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret2 = inst1.instProcedures();
        assertEquals("Invalid! Enter Valid Input as mentioned.", ret2);

        String input3 = "\n4 \ngrades_2\n";
        in = new ByteArrayInputStream(input3.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret3 = inst1.instProcedures();
        assertEquals(" ", ret3);

        String input4 = "\n0 \n1\n";
        in = new ByteArrayInputStream(input4.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret4 = inst1.instProcedures();
        assertEquals(" ", ret4);

        String input5 = "\n0\n";
        in = new ByteArrayInputStream(input5.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret5 = inst7.instProcedures();
        assertEquals(" ", ret5);

        String input6 = "\n1 \nCS401\n0.0 \n\n";
        in = new ByteArrayInputStream(input6.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret6 = inst1.instProcedures();
        assertEquals(" ", ret6);

        String input7 = "\n3\n";
        in = new ByteArrayInputStream(input7.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret7 = inst7.instProcedures();
        assertEquals(" ", ret7);

        String input8 = "\n3 \n44 \n\n";
        in = new ByteArrayInputStream(input8.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret8 = inst9.instProcedures();
        assertEquals(" ", ret8);

        String input9 = "\n2\n";
        in = new ByteArrayInputStream(input9.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret9 = inst7.instProcedures();
        assertEquals(" ", ret9);

        String input10 = "\n2 \nCS102\n";
        in = new ByteArrayInputStream(input10.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret10 = inst9.instProcedures();
        assertEquals(" ", ret10);
    }

    @AfterAll
    static void originalDbState() throws SQLException {
        con.close();
    }
}