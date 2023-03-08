# Testing Plan

### Introduction
The test plan is designed to establish the approach, scope and schedule of all testing activities of the project. The plan identifies the methods to be tested and the types of tests to be performed on them.



### Featues to be Tested
This includes various functional requirements of the application.
* Main:
    * Login: Lets the user login using email and password.
    * Change Password: Lets the user change his/her password.
    * Change Contact Details: Lets the user set new contact details.

* Student:
    * Enroll Course: Lets the student enroll an offered course in that semester within the deadline set by academic office.
    * Drop Course: Lets the student drop an already enrolled course in that semester within the deadline set by academic office.
    * View Grades: Lets the student view his/her grades obtained in completed courses.
    * CGPA: Lets the student view his/her current CGPA.

* Instructor:
    * View Grades: Lets the instructor view grades of students for their offered courses only.
    * Offer Course: Lets the instructor offer a course (existing in course catalog) in that semester within the deadline set by academic office.
    * Remove Course Offering: Lets the instructor remove a course offering for an offered course in that semester within the deadline set by academic office.
    * Download student details: Lets the instructor download student details for a particular course offering, that would help them enter and upload grades for enrolled students from the file itself.
    * Upload Grades: Lets the instructor upload grades for a particular course offering using a (.csv) file within the deadline set by academic office.

* Academic Office:
    * Edit Course Catalog: Lets the officer add, view or delete courses from the course catalog from which the instructors can offer a course.
    * View Grades: Lets the officer view grades of all students within the institute.
    * Generate transcript: Lets the officer generate transcript (marksheet) in a text file for each student.
    * End Semester: Lets the officer end the current semester once the grades for all course offerings are upload in that semester.
    * Check Graduation: Lets the officer check if a particular student has graduated.
    * Change Deadlines: Lets the officer change offer course (instructor), enroll course (student) and upload grades (instructor) deadlines in a semester.


### Approach
##### Unit Testing
Testing code units, which are the smallest piece of code that can be logically isolated in a system (like methods). Written unit tests for each method. A unit test function for a particular method could contain multiple tests in order to check all logical possibilities and cover all branches. JUnit open-source testing framework with Java has been used.


### Test Deliverables
* Test Plan: The current document
* Unit Tests: Various unit tests to test all methods (for all classes) with all possible logical factors and their expected results (considering preloaded data in the database).
* Code Coverage Report: Shows how much code is covered and how many branches are covered (how many lines are executed) after running all the unit tests. Done using JaCoCo code coverage report.


### Testing Tasks
* Test environment and database state required for testing should be ready prior to test execution phase.
* Quality Expectations must be met.
* Ensure that the maximum number of tests pass (preferably all) and software passes the test completeness criteria.
* Maximum code is covered and code branches are not missed.


### Environmental Needs
| No. | Resources            | Description                                                                                           |
|-----|----------------------|-------------------------------------------------------------------------------------------------------|
| 1.  | Java Development Kit | Appropriate JDK version to support java environment                                                   |
| 2.  | Build Tool           | Project should have gradle as build automation tool                                                   |
| 3.  | Database             | System should have PostgreSQL installed, and project should have JDBC connection through gradle file. |
| 4.  | Unit Testing         | Gradle file should have JUnit dependencies for unit testing.                                          |
| 5.  | Code Coverage        | Gradle file should have JaCoCo dependencies for code coverage report.                                 |


### Risks
In case if the project is released for production without covering all logical possibilities i.e. insufficient test cases can lead to inefficiency and cause hindrance to the user. To avoid such scenarios, we must try and add missed test cases eventually maximizing the code and branch coverage.





