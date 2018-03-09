package com.example.demo;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashSet;
import java.util.Set;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private String userEmail;

    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Interests> interests;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<AppRole> roles;

/*    @ManyToMany(fetch = FetchType.EAGER)
    private  Set<Catagories> catagories;*/

    public void removeRole(AppRole role) {
        this.roles.remove(role);
    }

    public void addRole(AppRole role) {
        this.roles.add(role);
    }

    public void removeIntrest(Interests interests){
        this.interests.remove(interests);
    }

    public void addInterest(Interests interests){
        this.interests.add(interests);
    }

    public AppUser() {
        this.roles = new HashSet<>();
/*
        this.catagories = new HashSet<>();
*/
        this.interests = new HashSet<>();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<AppRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AppRole> roles) {
        this.roles = roles;
    }


    /*public Set<Catagories> getCatagories() {
        return catagories;
    }

    public void setCatagories(Set<Catagories> catagories) {
        this.catagories = catagories;
    }*/

    public Set<Interests> getInterests() {
        return interests;
    }

    public void setInterests(Set<Interests> interests) {
        this.interests = interests;
    }
}
