package SceneManagement;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * This scene controller manages all of the Opening screen.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class OpeningController extends SceneController{
    // Properties

    // Constructor
    public OpeningController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Intro1.fxml"));
        scene = new Scene(root, Color.BLACK);
        initialize(stage);
    }

    // Methods
    /**
     * This initialize method initializes every component of the scene and plays the opening animation.
     * @param stage is the primary stage that will take the controller's scene.
     * @throws IOException is the file-not-found exception.
     */
    @Override
    public void initialize(Stage stage) throws IOException {
        SoundManager.getInstance().playBacktrack(SoundManager.Backtrack.OPENING);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Intro1.css").toExternalForm());

        // Initializing the fade in animation for the opening scene (or a single label in this case) with half the original speed.
        FadeIn animation = new FadeIn(root);
        animation.setSpeed(0.5);
        animation.play();
        // Initializing the fade out animation for the opening scene (or a single label in this case) with half the original
        // speed. This fade out animation will play 4 seconds after fade in animation plays.
        FadeOut animation2 = new FadeOut(root);
        animation2.setDelay(new Duration(3700));
        animation2.setSpeed(0.5);
        animation2.play();
        animation2.setOnFinished(event ->
        {
            try {
                // When animation is done, change the scene to the second opening scene.
                translateToNextOpening(stage, scene);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setResizable(false);
        stage.show();
    }

    private void translateToNextOpening(Stage stage, Scene scene) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/UI/Intro2.fxml"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Intro2.css").toExternalForm());
        scene.setRoot(root);

        // Wait 1 seconds to play the second opening screen animation, the play the animation.
        Label label = (Label) scene.lookup("#intro2Text");
        label.setVisible(false);
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
        Parent finalRoot1 = root;
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                // Make the root visible and play the animation on with half the normal speed.
                label.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot1);
                animation.setSpeed(0.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        // Initializing and playing the closing animation for opening scene.
        FadeOut animation2 = new FadeOut(root);
        animation2.setDelay(new Duration(3500));
        animation2.setSpeed(0.5);
        animation2.play();
        animation2.setOnFinished(event ->
        {
            try {
                // When animation is done, change the controller to main menu from SceneManagement.GameEngine.
                GameEngine.getInstance().setController(0);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });
    }
}
