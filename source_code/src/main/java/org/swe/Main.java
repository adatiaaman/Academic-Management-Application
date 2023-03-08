package org.swe;

import org.swe.ui.acadOfficeInterface;
import org.swe.ui.instructorInterface;
import org.swe.ui.studentInterface;

import java.io.IOException;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.Calendar;
import java.util.Scanner;

public class Main {
    public static int changePassword(String email, Connection con, String oldpw, String newpw) throws SQLException {

        String query = "select * from auth where email='" + email + "';";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        rs.next();
        if(rs.getString("password").equals(oldpw)){
//            System.out.println("Enter new password: ");
//            String newpw = sc.nextLine();
            String q="UPDATE auth SET password='"+newpw+"' WHERE email='"+email+"';";
            Statement st1 = con.createStatement();
            st1.executeUpdate(q);
            st1.close();
            st.close();
            return 1;
        }
        else{
            System.out.println("Incorrect! Cannot change password.");
            st.close();
            return 0;
        }
    }

    public static int changeContact(String email, Connection con, String contact) throws SQLException {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter new contact: ");
//        String contact = sc.nextLine();
        String q= "UPDATE auth SET contact='" + contact + "' WHERE email='" + email + "';";
        Statement st1 = con.createStatement();
        st1.executeUpdate(q);
        st1.close();
        return 1;
    }

    public static void navigation(Connection con, String email, String type, Scanner sc) throws ClassNotFoundException, SQLException, IOException {
//        Scanner sc = new Scanner(System.in);
        System.out.println("Press 0 to logout");
        System.out.println("Press 1 to change password");
        System.out.println("Press 2 to change contact details");
        System.out.println("Press 3 for Menu");
        int ch = sc.nextInt();
        sc.nextLine();

        if(ch == 0) {
            System.out.println("Logged out!\n");
            util(con, sc);
        }
        else if(ch == 1){
            System.out.println("Enter current password: ");
            String oldpw = sc.nextLine();
            System.out.println("Enter new password: ");
            String newpw = sc.nextLine();
            changePassword(email, con, oldpw, newpw);
            navigation(con, email, type, sc);
        }
        else if(ch == 2){
            System.out.println("Enter new contact: ");
            String contact = sc.nextLine();
            changeContact(email, con, contact);
            navigation(con, email, type, sc);
        }
        else if(ch == 3){
            if(type.equals("student")){
                studentInterface stud_ui = new studentInterface(email, con); // mockito
                stud_ui.studentProcedures();
                navigation(con, email, type, sc);
            }
            else if (type.equals("instructor")) {
                instructorInterface inst_ui = new instructorInterface(email, con);
                inst_ui.instProcedures();
                navigation(con, email, type, sc);
            }
            else{
                acadOfficeInterface dean_ui = new acadOfficeInterface(email, con);
                dean_ui.acadProcedures();
                navigation(con, email, type, sc);
            }
        }
        else {
            System.out.println("Invalid! Enter Valid Input as mentioned.");
            navigation(con, email, type, sc);
        }

    }

    public static void util(Connection con, Scanner sc) throws ClassNotFoundException, SQLException, IOException {
        System.out.println("""
                LogIn
                [Type "terminate" to end the application.]\s
                Email:\s""");
        String email = sc.nextLine();

        String query = "select * from auth where email='" + email + "';";
//        System.out.println(query);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        if(rs.next()){
            System.out.println("Password: ");
            String pw = sc.nextLine();
            String check_pw = rs.getString("password");
            if(pw.equals(check_pw)){
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String logQuery = "INSERT INTO login_log(email, time_stamp) VALUES (?, ?);";
                PreparedStatement log = con.prepareStatement(logQuery);
                log.setString(1, email);
                Calendar cal = Calendar.getInstance();
                Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
                log.setTimestamp(2, timestamp);
                log.executeUpdate();
                log.close();

                System.out.println("Correct Password");

                String q1 = "select * from student where email='" + email + "';";
                String q2 = "select * from acad_office where email='" + email + "';";
                String q3 = "select * from instructor where email='" + email + "';";

                Statement st1 = con.createStatement();
                ResultSet rs1 = st1.executeQuery(q1);
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery(q2);
                Statement st3 = con.createStatement();
                ResultSet rs3 = st3.executeQuery(q3);
                if(rs1.next()){
                    // student
                    System.out.println("STUDENT Logged in successfully\n");
                    navigation(con, email, "student", sc);
                } else if (rs2.next()) {
                    // acad_office
                    System.out.println("ACAD OFFICE Logged in successfully\n");
                    navigation(con, email, "acad_office", sc);
                } else if (rs3.next()) {
                    // instructor
                    System.out.println("INSTRUCTOR Logged in successfully\n");
                    navigation(con, email, "instructor", sc);
                }
            }
            else{
                System.out.println("Incorrect Password\n");
                util(con, sc);
            }
        }
        else if(!email.equals("terminate") && !email.equals("superuser")){
            System.out.println("Invalid Email\n");
            util(con, sc);
        }

        st.close();
        con.close();
    }

    public static String main() throws ClassNotFoundException, SQLException, IOException {

        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url");
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(url, username, password);
//        superUser initialise = new superUser(con);
//        initialise.init();
//        databaseInit.main(con);
        Scanner sc = new Scanner(System.in);
        util(con, sc);
        con.close();
        return "0";
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        main();
    }
}


/*
TESTING:

login log - done
log out - done
change contact - done
change password - done

enroll course - done
drop course - done
view grades - done
calc cgpa - done

offer course - done
remove offer - done
view grades - done
download details - done
upload grades - done

catalog add - done
catalog update - done
catalog read - done
catalog delete -
end sem - done
view grades - done
generate transcript - done
check grad - done
change deadlines - done
*/
