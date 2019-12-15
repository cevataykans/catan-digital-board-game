package SceneManagement;

import ServerCommunication.ServerHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class MatchmakingController extends SceneController{
    // Properties
    Button startMatchmaking;

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

        startMatchmaking = (Button) scene.lookup("#startMatchmaking");
        startMatchmaking.setOnMouseClicked(event ->
        {
            try {
                ServerHandler.getInstance().gameRequest();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }
}
