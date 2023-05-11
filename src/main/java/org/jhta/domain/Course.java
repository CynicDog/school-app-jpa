package org.jhta.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Course {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private int quota;

    private int regCount;

    private String status;

    @CreationTimestamp
    private Date createDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    Teacher teacher;

    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    Set<Registration> registrations = new HashSet<>();

    public Course() {
        this.regCount = 0;
        this.status = "모집중";
    }

    public Course(String name, int quota, Teacher teacher) {
        this.name = name;
        this.quota = quota;
        this.regCount = 0;
        this.status = "모집중";
        this.teacher = teacher;

        this.teacher.addCourse(this);
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public int getRegCount() {
        return regCount;
    }

    public void setRegCount(int regCount) {
        this.regCount = regCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void addRegistration(Registration registration) {
        this.registrations.add(registration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name) && Objects.equals(teacher, course.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, teacher);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quota=" + quota +
                ", regCount=" + regCount +
                ", status='" + status + '\'' +
                '}';
    }
}
