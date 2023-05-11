package org.jhta;

import org.jhta.domain.*;
import org.jhta.service.CourseService;
import org.jhta.service.UserService;
import org.jhta.subselect.RegisteredCourse;
import org.jhta.subselect.RegisteredStudent;
import org.jhta.util.KeyboardReader;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.menu();
    }

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("school-app-jpa");
    private KeyboardReader keyboard = new KeyboardReader();
    private LoginUser loginUser = null;
    private UserService userService = new UserService(emf);
    private CourseService courseService = new CourseService(emf);

    public void menu() {

        try {
            if (loginUser == null) {
                System.out.println("-------------------------------------------------------------");
                System.out.println("1.로그인(학생)  2.로그인(강사)  3.가입(학생)  4.가입(강사)  0.종료");
                System.out.println("-------------------------------------------------------------");
            } else {
                if ("학생".equals(loginUser.getType())) {
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("1.과정조회  2.과정신청  3.등록취소  4.신청현황  0.종료");
                    System.out.println("-------------------------------------------------------------");
                } else if ("강사".equals(loginUser.getType())) {
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("1.과정조회  2.과정등록  3.과정취소  4.과정현황  0.종료");
                    System.out.println("-------------------------------------------------------------");
                }
            }
            System.out.print("\nMenu : ");

            int menu = keyboard.readInt();
            System.out.println();

            if (menu == 0) {
                System.out.println("Program terminated.");
                System.exit(0);
            }

            if (loginUser == null) {
                if (menu == 1) {
                    학생로그인();
                } else if (menu == 2) {
                    강사로그인();
                } else if (menu == 3) {
                    학생회원가입();
                } else if (menu == 4) {
                    강사회원가입();
                }
            } else {
                if ("학생".equals(loginUser.getType())) {
                    if (menu == 1) {
                        학생과정조회();
                    } else if (menu == 2) {
                        학생과정신청();
                    } else if (menu == 3) {
                        학생등록취소();
                    } else if (menu == 4) {
                        학생신청현황조회();
                    }

                } else if ("강사".equals(loginUser.getType())) {
                    if (menu == 1) {
                        강사과정조회();
                    } else if (menu == 2) {
                        강사과정등록();
                    } else if (menu == 3) {
                        강사과정취소();
                    } else if (menu == 4) {
                        강사과정현황조회();
                    }
                }
            }
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }

        System.out.println("");

        menu();
    }

    private void 강사과정현황조회() {
        System.out.println("[ Course Registration Status Page ]");

        courseService.lookUpRegisteredStudentsById(this.loginUser.getId())
                .stream()
                .map(RegisteredStudent::toString)
                .forEach(System.out::println);
    }

    private void 강사과정취소() { // TODO
        System.out.println("[ Course Close Down Page ]\nFill up required information for closing down a course.\n");
        System.out.println("(Required) Course number: "); int course_no = keyboard.readInt();
    }

    private void 강사과정등록() {
        System.out.println("[ Course Opening Page ]\nFill up required information for opening up a course.\n");
        System.out.println("(Required) Name: "); String course_name = keyboard.readString();
        System.out.println("(Required) Quota: "); int course_quota = keyboard.readInt();

        Teacher teacher = userService.getTeacherById(this.loginUser.getId());
        Course course = new Course(course_name, course_quota, teacher);

        courseService.openUpCourse(course);

        System.out.println("\nNew course got successfully opened up.");
    }

    private void 강사과정조회() {
        System.out.println("[ Course List Page ]\n");

        courseService.lookUpCoursesById(loginUser.getId())
                .stream()
                .map(Course::toString)
                .forEach(System.out::println);
    }

    private void 학생신청현황조회() {
        System.out.println("[ Course Registration History Page ]");

        courseService.lookUpRegisteredCoursesById(this.loginUser.getId())
                .stream()
                .map(RegisteredCourse::toString)
                .forEach(System.out::println);
    }

    private void 학생등록취소() { // TODO
        System.out.println("[ Course Registration Abort Page ]\nFill up required information for aborting registration for a course.");
        System.out.println("(Required) Course registration number: "); int reg_no = keyboard.readInt();
    }

    private void 학생과정신청() {
        System.out.println("[ Course Applying Page ]\nFill up required information for applying for a course.");
        System.out.println("(Required) Course number: "); Long course_id = keyboard.readLong();

        Student student = userService.getStudentById(this.loginUser.getId());
        Course course = courseService.getCourseById(course_id);

        Registration registration = new Registration(student, course);

        courseService.applyCourse(registration);

        System.out.println("\nAppliying for the course got successfully done.");
    }

    private void 학생과정조회() {
        System.out.println("[ Available Courses Page ]");

        courseService.lookUpCourses("모집중")
                .stream()
                .map(Course::toString)
                .forEach(System.out::println);
    }

    private void 강사회원가입() {
        System.out.println("[ Registration Page (Teacher) ]\nFill up required information for registration.\n");
        System.out.println("(Required) Id: "); String teacher_id = keyboard.readString();
        System.out.println("(Required) Password: "); String teacher_password = keyboard.readString();
        System.out.println("(Required) Name: "); String teacher_name = keyboard.readString();
        System.out.println("(Required) Phone: "); String teacher_phone = keyboard.readString();
        System.out.println("(Required) Email: "); String teacher_email = keyboard.readString();
        System.out.println("(Required) Salary: "); int teacher_salary = keyboard.readInt();

        Teacher teacher = new Teacher(teacher_id, teacher_password, teacher_name, teacher_phone, teacher_email, teacher_salary);

        userService.registerTeacher(teacher);

        System.out.println("\nNew teacher got successfully registered.");
    }

    private void 학생회원가입() {
        System.out.println("[ Registration Page (Student) ]\nFill up required information for registration.\n");
        System.out.println("(Required) Id: "); String student_id = keyboard.readString();
        System.out.println("(Required) Password: "); String student_password = keyboard.readString();
        System.out.println("(Required) Name: "); String student_name = keyboard.readString();
        System.out.println("(Required) Phone: "); String student_phone = keyboard.readString();
        System.out.println("(Required) Email: "); String student_email = keyboard.readString();

        Student student = new Student(student_id, student_password, student_name, student_phone, student_email);

        userService.registerStudent(student);

        System.out.println("\nNew student got successfully registered.");
    }
    private void 강사로그인() {
        System.out.println("[ Login Page (Teacher) ]\nFill up required information for login.\n");
        System.out.println("(Required) Id: "); String teacher_id = keyboard.readString();
        System.out.println("(Required) Password: "); String teacher_password = keyboard.readString();

        Person person = userService.processLogin(teacher_id, teacher_password);

        this.loginUser = new LoginUser(person.getId(), person.getName(), person.getEmail(), "강사");

        System.out.println("\nHello, " + loginUser.getName() + ".");
    }
    private void 학생로그인() {
        System.out.println("[ Login Page (Student) ]\nFill up required information for login.\n");
        System.out.println("(Required) Id: "); String student_id = keyboard.readString();
        System.out.println("(Required) Password: "); String student_password = keyboard.readString();

        Person person = userService.processLogin(student_id, student_password);

        this.loginUser = new LoginUser(person.getId(), person.getName(), person.getEmail(), "학생");

        System.out.println("\nHello, " + loginUser.getName() + ".");
    }
}
