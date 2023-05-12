package org.jhta.service;

import org.jhta.domain.Course;
import org.jhta.subselect.RegisteredCourse;
import org.jhta.subselect.RegisteredStudent;
import org.jhta.domain.Registration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

public class CourseService {

    private final EntityManagerFactory emf;
    public CourseService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void openUpCourse(Course course) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(course);
            succeeded = true;

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);

        } finally {
            if (succeeded) {
                transaction.commit();

            } else {
                assert transaction != null;
                transaction.rollback();
            }

            em.close();
        }
    }

    public List<Course> lookUpCoursesById(String teacher_id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<Course> courses = new ArrayList<>();

        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            courses= em
                    .createQuery("select c from Course c where c.teacher.id = :teacherId", Course.class)
                    .setParameter("teacherId", teacher_id)
                    .getResultList();

            succeeded = true;
            return courses;

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);

        } finally {
            if (succeeded) {
                transaction.commit();

            } else {
                assert transaction != null;
                transaction.rollback();
            }

            em.close();
        }
    }

    public Course getCourseById(Long courseId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Course course = null;

        boolean succeeded = false;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            course = em.find(Course.class, courseId);

            succeeded = true;
            return course;

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);

        } finally {

            if (succeeded) {
                transaction.commit();

            } else {
                assert transaction != null;
                transaction.rollback();
            }

            em.close();
        }
    }

    public List<Course> lookUpCourses(String status) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<Course> courses = new ArrayList<>();

        boolean succeeded = false;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            courses = em
                    .createQuery("select c from Course c where c.status = :status", Course.class)
                    .setParameter("status", status)
                    .getResultList();

            succeeded = true;

            return courses;

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);

        } finally {

            if (succeeded) {
                transaction.commit();

            } else {
                assert transaction != null;
                transaction.rollback();
            }

            em.close();
        }
    }

    public void applyCourse(Registration registration) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(registration);

            succeeded = true;

            Course course = registration.getCourse();
            if (course.getRegCount() >= course.getQuota()) { throw new RuntimeException("Quota already filled up."); }

            course.setRegCount(course.getRegCount() + 1);
            if (course.getRegCount() == course.getQuota()) { course.setStatus("reached");}

            em.merge(course);

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);

        } finally {

            if (succeeded) {
                transaction.commit();

            } else {
                assert transaction != null;
                transaction.rollback();
            }

            em.close();
        }
    }

    public List<RegisteredStudent> lookUpRegisteredStudentsById(String teacher_id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<RegisteredStudent> students = new ArrayList<>();

        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            students = em.createQuery("select rs from RegisteredStudent rs where rs.teacher_id = :teacherId", RegisteredStudent.class)
                    .setParameter("teacherId", teacher_id)
                    .getResultList();

            return students;

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);

        } finally {
            if (succeeded) {
                transaction.commit();
            } else {
                assert transaction != null;
                transaction.rollback();
            }

            em.close();
        }
    }

    public List<RegisteredCourse> lookUpRegisteredCoursesByStudentId(String student_id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<RegisteredCourse> courses = new ArrayList<>();
        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            courses = em.createQuery("select rc from RegisteredCourse rc where rc.student_id = :studentId", RegisteredCourse.class)
                    .setParameter("studentId", student_id)
                    .getResultList();

            return courses;

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);

        } finally {
            if (succeeded) {
                transaction.commit();
            } else {
                assert transaction != null;
                transaction.rollback();
            }

            em.close();
        }
    }
}
