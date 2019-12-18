package SceneManagement;

import ServerCommunication.ServerHandler;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class MatchmakingController extends SceneController{
    // Properties
    Button startMatchmaking;
    ImageView goBack;
    ProgressIndicator loading;
    Label foundPlayers;
    Label totalPlayers;
    Label searchLabel;
    Separator separator;

    // Constructor
    public MatchmakingController(Stage stage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Matchmaking.fxml"));
        scene = stage.getScene();
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
        scene.getStylesheets().add(getClass().getResource("/UI/Matchmaking.css").toExternalForm());
        scene.setRoot(root);

        loading = (ProgressIndicator) scene.lookup("#loading");
        foundPlayers = (Label) scene.lookup("#foundPlayers");
        foundPlayers.setText("" + 1);
        totalPlayers = (Label) scene.lookup("#totalLabel");
        searchLabel = (Label) scene.lookup("#searchLabel");
        searchLabel.setAlignment(Pos.CENTER);
        separator = (Separator) scene.lookup("#separator");

        startMatchmaking = (Button) scene.lookup("#startMatchmaking");
        startMatchmaking.setOnMouseClicked(event ->
        {
            ServerHandler.getInstance().gameRequest();
            FadeOut buttonOut = new FadeOut(startMatchmaking);
            buttonOut.setSpeed(2);
            buttonOut.setOnFinished(event2 -> {
                startMatchmaking.setVisible(false);
                loading.setVisible(true);
                foundPlayers.setVisible(true);
                totalPlayers.setVisible(true);
                searchLabel.setVisible(true);
                separator.setVisible(true);
                new FadeIn(loading).play();
                new FadeIn(foundPlayers).play();
                new FadeIn(totalPlayers).play();
                new FadeIn(searchLabel).play();
                new FadeIn(separator).play();
            });
            buttonOut.play();
        });

        // Gets the "Go Back" button from the help's fxml file and adds the logic to going back to main menu.
        goBack = (ImageView) scene.lookup("#goBack");
        goBack.setOnMouseClicked(event ->
        {
            boolean result = ServerHandler.getInstance().logout();
            if(result){
                // Initializing closing animation for help scene.
                FadeOut animation2 = new FadeOut(root);
                animation2.setSpeed(3.5);
                animation2.setOnFinished(event1 ->
                {
                    try
                    {
                        // Make this scene invisible and change the controller to main menu from SceneManagement.GameEngine.
                        root.setVisible(false);
                        GameEngine.getInstance().setController(4);
                    }
                    catch (IOException e)
                    {
                        System.out.println(e);
                    }
                });
                animation2.play();
            }
        });
    }

    public void foundPlayerCount(int foundPlayerCount) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (foundPlayerCount <= 4)
                {
                    foundPlayers.setText("" + foundPlayerCount);
                }
                if ( foundPlayerCount == 4)
                {
                    searchLabel.setText("Players found, game is starting...");
                }
            }
        });
    }
}
