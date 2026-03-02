package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class is responsible for managing data in the application.
 * It provides methods for loading and saving data, as well as for performing various operations on the data.
 */
public class DataManager {

    private static DataManager instance;
    //make data file paths
    private final String USER_FILE = "data/users.json";
    private final String BUSINESS_FILE = "data/businesses.json";
    private final String REVIEW_FILE = "data/reviews.json";
    private final String DEAL_FILE = "data/deals.json";

    private List<User> users;
    private List<Business> businesses;
    private List<Review> reviews;
    private List<Deal> deals;

    private final Gson gson = new Gson();

    //pattersn singleton
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private DataManager() {
        users = loadList(USER_FILE, new TypeToken<List<User>>(){}.getType());
        businesses = loadList(BUSINESS_FILE, new TypeToken<List<Business>>(){}.getType());
        reviews = loadList(REVIEW_FILE, new TypeToken<List<Review>>(){}.getType());
        deals = loadList(DEAL_FILE, new TypeToken<List<Deal>>(){}.getType());
    }

    private <T> List<T> loadList(String filePath, Type type) {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private <T> void saveList(String filePath, List<T> list) {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        } catch (Exception ignored) {
        }

        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.err.println("Failed to save " + filePath);
        }
    }

    //User Checks

    //check if email exists
    public boolean emailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    //add new user
    public void addUser(User user) {
        users.add(user);
        saveList(USER_FILE, users);
    }

    //create new account
    public boolean createUser(String name, String email, String password) {
        if (emailExists(email)) {
            return false;
        }
        User newUser = new User(name, email, password);
        addUser(newUser);
        return true;
    }

    //check login info
    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    //check against stored login info
    public boolean validateLogin(String email, String password) {
        return login(email, password) != null;
    }

    //track what each user is doing and associate their movements with their account
    private User currentUser = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateUser(User user) {
        if (user == null || user.getEmail() == null) return;

        for (int i = 0; i < users.size(); i++) {
            User existing = users.get(i);
            if (existing.getEmail() != null && existing.getEmail().equalsIgnoreCase(user.getEmail())) {
                users.set(i, user);
                saveList(USER_FILE, users);
                return;
            }
        }

        users.add(user);
        saveList(USER_FILE, users);
    }

    //business methods

    //get all businesses
    public List<Business> getBusinesses() {
        return businesses;
    }

    //filter by category
    public List<Business> getBusinessesByCategory(String category) {
        List<Business> result = new ArrayList<>();
        for (Business b : businesses) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                result.add(b);
            }
        }
        return result;
    }

    //get bsusiness by id
    public Business getBusinessById(String id) {
        for (Business b : businesses) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    //get them by name
    public Business getBusinessByName(String name) {
        for (Business b : businesses) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    //sort by rating
    public List<Business> sortBusinessesByRating(List<Business> businessList) {
        List<Business> sorted = new ArrayList<>(businessList);
        sorted.sort((b1, b2) -> Double.compare(b2.getRating(), b1.getRating()));
        return sorted;
    }

    public List<Business> sortBusinessesByName(List<Business> businessList) {
        List<Business> sorted = new ArrayList<>(businessList);
        sorted.sort(Comparator.comparing(Business::getName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public int getReviewCountForBusiness(String businessName) {
        if (businessName == null) return 0;
        int count = 0;
        for (Review r : reviews) {
            if (r.getBusinessName() != null && r.getBusinessName().equalsIgnoreCase(businessName)) {
                count++;
            }
        }
        return count;
    }

    public List<Business> sortBusinessesByReviewCount(List<Business> businessList) {
        List<Business> sorted = new ArrayList<>(businessList);
        sorted.sort((b1, b2) -> Integer.compare(
                getReviewCountForBusiness(b2.getName()),
                getReviewCountForBusiness(b1.getName())
        ));
        return sorted;
    }

    //add new business
    public void addBusiness(Business business) {
        businesses.add(business);
        saveList(BUSINESS_FILE, businesses);
    }

    // Reviewing

    public void addReview(Review review) {
        reviews.add(review);
        saveList(REVIEW_FILE, reviews);
        updateBusinessRating(review.getBusinessName());
    }

    public List<Review> getReviewsForBusiness(String businessName) {
        List<Review> result = new ArrayList<>();
        for (Review r : reviews) {
            if (r.getBusinessName().equalsIgnoreCase(businessName)) {
                result.add(r);
            }
        }
        return result;
    }

    private void updateBusinessRating(String businessName) {
        double sum = 0;
        int count = 0;
        for (Review r : reviews) {
            if (r.getBusinessName().equalsIgnoreCase(businessName)) {
                sum += r.getStars();
                count++;
            }
        }
        for (Business b : businesses) {
            if (b.getName().equalsIgnoreCase(businessName) && count > 0) {
                b.setRating(sum / count);
                saveList(BUSINESS_FILE, businesses);
                break;
            }
        }
    }

    //deals

   //get all deals
    public List<Deal> getDeals() {
        return deals;
    }

    //deals for specific business
    public List<Deal> getDealsForBusiness(String businessName) {
        List<Deal> result = new ArrayList<>();
        for (Deal d : deals) {
            if (d.getBusinessName().equalsIgnoreCase(businessName)) {
                result.add(d);
            }
        }
        return result;
    }

    //add a new deal
    public void addDeal(Deal deal) {
        deals.add(deal);
        saveList(DEAL_FILE, deals);
        // Mark the business as having deal
        Business business = getBusinessByName(deal.getBusinessName());
        if (business != null) {
            business.setHasDeal(true);
            saveList(BUSINESS_FILE, businesses);
        }
    }

    //all businesses with deals
    public List<Business> getBusinessesWithDeals() {
        List<Business> result = new ArrayList<>();
        for (Business b : businesses) {
            if (b.hasDeal()) {
                result.add(b);
            }
        }
        return result;
    }
}
