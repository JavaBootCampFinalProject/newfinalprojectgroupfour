package com.example.demo;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
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

    private String Search;


    @ManyToMany(fetch = FetchType.EAGER)
    private Set<AppRole> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserCategories> category;

    public void addCategory(UserCategories categories) {
        this.category.add(categories);
    }

    public void removeRole(UserCategories categories) {
        this.category.remove(categories);
    }

    public void removeRole(AppRole role) {
        this.roles.remove(role);
    }

    public void addRole(AppRole role) {
        this.roles.add(role);
    }

    public AppUser() {
        this.roles = new HashSet<>();
        this.category = new HashSet<>();
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


    public Set<UserCategories> getCategory() {
        return category;
    }

    public void setCategory(Set<UserCategories> category) {
        this.category = category;
    }

    public String getSearch() {
        return Search;
    }

    public void setSearch(AppUser user) {
        Search = search;
        if user.getCategory().
        business entertainment general health science sports technology
    }
}
