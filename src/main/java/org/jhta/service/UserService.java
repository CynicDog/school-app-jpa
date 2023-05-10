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

        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(teacher);
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

    public void registerStudent(Student student) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            em.persist(student);
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

    public Person processLogin(String user_id, String user_password) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Person found = null;
        boolean succeeded = false;
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
            succeeded = true;

            return found;

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

    public Teacher getTeacherById(String user_id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Teacher found = null;
        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            found = em.find(Teacher.class, user_id);

            if (found == null) {
                throw new RuntimeException("No user found with the given identifier.");
            }
            succeeded = true;

            return found;

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

    public Student getStudentById(String user_id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;

        Student found = null;
        boolean succeeded = false;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            found = em.find(Student.class, user_id);

            if (found == null) {
                throw new RuntimeException("No user found with the given identifier.");
            }
            succeeded = true;

            return found;

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