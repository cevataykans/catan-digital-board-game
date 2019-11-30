import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerSelectionController implements Controller{
    // Properties
    Parent root;
    Scene scene;
    ImageView goBack;
    Button startButton;
    TextField player1Name;
    TextField player2Name;
    TextField player3Name;
    TextField player4Name;

    // Constructor
    public PlayerSelectionController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/PlayerSelection.fxml"));
        scene = new Scene(root, Color.BLACK);
        initialize(stage);
    }

    // Methods
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/PlayerSelection.css").toExternalForm());
        scene.setRoot(root);

        root.setVisible(false);
        Parent finalRoot = root;
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(10);
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
                    GameEngine.getInstance().setController(0);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        player1Name = (TextField) scene.lookup("#player1Name");
        player2Name = (TextField) scene.lookup("#player2Name");
        player3Name = (TextField) scene.lookup("#player3Name");
        player4Name = (TextField) scene.lookup("#player4Name");

        startButton = (Button) scene.lookup("#startButton");
        startButton.setOnMouseClicked(event ->
        {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(2);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    ArrayList<Player> players = new ArrayList<>();
                    players.add(new Player(player1Name.getText(), Color.BLUE));
                    players.add(new Player(player2Name.getText(), Color.WHITE));
                    players.add(new Player(player3Name.getText(), Color.ORANGE));
                    players.add(new Player(player4Name.getText(), Color.BROWN));
                    GameEngine.getInstance().setPlayers(players);
                    GameEngine.getInstance().setController(3);
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
