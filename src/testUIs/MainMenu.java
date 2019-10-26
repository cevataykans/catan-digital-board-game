package testUIs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));

        Scene scene = new Scene(root, 700, 400);
        scene.getStylesheets().add(MainMenu.class.getResource("MainMenu.css").toExternalForm());

        Rectangle playButton = (Rectangle) scene.lookup("#playButton");
        primaryStage.setTitle("CATAN");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
