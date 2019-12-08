package SceneManagement.GameManagement;

import SceneManagement.SingleGameController;
import animatefx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import GameFlow.*;

/**
 * This controller manages all the player andd resource selection logic. It has association with the Single-GameFlow.Game controller.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class StatusController {
    // Properties
    SingleGameController controller;
    Scene scene;
    Label statusText;

    // Constructor
    public StatusController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    /**
     * This method initializes the UI components (taken from the game's fxml) related to the status and it
     * adds the logic as a listener to the components.
     */
    private void initialize()
    {
        statusText = (Label) scene.lookup("#statusText");
        statusText.setText("-");
        statusText.setAlignment(Pos.CENTER);
    }

    /**
     * This method changes the status of the game corresponding to the code given as a parameter.
     * @param resultCode is the code of the current status.
     */
    public void informStatus(int resultCode)
    {
        FlowManager flowManager = new FlowManager();

        // Initialize an out animation for the previous status.
        FadeOutRight animation = new FadeOutRight(statusText);
        animation.setSpeed(3);
        animation.setOnFinished(event ->
        {
            Game game = Game.getInstance();

            // Check the status code given, change the status text corresponding to the code.
            if ( resultCode == -1 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", there is no connection for a road to build");
            }
            else if ( resultCode == -2 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", there is no connection for a settlement to build");
            }
            else if ( resultCode == -3 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", there is another building near");
            }
            else if ( resultCode == -4 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", this spot is occupied by a player");
            }
            else if ( resultCode == -5 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resources for a road");
            }
            else if ( resultCode == -6 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resources for a settlement");
            }
            else if ( resultCode == -7 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resources for a city");
            }
            else if ( resultCode == -8 )
            {
                statusText.setText( "Other player does not have enough resources");
            }
            else if ( resultCode == -9)
            {
                statusText.setText( "-");
            }
            else if ( resultCode == 0 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", build road first!");
            }
            else if ( resultCode == 1 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", build settlement first!");
            }
            else if ( resultCode == 2 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", build city first!");
            }
            else if ( resultCode == 3 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", move the robber first by clicking and dragging!");
            }
            else if ( resultCode == 4 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", select a resource for monopoly card!");
            }
            else if ( resultCode == 5 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", select two resources for year of plenty card!");
            }
            else if ( resultCode == 6 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", END YOUR TURN RIGHT NOW!!!!!");
            }
            else if ( resultCode == 7 )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", but first, lets roll the dice!");
            }
            else if ( resultCode == 8)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", choose a neighbor player first to steal a resource!");
            }
            else
            {
                statusText.setText( "Function could not detect the error, lol, exploded!");
            }
            // Initialize an in animation for the new status.
            FadeInLeft animation2 = new FadeInLeft(statusText);
            animation2.setSpeed(3);
            animation2.play();
        });
        animation.play();
    }
}
