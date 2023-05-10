package org.jhta.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Student extends Person {

    private String deleted;

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    Set<Registration> registrations = new HashSet<>();

    public Student() {
        this.deleted = "N";
    }

    // for storing
    public Student(String id, String password, String name, String phone, String email) {
        super(id, password, name, phone, email);
        this.deleted = "N";
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getDeleted() {
        return deleted;
    }

    public void addRegistration(Registration registration) {
        this.registrations.add(registration);
    }

}
