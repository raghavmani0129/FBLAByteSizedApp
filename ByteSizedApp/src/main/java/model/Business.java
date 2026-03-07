package model;


public class Business {
    //variables for business object
    private String id;          
    private String name;
    private String category;
    private String address;
    private double rating;      
    private String description;
    private boolean hasDeal;    


    //constructor with all the fields
    public Business(String id, String name, String category, String address, double rating, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.address = address;
        this.rating = rating;
        this.description = description;
        this.hasDeal = false;
    }

    //constructing idea w/o id - use to generate id
    public Business(String name, String category, String address, double rating, String description) {
        this.id = generateIdFromName(name);
        this.name = name;
        this.category = category;
        this.address = address;
        this.rating = rating;
        this.description = description;
        this.hasDeal = false;
    }

    //method to generate id from name
    private String generateIdFromName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }

    //getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDeal() {
        return hasDeal;
    }

    //setters
    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHasDeal(boolean hasDeal) {
        this.hasDeal = hasDeal;
    }

    //formats business for display
    @Override
    public String toString() {
        return String.format("%s (%.1f★) - %s", name, rating, category);
    }
}
