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

/**
 * This scene controller manages all of the Player Selection screen.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class PlayerSelectionController extends SceneController{
    // Properties
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
    /**
     * This initialize method initializes every component of the scene and the logic to get the player names and start the game.
     * @param stage is the primary stage that will take the controller's scene.
     * @throws IOException is the file-not-found exception.
     */
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/PlayerSelection.css").toExternalForm());
        scene.setRoot(root);

        // Wait 10 milliseconds to load the scene. After that, play the scene animation.
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
                // Make the root visible and play the animation on with 3.5x the normal speed.
                finalRoot.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot);
                animation.setSpeed(3.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        // Gets the "Go Back" button from the player selection's fxml file and adds the logic to going back to main menu.
        goBack = (ImageView) scene.lookup("#goBack");
        goBack.setOnMouseClicked(event ->
        {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    // When animation is done, make this scene invisible and change the controller to main menu from GameEngine
                    GameEngine.getInstance().setController(0);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        // Gets all 4 of the player name input field from the player selection's fxml file.
        player1Name = (TextField) scene.lookup("#player1Name");
        player2Name = (TextField) scene.lookup("#player2Name");
        player3Name = (TextField) scene.lookup("#player3Name");
        player4Name = (TextField) scene.lookup("#player4Name");

        // Gets the start button from player selection's fxml file and adds the click listener to start the game.
        startButton = (Button) scene.lookup("#startButton");
        startButton.setOnMouseClicked(event ->
        {
            // Initializing the closing animation for the player selection scene.
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(2);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    // Getting all the names from their text fields and initializing players with their corresponding
                    // names and colors. Then passing these players to GameEngine to change and initialize the Single-Game
                    // controller and scene.
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
