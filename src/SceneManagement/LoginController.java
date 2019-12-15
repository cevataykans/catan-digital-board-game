package SceneManagement;

import ServerCommunication.ServerHandler;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController extends SceneController {
    // Properties
    TextField username;
    PasswordField password;
    Button loginButton;
    Label registerButton;

    // Constructor
    public LoginController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Login.fxml"));
        scene = new Scene(root, Color.BLACK);
        initialize(stage);
    }

    // Methods
    /**
     * This initialize method initializes every component of the scene and the logic to either login online or register
     * new account.
     * @param stage is the primary stage that will take the controller's scene.
     * @throws IOException is the file-not-found exception.
     */
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Register.css").toExternalForm());
        scene.setRoot(root);

        // Wait 50 milliseconds to load the scene. After that, play the scene animation.
        root.setVisible(false);
        Parent finalRoot = root;
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                // Make the root visible and play the animation on with normal speed.
                finalRoot.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        // Get the login components from the fxml file.
        username = (TextField) scene.lookup("#usernameField");
        password = (PasswordField) scene.lookup("#passwordField");
        loginButton = (Button) scene.lookup("#loginButton");
        registerButton = (Label) scene.lookup("#registerButton");

        registerButton.setOnMouseClicked(event ->
        {
            // Initialize closing animation for main menu with 3.5x the normal speed.
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    // When animation is done, make this scene invisible and change the controller to help from SceneManagement.GameEngine.
                    finalRoot.setVisible(false);
                    GameEngine.getInstance().setController(5);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        loginButton.setOnMouseClicked(event2 -> {
            String enteredName = username.getText();
            String enteredPassword = password.getText();

            boolean result = ServerHandler.getInstance().login(enteredName, enteredPassword);
            if(result){
                try {
                    GameEngine.getInstance().setController(6);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        stage.setScene(scene);
        stage.setFullScreen(true);
    }
}
