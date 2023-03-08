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

class AcadOfficeInterfaceTest {
    static Connection con = null;
    static acadOfficeInterface acad;
    @BeforeAll
    static void initDb() throws ClassNotFoundException, SQLException {
        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection(url, username, password);
        acad = new acadOfficeInterface("acadoffice@iitrpr.ac.in", con);
    }

    @Test
    void acadProcedures() throws SQLException, IOException {
        String input1 = "\n6\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        System.setOut(ps);
        String ret1 = acad.acadProcedures();
        assertEquals("BACK", ret1);

        String input2 = "\n7\n";
        in = new ByteArrayInputStream(input2.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret2 = acad.acadProcedures();
        assertEquals("Invalid! Enter Valid Input as mentioned.", ret2);

        String input3 = "\n4\nstudent1@iitrpr.ac.in";
        in = new ByteArrayInputStream(input3.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret3 = acad.acadProcedures();
        assertEquals(" ", ret3);

        String input4 = "\n1\n";
        in = new ByteArrayInputStream(input4.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret4 = acad.acadProcedures();
        assertEquals(" ", ret4);

        String input5 = "\n2\n";
        in = new ByteArrayInputStream(input5.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret5 = acad.acadProcedures();
        assertEquals(" ", ret5);

        String input6 = "\n5\n0\n";
        in = new ByteArrayInputStream(input6.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret6 = acad.acadProcedures();
        assertEquals(" ", ret6);

        String input7 = "\n5\n0\n";
        in = new ByteArrayInputStream(input7.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret7 = acad.acadProcedures();
        assertEquals(" ", ret7);

        String input8 = "\n0\n4\n";
        in = new ByteArrayInputStream(input8.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret8 = acad.acadProcedures();
        assertEquals(" ", ret8);

        String input9 = "\n0\n2\n";
        in = new ByteArrayInputStream(input9.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret9 = acad.acadProcedures();
        assertEquals(" ", ret9);

        String input10 = "\n0\n3\nCS402\n";
        in = new ByteArrayInputStream(input10.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret10 = acad.acadProcedures();
        assertEquals(" ", ret10);

        String input11 = "\n0\n1\nCS101\ncs course f\n3\n1\n1\n4\n3.0 \nCSE\n1 \nCS102\n0 \n\n";
        in = new ByteArrayInputStream(input11.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret11 = acad.acadProcedures();
        assertEquals(" ", ret11);
    }

    @AfterAll
    static void originalDbState() throws SQLException {
        con.close();
    }
}