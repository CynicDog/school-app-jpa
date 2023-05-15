package org.jhta.service;

import org.jhta.domain.Person;
import org.jhta.domain.Student;
import org.jhta.domain.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class UserService {
    private final EntityManagerFactory emf;
    public UserService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void registerTeacher(Teacher teacher) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(teacher);
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

    public void registerStudent(Student student) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(student);
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

    public Person processLogin(String user_id, String user_password) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Person found = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            found = em.find(Person.class, user_id);

            if (found == null) {
                throw new RuntimeException("No user found with the given identifier.");
            }
            if (!user_password.equals(found.getPassword())) {
                throw new RuntimeException("Password doesn't match.");
            }

            transaction.commit();
            return found;

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public Teacher getTeacherById(String user_id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Teacher found = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            found = em.find(Teacher.class, user_id);

            if (found == null) {
                throw new RuntimeException("No user found with the given identifier.");
            }

            transaction.commit();
            return found;

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public Student getStudentById(String user_id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Student found = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            found = em.find(Student.class, user_id);

            if (found == null) {
                throw new RuntimeException("No user found with the given identifier.");
            }

            transaction.commit();
            return found;

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