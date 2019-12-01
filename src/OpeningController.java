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

public class OpeningController extends Controller{
    // Properties
    Parent root;
    Scene scene;

    // Constructor
    public OpeningController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Intro1.fxml"));
        scene = new Scene(root, Color.BLACK);
        initialize(stage);
    }

    // Methods
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Intro1.css").toExternalForm());

        FadeIn animation = new FadeIn(root);
        animation.setSpeed(0.5);
        animation.play();
        FadeOut animation2 = new FadeOut(root);
        animation2.setDelay(new Duration(4000));
        animation2.setSpeed(0.5);
        animation2.play();
        animation2.setOnFinished(event ->
        {
            try {
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
        stage.setMaximized(true);
        stage.show();
    }

    private void translateToNextOpening(Stage stage, Scene scene) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/UI/Intro2.fxml"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Intro2.css").toExternalForm());
        scene.setRoot(root);

        Label label = (Label) scene.lookup("#intro2Text");
        label.setVisible(false);
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        Parent finalRoot1 = root;
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                label.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot1);
                animation.setSpeed(0.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        FadeOut animation2 = new FadeOut(root);
        animation2.setDelay(new Duration(4000));
        animation2.setSpeed(0.5);
        animation2.play();
        animation2.setOnFinished(event ->
        {
            try {
                GameEngine.getInstance().setController(0);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });
        stage.setScene(scene);
    }
}
