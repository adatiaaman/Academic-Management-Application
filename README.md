# CS305 - Software Engineering Mini Project

##### Aman Pankaj Adatia - 2020CSB1154

A multi-user database application for  managing the academics of an academic institute. Command Line Interface (CLI) to interact with the database. The functionalities to be supported are based on the academic policies of our institute. 

Academic ecosystem for UG programs basically consists of the following stakeholders:
* Students
* Faculty
* Academics Office

<br>

To run and compile the program:

* Setup:
  * Create a PostgreSQL database named (mini_project)
  * Run create table and insert queries as given in _./src/main/resources_ folder in _tables.txt_ and _queries.txt_ respectively.
  * Set local psql database credentials in _config.properties_ file in _./src/main/resources_ folder.
  * Set the current working directory as your project folder.
  * You are set to run the project using below commands.
  * Follow the instructions as printed on terminal after running the project for executing various functionalities implemented.


* Commands: 

  `gradle build jacocotestreport`

  `gradle run`


* Requirements:
  * Java 17 (Oracle OpenJDK version 17.0.2)
  * PostgresSQL

<br>

Consists of:
* UML Diagram (_/uml_diagram_)
* JUnit unit testing plan (_TestPlan.md_)
* JaCoCo code coverage report
* ER Diagram for database (_/er_diagram_)

<br>

UML Diagram:
* Class diagram (structural - static)
* Sequence diagram (behavioural - dynamic)
* Activity diagram (behavioural - dynamic)

<br>

Functionalities and Assumptions:
* User authentication with email and password.
* Application stores the current semester and year going on in the database, along with deadlines for enrolling/dropping course for a student, offer/remove offer course for an instructor and uploading grades for an instructor.
* The deadlines are controlled by the academic office, and only they can end the semester and start the semester after grades are uploaded for all offered courses in that semester.
* Created a hypothetical UG curriculum for every batch and department of students. It consists of program core (compulsory) courses that need to be completed and the total credits (eg. 100) that should be fulfilled including electives for a student to graduate. Exact details regarding UG curriculum can be found in 'academic plan.txt' in resources folder.
* An enrolled course for a student is a program elective if it is not in the list of core courses for the students corresponding batch and department.
* Credit limit for a student in a semester being approximately 1.25 times the average of the credit limits of the previous two semesters for that student.
* Course catalog contains courses that are available for instructors to offer. It contains l-t-p-s-c structure of the course.
* Prerequisites for each course in catalog are stored with grade cutoff for each prerequisite of that course.
* A student can enroll for a course that is offered in the current semester when it is allowed by the academic office.
* A student can drop an already enrolled course in the current semester before the instructor has uploaded grades for that course and dropping is allowed by the academic office.
* Academic office can view grades and generate transcript of all students and check if a student has graduated or not.
* Instructor can download student details for their offered course, view grades and upload grades via (.csv) file for the course offering.
* Students can view their grades and CGPA.
* JUnit testing with preloaded data in the database, although database remains as it is after unit testing is completed.

