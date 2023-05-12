import org.jhta.domain.*;
import org.jhta.subselect.RegisteredCourse;
import org.jhta.subselect.RegisteredStudent;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionTest {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("school-app-jpa-test");

    @Test
    public void connectionTest() {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Student student1 = new Student("simon_test", "simon1", "Simon", "999-999-9999", "simon1@school.com", "student");
        Student student2 = new Student("alex_test", "alex1", "Alex", "999-999-9999", "alex1@school.com", "student");
        em.persist(student1);
        em.persist(student2);

        Teacher teacher1 = new Teacher("maria_test", "maria1", "Maria", "999-999-9999", "maria1@school.com", 999, "teacher");
        em.persist(teacher1);

        Course course1 = new Course("Algorithm 101", 10, teacher1);
        Course course2 = new Course("Graph Theory", 5, teacher1);
        em.persist(course1);
        em.persist(course2);

        Registration registration1 = new Registration(student1, course1);
        Registration registration2 = new Registration(student1, course2);
        Registration registration3 = new Registration(student2, course1);
        Registration registration4 = new Registration(student2, course2);
        em.persist(registration1);
        em.persist(registration2);
        em.persist(registration3);
        em.persist(registration4);

        Student found = em.find(Student.class, "simon_test");
        assertEquals("Simon", found.getName());

//    mysql > select student.deleted, person.id, person.email, person.name, person.phone, registration.course_id, course.name, course.status
//    -> from student
//    -> inner join person on student.id = person.id
//    -> inner join registration on person.id = registration.student_id
//    -> inner join course on registration.course_id = course.id;
//        +---------+-------+-------------------+-------+--------------+-----------+---------------+-----------+
//        | deleted | id    | email             | name  | phone        | course_id | name          | status    |
//        +---------+-------+-------------------+-------+--------------+-----------+---------------+-----------+
//        | N       | simon | simon1@school.com | Simon | 999-999-9999 |         4 | Algorithm 101 | 모집중      |
//        | N       | alex  | alex1@school.com  | Alex  | 999-999-9999 |         4 | Algorithm 101 | 모집중      |
//        | N       | simon | simon1@school.com | Simon | 999-999-9999 |         5 | Graph Theory  | 모집중      |
//        | N       | alex  | alex1@school.com  | Alex  | 999-999-9999 |         5 | Graph Theory  | 모집중      |
//        +---------+-------+-------------------+-------+--------------+-----------+---------------+-----------+

        em.createQuery("delete from Registration ").executeUpdate();
        em.createQuery("delete from Course").executeUpdate();
        em.createQuery("delete from Teacher").executeUpdate();
        em.createQuery("delete from Student").executeUpdate();
        em.createQuery("delete from Person").executeUpdate();

        em.getTransaction().commit(); em.close();
    }

    @Test
    public void compositeUniqueConstraintTest() {
        // Create and persist entities to the database
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Student student = new Student("john", "john1", "John Doe", "999-999-9999", "john1@school.com", "student");
        em.persist(student);

        Teacher teacher = new Teacher("jane", "jane1", "Jane Doe", "999-999-9999", "jane1@school.com", 999, "teacher");
        em.persist(teacher);

        Course course = new Course("Database", 10, teacher);
        em.persist(course);

        Registration registration1 = new Registration(student, course);
        em.persist(registration1);

        Registration registration2 = new Registration(student, course);
        em.persist(registration2);

//      Expect a PersistenceException to be thrown when trying to persist a duplicate registration
        assertThrows(PersistenceException.class, () -> { em.getTransaction().commit(); });
        em.close();
    }

    @Test
    public void subselectTest() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Student student = new Student("paloma", "paloma1", "Paloma", "999-999-9999", "paloma1@school.com", "student");
        em.persist(student);

        Teacher teacher = new Teacher("fred", "fred1", "Fred", "999-999-9999", "fred1@school.com", 999, "teacher");
        em.persist(teacher);

        Course course = new Course("Portuguese", 1, teacher);
        em.persist(course);

        Registration registration = new Registration(student, course);
        em.persist(registration);

        em.getTransaction().commit();
        em.getTransaction().begin();

        RegisteredStudent registeredStudent = em.createQuery("select rs from RegisteredStudent rs where rs.student_id = :studentId and rs.teacher_id = :teacherId", RegisteredStudent.class)
                .setParameter("studentId", student.getId())
                .setParameter("teacherId", teacher.getId())
                .getSingleResult();

        RegisteredCourse registeredCourse = em.createQuery("select rc from RegisteredCourse  rc where rc.student_id = :studentId and rc.course_name = :courseName", RegisteredCourse.class)
                .setParameter("studentId", student.getId())
                .setParameter("courseName", course.getName())
                .getSingleResult();

        assertAll(
                () -> assertEquals("Portuguese", registeredStudent.getCourse_name()),
                () -> assertEquals("paloma", registeredStudent.getStudent_id()),
                () -> assertEquals("Portuguese", registeredCourse.getCourse_name()),
                () -> assertEquals("paloma", registeredCourse.getStudent_id())
        );

        em.getTransaction().commit(); em.close();
    }
}
