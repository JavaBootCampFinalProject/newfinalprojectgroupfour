package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

@JsonIgnoreProperties(ignoreUnknown = true)

public class News {
    private ArrayList<Articles> articles;

    public News() {
        this.articles = new ArrayList<>();
    }


    public void addArticles(Articles articles){
        this.articles.add(articles);
    }


    public ArrayList<Articles> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Articles> articles) {
        this.articles = articles;
    }
}
