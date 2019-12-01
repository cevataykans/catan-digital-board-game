import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Flow manager is used to control required controllers and their transitions throughout the game.
 * Sigleton class
 * @author Talha Şen
 * @version 29.11.2019
 */

public class GameEngine {

    //Singleton Instance
    private static GameEngine gameEngine = null;

    // Properties
    private Controller controller;
    private Stage primaryStage;
    private ArrayList<Player> players;

    // Constructor
    private GameEngine() {
        primaryStage = null;
        players = null;
    }

    // Methods
    /**
     * returns the singleton instance
     * @return the singleton instance
     */
    public static GameEngine getInstance(){
        if( gameEngine == null )
            gameEngine = new GameEngine();
        return gameEngine;
    }

    public void initializePrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        try {
            ArrayList<Player> temp = new ArrayList<>();
            controller = new OpeningController(primaryStage);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void setPlayers(ArrayList<Player> players)
    {
        this.players = players;
    }

    public void setController(int controllerType) throws IOException {
        switch (controllerType)
        {
            case 0:
                controller = new MainMenuController(primaryStage);
                break;
            case 1:
                controller = new HelpController(primaryStage);
                break;
            case 2:
                controller = new PlayerSelectionController(primaryStage);
                break;
            case 3:
                controller = new SingleGameController(primaryStage, players);
                break;
        }
    }
}
