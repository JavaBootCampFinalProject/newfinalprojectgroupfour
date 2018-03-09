//package com.example.demo;
//
//import javax.persistence.*;
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//public class Catagories {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private long id;
//
//
//    private boolean business;
//    private boolean entertainment;
//    private boolean general;
//    private boolean health;
//    private boolean science;
//    private boolean sports;
//    private boolean technology;
//
//
//    @ManyToMany(mappedBy = "categories")
//    private Set<AppUser> category;
//
//    public Catagories(){
//        this.category = new HashSet<>();
//        this.business = false;
//        this.entertainment = false;
//        this.general = false;
//        this.science = false;
//        this.health = false;
//        this.sports = false;
//        this.technology = false;
//
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public boolean isBusiness() {
//        return business;
//    }
//
//    public void setBusiness(boolean business) {
//        this.business = business;
//    }
//
//    public boolean isEntertainment() {
//        return entertainment;
//    }
//
//    public void setEntertainment(boolean entertainment) {
//        this.entertainment = entertainment;
//    }
//
//    public boolean isGeneral() {
//        return general;
//    }
//
//    public void setGeneral(boolean general) {
//        this.general = general;
//    }
//
//    public boolean isHealth() {
//        return health;
//    }
//
//    public void setHealth(boolean health) {
//        this.health = health;
//    }
//
//    public boolean isScience() {
//        return science;
//    }
//
//    public void setScience(boolean science) {
//        this.science = science;
//    }
//
//    public boolean isSports() {
//        return sports;
//    }
//
//    public void setSports(boolean sports) {
//        this.sports = sports;
//    }
//
//    public boolean isTechnology() {
//        return technology;
//    }
//
//    public void setTechnology(boolean technology) {
//        this.technology = technology;
//    }
//
//    public Set<AppUser> getCategory() {
//        return category;
//    }
//
//    public void setCategory(Set<AppUser> category) {
//        this.category = category;
//    }
//}
//
