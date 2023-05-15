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

    public void deleteCourse(Course course) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            List<Registration> registrations = this.getRegistrationsByCourseId(course.getId());

            registrations.forEach(registration -> {

                course.removeRegistration(registration);
            });

            em.createQuery("delete from Registration r where r.course.id = :courseId")
                    .setParameter("courseId", course.getId())
                    .executeUpdate();

            em.createQuery("delete from Course c where c.id = :courseId")
                    .setParameter("courseId", course.getId())
                    .executeUpdate();

            transaction.commit();

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public void deleteRegistration(Registration registration) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            Course course = registration.getCourse();
            course.removeRegistration(registration);

            if ("reached".equals(course.getStatus())) {
                course.setStatus("available");
            }
            course.setRegCount(course.getRegCount() - 1);

            em.merge(course);

            em.createQuery("delete from Registration r where r.id = :registrationId")
                    .setParameter("registrationId", registration.getId())
                    .executeUpdate();

            transaction.commit();

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public List<Registration> getRegistrationsByCourseId(Long courseId) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<Registration> registrations = new ArrayList<>();

        try {
            transaction = em.getTransaction();
            transaction.begin();

            registrations = em.createQuery( "select r from Registration r where r.course.id = :courseId", Registration.class)
                    .setParameter("courseId", courseId)
                    .getResultList();

            transaction.commit();
            return registrations;

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();;
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }

    }

    public Registration getRegistrationById(Long registrationId) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            Registration registration = em.find(Registration.class, registrationId);

            transaction.commit();

            return registration;

        } catch (RuntimeException ex) {
            if(transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public void openUpCourse(Course course) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(course);

            transaction.commit();

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public List<Course> getCoursesByTeacherId(String teacher_id) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<Course> courses = new ArrayList<>();

        try {
            transaction = em.getTransaction();
            transaction.begin();

            courses = em
                    .createQuery("select c from Course c where c.teacher.id = :teacherId", Course.class)
                    .setParameter("teacherId", teacher_id)
                    .getResultList();

            transaction.commit();
            return courses;

        } catch (RuntimeException ex) {
            if(transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public Course getCourseById(Long courseId) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Course course = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            course = em.find(Course.class, courseId);

            transaction.commit();

            return course;

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public List<Course> getCoursesByStatus(String status) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<Course> courses = new ArrayList<>();

        try {
            transaction = em.getTransaction();
            transaction.begin();

            courses = em
                    .createQuery("select c from Course c where c.status = :status", Course.class)
                    .setParameter("status", status)
                    .getResultList();

            transaction.commit();
            return courses;

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public void applyCourse(Registration registration) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(registration);

            Course course = registration.getCourse();
            if (course.getRegCount() >= course.getQuota()) { throw new RuntimeException("Quota already filled up."); }

            course.setRegCount(course.getRegCount() + 1);
            if (course.getRegCount() == course.getQuota()) { course.setStatus("reached");}

            em.merge(course);
            transaction.commit();

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public List<RegisteredStudent> getRegisteredStudentsByTeacherId(String teacher_id) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<RegisteredStudent> students = new ArrayList<>();

        try {
            transaction = em.getTransaction();
            transaction.begin();

            students = em.createQuery("select rs from RegisteredStudent rs where rs.teacher_id = :teacherId", RegisteredStudent.class)
                    .setParameter("teacherId", teacher_id)
                    .getResultList();

            transaction.commit();
            return students;

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public List<RegisteredCourse> getRegisteredCoursesByStudentId(String student_id) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        List<RegisteredCourse> courses = new ArrayList<>();

        try {
            transaction = em.getTransaction();
            transaction.begin();

            courses = em.createQuery("select rc from RegisteredCourse rc where rc.student_id = :studentId", RegisteredCourse.class)
                    .setParameter("studentId", student_id)
                    .getResultList();

            transaction.commit();
            return courses;

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }
}
