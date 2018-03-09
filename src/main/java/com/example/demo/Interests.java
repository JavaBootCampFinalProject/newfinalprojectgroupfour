package com.example.demo;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Interests {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String interestName;

    @ManyToMany(mappedBy = "interests")
    private Set<AppUser> userIntrests;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public Set<AppUser> getUserIntrests() {
        return userIntrests;
    }

    public void setUserIntrests(Set<AppUser> userIntrests) {
        this.userIntrests = userIntrests;
    }
}
