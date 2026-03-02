package model;

import java.util.HashSet;
import java.util.Set;


public class User {

    private String name;
    private String email;
    private String password;
    private Set<String> favoriteBusinessIds;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.favoriteBusinessIds = new HashSet<>();
    }

    //getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getFavoriteBusinessIds() {
        return favoriteBusinessIds;
    }

    // favorites
    public void addFavorite(String businessId) {
        favoriteBusinessIds.add(businessId);
    }

    public void removeFavorite(String businessId) {
        favoriteBusinessIds.remove(businessId);
    }

    public boolean isFavorite(String businessId) {
        return favoriteBusinessIds.contains(businessId);
    }
}