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

public class MainMenuController implements Controller{
    // Properties
    Parent root;
    Scene scene;
    Button playButton;
    Button helpButton;
    Button exitButton;

    // Constructor
    public MainMenuController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/MainMenu.fxml"));
        scene = new Scene(root, Color.BLACK);
        initialize(stage);
    }

    // Methods
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/MainMenu.css").toExternalForm());
        scene.setRoot(root);

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
                finalRoot.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        playButton = (Button) scene.lookup("#playButton");
        helpButton = (Button) scene.lookup("#helpButton");
        exitButton = (Button) scene.lookup("#exitButton");

        playButton.setOnMouseClicked(event -> {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
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

        helpButton.setOnMouseClicked(event -> {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
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

        exitButton.setOnMouseClicked(event -> {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(2);
            animation2.setOnFinished(event1 ->
            {
                Platform.exit();
                System.exit(0);
            });
            animation2.play();
        });

        stage.setScene(scene);
    }
}
