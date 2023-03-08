package org.swe.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class acadOffice {
    public String email;
    Connection con;

    public acadOffice(String email, Connection con) {
        this.email = email;
        this.con = con;
    }

    public int viewGrades() throws SQLException {
        String getId = "SELECT offer_id FROM offered;";
        Statement stgi = con.createStatement();
        ResultSet rsgi = stgi.executeQuery(getId);
        while (rsgi.next()) {
            int id = rsgi.getInt("offer_id");
            System.out.println("Offer ID: " + id);
            String getGrade = "SELECT * FROM grades WHERE offer_id=" + Integer.toString(id) + ";";
            Statement stgg = con.createStatement();
            ResultSet rsgg = stgg.executeQuery(getGrade);
            while (rsgg.next()) {
                String studentEmail = rsgg.getString("email");
                String course_code = rsgg.getString("course_code");
                String grade = rsgg.getString("grade");
                String query2 = "SELECT entry_num, name FROM student WHERE email='" + studentEmail + "';";
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery(query2);
                rs2.next();
                String studentName = rs2.getString("name");
                String entryNum = rs2.getString("entry_num");
                System.out.println(studentName + " (" + entryNum + ") - " + course_code + ": " + grade);
            }
            stgg.close();
            System.out.println("\n");
        }
        stgi.close();
        System.out.println("\n");
        return 1;
    }

    public int addCourseCatalog(String code, String name, int l, int t, int p, int s, double c, String dept, String[] preCode, int[] preGrade, int numPrereq) throws SQLException {
        String check = "SELECT * FROM course WHERE code='" + code + "';";
        Statement stch = con.createStatement();
        ResultSet rsch = stch.executeQuery(check);
        if (rsch.next()) {
            if (rsch.getInt("status") == 1) {
                System.out.println("Catalog already has a course with same code! Try updating instead.");
                return 0;
            } else {
                String query = " UPDATE course SET status=1 WHERE code='" + code + "';";
                Statement st = con.createStatement();
                st.executeUpdate(query);
                System.out.println("Course was previously deleted. Hence, it is again uploaded with previous l-t-p-s-c and pre-requisite structure.");
                return 1;
            }
        }
        Statement st = con.createStatement();
        String query = "INSERT INTO course VALUES('"
                + code + "', '" +
                name + "', " +
                Integer.toString(l) + ", " +
                Integer.toString(t) + ", " +
                Integer.toString(p) + ", " +
                Integer.toString(s) + ", " +
                Double.toString(c) + ", '" +
                dept + "', 1);";
        System.out.println(query);
        st.executeUpdate(query);

        for (int i = 0; i < numPrereq; i++) {
            String prereqCode = preCode[i];
            int prereqGrade = preGrade[i];
            String prereqQuery = "INSERT INTO prereq VALUES('" + code + "', '" + prereqCode + "', '" + Integer.toString(prereqGrade) + "');";
            Statement st1 = con.createStatement();
            st1.executeUpdate(prereqQuery);
            st1.close();
        }
        stch.close();
        st.close();
        System.out.println("Course added to the course catalog.\n");
        return 1;

    }

    //    public int updateCourseCatalog() throws SQLException {}
    public int deleteCourseCatalog(String code) throws SQLException {
//        System.out.println("Enter course code: ");
//        String code = sc.nextLine();
        String checkCatalogQuery = "SELECT * FROM course WHERE code='" + code + "' AND status=1;";
        Statement stcc = con.createStatement();
        ResultSet rscc = stcc.executeQuery(checkCatalogQuery);
        if (!rscc.next()) {
            System.out.println("Course already deleted from catalot or not found in catalog! Try again.");
            return 0;
        }
        stcc.close();

        String delCourse = "UPDATE course SET status=0 WHERE code='" + code + "';";
        Statement stdc = con.createStatement();
        stdc.executeUpdate(delCourse);
        System.out.println("Course removed from catalog (It can be added again with same l-t-p-s-c and prerequisite structure).");
        System.out.println("Instructors can't offer course with this course code.\n");
        stdc.close();

        return 1;
    }

    public int readCourseCatalog() throws SQLException {
        String readCatalog = "SELECT * FROM course;";
        Statement strc = con.createStatement();
        ResultSet rsrc = strc.executeQuery(readCatalog);
        while (rsrc.next()) {
            System.out.println("Course: " + rsrc.getString("code") + " (" + rsrc.getString("name") + "): "
                    + rsrc.getInt("l") + "-" + rsrc.getInt("t") + "-" + rsrc.getInt("p") + "-" + rsrc.getInt("s") + "-" + rsrc.getInt("c")
                    + " - " + rsrc.getString("dept"));
        }
        strc.close();
        return 1;
    }

//    public int editCourseCatalog() throws SQLException {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Press 0 to add course\nPress 1 to update course\nPress 2 to view course catalog\nPress 3 to delete a course from catalog");
//        int ch = sc.nextInt();
//        sc.nextLine();
//        if(ch == 0) { // add
    // code, name, ltpsc, dept
//            sc.nextLine();
//            System.out.println("Enter course code: ");
//            String code = sc.nextLine();
//            String check = "SELECT * FROM course WHERE code='" + code +"';";
//            Statement stch = con.createStatement();
//            ResultSet rsch = stch.executeQuery(check);
//            if(rsch.next()) {
//                if(rsch.getInt("status") == 1) {
//                    System.out.println("Catalog already has a course with same code! Try updating instead.");
//                    return 0;
//                }
//                else {
//                    System.out.println("Course was previously deleted. Hence, it is again uploaded with previous l-t-p-s-c and pre-requisite structure");
//                    String query = " UPDATE course SET status=1 WHERE code='" + code +"';";
//                    Statement st = con.createStatement();
//                    st.executeUpdate(query);
//                    return 1;
//                }
//            }

//            System.out.println(code + "\n" + "Enter course name: ");
//            String name = sc.nextLine();
//            System.out.println(name + "\n" +"Enter lecture: ");
//            int l = sc.nextInt();
//            System.out.println("Enter tutorial: ");
//            int t = sc.nextInt();
//            System.out.println("Enter practical: ");
//            int p = sc.nextInt();
//            System.out.println("Enter study hours: ");
//            int s = sc.nextInt();
//            System.out.println("Enter credits: ");
//            float c = sc.nextFloat();
//            sc.nextLine();
//            System.out.println("Enter course department: ");
//            String dept = sc.nextLine();

//            Statement st = con.createStatement();
//            String query = "INSERT INTO course VALUES('"
//                    + code + "', '" +
//                    name + "', " +
//                    Integer.toString(l) + ", " +
//                    Integer.toString(t) + ", " +
//                    Integer.toString(p) + ", " +
//                    Integer.toString(s) + ", " +
//                    Float.toString(c) + ", '" +
//                    dept + "');";
//            System.out.println(query);
//            st.executeUpdate(query);

//            System.out.println("How many pre-requisites does this course have?");
//            int numPrereq = sc.nextInt();
//            sc.nextLine();
//            for(int i=0; i<numPrereq; i++) {
//                System.out.println("Please enter course code (" + (i+1) + "): ");
//                String prereqCode = sc.nextLine();
//                System.out.println("Please enter pre-requisite grade requirement [10/9/8/7/6/5] (enter 0 if no grade criteria): ");
//                int prereqGrade = sc.nextInt();
//                sc.nextLine();
//                String prereqQuery = "INSERT INTO prereq VALUES('";
//                prereqQuery += code + "', '" + prereqCode + "', '" + Integer.toString(prereqGrade) + "');";
//                Statement st1 = con.createStatement();
//                st1.executeUpdate(prereqQuery);
//            }
//            System.out.println("Course added to the course catalog.\n");
//            return 1;
//        }
//        else if (ch == 1) { // update - think about previously offered course and their enrollments
//            sc.nextLine();
//            System.out.println("Please enter course code for the course to be updated: ");
//            String code = sc.nextLine();
//            String findQuery = "SELECT * FROM course WHERE code='" + code +"' AND status=1;";
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery(findQuery);
//            if(!rs.next()){
//                System.out.println("Course does not exist in catalog (or deleted before)! Try again.");
//                return 0;
//            }
//            System.out.println(name + "\n" +"Enter lecture: ");
//            int l = sc.nextInt();
//            System.out.println("Enter tutorial: ");
//            int t = sc.nextInt();
//            System.out.println("Enter practical: ");
//            int p = sc.nextInt();
//            System.out.println("Enter study hours: ");
//            int s = sc.nextInt();
//            System.out.println("Enter credits: ");
//            float c = sc.nextFloat();
//            sc.nextLine();
//            String updateCourse = "UPDATE course SET l="

//            String change = "l";
//            while(!change.equals("x")) {
//                System.out.println("Please enter (l/t/p/s) to be changed (Enter x to finish/quit): ");
//
//                change = sc.nextLine();
//                Statement stc = con.createStatement();
//                if(change.equals("l")) {
//                    System.out.println("Enter new (lectures) l: ");
//                    int newVal = sc.nextInt();
//                    sc.nextLine();
//                    String changeQuery = "UPDATE course SET l=" + Integer.toString(newVal) + " WHERE code='" + code + "';";
//
//                    stc.executeUpdate(changeQuery);
//                }
//                else if (change.equals("t")) {
//                    System.out.println("Enter new (tutorials) t: ");
//                    int newVal = sc.nextInt();
//                    sc.nextLine();
//                    String changeQuery = "UPDATE course SET t=" + Integer.toString(newVal) + " WHERE code='" + code + "';";
//
//                    stc.executeUpdate(changeQuery);
//                }
//                else if (change.equals("p")) {
//                    System.out.println("Enter new (practicals) p: ");
//                    int newVal = sc.nextInt();
//                    sc.nextLine();
//                    String changeQuery = "UPDATE course SET p=" + Integer.toString(newVal) + " WHERE code='" + code + "';";
//
//                    stc.executeUpdate(changeQuery);
//                }
//                else if (change.equals("s")) {
//                    System.out.println("Enter new (study hours) s: ");
//                    int newVal = sc.nextInt();
//                    sc.nextLine();
//                    String changeQuery = "UPDATE course SET s=" + Integer.toString(newVal) + " WHERE code='" + code + "';";
//
//                    stc.executeUpdate(changeQuery);
//                }
////                else if (change.equals("c")) {
////                    System.out.println("Enter new (credits) c=: ");
////                    float newVal = sc.nextFloat();
////                    String changeQuery = "UPDATE course SET c=" + Float.toString(newVal) + " WHERE code='" + code + "';";
////
////                    stc.executeUpdate(changeQuery);
////                }
//                else if (change.equals("x")) {
//                    System.out.println("Course details updated");
//                }
//                else {
//                    System.out.println("Invalid! Try again");
//                }
//                stc.close();
//            }

//            return 1;
//        }
//        else if (ch == 2) {
//            String readCatalog = "SELECT * FROM course;";
//            Statement strc = con.createStatement();
//            ResultSet rsrc = strc.executeQuery(readCatalog);
//            while (rsrc.next()) {
//                System.out.println("Course: " + rsrc.getString("code") + " (" + rsrc.getString("name") + "): "
//                        + rsrc.getInt("l") + "-" + rsrc.getInt("t") + "-" + rsrc.getInt("p") + "-" + rsrc.getInt("s") + "-" + rsrc.getInt("c")
//                        + " - " + rsrc.getString("dept"));
//            }
//            return 1;
//        }
//        else if (ch == 3) {
//            sc.nextLine();
//            System.out.println("Enter course code: ");
//            String code = sc.nextLine();
//            String checkCatalogQuery = "SELECT * FROM course WHERE code='" + code +"' AND status=1;";
//            Statement stcc = con.createStatement();
//            ResultSet rscc = stcc.executeQuery(checkCatalogQuery);
//            if(!rscc.next()) {
//                System.out.println("Course already deleted from catalot or not found in catalog! Try again.");
//                return 0;
//            }
//            String delCourse = "UPDATE course SET status=0 WHERE code='" + code +"';";
//            Statement stdc = con.createStatement();
//            stdc.executeUpdate(delCourse);
//            return 1;
//        }
//        else {
//            System.out.println("Invalid input! Please try again.");
//            return 0;
//        }
//    }

    public double calculateCGPA(String stuEmail) throws SQLException {
        double totalCredits = 0;
        double totalWeightage = 0;
        String query = "SELECT * FROM grades WHERE email='" + stuEmail + "';";
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
        }
        st.close();
        if (totalCredits == 0) {
            return 0;
        }
        return (totalWeightage / totalCredits);
    }

    public int generateTranscript() throws SQLException, IOException {
        // seperate file for each student
        String students = "SELECT * FROM student;";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(students);
        while (rs.next()) {
            String stuEmail = rs.getString("email");
            String entryNum = rs.getString("entry_num");
            String studentName = rs.getString("name");
            String file = "C:\\Users\\DELL\\IdeaProjects\\mini_project\\src\\main\\resources\\transcript\\transcript_";
            file += entryNum;
            file += ".txt";
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            String grades = "SELECT * FROM grades WHERE email='" +
                    stuEmail + "';";
            bw.write(entryNum + " - " + studentName);
            bw.newLine();
            double cgpa = calculateCGPA(stuEmail);
            bw.write("CGPA: " + cgpa);
            bw.newLine();
            Statement stg = con.createStatement();
            ResultSet rsg = stg.executeQuery(grades);
            while (rsg.next()) {
                int offerId = rsg.getInt("offer_id");
                String code = rsg.getString("course_code");
                String grade = rsg.getString("grade");
                bw.write("ID: " + offerId + ", Course code: " + code + ", Grade: " + grade);
                bw.newLine();
            }
            stg.close();
            bw.close();
        }
        st.close();
        System.out.println("Transcripts generated\n");
        return 1;
    }

    public int endSemester() throws SQLException {
        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();

        String coursesOffered = "SELECT * FROM offered WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + " AND status=0;";
        Statement stco = con.createStatement();
        ResultSet rsco = stco.executeQuery(coursesOffered);
        if (rsco.next()) {
            System.out.println("Grades of all the offered courses in the current semester have not been uploaded by the instructor");
            System.out.println("So, the semester cannot be ended unless all grades have been uploaded.\n");
            return 0;
        } else {
            String newTime = "UPDATE time_frame SET status=0 WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
            Statement stnt = con.createStatement();
            stnt.executeUpdate(newTime);
            curSem++;
            if (curSem == 3) {
                curSem = 1;
                curYear++;
            }
            String timeChange = "INSERT INTO time_frame VALUES(" + Integer.toString(curSem) + ", " + Integer.toString(curYear) + ", 1);";
            Statement stct = con.createStatement();
            stct.executeUpdate(timeChange);
            stnt.close();

            String createDeadline = "INSERT INTO deadlines VALUES(1, 1, 1, " + Integer.toString(curSem) + ", " + Integer.toString(curYear) + ");";
            Statement st = con.createStatement();
            st.executeUpdate(createDeadline);
            System.out.println("Timeline successfully changed.");
            System.out.println("New Semester has started!\n");
            st.close();

            return 1;
        }
    }

    public int checkGraduation(String stuEmail) throws SQLException {
        int graduated = 1;
//        System.out.println("Enter student email: ");
//        Scanner sc = new Scanner(System.in);
//        String stuEmail = sc.nextLine();
        // fulfill all required credits (program, hs, ms, open)
        String studentDetails = "SELECT * FROM student WHERE email='" + stuEmail + "';";
        Statement sttd = con.createStatement();
        ResultSet rstd = sttd.executeQuery(studentDetails);
        rstd.next();
        String dept = rstd.getString("dept");
        int year = rstd.getInt("year_joining");
        String fetchCurriculum = "SELECT * FROM ug_curriculum WHERE batch=" + Integer.toString(year) + " AND dept='" + dept + "';";
        Statement stfc = con.createStatement();
        ResultSet rsfc = stfc.executeQuery(fetchCurriculum);
        rsfc.next();
        double creditsReq = rsfc.getDouble("total_credits");
        double doneCredits = 0;
//        int coreCredits = rsfc.getInt("program_credits");
//        int hsCredits = rsfc.getInt("hs_credits");
//        int openCredits = rsfc.getInt("open_credits");
//        int msCredits = rsfc.getInt("ms_credits");
        String listCourses = rsfc.getString("courses");
        String[] courses = listCourses.split(",");
//        String listType = rsfc.getString("type");
//        String[] type = listType.split(",");
        Map<String, Integer> courseDone = new HashMap<>();
        for (String course : courses) {
            String checkDone = "SELECT * FROM grades WHERE email='"
                    + stuEmail + "' AND course_code='" + course
                    + "' AND grade<>'F';";
            Statement stcd = con.createStatement();
            ResultSet rscd = stcd.executeQuery(checkDone);
            if (!rscd.next()) {
                graduated = 0;
                break;
            }
            courseDone.put(course, 1);
            String countCreds = "SELECT * FROM course WHERE code='" + course + "'";
            Statement stcc = con.createStatement();
            ResultSet rscc = stcc.executeQuery(countCreds);
            rscc.next(); // assumption course exists
            doneCredits += rscc.getDouble("c");
        }
        if (graduated == 0) {
            System.out.println("Student: " + stuEmail + " not GRADUATED.");
            return graduated;
        }
        String electiveAdd = "SELECT * FROM grades WHERE email='" + stuEmail + "' AND grade<>='F';";
        Statement stea = con.createStatement();
        ResultSet rsea = stea.executeQuery(electiveAdd);
        while (rsea.next()) {
            String course = rsea.getString("course_code");
            if (courseDone.get(course) == 1) {
                continue;
            }
            String countCreds = "SELECT * FROM course WHERE code='" + course + "'";
            Statement stcc = con.createStatement();
            ResultSet rscc = stcc.executeQuery(countCreds);
            rscc.next(); // assumption course exists
            doneCredits += rscc.getDouble("c");
        }
//        if (doneCredits < creditsReq) {
//            graduated = 0;
//        }
        if (doneCredits < creditsReq) {
            System.out.println("Student: " + stuEmail + " not GRADUATED");
        } else {
            System.out.println("Student: " + stuEmail + " GRADUATED");
        }
        return graduated;
    }

    public int changeDeadlines(int check) throws SQLException {
        String timeFetch = "SELECT * FROM time_frame WHERE status=1;";
        Statement tss = con.createStatement();
        ResultSet trs = tss.executeQuery(timeFetch);
        trs.next();
        int curSem = trs.getInt("sem");
        int curYear = trs.getInt("year");
        tss.close();

//        Scanner sc = new Scanner(System.in);
//        System.out.println("Press 0 to change deadline for offering course");
//        System.out.println("Press 1 to change deadline for enrolling course");
//        System.out.println("Press 2 to change deadline for uploading grades");
//        int check = sc.nextInt();
//        sc.nextLine();
        String query = "SELECT * FROM deadlines WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        rs.next();
        if (check == 0) {
            int get = rs.getInt("offer_status");
            if (get == 0) {
                get = 1;
                String updateEnable = "UPDATE deadlines SET offer_status=" + Integer.toString(get) + " WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
                Statement stue = con.createStatement();
                stue.executeUpdate(updateEnable);
                System.out.println("Course offering now allowed.");
                return 1;
            } else {
                get = 0;
                String updateEnable = "UPDATE deadlines SET offer_status=" + Integer.toString(get) + " WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
                Statement stue = con.createStatement();
                stue.executeUpdate(updateEnable);
                System.out.println("Course offering now not allowed.");
                return 1;
            }
        } else if (check == 1) {
            int get = rs.getInt("enroll_status");
            if (get == 0) {
                get = 1;
                String updateEnable = "UPDATE deadlines SET enroll_status=" + Integer.toString(get) + " WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
                Statement stue = con.createStatement();
                stue.executeUpdate(updateEnable);
                System.out.println("Course enrollment now allowed.");
                return 1;
            } else {
                get = 0;

                String updateEnable = "UPDATE deadlines SET enroll_status=" + Integer.toString(get) + " WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
                Statement stue = con.createStatement();
                stue.executeUpdate(updateEnable);
                System.out.println("Course enrollment now not allowed.");
                return 1;
            }
        } else if (check == 2) {
            int get = rs.getInt("grade_status");
            if (get == 0) {
                get = 1;
                String updateEnable = "UPDATE deadlines SET grade_status=" + Integer.toString(get) + " WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
                Statement stue = con.createStatement();
                stue.executeUpdate(updateEnable);
                System.out.println("Grade upload now allowed.");
                return 1;
            } else {
                get = 0;
                String updateEnable = "UPDATE deadlines SET grade_status=" + Integer.toString(get) + " WHERE sem=" + Integer.toString(curSem) + " AND year=" + Integer.toString(curYear) + ";";
                Statement stue = con.createStatement();
                stue.executeUpdate(updateEnable);
                System.out.println("Grade upload now not allowed.");
                return 1;
            }
        } else {
            System.out.println("INVALID Input");
            return 0;
        }
    }
}
//    public String acadProcedures() throws SQLException, IOException {
//        // edit course catalog --
//        // view all grades --
//        // generate student transcript (.txt) (marksheet)
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Press 0 to Edit Course Catalog");
//        System.out.println("Press 1 to view Grade of all Students");
//        System.out.println("Press 2 to Generate Transcript of Students");
//        System.out.println("Press 3 to end the current Semester");
//        System.out.println("Press 4 to check if a student has graduated");
//        System.out.println("Press 5 to change deadlines for offering, enrolling or grades upload");
//        System.out.println("Press 6 to go back");
//        int ch = sc.nextInt();
//        sc.nextLine();
//        if(ch == 0) {
//            System.out.println("Press 1 to add course\nPress 2 to view course catalog\nPress 3 to delete a course from catalog");
//            int catalog = sc.nextInt(); // update course catalog
//            sc.nextLine();
//            if (catalog == 1) {
//                System.out.println("Enter course code: ");
//                String code = sc.nextLine();
//                System.out.println(code + "\n" + "Enter course name: ");
//                String name = sc.nextLine();
//                System.out.println(name + "\n" +"Enter lecture: ");
//                int l = sc.nextInt();
//                System.out.println("Enter tutorial: ");
//                int t = sc.nextInt();
//                System.out.println("Enter practical: ");
//                int p = sc.nextInt();
//                System.out.println("Enter study hours: ");
//                int s = sc.nextInt();
//                System.out.println("Enter credits: ");
//                double c = sc.nextDouble();
//                sc.nextLine();
//                System.out.println("Enter course department: ");
//                String dept = sc.nextLine();
//                System.out.println("How many pre-requisites does this course have?");
//                int numPrereq = sc.nextInt();
//                sc.nextLine();
//                String[] preCode = new String[numPrereq];
//                int[] preGrade = new int[numPrereq];
//                for(int i=0; i<numPrereq; i++) {
//                    System.out.println("Please enter course code (" + (i+1) + "): ");
//                    preCode[i] = sc.nextLine();
//                    System.out.println("Please enter pre-requisite grade requirement [10/9/8/7/6/5] (enter 0 if no grade criteria): ");
//                    preGrade[i] = sc.nextInt();
//                    sc.nextLine();
//                }
//                addCourseCatalog(code, name, l, t, p, s, c, dept, preCode, preGrade, numPrereq);
//            }
//            else if (catalog == 2) { // done
//                readCourseCatalog();
//            }
//            else if (catalog == 3) {
//                System.out.println("Enter course code: ");
//                String code = sc.nextLine();
//                deleteCourseCatalog(code);
//            }
//            else {
//                System.out.println("Invalid input! Please try again.");
//            }
////            editCourseCatalog(); // done, ret
//        }
//        else if (ch == 1) {
//            viewGrades(); // done
//        }
//        else if (ch == 2) {
//            generateTranscript(); // done
//        }
//        else if (ch == 3) {
//            // start sem
//            endSemester(); // done, ret
//        }
//        else if (ch == 4) { // done, ret
//            System.out.println("Enter student email: ");
//            String stuEmail = sc.nextLine();
//            checkGraduation(stuEmail);
//
//        }
//        else if (ch == 5) { // done, ret
//            System.out.println("Press 0 to change deadline for offering course");
//            System.out.println("Press 1 to change deadline for enrolling course");
//            System.out.println("Press 2 to change deadline for uploading grades");
//            int check = sc.nextInt();
//            sc.nextLine();
//            changeDeadlines(check);
//
//        }
//        else if (ch == 6) {
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
