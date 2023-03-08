package org.swe.server;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class instructor {
    public String email;
    Connection con;

    public instructor(String email, Connection con) {
        this.email = email;
        this.con = con;
    }

    public int viewGrades(int courseId) throws SQLException {
//        String offeredCourses = "SELECT * FROM offered WHERE inst_email='"
//                + email + "' AND status=1;";
//        Statement stoc = con.createStatement();
//        ResultSet rsoc = stoc.executeQuery(offeredCourses);
//        int cnt = 0;
//        while (rsoc.next()) {
//            if (cnt == 0) {
//                System.out.println("List of courses offered by you: ");
//            }
//            cnt++;
//            System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
//        }
//        if (cnt == 0) {
//            System.out.println("No courses offered by you where grades are uploaded.");
//            return 0;
//        }
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Please enter offer ID of the course for which you want to view grades: ");
//        int courseId = sc.nextInt();
        String query = "SELECT * FROM grades WHERE offer_id=" + Integer.toString(courseId) + ";";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            String studentEmail = rs.getString("email");
            String course_code = rs.getString("course_code");
            String grade = rs.getString("grade");
            String query2 = "SELECT entry_num, name FROM student WHERE email='" + studentEmail + "';";
            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery(query2);
            rs2.next();
            String studentName = rs2.getString("name");
            String entryNum = rs2.getString("entry_num");
            System.out.println(studentName + " (" + entryNum + ") - " + course_code + ": " + grade);
            st2.close();
        }
        System.out.println("\n");
        st.close();
        return 1;
    }

    public int uploadGrades(String fileName, String offId) throws IOException, SQLException {
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
        if (rsdd.getInt("grade_status") == 0) {
            System.out.println("Grade upload is not allowed by academic office. Try again later.");
            return 0;
        }
        stdd.close();

        String invalidId = "SELECT * FROM offered WHERE offer_id=" + offId + " AND inst_email='" + email + "'";
        Statement stii = con.createStatement();
        ResultSet rsii = stii.executeQuery(invalidId);
        if (!rsii.next()) {
            System.out.println("Invalid course ID!");
            return 0;
        }
        stii.close();

        String f = "";
        int id = 0;
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter file name (eg. 'grades_1')");
//        String fileName = sc.nextLine();
//        String gradeFile = "C:\\Users\\DELL\\IdeaProjects\\mini_project\\" + fileName + ".csv";
        String gradeFile = "C:\\Users\\DELL\\IdeaProjects\\mini_project\\src\\main\\resources\\grades\\" + fileName + ".csv";
        if (!new File(gradeFile).exists()) {
            System.out.println("File does not exist.");
            return 0;
        }
        BufferedReader br = new BufferedReader(new FileReader(gradeFile));

        br.readLine();
        while ((f = br.readLine()) != null) {
            String[] grade = f.split(",");
            if (grade.length < 3) {
                System.out.println("Please upload grades for remaining students in the sheet and run again.");
                id = 0;
                break;
            }
//            System.out.println(grade[0] + grade[1] + grade[2]);
            int offerId = Integer.parseInt(grade[0]);
            id = offerId;
            String studentEmail = grade[1];
            String gradeObtained = grade[2];
            String courseEnroll = "SELECT * FROM offered where offer_id=" + Integer.toString(id) + ";";
            Statement stce = con.createStatement();
            ResultSet rsce = stce.executeQuery(courseEnroll);
            rsce.next();
            String code = rsce.getString("course_code");
            // insert into grades table
            String query = "INSERT INTO grades VALUES ('"
                    + studentEmail + "', "
                    + Integer.toString(offerId) + ", '"
                    + code + "', '" + gradeObtained + "');";
            Statement st = con.createStatement();
            st.executeUpdate(query);
            stce.close();
            st.close();
        }
        if (id != 0) {
            String endOffer = "UPDATE offered set status=1 WHERE offer_id="
                    + Integer.toString(id) + ";";
            Statement steo = con.createStatement();
            steo.executeUpdate(endOffer);
            steo.close();
            System.out.println("Grades successfully uploaded for course ID: " + id + "\n");
            return 1;
        }
        System.out.println("No grades to upload for course ID: " + id + "!\n");
        return 0;
    }

    public int downloadStudentDetails(int courseId) throws IOException, SQLException {
        Scanner sc = new Scanner(System.in);
        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();

//        String offeredCourses = "SELECT * FROM offered WHERE sem="
//                + Integer.toString(curSem) + " AND year="
//                + Integer.toString(curYear) + " AND inst_email='"
//                + email + "';";
//        Statement stoc = con.createStatement();
//        ResultSet rsoc = stoc.executeQuery(offeredCourses);
//        int cnt = 0;
//        while (rsoc.next()) {
//            if (cnt == 0) {
//                System.out.println("List of courses offered by you: ");
//            }
//            cnt++;
//            System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
//        }
//        if (cnt == 0) {
//            System.out.println("No courses offered by you.");
//            return 0;
//        }
//
//        System.out.println("Please enter offer ID for the course as per above list: ");
//        int courseId = sc.nextInt();
        String checkValid = "SELECT * FROM offered WHERE offer_id=" + Integer.toString(courseId) + ";";
        Statement stcv = con.createStatement();
        ResultSet rscv = stcv.executeQuery(checkValid);
        if (!rscv.next()) {
            System.out.println("Invalid course ID.");
            return 0;
        }
        stcv.close();

        String query = "SELECT * FROM register_student WHERE offer_id="
                + Integer.toString(courseId);

        String fileGrade = "C:\\Users\\DELL\\IdeaProjects\\mini_project\\src\\main\\resources\\grades\\grades_" + Integer.toString(courseId) + ".csv";
        FileWriter fw = new FileWriter(fileGrade);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("offer_id,email,grade");
        bw.newLine();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            bw.write(Integer.toString(courseId) + "," + rs.getString("email"));
            bw.newLine();
        }
        st.close();
        bw.close();
        System.out.println("Student details downloaded for course ID: " + courseId + "\n");
        return 1;
    }

    public int offerCourse(String code, float cgpaCriteria) throws SQLException {
        String timeFetch = "SELECT * FROM time_frame where status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");

        String checkDeadlines = "SELECT * FROM deadlines WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
        Statement stdd = con.createStatement();
        ResultSet rsdd = stdd.executeQuery(checkDeadlines);
        rsdd.next();
        if (rsdd.getInt("offer_status") == 0) {
            System.out.println("Course offering is not allowed by academic office. Try again later.");
            return 0;
        }
        stdd.close();

//        Scanner sc = new Scanner(System.in);
//        System.out.println("\nCourse Offering (" + curSem + ", " + curYear +")");
//        System.out.println("Please enter course code: ");
//        String code = sc.nextLine();
//        System.out.println("Enter CGPA criteria: ");
//        float cgpaCriteria = sc.nextFloat();
        String offerCheck = "SELECT * FROM offered WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + " AND course_code='" + code + "' AND inst_email='" + email + "';";
        Statement stoc = con.createStatement();
        ResultSet rsoc = stoc.executeQuery(offerCheck);
        if (rsoc.next()) {
            System.out.println("Course with same code already offered by you in the current semester.");
            return 0;
        }
        stoc.close();

        String checkCatalogQuery = "SELECT * FROM course WHERE code='" + code + "' AND status=1;";
        Statement stcc = con.createStatement();
        ResultSet rscc = stcc.executeQuery(checkCatalogQuery);
        if (!rscc.next()) {
            System.out.println("Course not found in catalog or deleted from catalog! Try again.");
            return 0;
        }
        stcc.close();

        String query = "INSERT INTO offered(course_code, inst_email, cgpa_cutoff, sem, year, status) " +
                "VALUES ('" +
                code + "', '" +
                email + "', " +
                Float.toString(cgpaCriteria) + ", " +
                Integer.toString(curSem) + ", " +
                Integer.toString(curYear) + ", 0);";
        Statement st = con.createStatement();
        st.executeUpdate(query);
        st.close();
        System.out.println("Course (" + code + ") successfully offered.\n");
        return 1;
    }

    public int removeOffer(String courseCode) throws SQLException {
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
        if (rsdd.getInt("offer_status") == 0) {
            System.out.println("Course offering removal is not allowed by academic office. Try again later.");
            return 0;
        }
        stdd.close();

//        String offeredCourses = "SELECT * FROM offered WHERE sem="
//                + Integer.toString(curSem) + " AND year="
//                + Integer.toString(curYear) + " AND inst_email='"
//                + email + "' AND status=0;";
//        Statement stoc = con.createStatement();
//        ResultSet rsoc = stoc.executeQuery(offeredCourses);
//        int cnt = 0;
//        while (rsoc.next()) {
//            if (cnt == 0) {
//                System.out.println("List of courses offered by you in the current semester (" + curSem + ", " + curYear + "): ");
//            }
//            cnt++;
//            System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
//        }
//        if(cnt == 0) {
//            System.out.println("No course offered by you.");
//
//        }
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter the offer ID of the course offering you want to deregister: ");
//        int offerId = sc.nextInt();
        String checkValid = "SELECT * FROM offered WHERE status=0 AND course_code='"
                + courseCode + "' AND inst_email='" + email
                + "' AND sem=" + Integer.toString(curSem)
                + " AND year=" + Integer.toString(curYear) + ";";
        Statement stcv = con.createStatement();
        ResultSet rscv = stcv.executeQuery(checkValid);
        if (!rscv.next()) {
            System.out.println("Invalid course code.");
            return 0;
        }
        int offerId = rscv.getInt("offer_id");

        String delEnrollments = "DELETE FROM register_student WHERE offer_id="
                + Integer.toString(offerId) + ";";
        String delOffering = "DELETE FROM offered WHERE offer_id="
                + Integer.toString(offerId) + ";";
        Statement stde = con.createStatement();
        stde.executeUpdate(delEnrollments);
        Statement stdo = con.createStatement();
        stdo.executeUpdate(delOffering);
        stde.close();
        stdo.close();
        stcv.close();
        System.out.println("Offering (" + offerId + ") successfully removed.\n");
        return 1;
    }
}

//    public String instProcedures() throws SQLException, IOException {
//        // view all grades -- * status
//        // register: offered --
//        // deregister course: remove from (offered, grades) ***
//        // update grades via .csv -*
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
//        System.out.println("Press 0 to view Grade of all Students");
//        System.out.println("Press 1 to Offer a Course");
//        System.out.println("Press 2 to Remove an Offering");
//        System.out.println("Press 3 to Download student details for an Offering");
//        System.out.println("Press 4 to update student grades (via .csv)");
//        System.out.println("Press 5 to go back");
//        int ch = sc.nextInt();
//        sc.nextLine();
//        if(ch == 0) {
//            String offeredCourses = "SELECT * FROM offered WHERE inst_email='"
//                    + email + "' AND status=1;";
//            Statement stoc = con.createStatement();
//            ResultSet rsoc = stoc.executeQuery(offeredCourses);
//            int cnt = 0;
//            while (rsoc.next()) {
//                if (cnt == 0) {
//                    System.out.println("List of courses offered by you of which grades are uploaded: ");
//                }
//                cnt++;
//                System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
//            }
//            if (cnt == 0) {
//                System.out.println("No courses offered by you where grades are uploaded.");
//            }
//            else{
//                System.out.println("Please enter offer ID of the course for which you want to view grades: ");
//                int courseId = sc.nextInt();
//                sc.nextLine();
//                viewGrades(courseId); // done
//            }
//        }
//        else if (ch == 1) {
//            System.out.println("\nCourse Offering (" + curSem + ", " + curYear +")");
//            System.out.println("Please enter course code: ");
//            String code = sc.nextLine();
//            System.out.println("Enter CGPA criteria: ");
//            float cgpaCriteria = sc.nextFloat();
//            offerCourse(code, cgpaCriteria); // done, ret
//        }
//        else if (ch == 2) {
//            String offeredCourses = "SELECT * FROM offered WHERE sem="
//                    + Integer.toString(curSem) + " AND year="
//                    + Integer.toString(curYear) + " AND inst_email='"
//                    + email + "' AND status=0;";
//            Statement stoc = con.createStatement();
//            ResultSet rsoc = stoc.executeQuery(offeredCourses);
//            int cnt = 0;
//            while (rsoc.next()) {
//                if (cnt == 0) {
//                    System.out.println("List of courses offered by you in the current semester (" + curSem + ", " + curYear + "): ");
//                }
//                cnt++;
//                System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
//            }
//            if(cnt == 0) {
//                System.out.println("No course offered by you.");
//            }
//            else {
//                System.out.println("Enter the course code of the course offering you want to deregister in the current semester: ");
////                String offerId = sc.nextInt();
////                sc.nextLine();
////                removeOffer(offerId); // done, ret
//                String courseCode = sc.nextLine();
//                removeOffer(courseCode);
//            }
//        }
//        else if(ch == 3) {
//            String offeredCourses = "SELECT * FROM offered WHERE sem="
//                    + Integer.toString(curSem) + " AND year="
//                    + Integer.toString(curYear) + " AND inst_email='"
//                    + email + "';";
//            Statement stoc = con.createStatement();
//            ResultSet rsoc = stoc.executeQuery(offeredCourses);
//            int cnt = 0;
//            while (rsoc.next()) {
//                if (cnt == 0) {
//                    System.out.println("List of courses offered by you: ");
//                }
//                cnt++;
//                System.out.println(rsoc.getInt("offer_id") + ": " + rsoc.getString("course_code"));
//            }
//            if (cnt == 0) {
//                System.out.println("No courses offered by you.");
//
//            }
//            else {
//                System.out.println("Please enter offer ID for the course as per above list: ");
//                int courseId = sc.nextInt();
//                sc.nextLine();
//                downloadStudentDetails(courseId); // done
//            }
//        }
//        else if (ch == 4) {
//            System.out.println("Enter file name (eg. 'grades_[Offer_ID]'): ");
//            String fileName = sc.nextLine();
//            String offerId = fileName.substring(7);
////            int id = Integer.parseInt(offerId);
//
//            uploadGrades(fileName, offerId); // done, ret
//        }
//        else if (ch == 5) {
//            System.out.println("BACK");
//            return "BACK";
//        }
//        else {
//            System.out.println("Invalid! Enter Valid Input as mentioned.");
//            return "Invalid! Enter Valid Input as mentioned.";
//        }
//        return " ";
//    }
//}
