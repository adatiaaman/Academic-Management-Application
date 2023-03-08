package org.swe.ui;

import org.swe.server.student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class studentInterface {
    public String email;
    Connection con;
    public studentInterface(String email, Connection con){
        this.email = email;
        this.con = con;
    }
    public String studentProcedures() throws SQLException {
        // enroll checks: prereq, credit limit, cgpa criteria, already taken before
        // drop: tables(register_student, ticket)
        // view grades --
        // cgpa --

        student stud = new student(email, con);

        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();

        Scanner sc = new Scanner(System.in);

        System.out.println("Press 0 to Enroll in a Course");
        System.out.println("Press 1 to Drop from a Course");
        System.out.println("Press 2 to view your Grades");
        System.out.println("Press 3 to compute your current CGPA");
        System.out.println("Press 4 to go back");
        int ch = sc.nextInt();
        sc.nextLine();

        if(ch == 0) {
            String coursesOffered = "SELECT * FROM offered WHERE sem=" + Integer.toString(curSem) +" AND year=" + Integer.toString(curYear) + " AND status=0;";
            Statement stco = con.createStatement();
            ResultSet rsco = stco.executeQuery(coursesOffered);
            System.out.println("Following course are offered in the current semester: ");
            while (rsco.next()) {
                int offerId = rsco.getInt("offer_id");
                String code = rsco.getString("course_code");
                String instructor = rsco.getString("inst_email");
                System.out.println("ID: " + offerId + ": " + code + " [" + instructor + "] ");
            }
            stco.close();

            System.out.println("Enter the offer ID to enroll in the course as per the given list above: ");
            int enrollOfferid = sc.nextInt();
            sc.nextLine();
            stud.enrollCourse(enrollOfferid); // done, ret
        }
        else if (ch == 1) {
            String dropOptions = "SELECT * FROM register_student WHERE email='"
                    + email +"' AND sem=" + Integer.toString(curSem)
                    + " AND year=" + Integer.toString(curYear) + ";";
            Statement stdo = con.createStatement();
            ResultSet rsdo = stdo.executeQuery(dropOptions);
            while (rsdo.next()) {
                int offerId = rsdo.getInt("offer_id");
                String checkId = "SELECT status FROM offered where offer_id="+Integer.toString(offerId)+";";
                Statement stci = con.createStatement();
                ResultSet rsci = stci.executeQuery(checkId);
                rsci.next();
                if(rsci.getInt("status") == 1) {
                    continue;
                }
                String code = rsdo.getString("course_code");
                System.out.println("ID: " + offerId + ": " + code);
            }
            stdo.close();
//            Scanner sc = new Scanner(System.in);
            System.out.println("Please enter the offer ID of the course you want to drop in the current semester as per the list above: ");
            int dropOfferId = sc.nextInt();
            sc.nextLine();
            stud.dropCourse(dropOfferId); // done, ret
        }
        else if (ch == 2) {
            stud.viewGrades(); // done
        }
        else if (ch == 3) {
            double cgpa = stud.calculateCGPA(); // done
            System.out.println("CGPA: " + cgpa);
        }
        else if (ch == 4) {
            System.out.println("BACK");
            return "BACK";
        }
        else {
            System.out.println("Invalid! Enter Valid Input as mentioned.");
            return "Invalid! Enter Valid Input as mentioned.";
        }
        return " ";
    }
}

