package com.pucp.lab6_20175719.Entry;

import java.io.Serializable;

public class ListElementEntry implements Serializable {
    private String id;
    private String title;
    private double mount;
    private String description;
    private String date;

    public ListElementEntry() {}

    public ListElementEntry(String id, String title, double mount, String description, String date) {
        this.id = id;
        this.title = title;
        this.mount = mount;
        this.description = description;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMount() {
        return mount;
    }

    public void setMount(double mount) {
        this.mount = mount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
