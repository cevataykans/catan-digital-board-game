package SceneManagement;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This scene controller manages all of the Help screen.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class HelpController extends SceneController{
    // Properties
    ImageView goBack;

    // Constructor
    public HelpController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Help.fxml"));
        scene = new Scene(root, Color.BLACK);
        initialize(stage);
    }

    // Methods
    /**
     * This initialize method initializes every component of the scene and the logic to going back to main menu.
     * @param stage is the primary stage that will take the controller's scene.
     * @throws IOException is the file-not-found exception.
     */
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Help.css").toExternalForm());
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
                // Make the root visible and play the animation on with 3.5x times the normal speed.
                finalRoot.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot);
                animation.setSpeed(3.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        // Gets the "Go Back" button from the help's fxml file and adds the logic to going back to main menu.
        goBack = (ImageView) scene.lookup("#goBack");
        goBack.setOnMouseClicked(event ->
        {
            // Initializing closing animation for help scene.
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    // Make this scene invisible and change the controller to main menu from SceneManagement.GameEngine.
                    finalRoot.setVisible(false);
                    GameEngine.getInstance().setController(0);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        // Set the help scene as game's scene.
        stage.setScene(scene);
    }
}
