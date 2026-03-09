package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Window;
import model.Business;
import model.Deal;
import model.ForumReply;
import model.ForumThread;
import model.Review;
import model.User;
import util.DataManager;
import util.ValidationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

/**
 * Main controller for the Byte-Sized Business Boost application.
 */
public class MainController {

    // 6 Categories
    @FXML private ToggleButton foodTab;
    @FXML private ToggleButton allTab;
    @FXML private ToggleButton retailTab;
    @FXML private ToggleButton servicesTab;
    @FXML private ToggleButton healthTab;
    @FXML private ToggleButton specialtyTab;
    @FXML private ToggleButton professionalsTab;
    // ToggleGroup 
    private ToggleGroup categoryToggleGroup;

    // Filter toggles
    @FXML private CheckBox favoritesOnly;
    @FXML private CheckBox dealsOnly;

    // Sort + report + recommendations
    @FXML private ComboBox<String> sortMode;
    @FXML private Button reportButton;
    @FXML private Button faqButton;
    @FXML private ListView<Business> recommendedList;

    // Business List
    @FXML private ListView<Business> businessList;

    // Business Information
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label ratingLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button favoriteButton;
    @FXML private VBox dealsBox;
    @FXML private VBox reviewsBox;
    @FXML private ScrollPane reviewsScrollPane;
    @FXML private TextField searchField;
    @FXML private TextField locationField;

    // Review submission controls
    @FXML private TextField reviewTextArea;
    @FXML private ToggleButton star1;
    @FXML private ToggleButton star2;
    @FXML private ToggleButton star3;
    @FXML private ToggleButton star4;
    @FXML private ToggleButton star5;
    @FXML private Button submitReviewButton;

    private final DataManager dataManager = DataManager.getInstance();
    private ObservableList<Business> allBusinesses = FXCollections.observableArrayList();
    private Business selectedBusiness = null;
    private ToggleGroup starRatingGroup;

    /**
     * Initialize when Main screen loads.
     */
    @FXML
    public void initialize() {
        // Only select one star
        starRatingGroup = new ToggleGroup();
        star1.setToggleGroup(starRatingGroup);
        star2.setToggleGroup(starRatingGroup);
        star3.setToggleGroup(starRatingGroup);
        star4.setToggleGroup(starRatingGroup);
        star5.setToggleGroup(starRatingGroup);

        // Business data
        loadBusinesses();

        // Search field
        if (searchField != null) searchField.clear();

        // Selection listener for business list
        businessList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                User currentUser = dataManager.getCurrentUser();
                if (currentUser != null) {
                    currentUser.addViewedBusiness(newVal.getId());
                    dataManager.updateUser(currentUser);
                }
                showBusinessDetails(newVal);
            }
        });

        // Favorite button
        favoriteButton.setOnAction(e -> toggleFavorite());

        // Review submission
        submitReviewButton.setOnAction(e -> submitReview());

        // Make toggles mutually exclusive
        categoryToggleGroup = new ToggleGroup();
        allTab.setToggleGroup(categoryToggleGroup);
        foodTab.setToggleGroup(categoryToggleGroup);
        retailTab.setToggleGroup(categoryToggleGroup);
        servicesTab.setToggleGroup(categoryToggleGroup);
        healthTab.setToggleGroup(categoryToggleGroup);
        specialtyTab.setToggleGroup(categoryToggleGroup);
        professionalsTab.setToggleGroup(categoryToggleGroup);

        if (sortMode != null) {
            sortMode.setItems(FXCollections.observableArrayList(
                    "Rating (High to Low)",
                    "Most Reviewed",
                    "Name (A to Z)"
            ));
            sortMode.getSelectionModel().selectFirst();
            sortMode.setOnAction(e -> applyFilters());
        }

        if (reportButton != null) {
            reportButton.setOnAction(e -> showReport());
        }

        if (faqButton != null) {
            faqButton.setOnAction(e -> showFaq());
        }

        // Select default category as "All" (show all businesses at first)
        allTab.setSelected(true);
        handleCategoryTab();

        if (dataManager.consumeJustSignedUp()) {
            User currentUser = dataManager.getCurrentUser();
            if (currentUser != null) {
                Platform.runLater(() -> showWalkthrough(currentUser));
            }
        }
    }

    /**
     * Load businesses from data manager.
     */
    private void loadBusinesses() {
        List<Business> businesses = dataManager.getBusinesses();

        if (businesses.isEmpty()) {
            initializeSampleData();
            businesses = dataManager.getBusinesses();
        }

        allBusinesses.clear();
        allBusinesses.addAll(businesses);
        businessList.setItems(allBusinesses);
    }

    // Sample businesses to test with
    private void initializeSampleData() {
        Business cafe = new Business("joe_cafe", "Joe's Cafe", "Food", "123 Main St", 4.5,
                "Cozy family-owned cafe serving fresh coffee and homemade pastries.");
        Business techFix = new Business("tech_fix", "Tech Fix", "Specialty", "456 Oak Rd", 4.0,
                "Repair laptops, phones, and tablets. Fast and reliable service.");
        Business yoga = new Business("green_yoga", "Green Yoga", "Health", "789 Pine Ave", 5.0,
                "Yoga and wellness studio offering classes for all levels.");
        Business boutique = new Business("style_boutique", "Style Boutique", "Retail", "321 Elm St", 3.5,
                "Clothing and accessories from local designers.");
        Business autoPro = new Business("auto_pro", "AutoPro", "Services", "654 Maple Blvd", 4.2,
                "Auto repair shop specializing in foreign and domestic vehicles.");
        Business photoStudio = new Business("photo_studio", "Local Lens Photography", "Local Professionals",
                "890 Cedar Ln", 4.8, "Professional photography services for events and portraits.");

        dataManager.addBusiness(cafe);
        dataManager.addBusiness(techFix);
        dataManager.addBusiness(yoga);
        dataManager.addBusiness(boutique);
        dataManager.addBusiness(autoPro);
        dataManager.addBusiness(photoStudio);

        // More samples
        Business bakery = new Business("urban_bakery", "Urban Bakery", "Food", "111 Baker St", 4.3,
                "Artisan breads, sandwiches, and pastries.");
        Business bookstore = new Business("page_turner", "Page Turner Books", "Retail", "22 Library Rd", 4.6,
                "Independent bookstore with curated selections.");
        Business cleaners = new Business("speedy_cleaners", "Speedy Cleaners", "Services", "77 Clean Ave", 3.9,
                "Dry cleaning and laundry services with pickup.");
        Business spa = new Business("oasis_spa", "Oasis Spa", "Health", "9 Relax Blvd", 4.7,
                "Full-service spa and wellness treatments.");
        Business bikeShop = new Business("pedal_power", "Pedal Power", "Specialty", "303 Cycle Ln", 4.2,
                "Bicycle sales, service, and rentals.");
        Business accountant = new Business("numbers_plus", "Numbers Plus Accounting", "Local Professionals", "500 Ledger St", 4.4,
                "Tax preparation and bookkeeping services.");

        dataManager.addBusiness(bakery);
        dataManager.addBusiness(bookstore);
        dataManager.addBusiness(cleaners);
        dataManager.addBusiness(spa);
        dataManager.addBusiness(bikeShop);
        dataManager.addBusiness(accountant);

        // Sample Deals
        Deal cafeDeal = new Deal("Joe's Cafe", "20% off all pastries", LocalDate.now().plusDays(30));
        Deal techDeal = new Deal("Tech Fix", "Free diagnostic with any repair", LocalDate.now().plusDays(14));
        Deal bakeryDeal = new Deal("Urban Bakery", "Buy one pastry, get one 50% off", LocalDate.now().plusDays(10));
        Deal bookstoreDeal = new Deal("Page Turner Books", "10% off bestsellers", LocalDate.now().plusDays(25));
        dataManager.addDeal(cafeDeal);
        dataManager.addDeal(techDeal);
        dataManager.addDeal(bakeryDeal);
        dataManager.addDeal(bookstoreDeal);

        // Sample reviews
        User sampleUser = new User("Sample User", "user@example.com", "Password123");
        dataManager.addUser(sampleUser);
        dataManager.addReview(new Review("user@example.com", "Joe's Cafe", 5,
                "Best coffee in town! The pastries are amazing."));
        dataManager.addReview(new Review("user@example.com", "Green Yoga", 5,
                "Great instructors and a peaceful atmosphere."));
    }

    private void showBusinessDetails(Business business) {
        if (business == null) return;

        selectedBusiness = business;

        // Basic info
        nameLabel.setText(business.getName());
        addressLabel.setText("Address: " + (business.getAddress() != null ? business.getAddress() : ""));
        ratingLabel.setText(String.format("Rating: %.1f / 5.0", business.getRating()));
        descriptionArea.setText(business.getDescription());

        // Favorite button
        updateFavoriteButton();

        // Display deals
        displayDeals(business);

        // Display reviews
        displayReviews(business);

        // Reset review submission form
        resetReviewForm();
    }

    // Update favorite button depending on whether business is pressed or not
    private void updateFavoriteButton() {
        if (selectedBusiness == null) return;

        User currentUser = dataManager.getCurrentUser();
        if (currentUser == null) {
            favoriteButton.setText("☆ Favorite");
            favoriteButton.getStyleClass().removeAll("favorited");
            return;
        }

        boolean isFavorite = currentUser.isFavorite(selectedBusiness.getId());
        if (isFavorite) {
            favoriteButton.setText("★ Favorited");
            if (!favoriteButton.getStyleClass().contains("favorited")) {
                favoriteButton.getStyleClass().add("favorited");
            }
        } else {
            favoriteButton.setText("☆ Favorite");
            favoriteButton.getStyleClass().removeAll("favorited");
        }
    }

    // Favorite button action
    @FXML
    private void toggleFavorite() {
        if (selectedBusiness == null) return;

        User currentUser = dataManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Please log in to favorite businesses.");
            return;
        }

        String businessId = selectedBusiness.getId();
        if (currentUser.isFavorite(businessId)) {
            currentUser.removeFavorite(businessId);
        } else {
            currentUser.addFavorite(businessId);
        }

        dataManager.updateUser(currentUser);

        updateFavoriteButton();

        // Refresh the list if favorites filter is active
        if (favoritesOnly.isSelected()) {
            applyFilters();
        }

        refreshRecommendations();
    }

    // Display deals
    private void displayDeals(Business business) {
        dealsBox.getChildren().clear();

        List<Deal> deals = dataManager.getDealsForBusiness(business.getName());

        if (deals.isEmpty()) {
            Label noDeals = new Label("No current deals available.");
            noDeals.getStyleClass().add("subtle");
            dealsBox.getChildren().add(noDeals);
        } else {
            for (Deal deal : deals) {
                VBox dealBox = new VBox(5);
                dealBox.getStyleClass().add("deal-box");
                Label dealDesc = new Label("Deal: " + deal.getDescription());
                dealDesc.getStyleClass().add("deal-title");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                Label expiry = new Label("Expires: " + deal.getExpirationDate().format(formatter));
                expiry.getStyleClass().add("subtle");

                dealBox.getChildren().addAll(dealDesc, expiry);
                dealsBox.getChildren().add(dealBox);
            }
        }
    }

    // Display reviews
    private void displayReviews(Business business) {
        reviewsBox.getChildren().clear();

        List<Review> reviews = dataManager.getReviewsForBusiness(business.getName());

        if (reviews.isEmpty()) {
            Label noReviews = new Label("No reviews yet. Be the first to review!");
            noReviews.getStyleClass().add("subtle");
            reviewsBox.getChildren().add(noReviews);
        } else {
            for (Review review : reviews) {
                VBox reviewBox = new VBox(5);
                reviewBox.getStyleClass().add("review-box");

                // Star rating
                String stars = "★".repeat(review.getStars()) + "☆".repeat(5 - review.getStars());
                Label starsLabel = new Label(stars);
                starsLabel.getStyleClass().add("review-stars");

                // Text
                if (review.getText() != null && !review.getText().isEmpty()) {
                    Text reviewText = new Text(review.getText());
                    reviewText.setWrappingWidth(400);
                    reviewBox.getChildren().addAll(starsLabel, reviewText);
                } else {
                    reviewBox.getChildren().add(starsLabel);
                }

                User currentUser = dataManager.getCurrentUser();
                if (currentUser != null
                        && review.getUserEmail() != null
                        && currentUser.getEmail() != null
                        && review.getUserEmail().equalsIgnoreCase(currentUser.getEmail())) {
                    Button deleteButton = new Button("Delete");
                    deleteButton.getStyleClass().add("secondary");
                    deleteButton.setOnAction(e -> handleDeleteReview(review));
                    HBox actions = new HBox(10, deleteButton);
                    reviewBox.getChildren().add(actions);
                }

                reviewsBox.getChildren().add(reviewBox);
            }
        }
    }

    private void handleDeleteReview(Review review) {
        if (review == null) return;
        User currentUser = dataManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Please log in to delete your review.");
            return;
        }
        if (review.getUserEmail() == null || currentUser.getEmail() == null
                || !review.getUserEmail().equalsIgnoreCase(currentUser.getEmail())) {
            showAlert("You can only delete your own reviews.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Review");
        confirm.setHeaderText("Delete your review?");
        confirm.setContentText("This action cannot be undone.");
        applyThemeToDialog(confirm.getDialogPane());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        boolean deleted = dataManager.deleteReview(review);
        if (!deleted) {
            showAlert("Could not delete review. Please try again.");
            return;
        }
        if (selectedBusiness != null) {
            showBusinessDetails(selectedBusiness);
        }
        showAlert("Review deleted.");
    }

    // Submit review + business rating
    private void submitReview() {
        if (selectedBusiness == null) {
            showAlert("Please select a business first.");
            return;
        }

        User currentUser = dataManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Please log in to submit a review.");
            return;
        }

        // Star rating
        Toggle selectedStar = starRatingGroup.getSelectedToggle();
        if (selectedStar == null) {
            showAlert("Please select a star rating.");
            return;
        }

        int rating = 0;
        if (selectedStar == star1) rating = 1;
        else if (selectedStar == star2) rating = 2;
        else if (selectedStar == star3) rating = 3;
        else if (selectedStar == star4) rating = 4;
        else if (selectedStar == star5) rating = 5;

        if (!ValidationUtil.isValidRating(rating)) {
            showAlert("Invalid rating. Please select 1-5 stars.");
            return;
        }

        // Text review
        String reviewText = reviewTextArea.getText() != null ? reviewTextArea.getText().trim() : "";
        if (reviewText.isEmpty()) {
            showAlert("Review text cannot be blank.");
            return;
        }

        // Create and save
        Review review = new Review(currentUser.getEmail(), selectedBusiness.getName(), rating, reviewText);
        dataManager.addReview(review);

        // Refresh display
        showBusinessDetails(selectedBusiness);
        showAlert("Review submitted successfully!");
    }

    // Reset submission form
    private void resetReviewForm() {
        starRatingGroup.selectToggle(null);
        reviewTextArea.clear();
    }

    // Category tab selection
    @FXML
    private void handleCategoryTab() {
        String category = null;
        if (foodTab.isSelected()) category = "Food";
        else if (retailTab.isSelected()) category = "Retail";
        else if (servicesTab.isSelected()) category = "Services";
        else if (healthTab.isSelected()) category = "Health";
        else if (specialtyTab.isSelected()) category = "Specialty";
        else if (professionalsTab.isSelected()) category = "Local Professionals";

        applyFilters(category);
    }

    // Apply active filters
    private void applyFilters() {
        applyFilters(getSelectedCategory());
    }

    private void applyFilters(String category) {
        List<Business> filtered;
        if (category == null || category.isEmpty()) {
            filtered = new java.util.ArrayList<>(dataManager.getBusinesses());
        } else {
            filtered = dataManager.getBusinessesByCategory(category);
        }

        // Apply favorites filter
        if (favoritesOnly.isSelected()) {
            User currentUser = dataManager.getCurrentUser();
            if (currentUser != null) {
                filtered.removeIf(b -> !currentUser.isFavorite(b.getId()));
            }
        }

        // Apply deals filter
        if (dealsOnly.isSelected()) {
            filtered.removeIf(b -> !b.hasDeal());
        }

        // Sort based on selected mode
        String mode = (sortMode != null && sortMode.getValue() != null) ? sortMode.getValue() : "Rating (High to Low)";
        if ("Most Reviewed".equals(mode)) {
            filtered = dataManager.sortBusinessesByReviewCount(filtered);
        } else if ("Name (A to Z)".equals(mode)) {
            filtered = dataManager.sortBusinessesByName(filtered);
        } else {
            filtered = dataManager.sortBusinessesByRating(filtered);
        }

        // Apply search filter
        String query = (searchField != null) ? searchField.getText().trim().toLowerCase() : "";
        if (!query.isEmpty()) {
            filtered.removeIf(b -> !(b.getName().toLowerCase().contains(query)
                    || b.getAddress().toLowerCase().contains(query)
                    || b.getCategory().toLowerCase().contains(query)));
        }

        // Apply location filter (town or ZIP)
        String locationQuery = (locationField != null) ? locationField.getText().trim().toLowerCase() : "";
        if (!locationQuery.isEmpty()) {
            filtered.removeIf(b -> b.getAddress() == null || !b.getAddress().toLowerCase().contains(locationQuery));
        }

        // Update the list view
        ObservableList<Business> observableList = FXCollections.observableArrayList(filtered);
        businessList.setItems(observableList);

        refreshRecommendations();
    }

    private void refreshRecommendations() {
        if (recommendedList == null) return;

        User currentUser = dataManager.getCurrentUser();
        List<Business> all = new ArrayList<>(dataManager.getBusinesses());

        // If user has favorites, recommend from those categories first
        Set<String> favoriteCategories = new HashSet<>();
        if (currentUser != null) {
            for (String businessId : currentUser.getFavoriteBusinessIds()) {
                Business b = dataManager.getBusinessById(businessId);
                if (b != null && b.getCategory() != null) {
                    favoriteCategories.add(b.getCategory().toLowerCase());
                }
            }
        }

        List<Business> recommended = new ArrayList<>();
        for (Business b : all) {
            if (selectedBusiness != null && b.getId().equals(selectedBusiness.getId())) continue;
            if (!favoriteCategories.isEmpty() && b.getCategory() != null && favoriteCategories.contains(b.getCategory().toLowerCase())) {
                recommended.add(b);
            }
        }

        // Fallback to top-rated if no favorites/categories
        if (recommended.isEmpty()) {
            recommended.addAll(all);
        }

        recommended = dataManager.sortBusinessesByRating(recommended);
        if (recommended.size() > 5) {
            recommended = recommended.subList(0, 5);
        }

        recommendedList.setItems(FXCollections.observableArrayList(recommended));
    }

    private void showReport() {
        List<Business> currentList = businessList.getItems() != null ? new ArrayList<>(businessList.getItems()) : new ArrayList<>();
        int totalBusinesses = currentList.size();

        double avgRating = 0;
        if (!currentList.isEmpty()) {
            double sum = 0;
            for (Business b : currentList) sum += b.getRating();
            avgRating = sum / currentList.size();
        }

        int totalReviews = 0;
        for (Business b : currentList) {
            totalReviews += dataManager.getReviewCountForBusiness(b.getName());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Your Report\n\n");

        User currentUser = dataManager.getCurrentUser();
        if (currentUser == null) {
            sb.append("Log in to see your viewing history, favorites, and reviews.\n\n");
        } else {
            sb.append("Account: ").append(currentUser.getEmail() != null ? currentUser.getEmail() : "").append("\n\n");

            sb.append("Recently viewed:\n");
            List<String> viewed = currentUser.getViewedBusinessIds();
            if (viewed.isEmpty()) {
                sb.append("- No businesses viewed yet\n");
            } else {
                for (int i = 0; i < Math.min(10, viewed.size()); i++) {
                    Business b = dataManager.getBusinessById(viewed.get(i));
                    sb.append("- ").append(b != null ? b.getName() : viewed.get(i)).append("\n");
                }
            }

            sb.append("\nFavorites:\n");
            Set<String> favorites = currentUser.getFavoriteBusinessIds();
            if (favorites.isEmpty()) {
                sb.append("- No favorites yet\n");
            } else {
                int count = 0;
                for (String id : favorites) {
                    if (count >= 10) break;
                    Business b = dataManager.getBusinessById(id);
                    sb.append("- ").append(b != null ? b.getName() : id).append("\n");
                    count++;
                }
            }

            sb.append("\nBusinesses you reviewed:\n");
            List<Review> myReviews = dataManager.getReviewsForUser(currentUser.getEmail());
            if (myReviews.isEmpty()) {
                sb.append("- No reviews yet\n");
            } else {
                Set<String> reviewedBusinesses = new HashSet<>();
                for (Review r : myReviews) {
                    if (r != null && r.getBusinessName() != null) {
                        reviewedBusinesses.add(r.getBusinessName());
                    }
                }
                int count = 0;
                for (String name : reviewedBusinesses) {
                    if (count >= 10) break;
                    sb.append("- ").append(name).append("\n");
                    count++;
                }
            }

            sb.append("\n---\n\n");
        }

        sb.append("Businesses shown (current filters)\n\n");
        sb.append("Businesses shown: ").append(totalBusinesses).append("\n");
        sb.append(String.format("Average rating: %.2f\n", avgRating));
        sb.append("Total reviews (across shown businesses): ").append(totalReviews).append("\n\n");

        sb.append("Top 5 by rating:\n");
        List<Business> byRating = dataManager.sortBusinessesByRating(currentList);
        for (int i = 0; i < Math.min(5, byRating.size()); i++) {
            Business b = byRating.get(i);
            sb.append("- ").append(b.getName())
                    .append(String.format(" (%.1f★, %d reviews)", b.getRating(), dataManager.getReviewCountForBusiness(b.getName())))
                    .append("\n");
        }

        sb.append("\nTop 5 by review count:\n");
        List<Business> byReviews = dataManager.sortBusinessesByReviewCount(currentList);
        for (int i = 0; i < Math.min(5, byReviews.size()); i++) {
            Business b = byReviews.get(i);
            sb.append("- ").append(b.getName())
                    .append(String.format(" (%d reviews, %.1f★)", dataManager.getReviewCountForBusiness(b.getName()), b.getRating()))
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText("Byte-Sized Business Boost - Report");

        TextArea reportArea = new TextArea(sb.toString());
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.getStyleClass().add("report-area");
        reportArea.setMaxWidth(Double.MAX_VALUE);
        reportArea.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        alert.getDialogPane().setContent(reportArea);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        applyThemeToDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    // Show what is currently selected category
    private String getSelectedCategory() {
        if (foodTab.isSelected()) return "Food";
        if (retailTab.isSelected()) return "Retail";
        if (servicesTab.isSelected()) return "Services";
        if (healthTab.isSelected()) return "Health";
        if (specialtyTab.isSelected()) return "Specialty";
        if (professionalsTab.isSelected()) return "Local Professionals";
        return null;
    }

    //favorites toggle
    @FXML
    private void handleFavoritesToggle() {
        if (favoritesOnly.isSelected() && dataManager.getCurrentUser() == null) {
            showAlert("Please log in to filter by favorites.");
            favoritesOnly.setSelected(false);
            return;
        }
        applyFilters();
    }

    //deals toggle
    @FXML
    private void handleDealsToggle() {
        applyFilters();
    }

    //filtering
    @FXML
    private void handleSearch() {
        applyFilters(getSelectedCategory());
    }

    @FXML
    private void handleLocationSearch() {
        applyFilters(getSelectedCategory());
    }

    private void showFaq() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Help Center");
        dialog.setHeaderText("Community Questions & Answers");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        ListView<ForumThread> threadList = new ListView<>();
        threadList.getStyleClass().add("list-view");
        threadList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ForumThread item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String title = item.getTitle() != null ? item.getTitle() : "(untitled)";
                    String author = item.getAuthor() != null ? item.getAuthor() : "";
                    setText(title + (author.isBlank() ? "" : " — " + author));
                }
            }
        });

        List<ForumThread> threads = new ArrayList<>(dataManager.getForumThreads());
        threadList.setItems(FXCollections.observableArrayList(threads));

        Label titleLabel = new Label("Select a question");
        titleLabel.getStyleClass().add("h2");
        Label authorLabel = new Label("");
        authorLabel.getStyleClass().add("subtle");
        TextArea questionArea = new TextArea();
        questionArea.setEditable(false);
        questionArea.setWrapText(true);

        VBox repliesBox = new VBox(10);
        ScrollPane repliesScroll = new ScrollPane(repliesBox);
        repliesScroll.setFitToWidth(true);
        repliesScroll.setPrefHeight(260);

        TextField replyField = new TextField();
        replyField.setPromptText("Write a reply...");
        Button replyButton = new Button("Reply");
        replyButton.getStyleClass().add("primary");
        replyButton.setDisable(true);

        Button askButton = new Button("Ask Question");
        askButton.getStyleClass().add("accent");

        HBox replyRow = new HBox(10, replyField, replyButton);
        HBox.setHgrow(replyField, Priority.ALWAYS);

        VBox rightPane = new VBox(10, new HBox(10, askButton), titleLabel, authorLabel, questionArea,
                new Label("Replies"), repliesScroll, replyRow);
        rightPane.setPrefWidth(560);

        SplitPane split = new SplitPane();
        VBox leftPane = new VBox(10, new Label("Questions"), threadList);
        leftPane.setPrefWidth(360);
        VBox.setVgrow(threadList, Priority.ALWAYS);
        split.getItems().addAll(leftPane, rightPane);
        split.setDividerPositions(0.38);

        final ForumThread[] selectedThread = new ForumThread[]{null};

        Runnable refreshThreadList = () -> {
            threadList.setItems(FXCollections.observableArrayList(new ArrayList<>(dataManager.getForumThreads())));
        };

        Runnable refreshReplies = () -> {
            repliesBox.getChildren().clear();
            ForumThread t = selectedThread[0];
            if (t == null) return;

            List<ForumReply> replies = t.getReplies();
            if (replies.isEmpty()) {
                Label none = new Label("No replies yet. Be the first to respond.");
                none.getStyleClass().add("subtle");
                repliesBox.getChildren().add(none);
                return;
            }

            for (ForumReply r : replies) {
                VBox bubble = new VBox(6);
                bubble.getStyleClass().add("review-box");
                Label who = new Label(r.getAuthor() != null ? r.getAuthor() : "");
                who.getStyleClass().add("deal-title");
                Label msg = new Label(r.getMessage() != null ? r.getMessage() : "");
                msg.setWrapText(true);
                bubble.getChildren().addAll(who, msg);
                repliesBox.getChildren().add(bubble);
            }
        };

        threadList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedThread[0] = newV;
            if (newV == null) {
                titleLabel.setText("Select a question");
                authorLabel.setText("");
                questionArea.setText("");
                replyButton.setDisable(true);
                repliesBox.getChildren().clear();
                return;
            }

            titleLabel.setText(newV.getTitle() != null ? newV.getTitle() : "(untitled)");
            authorLabel.setText(newV.getAuthor() != null ? "Asked by " + newV.getAuthor() : "");
            questionArea.setText(newV.getQuestion() != null ? newV.getQuestion() : "");
            replyButton.setDisable(false);
            refreshReplies.run();
        });

        askButton.setOnAction(e -> {
            Dialog<ForumThread> askDialog = new Dialog<>();
            askDialog.setTitle("Ask a Question");
            askDialog.setHeaderText("Post a new question");
            askDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

            TextField titleField = new TextField();
            titleField.setPromptText("Title");
            TextArea questionField = new TextArea();
            questionField.setPromptText("Write your question...");
            questionField.setWrapText(true);
            questionField.setPrefRowCount(6);
            VBox askContent = new VBox(10, titleField, questionField);
            askDialog.getDialogPane().setContent(askContent);
            applyThemeToDialog(askDialog.getDialogPane());

            askDialog.setResultConverter(bt -> {
                if (bt != ButtonType.OK) return null;
                String title = titleField.getText() != null ? titleField.getText().trim() : "";
                String q = questionField.getText() != null ? questionField.getText().trim() : "";
                if (title.isEmpty() || q.isEmpty()) return null;

                User u = dataManager.getCurrentUser();
                String author = (u != null && u.getEmail() != null) ? u.getEmail() : "Anonymous";
                return new ForumThread("t-" + UUID.randomUUID(), title, q, author, System.currentTimeMillis());
            });

            Optional<ForumThread> result = askDialog.showAndWait();
            if (result.isEmpty() || result.get() == null) return;
            dataManager.addForumThread(result.get());
            refreshThreadList.run();
        });

        replyButton.setOnAction(e -> {
            ForumThread t = selectedThread[0];
            if (t == null) return;

            String msg = replyField.getText() != null ? replyField.getText().trim() : "";
            if (msg.isEmpty()) return;

            User u = dataManager.getCurrentUser();
            String author = (u != null && u.getEmail() != null) ? u.getEmail() : "Anonymous";
            ForumReply reply = new ForumReply(author, msg, System.currentTimeMillis());

            dataManager.addForumReply(t.getId(), reply);
            replyField.clear();

            for (ForumThread updated : dataManager.getForumThreads()) {
                if (updated != null && updated.getId() != null && updated.getId().equalsIgnoreCase(t.getId())) {
                    selectedThread[0] = updated;
                    break;
                }
            }
            refreshReplies.run();
        });

        dialog.getDialogPane().setContent(split);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        applyThemeToDialog(dialog.getDialogPane());
        dialog.showAndWait();
    }

    private void showWalkthrough(User user) {
        if (user == null) return;

        showTutorialStepNear(allTab,
                "Welcome to ByteSizedApp",
                "Use the category tabs at the top to browse businesses. Use Favorites/Deals toggles to narrow down results.");
        showTutorialStepNear(locationField,
                "Find businesses near you",
                "Type a Union County town name (like \"Westfield\") or a ZIP code (like \"07090\") in the Town/ZIP box to filter results.");
        showTutorialStepNear(favoriteButton,
                "Favorites",
                "Click ☆ Favorite to save businesses you like (favorites are tied to your account). Use the Favorites Only filter to narrow results.");
        showTutorialStepNear(reviewTextArea,
                "Reviews",
                "Select a star rating and write a short review before submitting. Reviews can’t be blank.");
        showTutorialStepNear(reportButton,
                "Sorting & Report",
                "Use the sort dropdown to organize results. Click Report to see a summary and your activity.");

        user.setTutorialSeen(true);
        dataManager.updateUser(user);
    }

    private void showTutorialStepNear(Node anchor, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How to Use the App");
        alert.setHeaderText(title);
        alert.setContentText(message);
        applyThemeToDialog(alert.getDialogPane());

        alert.setOnShown(e -> {
            if (anchor == null) return;
            Bounds bounds = anchor.localToScreen(anchor.getBoundsInLocal());
            if (bounds == null) return;

            Window window = alert.getDialogPane().getScene().getWindow();
            if (window == null) return;

            Rectangle2D visual = Screen.getPrimary().getVisualBounds();

            double x = bounds.getMaxX() + 12;
            double y = bounds.getMinY();

            double w = window.getWidth();
            double h = window.getHeight();

            if (x + w > visual.getMaxX()) {
                x = bounds.getMinX() - w - 12;
            }
            if (x < visual.getMinX()) {
                x = visual.getMinX() + 12;
            }

            if (y + h > visual.getMaxY()) {
                y = visual.getMaxY() - h - 12;
            }
            if (y < visual.getMinY()) {
                y = visual.getMinY() + 12;
            }

            window.setX(x);
            window.setY(y);
        });

        alert.showAndWait();
    }

    //alert when something happens
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyThemeToDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    private void applyThemeToDialog(DialogPane dialogPane) {
        if (dialogPane == null) return;
        try {
            String css = getClass().getResource("/theme.css").toExternalForm();
            if (!dialogPane.getStylesheets().contains(css)) {
                dialogPane.getStylesheets().add(css);
            }
        } catch (Exception ignored) {
        }
    }
}
