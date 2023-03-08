package org.swe;

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
import java.sql.Statement;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    static Connection con = null;

    @BeforeAll
    static void initDb() throws ClassNotFoundException, SQLException {
        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection(url, username, password);

    }

    @Test
    void simulateLogIn() throws SQLException, IOException, ClassNotFoundException {
//        String input = "\nstudent0@iitrpr.ac.in\nstudent1@iitrpr.ac.in\npassword0\nstudent1@iitrpr.ac.in\npassword1\n1 \npassword1\npassword1\n2 \n1234567890\n4 \n3\n4\n0 \nterminate\n";
        String input = "\nstudent0@iitrpr.ac.in\nstudent1@iitrpr.ac.in\npassword0\nstudent1@iitrpr.ac.in\npassword1\n1 \npassword1\npassword1\n2 \n1234567890\n4 \n0 \nterminate\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        System.setOut(ps);
        String ret = Main.main();
        assertEquals("0", ret);

        String input1 = "\ninst1@iitrpr.ac.in\npassword1\n0 \nterminate\n";
        in = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret1 = Main.main();
        assertEquals("0", ret1);

        String input2 = "\nacadoffice@iitrpr.ac.in\npassword1\n0 \nterminate\n";
        in = new ByteArrayInputStream(input2.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        String ret2 = Main.main();
        assertEquals("0", ret2);

        String input3 = "\nacadoffice@iitrpr.ac.in\npassword1\n3 \n6 \n0 \nterminate\n";
        in = new ByteArrayInputStream(input3.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        assertThrows(RuntimeException.class, Main::main);

        // coverage purpose only
        String input4 = "\nstudent1@iitrpr.ac.in\npassword1\n3 \n4 \n0 \nterminate\n";
        in = new ByteArrayInputStream(input4.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        assertThrows(RuntimeException.class, Main::main);

        String input5 = "\ninst1@iitrpr.ac.in\npassword1\n3 \n5 \n0 \nterminate\n";
        in = new ByteArrayInputStream(input5.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        assertThrows(RuntimeException.class, Main::main);


        String input6 = "\nterminate\n";
        in = new ByteArrayInputStream(input6.getBytes());
        System.setIn(in);
        out = new ByteArrayOutputStream();
        ps = new PrintStream(out);
        System.setOut(ps);
        Main.main(null);
    }

    @Test
    void changePassword() throws SQLException {
        String stuEmail = "student2@iitrpr.ac.in";
        int check1 = Main.changePassword(stuEmail, con, "password1", "password1");
        int check2 = Main.changePassword(stuEmail, con, "password2", "password1");
        assertEquals(0, check1);
        assertEquals(1, check2);
    }

    @Test
    void changeContact() throws SQLException {
        String stuEmail = "student2@iitrpr.ac.in";
        int check1 = Main.changeContact(stuEmail, con, "1234567891");
        assertEquals(1, check1);
    }

    @AfterAll
    static void originalDbState() throws SQLException {
        String pass = "UPDATE auth SET password='password2' WHERE email='student2@iitrpr.ac.in';";
        Statement stp = con.createStatement();
        stp.executeUpdate(pass);
        con.close();
    }
}