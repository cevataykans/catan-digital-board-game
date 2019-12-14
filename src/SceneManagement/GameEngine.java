package SceneManagement;

import Player.Player;
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
    private SceneController controller;
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

    /**
     * Initializes the primary stage of the game, the stage will persist throught the game's lifetime.
     * @param primaryStage is the primary stage.
     */
    public void initializePrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        try {
            // To run the game from the beginning, comment the lines below and uncomment the commented line.
            // This is for faster testing.
           /* ArrayList<Player> temp = new ArrayList<>();
            temp.add(new Player("Talha", Color.BLUE));
            temp.add(new Player("Hakan", Color.WHITE));
            temp.add(new Player("Rafi", Color.ORANGE));
            temp.add(new Player("Cevat", Color.BROWN));
            controller = new SingleGameController(primaryStage, temp);*/
            controller = new SceneManagement.MainMenuController(primaryStage);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        // Maximize the stage window and show the stage.
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * This method sets the players of the game after players are initialized in player selection scene.
     * @param players is the players.
     */
    public void setPlayers(ArrayList<Player> players)
    {
        this.players = players;
    }

    /**
     * This method changes the controller based on the scene transition. This class has only 1 active controller at a time,
     * and it is switched with this method.
     * @param controllerType is code that will be scanned to determine the new controller.
     * @throws IOException is file exception.
     */
    public void setController(int controllerType) throws IOException {
        switch (controllerType)
        {
            // 0 is the code for Main Menu Controller.
            case 0:
                controller = new MainMenuController(primaryStage);
                break;
            // 1 is the code for Help Controller.
            case 1:
                controller = new HelpController(primaryStage);
                break;
            // 2 is the code for Player Selection Controller.
            case 2:
                controller = new PlayerSelectionController(primaryStage);
                break;
            // 3 is the code for Single Game Controller.
            case 3:
                controller = new SingleGameController(primaryStage, players);
                break;
            // 4 is the code for Login Controller.
            case 4:
                controller = new LoginController(primaryStage);
                break;
            // 5 is the code for Register Controller.
            case 5:
                controller = new RegisterController(primaryStage);
                break;
            // 6 is the coded for Matchmaking Controller.
            case 6:
                controller = new MatchmakingController(primaryStage);
                break;
            // 7 is the coded for Matchmaking Controller.
            case 7:
                controller = new MultiGameController(primaryStage);
                break;
            /* More will be added when new controllers are implemented alongside with multi-player */
        }
    }

    public SceneController getController() {
        return controller;
    }
}
