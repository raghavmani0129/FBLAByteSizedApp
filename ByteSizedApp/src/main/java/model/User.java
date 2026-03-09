package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class User {

    private String name;
    private String email;
    private String password;
    private Set<String> favoriteBusinessIds;
    private boolean tutorialSeen;
    private List<String> viewedBusinessIds;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.favoriteBusinessIds = new HashSet<>();
        this.tutorialSeen = false;
        this.viewedBusinessIds = new ArrayList<>();
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
        if (favoriteBusinessIds == null) {
            favoriteBusinessIds = new HashSet<>();
        }
        return favoriteBusinessIds;
    }

    public List<String> getViewedBusinessIds() {
        if (viewedBusinessIds == null) {
            viewedBusinessIds = new ArrayList<>();
        }
        return viewedBusinessIds;
    }

    public boolean isTutorialSeen() {
        return tutorialSeen;
    }

    public void setTutorialSeen(boolean tutorialSeen) {
        this.tutorialSeen = tutorialSeen;
    }

    // favorites
    public void addFavorite(String businessId) {
        getFavoriteBusinessIds().add(businessId);
    }

    public void removeFavorite(String businessId) {
        getFavoriteBusinessIds().remove(businessId);
    }

    public boolean isFavorite(String businessId) {
        return getFavoriteBusinessIds().contains(businessId);
    }

    public void addViewedBusiness(String businessId) {
        if (businessId == null || businessId.isBlank()) return;

        List<String> history = getViewedBusinessIds();
        history.removeIf(id -> id != null && id.equalsIgnoreCase(businessId));
        history.add(0, businessId);

        int max = 30;
        if (history.size() > max) {
            history.subList(max, history.size()).clear();
        }
    }
}
