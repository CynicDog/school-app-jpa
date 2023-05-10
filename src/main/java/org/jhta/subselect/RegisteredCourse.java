package org.jhta.subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@org.hibernate.annotations.Immutable
@org.hibernate.annotations.Subselect(value =
        "select r.id as id, s.id as student_id, r.createDate as registeredDate, r.cancelled as cancelled, c.name as course_name " +
                "from Registration r " +
                "left outer join Course c on c.id = r.course_id " +
                "left outer join Student s on s.id = r.student_id " )
@org.hibernate.annotations.Synchronize({"Registration", "Course", "Student"})
public class RegisteredCourse {

    @Id
    private Long id;
    private String student_id;
    private Date registeredDate;
    private String cancelled;
    private String course_name;

    public Long getId() {
        return id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public String getCancelled() {
        return cancelled;
    }

    public String getCourse_name() {
        return course_name;
    }

    @Override
    public String toString() {
        return "RegisteredCourse{" +
                "id=" + id +
                ", student_id='" + student_id + '\'' +
                ", registeredDate=" + registeredDate +
                ", cancelled='" + cancelled + '\'' +
                ", course_name='" + course_name + '\'' +
                '}';
    }
}


