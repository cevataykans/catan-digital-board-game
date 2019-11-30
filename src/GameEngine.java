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
    private static Stage primaryStage = null;
    private static ArrayList<Player> players = null;

    // Properties
    private Controller controller;

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
            temp.add(new Player("xx", Color.BLUE));
            temp.add(new Player("xx", Color.WHITE));
            temp.add(new Player("xx", Color.ORANGE));
            temp.add(new Player("xx", Color.BROWN));
            controller = new SingleGameController(primaryStage, temp);
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
