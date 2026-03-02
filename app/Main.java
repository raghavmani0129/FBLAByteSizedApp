package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {


    private static Stage primaryStage;

  
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Byte-Sized Business Boost");
        primaryStage.setResizable(true);

        // Load login screen first 
        loadScene("view/login.fxml", 400, 380);
        primaryStage.show();
    }


    public static void loadScene(String fxmlPath, int width, int height) {
        try {
            // Load the FXML file and create the screen graph
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/" + fxmlPath));
            Parent root = loader.load();

            // Create a new screen with the specified dimensions
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
            //errors
        } catch (Exception e) {
            // Log the error and provide user-friendly feedback
            System.err.println("Failed to load scene: " + fxmlPath);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            // errors
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error Loading Screen");
            alert.setHeaderText("Failed to load: " + fxmlPath);
            alert.setContentText(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
