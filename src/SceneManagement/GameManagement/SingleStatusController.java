package SceneManagement.GameManagement;

import SceneManagement.MultiGameController;
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

public class SingleStatusController {
    // Properties
    SingleGameController controller;
    Scene scene;
    Label statusText;

    // Constructor
    public SingleStatusController(Scene scene, SingleGameController controller)
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
    public void informStatus(Response resultCode)
    {
        System.out.println(resultCode.toString());
        FlowManager flowManager = new FlowManager();

        // Initialize an out animation for the previous status.
        FadeOutRight animation = new FadeOutRight(statusText);
        animation.setSpeed(3);
        animation.setOnFinished(event ->
        {

            // Check the status code given, change the status text corresponding to the code.
            if ( resultCode == Response.ERROR_NO_CONNECTION_FOR_ROAD )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", there is no connection for a road to build");
            }
            else if ( resultCode == Response.ERROR_NO_CONNECTION_FOR_SETTLEMENT )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", there is no connection for a settlement to build");
            }
            else if ( resultCode == Response.ERROR_THERE_IS_NEAR_BUILDING_FOR_SETTLEMENT )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", there is another building near");
            }
            else if ( resultCode == Response.ERROR_OCCUPIED_BY )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", this spot is occupied by a player");
            }
            else if ( resultCode == Response.ERROR_NO_RESOURCE_FOR_ROAD )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resources for a road");
            }
            else if ( resultCode == Response.ERROR_NO_RESOURCE_FOR_SETTLEMENT )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resources for a settlement");
            }
            else if ( resultCode == Response.ERROR_NO_RESOURCE_FOR_CITY )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resources for a city");
            }
            else if ( resultCode == Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resource amount!");
            }
            else if ( resultCode == Response.ERROR_PLAYER_REFUSED_TRADE)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + "'s offer is declined!");
            }
            else if ( resultCode == Response.MUST_ROAD_BUILD )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", build road first!");
            }
            else if ( resultCode == Response.MUST_SETTLEMENT_BUILD )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", build settlement first!");
            }
            else if ( resultCode == Response.MUST_CITY_BUILD )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", build city first!");
            }
            else if ( resultCode == Response.MUST_INSIDE_TILE_SELECTION )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", move the robber first by clicking and dragging!");
            }
            else if ( resultCode == Response.MUST_RESOURCE_SELECTION_MONOPOLY )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", select a resource for monopoly card!");
            }
            else if ( resultCode == Response.MUST_RESOURCE_SELECTION_PLENTY )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", select two resources for year of plenty card!");
            }
            else if ( resultCode == Response.MUST_END_TURN )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", END YOUR TURN RIGHT NOW!!!!!");
            }
            else if ( resultCode == Response.MUST_ROLL_DICE )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", but first, lets roll the dice!");
            }
            else if ( resultCode == Response.MUST_GET_NEIGHBOR)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", choose a neighbor player first to steal a resource!");
            }
            else if( resultCode == Response.ERROR_NO_RESOURCE_FOR_CARD )
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", not enough resource for a card!");
            }
            else if(resultCode == Response.INFORM_WAIT_FOR_TRADE_RESPONSE)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " made an offer!");
            }
            else if(resultCode == Response.INFORM_ROAD_CAN_BE_BUILT)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " you can build a road at that spot.");
            }
            else if(resultCode == Response.INFORM_SETTLEMENT_CAN_BE_BUILT)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " you can build a settlement at that spot.");
            }
            else if(resultCode == Response.INFORM_CITY_CAN_BE_BUILT)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " you can build a city at that spot.");
            }
            else if(resultCode == Response.INFORM_INSIDE_TILE)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " that is inside a hexagon.");
            }
            else if(resultCode == Response.INFORM_SEA_TILE)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " that is inside sea.");
            }
            else if(resultCode == Response.INFORM_ROLL_DICE)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " roll the dice to start the turn.");
            }
            else if(resultCode == Response.ERROR_OUTSIDE_GAMEBOARD)
            {
                statusText.setText(flowManager.getCurrentPlayer().getName() + " that place is not inside the game area.");
            }
            else if ( resultCode == Response.ERROR_CARD_NOT_PLAYABLE)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", development card is not playable this turn.");
            }
            else if ( resultCode == Response.ERROR_CARD_DRAGGED_OUTSIDE)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", please drag the card inside the playable area.");
            }
            else if ( resultCode == Response.MUST_GET_HALF_RESOURCE_PERFECT_BALANCE)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", please satisfy Thanos.");
            }
            else if ( resultCode == Response.MUST_PLAYER_GETS_POINT)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", please get a point.");
            }
            else if ( resultCode == Response.MUST_WAITING_FOR_TRADE)
            {
                statusText.setText( flowManager.getCurrentPlayer().getName() + ", wait for the trade offeree to respond.");
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
