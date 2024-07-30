package com.example.b07demosummer2024;

public class Item implements Comparable<Item> {
    private Integer lot_number;
    private String name;
    private String category;
    private String period;
    private String description;
    private String mediaLink;
    private String mediaType;
    private boolean selected;


    public Item() {
        lot_number = null;
        name = "";
        category = "";
        period = "";
        description = "";
        mediaLink = "";
        mediaType = "";
        selected = false;
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
    public String getMediaLink() { return mediaLink; }
    public void setMediaLink(String mediaLink) { this.mediaLink = mediaLink; }
    public boolean getSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    @Override
    public int compareTo(Item item) {
        return this.lot_number.compareTo(item.lot_number);
    }
}
