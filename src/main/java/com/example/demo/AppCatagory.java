package com.example.demo;

import javax.persistence.*;
import java.util.Set;

@Entity
public class AppCatagory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String catagoryName;

    @ManyToMany(mappedBy = "appCatagory")
    private Set<AppUser> userCatagory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCatagoryName() {
        return catagoryName;
    }

    public void setCatagoryName(String catagoryName) {
        this.catagoryName = catagoryName;
    }

    public Set<AppUser> getUserCatagory() {
        return userCatagory;
    }

    public void setUserCatagory(Set<AppUser> userCatagory) {
        this.userCatagory = userCatagory;
    }
}
