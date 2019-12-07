package SceneManagement.GameManagement;

import GameFlow.Game;
import GameFlow.ResourceManager;
import SceneManagement.SingleGameController;
import animatefx.animation.FadeInLeft;
import animatefx.animation.FadeOutRight;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import Player.Player;

/**
 * This controller manages all the player andd resource selection logic. It has association with the Single-GameFlow.Game controller.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class SelectionController {
    // Properties
    private SingleGameController controller;
    private Scene scene;
    private AnchorPane selectionBox;
    private Label selectionLabel;

    // Constructor
    public SelectionController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    /**
     * This method initializes the UI components (taken from the game's fxml) related to the selection and it
     * adds the logic as a listener to the components.
     */
    private void initialize()
    {
        selectionBox = (AnchorPane) scene.lookup("#selectionBox");
        selectionLabel = (Label) scene.lookup("#selectionLabel");
    }

    /**
     * This method prepares a player selection screen in the game's UI. It takes player list as a parameter which determines
     * what players need to be shown in the selection. When current player selects one of the players who are shown in the selection
     * screen, stealing functionality is called to steal one of that player's resources.
     * @param playersToSelect is the players to be shown.
     */
    public void showPlayerSelection(ArrayList<Player> playersToSelect) {

        // Get the game for must management
        Game game = Game.getInstance();

        // Clear old players contained in the selection container.
        selectionBox.getChildren().clear();

        // Inform the current player that he/she needs to select a player to steal a resource.
        controller.getStatusController().informStatus(8);
        selectionLabel.setText( "Choose Your Player.Player");

        // Initialize rectangle list for UI. Rectangles represent the players with their corresponding colors.
        ArrayList<Rectangle> players = new ArrayList<>();
        for ( int i = 0; i < playersToSelect.size(); i++ )
        {
            // According to the index of the array list, configure the player information
            Rectangle otherPlayer = new Rectangle(i * 300 + 150, 100, 200, 400);
            otherPlayer.setFill( playersToSelect.get( i).getColor() );
            int finalI = i;
            otherPlayer.setOnMousePressed(e -> {
                // Check if the action is to select a player. This is to prevent background action that current player
                // may do and interrupt the game's flow.
                if ( game.checkMust() == 8 )
                {
                    // Clear the action of selecting a user from the Flow Manager.
                    game.doneMust();
                }
                // Call stealing function with the selected player.
                stealResourceFromPlayer( playersToSelect.get( finalI) );
                // Refresh current player information (stolen resource is added).
                controller.getInfoController().setupCurrentPlayer();
            });
            // Add player neighbored to the hexagon to the list
            players.add(otherPlayer);

            // Add the UI representation of the player to the container with its own representation style.
            players.get(i).getStyleClass().add("resourceBox");
            selectionBox.getChildren().add(players.get(i));
        }
        // Play an in animation for the selection screen with its players.
        new FadeInLeft(selectionBox).play();
        selectionBox.setVisible(true);
    }

    /**
     * This function steals one resource from the given player.
     * @param stealingFrom is the unfortunate player who will get one of his/her resources stolen.
     */
    private void stealResourceFromPlayer( Player stealingFrom)
    {
        // Steal one of the player's resources and add it to the current player.
        new ResourceManager().stealResourceFromPlayer( Game.getInstance().getCurrentPlayer(), stealingFrom );

        // Play an out animation for the selection screen after user selects a player.
        new FadeOutRight( selectionBox).play();
        selectionBox.setVisible(false);
        // Refresh current player's information.
        controller.getInfoController().setupCurrentPlayer();
    }

    /**
     * This method prepares a resource selection screen in the game's UI. It takes id a parameter which determines
     * what calls this function, monopoly or year of plenty cards. Then corresponding to this id the status is changed.
     * When the current player selects one of the resources, either monopoly or year of plenty functionality is called.
     * @param id is the caller's identity.
     */
    public void showResourceSelection(String id) {
        selectionBox.getChildren().clear();
        // Change the status corresponding to the caller's identity.
        if ( id == "MONOPOLY")
        {
            controller.getStatusController().informStatus(5);
        }
        else if ( id == "YEAROFPLENTY")
        {
            controller.getStatusController().informStatus(6);
        }
        selectionLabel.setText("Choose Your Resource");
        // Initialize a list that will contain images of resources to be shown in the UI.
        ArrayList<ImageView> resources = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            switch (i) {
                // Add every type of resource to the list with their corresponding images and animations.
                // Resources are presented from left to right as: wood, wool, grain, brick and ore.
                case 0:
                    ImageView lumber = new ImageView("/images/wood.jpg");
                    lumber.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    lumber.setX(25);
                    lumber.setY(100);
                    resources.add(lumber);
                    break;
                case 1:
                    ImageView wool = new ImageView("/images/sheep.jpg");
                    wool.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    wool.setX(275);
                    wool.setY(100);
                    resources.add(wool);
                    break;
                case 2:
                    ImageView grain = new ImageView("/images/grain.jpg");
                    grain.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    grain.setX(525);
                    grain.setY(100);
                    resources.add(grain);
                    break;
                case 3:
                    ImageView brick = new ImageView("/images/brick.jpg");
                    brick.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    brick.setX(775);
                    brick.setY(100);
                    resources.add(brick);
                    break;
                case 4:
                    ImageView ore = new ImageView("/images/ore.jpg");
                    ore.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    ore.setX(1025);
                    ore.setY(100);
                    resources.add(ore);
                    break;
            }
            // Add the resources to the selection screen.
            resources.get(i).getStyleClass().add("resourceBox");
            selectionBox.getChildren().add(resources.get(i));
        }
        // After current player chooses a resource, play an out animation for the selection screen.
        new FadeInLeft(selectionBox).play();
        selectionBox.setVisible(true);
    }
}
