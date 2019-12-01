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

public class HelpController extends Controller{
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
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Help.css").toExternalForm());
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
                animation.setSpeed(3.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        goBack = (ImageView) scene.lookup("#goBack");
        goBack.setOnMouseClicked(event ->
        {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
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

        stage.setScene(scene);
    }
}
