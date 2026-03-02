package model;

public class Review {
    //variables for review
    private String userEmail;  
    private String businessName; 
    private int stars;          
    private String text;        

    public Review(String userEmail, String businessName, int stars, String text) {
        this.userEmail = userEmail;
        this.businessName = businessName;
        this.stars = stars;
        this.text = text;
    }

    //getters
    public String getUserEmail() {
        return userEmail;
    }

    public String getBusinessName() {
        return businessName;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }

    // setters
    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setText(String text) {
        this.text = text;
    }
}