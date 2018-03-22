package com.example.demo;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    private Set<AppRole> roles;

    @ManyToMany(mappedBy = "userApplied")
    private Set<Programs> applied;

    @ManyToMany(mappedBy = "userApproved")
    private Set<Programs> approved;

    @ManyToMany(mappedBy = "userInCourse")
    private Set<Programs> inCourse;

    public void removeRole(AppRole role) {
        this.roles.remove(role);
    }

    public void addRole(AppRole role) {
        this.roles.add(role);
    }


    private boolean criteriaEnglish=false;
    private boolean criteriaUnemployed=false;
    private boolean criteriaUnderEmployed=false;
    private boolean criteriaComputerComfortable=false;
    private boolean criteriaItInterest=false;
    private boolean criteriaDiploma=false;
    private boolean criteriaWorkInUs=false;
    private boolean criteriaUnderstandOOP=false;
    private boolean criteriaExperienceOOP=false;
    private boolean criteriaCompSciMajor=false;
    private boolean criteriaRecentGraduate=false;
    private boolean criteriaCurrentEarnings=false;



    public AppUser() {
        this.roles = new HashSet<>();
        this.applied = new HashSet<>();
        this.approved = new HashSet<>();
        this.inCourse = new HashSet<>();
    }
    boolean[] techCriteria= new boolean[7];
    boolean[] javaCriteria=new boolean[6];

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

    public Set<Programs> getApplied() {
        return applied;
    }

    public void setApplied(Set<Programs> applied) {
        this.applied = applied;
    }

    public Set<Programs> getApproved() {
        return approved;
    }

    public void setApproved(Set<Programs> approved) {
        this.approved = approved;
    }

    public Set<Programs> getInCourse() {
        return inCourse;
    }

    public void setInCourse(Set<Programs> inCourse) {
        this.inCourse = inCourse;
    }

    public boolean isCriteriaEnglish() {
        return criteriaEnglish;
    }

    public void setCriteriaEnglish(boolean criteriaEnglish) {
        this.criteriaEnglish = criteriaEnglish;
    }

    public boolean isCriteriaUnemployed() {
        return criteriaUnemployed;
    }

    public void setCriteriaUnemployed(boolean criteriaUnemployed) {
        this.criteriaUnemployed = criteriaUnemployed;
    }

    public boolean isCriteriaUnderEmployed() {
        return criteriaUnderEmployed;
    }

    public void setCriteriaUnderEmployed(boolean criteriaUnderEmployed) {
        this.criteriaUnderEmployed = criteriaUnderEmployed;
    }

    public boolean isCriteriaComputerComfortable() {
        return criteriaComputerComfortable;
    }

    public void setCriteriaComputerComfortable(boolean criteriaComputerComfortable) {
        this.criteriaComputerComfortable = criteriaComputerComfortable;
    }

    public boolean isCriteriaItInterest() {
        return criteriaItInterest;
    }

    public void setCriteriaItInterest(boolean criteriaItInterest) {
        this.criteriaItInterest = criteriaItInterest;
    }

    public boolean isCriteriaDiploma() {
        return criteriaDiploma;
    }

    public void setCriteriaDiploma(boolean criteriaDiploma) {
        this.criteriaDiploma = criteriaDiploma;
    }

    public boolean isCriteriaWorkInUs() {
        return criteriaWorkInUs;
    }

    public void setCriteriaWorkInUs(boolean criteriaWorkInUs) {
        this.criteriaWorkInUs = criteriaWorkInUs;
    }

    public boolean isCriteriaUnderstandOOP() {
        return criteriaUnderstandOOP;
    }

    public void setCriteriaUnderstandOOP(boolean criteriaUnderstandOOP) {
        this.criteriaUnderstandOOP = criteriaUnderstandOOP;
    }

    public boolean isCriteriaExperienceOOP() {
        return criteriaExperienceOOP;
    }

    public void setCriteriaExperienceOOP(boolean criteriaExperienceOOP) {
        this.criteriaExperienceOOP = criteriaExperienceOOP;
    }

    public boolean isCriteriaCompSciMajor() {
        return criteriaCompSciMajor;
    }

    public void setCriteriaCompSciMajor(boolean criteriaCompSciMajor) {
        this.criteriaCompSciMajor = criteriaCompSciMajor;
    }

    public boolean isCriteriaRecentGraduate() {
        return criteriaRecentGraduate;
    }

    public void setCriteriaRecentGraduate(boolean criteriaRecentGraduate) {
        this.criteriaRecentGraduate = criteriaRecentGraduate;
    }

    public boolean isCriteriaCurrentEarnings() {
        return criteriaCurrentEarnings;
    }

    public void setCriteriaCurrentEarnings(boolean criteriaCurrentEarnings) {
        this.criteriaCurrentEarnings = criteriaCurrentEarnings;
    }
}
