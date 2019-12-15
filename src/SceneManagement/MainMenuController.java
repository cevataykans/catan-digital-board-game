package SceneManagement;

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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This scene controller manages all of the Main Menu screen.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class MainMenuController extends SceneController{
    // Properties
    Button playLocalButton;
    Button playOnlineButton;
    Button helpButton;
    Button exitButton;

    // Constructor
    public MainMenuController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/MainMenu.fxml"));
        scene = stage.getScene();
        initialize(stage);
    }

    // Methods
    /**
     * This initialize method initializes every component of the scene and the logic to going back to navigate through
     * main menu.
     * @param stage is the primary stage that will take the controller's scene.
     * @throws IOException is the file-not-found exception.
     */
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/MainMenu.css").toExternalForm());
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
                SoundManager.getInstance().playEffect(SoundManager.Effect.BUGLE);
                SoundManager.getInstance().playBacktrack(SoundManager.Backtrack.MAIN_MENU);
            }
        });
        new Thread(sleeper).start();

        // Get the 3 main buttons of the main menu from its fxml file.
        playLocalButton = (Button) scene.lookup("#playLocalButton");
        playOnlineButton = (Button) scene.lookup("#playOnlineButton");
        helpButton = (Button) scene.lookup("#helpButton");
        exitButton = (Button) scene.lookup("#exitButton");

        // Play Local button changes scene to Player Selection
        playLocalButton.setOnMouseClicked(event -> {
            // Initialize closing animation for main menu with 3.5x the normal speed.
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    // When animation is done, make this scene invisible and change the controller to player selection
                    // from GameEngine.
                    finalRoot.setVisible(false);
                    GameEngine.getInstance().setController(2);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        // Play Online button changes scene to Login.
        playOnlineButton.setOnMouseClicked(event -> {
            // Initialize closing animation for main menu with 3.5x the normal speed.
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    // When animation is done, make this scene invisible and change the controller to login
                    // from GameEngine.
                    finalRoot.setVisible(false);
                    GameEngine.getInstance().setController(4);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        // Help button changes scene to Help
        helpButton.setOnMouseClicked(event -> {
            // Initialize closing animation for main menu with 3.5x the normal speed.
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    // When animation is done, make this scene invisible and change the controller to help from SceneManagement.GameEngine.
                    finalRoot.setVisible(false);
                    GameEngine.getInstance().setController(1);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        // Exit button closes the game.
        exitButton.setOnMouseClicked(event -> {
            // // Initialize closing animation for main menu with 2x the normal speed.
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(2);
            animation2.setOnFinished(event1 ->
            {
                // Close the application.
                Platform.exit();
                System.exit(0);
            });
            animation2.play();
        });
    }
}
