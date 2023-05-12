package org.jhta.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Teacher extends Person {

    private String retired;
    @NotNull
    private int salary;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER)
    Set<Course> courses = new HashSet<>();

    public Teacher() {
        this.retired = "N";
    }

    // for storing
    public Teacher(String id, String password, String name, String phone, String email, int salary, String type) {
        super(id, password, name, phone, email, type);
        this.retired = "N";
        this.salary = salary;
    }

    public String getRetired() {
        return retired;
    }

    public void setRetired(String retired) {
        this.retired = retired;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }
}
