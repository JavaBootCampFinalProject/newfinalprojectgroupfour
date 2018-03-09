package com.example.demo;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Interests {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String interest;

    @ManyToMany(mappedBy = "interests")
    private Set<AppUser> interests;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public Set<AppUser> getInterests() {
        return interests;
    }

    public void setInterests(Set<AppUser> interests) {
        this.interests = interests;
    }
}
