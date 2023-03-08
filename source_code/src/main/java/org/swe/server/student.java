package org.swe.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class student {
    public String email;
    Connection con;

    public student(String email, Connection con) {
        this.email = email;
        this.con = con;
    }

    public int enrollCourse(int enrollOfferid) throws SQLException {
        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();

        String checkDeadlines = "SELECT * FROM deadlines WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
        Statement stdd = con.createStatement();
        ResultSet rsdd = stdd.executeQuery(checkDeadlines);
        rsdd.next();
        if (rsdd.getInt("enroll_status") == 0) {
            System.out.println("Enroll course is not allowed by academic office. Try again later.");
            return 0;
        }
        stdd.close();

//        String coursesOffered = "SELECT * FROM offered WHERE sem=" + Integer.toString(curSem) +" AND year=" + Integer.toString(curYear) + " AND status=0;";
//        Statement stco = con.createStatement();
//        ResultSet rsco = stco.executeQuery(coursesOffered);
//        System.out.println("Following course are offered in the current semester: ");
//        while (rsco.next()) {
//            int offerId = rsco.getInt("offer_id");
//            String code = rsco.getString("course_code");
//            String instructor = rsco.getString("inst_email");
//            System.out.println("ID: " + offerId + ": " + code + " [" + instructor + "] ");
//        }
//        stco.close();
//
//        System.out.println("Enter the offer ID to enroll in the course as per the given list above: ");
//        int enrollOfferid = sc.nextInt();
//        sc.nextLine();
        String courseEnroll = "SELECT * FROM offered where offer_id=" + Integer.toString(enrollOfferid) + ";";
        Statement stce = con.createStatement();
        ResultSet rsce = stce.executeQuery(courseEnroll);
        if (!rsce.next()) {
            System.out.println("Invalid offer ID!");
            return 0;
        }

        String code = rsce.getString("course_code");
        // checks - cgpa, already taken, prereq, credit limit
        double cgpa = calculateCGPA();
        double cgpaCutOff = rsce.getDouble("cgpa_cutoff");
        if (cgpa < cgpaCutOff) {
            System.out.println("Course " + code + " cannot be enrolled as your cgpa doesn't clear the cut off\n");
            return 0;
        }
        String checkTaken1 = "SELECT * FROM grades WHERE course_code='" + code + "' AND grade<>'F' " + " AND email='"
                + email + "';";
        Statement stct1 = con.createStatement();
        ResultSet rsct1 = stct1.executeQuery(checkTaken1);
        if (rsct1.next()) {
            System.out.println("Course " + code + " cannot be enrolled as you have already passed this course before.");
            return 0;
        }
        stct1.close();
        String checkTaken2 = "SELECT * FROM register_student WHERE offer_id=" + Integer.toString(enrollOfferid) + " AND email='"
                + email + "';";
        Statement stct2 = con.createStatement();
        ResultSet rsct2 = stct2.executeQuery(checkTaken2);
        if (rsct2.next()) {
            System.out.println("Course " + code + " cannot be enrolled as you are enrolled in it.");
            return 0;
        }
        stct2.close();

        // credit_limit
        String stuQuery = "SELECT * FROM student WHERE email='" + email + "';";
        Statement stust = con.createStatement();
        ResultSet sturs = stust.executeQuery(stuQuery);
        sturs.next();
        int stuSem = 2 * (curYear - sturs.getInt("year_joining"));
        if (curSem == 2) {
            stuSem++;
        }
        if (stuSem > 8) {
            stuSem = 8;
        }
        String creditQuery = "SELECT sem" + Integer.toString(stuSem) + " FROM credit_limit;";
        Statement stc = con.createStatement();
        ResultSet rsc = stc.executeQuery(creditQuery);
        rsc.next();
        int limit = rsc.getInt("sem" + Integer.toString(stuSem));
        int credits = 0;
        String countCredit = "SELECT * FROM register_student where email='" + email + "';";
        Statement stcc = con.createStatement();
        ResultSet rscc = stcc.executeQuery(countCredit);
        while (rscc.next()) {
            String course_code = rscc.getString("course_code");
            String fetchCred = "SELECT c FROM course WHERE code='" + course_code + "';";
            Statement sttm = con.createStatement();
            ResultSet rstm = sttm.executeQuery(fetchCred);
            rstm.next();
            credits += rstm.getInt("c");
            sttm.close();
        }
        if (credits > limit) {
            System.out.println("Course " + code + " cannot be enrolled as the credit limit (" + limit + ") for this semester has passed.");
            return 0;
        }
        // prereq
        String prereqCheck = "SELECT * FROM prereq WHERE course_code='" + code + "';";
        Statement stpc = con.createStatement();
        ResultSet rspc = stpc.executeQuery(prereqCheck);
        Boolean prereqOk = true;
        Map<String, Integer> gradeSg = new HashMap<>();
        gradeSg.put("A", 10);
        gradeSg.put("A-", 9);
        gradeSg.put("B", 8);
        gradeSg.put("B-", 7);
        gradeSg.put("C", 6);
        gradeSg.put("C-", 5);
        gradeSg.put("D", 4);
        gradeSg.put("E", 2);
        gradeSg.put("F", 0);

        while (rspc.next()) {
            String prereCode = rspc.getString("prereq_id");
            int grade = rspc.getInt("grade");
            String gradeCheck = "SELECT * FROM register_student WHERE email='" + email + "' AND course_code='" + prereCode + "';";
            Statement stgc = con.createStatement();
            ResultSet rsgc = stgc.executeQuery(gradeCheck);
            int cc = 0;
            while (rsgc.next()) {
                int offerId = rsgc.getInt("offer_id");
                String gradeOffer = "SELECT * FROM grades WHERE email='" + email + "' and offer_id=" + Integer.toString(offerId) + ";";
                Statement stgo = con.createStatement();
                ResultSet rsgo = stgo.executeQuery(gradeOffer);
                if (rsgo.next()) {
                    if (gradeSg.get(rsgo.getString("grade")) >= grade) {
                        cc++;
                    }
                }
                stgo.close();
            }
            if (cc == 0) {
                prereqOk = false;
                break;
            }
        }
        if (!prereqOk) {
            System.out.println("Course " + code + " cannot be enrolled as the pre-requisite criteria is not satisfied.");
            return 0;
        }
        String enrollCourse = "INSERT INTO register_student VALUES('" + email + "', " + Integer.toString(enrollOfferid) + ", '" + code + "', " + Integer.toString(curSem) + ", " + Integer.toString(curYear) + ");";
        Statement enrollFinal = con.createStatement();
        enrollFinal.executeUpdate(enrollCourse);
        System.out.println("Course (" + enrollOfferid + ") successfully enrolled.\n");

        stust.close();
        stc.close();
        stcc.close();
        stpc.close();

        return 1;
    }

    public int dropCourse(int dropOfferId) throws SQLException {
        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();

        String checkDeadlines = "SELECT * FROM deadlines WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
        Statement stdd = con.createStatement();
        ResultSet rsdd = stdd.executeQuery(checkDeadlines);
        rsdd.next();
        if (rsdd.getInt("enroll_status") == 0) {
            System.out.println("Drop course is not allowed by academic office. Try again later.");
            return 0;
        }
        stdd.close();

//        String studentDetails = "SELECT * FROM student WHERE email='" + email + "';";
//        Statement sttd = con.createStatement();
//        ResultSet rstd = sttd.executeQuery(studentDetails);
//        rstd.next();

//        String dropOptions = "SELECT * FROM register_student WHERE email='"
//                + email +"' AND sem=" + Integer.toString(curSem)
//                + " AND year=" + Integer.toString(curYear) + ";";
//        Statement stdo = con.createStatement();
//        ResultSet rsdo = stdo.executeQuery(dropOptions);
//        while (rsdo.next()) {
//            int offerId = rsdo.getInt("offer_id");
//            String checkId = "SELECT status FROM offered where offer_id="+Integer.toString(offerId)+";";
//            Statement stci = con.createStatement();
//            ResultSet rsci = stci.executeQuery(checkId);
//            rsci.next();
//            if(rsci.getInt("status") == 1) {
//                continue;
//            }
//            String code = rsdo.getString("course_code");
//            System.out.println("ID: " + offerId + ": " + code);
//        }
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Please enter the offer ID of the course you want to drop in the current semester as per the list above: ");
//        int dropOfferId = sc.nextInt();
//        sc.nextLine();
        String checkId = "SELECT * FROM register_student WHERE offer_id=" + Integer.toString(dropOfferId) + " AND email='" + email + "' AND sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
        Statement stce = con.createStatement();
        ResultSet rsce = stce.executeQuery(checkId);
        String checkGrade = "SELECT * FROM grades WHERE offer_id=" + Integer.toString(dropOfferId) + " AND email='" + email + "';";
        Statement stcg = con.createStatement();
        ResultSet rscg = stcg.executeQuery(checkGrade);
        if (!rsce.next() || rscg.next()) {
            System.out.println("Invalid offer ID or Grades already submitted.");
            return 0;
        }
        stce.close();
        stcg.close();

        String dropCourse = "DELETE FROM register_student WHERE sem="
                + Integer.toString(curSem) + " AND year="
                + Integer.toString(curYear) + " AND email='"
                + email + "' AND offer_id=" + Integer.toString(dropOfferId) + ";";
        Statement stdc = con.createStatement();
        stdc.executeUpdate(dropCourse);
        System.out.println("Course (" + dropOfferId + ") successfully dropped.\n");
        stdc.close();

        return 1;
    }


    public double calculateCGPA() throws SQLException {
        double totalCredits = 0;
        double totalWeightage = 0;
        String query = "SELECT * FROM grades WHERE email='" + email + "';";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            int offer_id = rs.getInt("offer_id");
            String query1 = "SELECT * FROM offered WHERE offer_id=";
            query1 += Integer.toString(offer_id);
            query1 += ";";
            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery(query1);
            rs1.next();
            String course_code = rs1.getString("course_code");
            String query2 = "SELECT * FROM course WHERE code='" + course_code + "';";
            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery(query2);
            rs2.next();
            double cred = rs2.getInt("c");
            totalCredits += cred;
            String grade = rs.getString("grade");
            switch (grade) {
                case "A" -> totalWeightage += cred * 10;
                case "A-" -> totalWeightage += cred * 9;
                case "B" -> totalWeightage += cred * 8;
                case "B-" -> totalWeightage += cred * 7;
                case "C" -> totalWeightage += cred * 6;
                case "C-" -> totalWeightage += cred * 5;
                case "D" -> totalWeightage += cred * 4;
//                case "E" -> totalWeightage += cred * 2;
                case "F" -> totalWeightage += cred * 0;
            }
            st1.close();
            st2.close();
        }
        if (totalCredits == 0) {
            return 0;
        }
        st.close();
        return (totalWeightage / totalCredits);
    }

    public int viewGrades() throws SQLException {
        String query = "SELECT * FROM grades WHERE email='" + email + "';";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {

            String grade = rs.getString("grade");
            int offer_id = rs.getInt("offer_id");
            String query1 = "SELECT * FROM offered WHERE offer_id=" + Integer.toString(offer_id) + ";";
            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery(query1);
            rs1.next();
            String course_code = rs1.getString("course_code");
            String query2 = "SELECT entry_num, name FROM student WHERE email='" + email + "';";
            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery(query2);
            rs2.next();
            String studentName = rs2.getString("name");
            String entryNum = rs2.getString("entry_num");
            System.out.println(studentName + " (" + entryNum + ") - " + course_code + ": " + grade);
            st1.close();
            st2.close();
        }
        double cgpa = calculateCGPA();
        System.out.println("CGPA: " + cgpa + "\n");
        st.close();
        return 1;
    }
}

//    public String studentProcedures() throws SQLException {
//        // enroll checks: prereq, credit limit, cgpa criteria, already taken before
//        // drop: tables(register_student, ticket)
//        // view grades --
//        // cgpa --
//
//        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
//        Statement tss = con.createStatement();
//        ResultSet trs = tss.executeQuery(timeFetch);
//        trs.next();
//        int curSem = trs.getInt("sem");
//        int curYear = trs.getInt("year");
//        tss.close();
//
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Press 0 to Enroll in a Course");
//        System.out.println("Press 1 to Drop from a Course");
//        System.out.println("Press 2 to view your Grades");
//        System.out.println("Press 3 to compute your current CGPA");
//        System.out.println("Press 4 to go back");
//        int ch = sc.nextInt();
//        sc.nextLine();
//        if(ch == 0) {
//            String coursesOffered = "SELECT * FROM offered WHERE sem=" + Integer.toString(curSem) +" AND year=" + Integer.toString(curYear) + " AND status=0;";
//            Statement stco = con.createStatement();
//            ResultSet rsco = stco.executeQuery(coursesOffered);
//            System.out.println("Following course are offered in the current semester: ");
//            while (rsco.next()) {
//                int offerId = rsco.getInt("offer_id");
//                String code = rsco.getString("course_code");
//                String instructor = rsco.getString("inst_email");
//                System.out.println("ID: " + offerId + ": " + code + " [" + instructor + "] ");
//            }
//            stco.close();
//
//            System.out.println("Enter the offer ID to enroll in the course as per the given list above: ");
//            int enrollOfferid = sc.nextInt();
//            sc.nextLine();
//            enrollCourse(enrollOfferid); // done, ret
//        }
//        else if (ch == 1) {
//            String dropOptions = "SELECT * FROM register_student WHERE email='"
//                    + email +"' AND sem=" + Integer.toString(curSem)
//                    + " AND year=" + Integer.toString(curYear) + ";";
//            Statement stdo = con.createStatement();
//            ResultSet rsdo = stdo.executeQuery(dropOptions);
//            while (rsdo.next()) {
//                int offerId = rsdo.getInt("offer_id");
//                String checkId = "SELECT status FROM offered where offer_id="+Integer.toString(offerId)+";";
//                Statement stci = con.createStatement();
//                ResultSet rsci = stci.executeQuery(checkId);
//                rsci.next();
//                if(rsci.getInt("status") == 1) {
//                    continue;
//                }
//                String code = rsdo.getString("course_code");
//                System.out.println("ID: " + offerId + ": " + code);
//            }
////            Scanner sc = new Scanner(System.in);
//            System.out.println("Please enter the offer ID of the course you want to drop in the current semester as per the list above: ");
//            int dropOfferId = sc.nextInt();
//            sc.nextLine();
//            dropCourse(dropOfferId); // done, ret
//        }
//        else if (ch == 2) {
//            viewGrades(); // done
//        }
//        else if (ch == 3) {
//            double cgpa = calculateCGPA(); // done
//            System.out.println("CGPA: " + cgpa);
//        }
//        else if (ch == 4) {
//            System.out.println("BACK");
//            return "BACK";
//        }
//        else {
//            System.out.println("Invalid! Enter Valid Input as mentioned.");
//            return "Invalid! Enter Valid Input as mentioned.";
//        }
//        return " ";
//    }

