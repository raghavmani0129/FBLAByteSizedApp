package model;

import java.time.LocalDate;

public class Deal {

    private String businessName;
    private String description;
    private LocalDate expirationDate;

    public Deal(String businessName, String description, LocalDate expirationDate) {
        this.businessName = businessName;
        this.description = description;
        this.expirationDate = expirationDate;
    }

    //getters
    public String getBusinessName() {
        return businessName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    // -------------------- Setters --------------------
    public void setDescription(String description) {
        this.description = description;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}