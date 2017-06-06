package com.cassiomolin.example.model;


import java.time.LocalDate;
import java.util.List;

/**
 * Model class to represent a shopping list.
 *
 * @author cassiomolin
 */
public class ShoppingList {

    private String id;
    private String name;
    private LocalDate date;
    private List<String> items;
    private String fileName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
