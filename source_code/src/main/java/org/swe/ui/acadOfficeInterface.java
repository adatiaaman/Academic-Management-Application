package org.swe.ui;

import org.swe.server.acadOffice;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class acadOfficeInterface {
    public String email;
    Connection con;
    public acadOfficeInterface(String email, Connection con){
        this.email = email;
        this.con = con;
    }
    public String acadProcedures() throws SQLException, IOException {
        // edit course catalog --
        // view all grades --
        // generate student transcript (.txt) (marksheet)

        acadOffice dean = new acadOffice(email, con);

        Scanner sc = new Scanner(System.in);

        System.out.println("Press 0 to Edit Course Catalog");
        System.out.println("Press 1 to view Grade of all Students");
        System.out.println("Press 2 to Generate Transcript of Students");
        System.out.println("Press 3 to end the current Semester");
        System.out.println("Press 4 to check if a student has graduated");
        System.out.println("Press 5 to change deadlines for offering, enrolling or grades upload");
        System.out.println("Press 6 to go back");

        int ch = sc.nextInt();
        sc.nextLine();

        if(ch == 0) {
            System.out.println("Press 1 to add course\nPress 2 to view course catalog\nPress 3 to delete a course from catalog");
            int catalog = sc.nextInt(); // update course catalog
            sc.nextLine();
            if (catalog == 1) {
                System.out.println("Enter course code: ");
                String code = sc.nextLine();
                System.out.println(code + "\n" + "Enter course name: ");
                String name = sc.nextLine();
                System.out.println(name + "\n" +"Enter lecture: ");
                int l = sc.nextInt();
                System.out.println("Enter tutorial: ");
                int t = sc.nextInt();
                System.out.println("Enter practical: ");
                int p = sc.nextInt();
                System.out.println("Enter study hours: ");
                int s = sc.nextInt();
                System.out.println("Enter credits: ");
                double c = sc.nextDouble();
                sc.nextLine();
                System.out.println("Enter course department: ");
                String dept = sc.nextLine();
                System.out.println("How many pre-requisites does this course have?");
                int numPrereq = sc.nextInt();
                sc.nextLine();
                String[] preCode = new String[numPrereq];
                int[] preGrade = new int[numPrereq];
                for(int i=0; i<numPrereq; i++) {
                    System.out.println("Please enter course code (" + (i+1) + "): ");
                    preCode[i] = sc.nextLine();
                    System.out.println("Please enter pre-requisite grade requirement [10/9/8/7/6/5] (enter 0 if no grade criteria): ");
                    preGrade[i] = sc.nextInt();
                    sc.nextLine();
                }
                dean.addCourseCatalog(code, name, l, t, p, s, c, dept, preCode, preGrade, numPrereq);
            }
            else if (catalog == 2) { // done
                dean.readCourseCatalog();
            }
            else if (catalog == 3) {
                System.out.println("Enter course code: ");
                String code = sc.nextLine();
                dean.deleteCourseCatalog(code);
            }
            else {
                System.out.println("Invalid input! Please try again.");
            }
//            editCourseCatalog(); // done, ret
        }
        else if (ch == 1) {
            dean.viewGrades(); // done
        }
        else if (ch == 2) {
            dean.generateTranscript(); // done
        }
        else if (ch == 3) {
            // start sem
            dean.endSemester(); // done, ret
        }
        else if (ch == 4) { // done, ret
            System.out.println("Enter student email: ");
            String stuEmail = sc.nextLine();
            dean.checkGraduation(stuEmail);

        }
        else if (ch == 5) { // done, ret
            System.out.println("Press 0 to change deadline for offering course");
            System.out.println("Press 1 to change deadline for enrolling course");
            System.out.println("Press 2 to change deadline for uploading grades");
            int check = sc.nextInt();
            sc.nextLine();
            dean.changeDeadlines(check);

        }
        else if (ch == 6) {
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
