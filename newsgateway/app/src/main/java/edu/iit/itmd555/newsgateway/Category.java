package edu.iit.itmd555.newsgateway;
import java.util.ArrayList;
import java.util.List;

public class Category {
    private String category;
    private List<Source> sources;
    private String color;

    public Category(String category) {
        this.category = category;
        this.sources = new ArrayList<>();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
}
