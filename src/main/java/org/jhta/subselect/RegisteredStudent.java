package org.jhta.subselect;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@org.hibernate.annotations.Immutable
@org.hibernate.annotations.Subselect(value =
        " select r.id as id, c.id as course_id, c.name as course_name, c.quota as course_quota, c.regCount as course_count, c.status as course_status, s.id as student_id, t.id as teacher_id " +
                " from Course c " +
                " left outer join Teacher t on t.id = c.teacher_id " +
                " left outer join Registration r on r.course_id = c.id " +
                " left outer join Student s on s.id = r.student_id ")
@org.hibernate.annotations.Synchronize({"Course, Teacher", "Registration", "Student"})
public class RegisteredStudent {

    @Id
    private Long id;
    private Long course_id;
    private String course_name;
    private int course_quota;
    private int course_count;
    private String course_status;
    private String student_id;
    private String teacher_id;

    public Long getId() {
        return id;
    }

    public Long getCourse_id() {
        return course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public int getCourse_quota() {
        return course_quota;
    }

    public int getCourse_count() {
        return course_count;
    }

    public String getCourse_status() {
        return course_status;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    @Override
    public String toString() {
        return "RegisteredStudent{" +
                "id=" + id +
                ", course_id=" + course_id +
                ", course_name='" + course_name + '\'' +
                ", course_quota=" + course_quota +
                ", course_count=" + course_count +
                ", course_status='" + course_status + '\'' +
                ", student_id='" + student_id + '\'' +
                ", teacher_id='" + teacher_id + '\'' +
                '}';
    }
}
