package com.example.examcooking;

public class Recipe {
    private String id;
    private String title;
    private String image;
    private String ingredients;
    private String steps;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public Recipe() {    }

    public Recipe(String id, String title, String image, String ingredients, String steps) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }
}
