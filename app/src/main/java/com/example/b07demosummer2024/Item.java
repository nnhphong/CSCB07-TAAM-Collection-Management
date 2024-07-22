package com.example.b07demosummer2024;

import android.net.Uri;

public class Item {
    private Integer lot_number;
    private String name;
    private String category;
    private String period;
    private String description;
    private Uri media;

    public Item() {
        lot_number = null;
        name = "";
        category = "";
        period = "";
        description = "";
    }

    public Item(Integer lot_number, String name, String category, String period, String description) {
        this.lot_number = lot_number;
        this.name = name;
        this.category = category;
        this.period = period;
        this.description = description;
    }

    // Getters and setters
    public Integer getLotNumber() { return this.lot_number; }
    public void setLotNumber(Integer lot_number) { this.lot_number = lot_number; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
