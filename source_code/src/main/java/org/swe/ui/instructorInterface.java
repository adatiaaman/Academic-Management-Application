package org.swe.ui;

import org.swe.server.instructor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class instructorInterface {
    public String email;
    Connection con;

    public instructorInterface(String email, Connection con) {
        this.email = email;
        this.con = con;
    }

    public String instProcedures() throws SQLException, IOException {
        // view all grades -- * status
        // register: offered --
        // deregister course: remove from (offered, grades) ***
        // update grades via .csv -*

        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();

        instructor inst = new instructor(email, con);

        Scanner sc = new Scanner(System.in);

        System.out.println("Press 0 to view Grade of all Students");
        System.out.println("Press 1 to Offer a Course");
        System.out.println("Press 2 to Remove an Offering");
        System.out.println("Press 3 to Download student details for an Offering");
        System.out.println("Press 4 to update student grades (via .csv)");
        System.out.println("Press 5 to go back");

        int ch = sc.nextInt();
        sc.nextLine();

        if(ch == 0) {
            String offeredCourses = "SELECT * FROM offered WHERE inst_email='"
                    + email + "' AND status=1;";
            Statement stoc = con.createStatement();
            ResultSet rsoc = stoc.executeQuery(offeredCourses);
            int cnt = 0;
            while (rsoc.next()) {
                if (cnt == 0) {
                    System.out.println("List of courses offered by you of which grades are uploaded: ");
                }
                cnt++;
                System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
            }
            if (cnt == 0) {
                System.out.println("No courses offered by you where grades are uploaded.");
            }
            else{
                System.out.println("Please enter offer ID of the course for which you want to view grades: ");
                int courseId = sc.nextInt();
                sc.nextLine();
                inst.viewGrades(courseId); // done
            }
            stoc.close();
        }
        else if (ch == 1) {
            System.out.println("\nCourse Offering (" + curSem + ", " + curYear +")");
            System.out.println("Please enter course code: ");
            String code = sc.nextLine();
            System.out.println("Enter CGPA criteria: ");
            float cgpaCriteria = sc.nextFloat();
            inst.offerCourse(code, cgpaCriteria); // done, ret
        }
        else if (ch == 2) {
            String offeredCourses = "SELECT * FROM offered WHERE sem="
                    + Integer.toString(curSem) + " AND year="
                    + Integer.toString(curYear) + " AND inst_email='"
                    + email + "' AND status=0;";
            Statement stoc = con.createStatement();
            ResultSet rsoc = stoc.executeQuery(offeredCourses);
            int cnt = 0;
            while (rsoc.next()) {
                if (cnt == 0) {
                    System.out.println("List of courses offered by you in the current semester (" + curSem + ", " + curYear + "): ");
                }
                cnt++;
                System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
            }
            if(cnt == 0) {
                System.out.println("No course offered by you.");
            }
            else {
                System.out.println("Enter the course code of the course offering you want to deregister in the current semester: ");
//                String offerId = sc.nextInt();
//                sc.nextLine();
//                removeOffer(offerId); // done, ret
                String courseCode = sc.nextLine();
                inst.removeOffer(courseCode);
            }
            stoc.close();
        }
        else if(ch == 3) {
            String offeredCourses = "SELECT * FROM offered WHERE sem="
                    + Integer.toString(curSem) + " AND year="
                    + Integer.toString(curYear) + " AND inst_email='"
                    + email + "';";
            Statement stoc = con.createStatement();
            ResultSet rsoc = stoc.executeQuery(offeredCourses);
            int cnt = 0;
            while (rsoc.next()) {
                if (cnt == 0) {
                    System.out.println("List of courses offered by you: ");
                }
                cnt++;
                System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
            }
            if (cnt == 0) {
                System.out.println("No courses offered by you.");

            }
            else {
                System.out.println("Please enter offer ID for the course as per above list: ");
                int courseId = sc.nextInt();
                sc.nextLine();
                inst.downloadStudentDetails(courseId); // done
            }
            stoc.close();
        }
        else if (ch == 4) {
            System.out.println("Enter file name (eg. 'grades_[Offer_ID]'): ");
            String fileName = sc.nextLine();
            String offerId = fileName.substring(7);
//            int id = Integer.parseInt(offerId);

            inst.uploadGrades(fileName, offerId); // done, ret
        }
        else if (ch == 5) {
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

