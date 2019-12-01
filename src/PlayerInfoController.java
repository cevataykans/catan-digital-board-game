import animatefx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.PopupWindow;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This controller manages all the player information logic. It has association with the Single-Game controller.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class PlayerInfoController {
    // Properties
    SingleGameController controller;
    Scene scene;

    ArrayList<Circle> otherPlayers;
    ArrayList<AnchorPane> playerAnchors;
    ArrayList<Label> playerNames;
    ArrayList<Label> currPlayerResources;
    ArrayList<ProgressIndicator> playerScores;
    ArrayList<ImageView> longestRoads;
    ArrayList<ImageView> largestArmies;
    boolean other1Shown = false;
    boolean other2Shown = false;
    boolean other3Shown = false;

    // Constructor
    public PlayerInfoController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    /**
     * This method initializes the UI components (taken from the game's fxml) related to the player information and it
     * adds the logic as a listener to the components.
     * IMPORTANT NOTE: THE CONVENTION OF OTHER PLAYERS ARE AS FOLLOWS:
     * other1 + (additional info about variable or nothing) = player which has the next turn.
     * other2 + (additional info about variable or nothing) = player which has 2 turns after.
     * other3 + (additional info about variable or nothing) = player which has 3 turns after.
     */
    private void initialize() {
        // Get the UI representations of other players from the player information controller's fxml file.
        Circle other1 = (Circle) scene.lookup("#other1Info");
        Circle other2 = (Circle) scene.lookup("#other2Info");
        Circle other3 = (Circle) scene.lookup("#other3Info");

        other1.setOnMouseEntered(event ->
        {
            // If the player information container is not already shown, show it.
            if ( !other1Shown)
            {
                other1Shown = true;
                new Pulse(other1).play();
                showPlayer(1);
                //showTradePopup(other1);
            }
        });
        other1.setOnMouseExited(event ->
        {
            if ( other1Shown)
            {
                // If the player information container is already shown, hide it.
                hidePlayer(1);
                //hideTradePopup(other1);
            }
        });

        other2.setOnMouseEntered(event ->
        {
            if ( !other2Shown)
            {
                // If the player information container is not already shown, show it.
                other2Shown = true;
                new Pulse(other2).play();
                showPlayer(2);
                //showTradePopup(other2);
            }
        });
        other2.setOnMouseExited(event ->
        {
            // If the player information container is already shown, hide it.
            if ( other2Shown) {
                hidePlayer(2);
                //hideTradePopup(other2);
            }
        });

        other3.setOnMouseEntered(event ->
        {
            if ( !other3Shown)
            {
                // If the player information container is not already shown, show it.
                other3Shown = true;
                new Pulse(other3).play();
                showPlayer(3);
                //showTradePopup(other3);
            }
        });
        other3.setOnMouseExited(event ->
        {
            // If the player information container is already shown, hide it.
            if ( other3Shown) {
                hidePlayer(3);
                //hideTradePopup(other3);
            }
        });

        // Add the other player representations as circles to a list.
        otherPlayers = new ArrayList<>();
        otherPlayers.add(other1);
        otherPlayers.add(other2);
        otherPlayers.add(other3);

        // Get the UI representations of other players' containers from the player information controller's fxml file.
        AnchorPane currentPlayerBox = (AnchorPane) scene.lookup("#currentPlayerInf");
        AnchorPane otherPlayer1Box = (AnchorPane) scene.lookup("#other1Box");
        AnchorPane otherPlayer2Box = (AnchorPane) scene.lookup("#other2Box");
        AnchorPane otherPlayer3Box = (AnchorPane) scene.lookup("#other3Box");
        playerAnchors = new ArrayList<>();
        playerAnchors.add(currentPlayerBox);
        playerAnchors.add(otherPlayer1Box);
        playerAnchors.add(otherPlayer2Box);
        playerAnchors.add(otherPlayer3Box);

        // Get the UI representations of other players' names from the player information controller's fxml file.
        Label currentPlayer = (Label) scene.lookup("#currentName");
        currentPlayer.setAlignment(Pos.CENTER);
        Label otherPlayer1 = (Label) scene.lookup("#other1Name");
        Label otherPlayer2 = (Label) scene.lookup("#other2Name");
        Label otherPlayer3 = (Label) scene.lookup("#other3Name");
        playerNames = new ArrayList<>();
        playerNames.add(currentPlayer);
        playerNames.add(otherPlayer1);
        playerNames.add(otherPlayer2);
        playerNames.add(otherPlayer3);

        // Get the UI representations of other players' scores from the player information controller's fxml file.
        ProgressIndicator currentPlayerIndicator = (ProgressIndicator) scene.lookup("#currentScore");
        ProgressIndicator otherPlayer1Progress = (ProgressIndicator) scene.lookup("#other1Score");
        ProgressIndicator otherPlayer2Progress = (ProgressIndicator) scene.lookup("#other2Score");
        ProgressIndicator otherPlayer3Progress = (ProgressIndicator) scene.lookup("#other3Score");
        playerScores = new ArrayList<>();
        playerScores.add(currentPlayerIndicator);
        playerScores.add(otherPlayer1Progress);
        playerScores.add(otherPlayer2Progress);
        playerScores.add(otherPlayer3Progress);

        // Get the UI representations of current player's resources from the player information controller's fxml file.
        Label lumberCount = (Label) scene.lookup("#lumberCount");
        Label woolCount = (Label) scene.lookup("#woolCount");
        Label grainCount = (Label) scene.lookup("#grainCount");
        Label brickCount = (Label) scene.lookup("#brickCount");
        Label oreCount = (Label) scene.lookup("#oreCount");
        currPlayerResources = new ArrayList<>();
        currPlayerResources.add(lumberCount);
        currPlayerResources.add(woolCount);
        currPlayerResources.add(grainCount);
        currPlayerResources.add(brickCount);
        currPlayerResources.add(oreCount);

        // Get the UI representations of other players' longest roads from the player information controller's fxml file.
        ImageView currentLR = (ImageView) scene.lookup("#currentLR");
        ImageView other1LR = (ImageView) scene.lookup("#other1LR");
        ImageView other2LR = (ImageView) scene.lookup("#other2LR");
        ImageView other3LR = (ImageView) scene.lookup("#other3LR");
        longestRoads = new ArrayList<>();
        longestRoads.add(currentLR);
        longestRoads.add(other1LR);
        longestRoads.add(other2LR);
        longestRoads.add(other3LR);

        // Get the UI representations of other players' largest armies from the player information controller's fxml file.
        ImageView currentLA = (ImageView) scene.lookup("#currentLA");
        ImageView other1LA = (ImageView) scene.lookup("#other1LA");
        ImageView other2LA = (ImageView) scene.lookup("#other2LA");
        ImageView other3LA = (ImageView) scene.lookup("#other3LA");
        largestArmies = new ArrayList<>();
        largestArmies.add(currentLA);
        largestArmies.add(other1LA);
        largestArmies.add(other2LA);
        largestArmies.add(other3LA);

        // For initial start of the game, refresh/show all of the information related to all players.
        setupOtherPlayers();
        setupCurrentPlayer();
        setupLongestRoad();
        setupLargestArmy();
    }

    /**
     * This function refreshes/sets up the information of the other players.
     */
    public void setupOtherPlayers() {
        // Initialize out animation for previous representation of other players with 3x the normal speed.
        FadeOut animation1 = new FadeOut(otherPlayers.get(0));
        FadeOut animation2 = new FadeOut(otherPlayers.get(1));
        FadeOut animation3 = new FadeOut(otherPlayers.get(2));
        animation1.setSpeed(3);
        animation2.setSpeed(3);
        animation3.setSpeed(3);
        animation1.play();
        animation2.play();
        animation3.play();

        animation1.setOnFinished(event ->
        {
            // Initialize in animation for the new other1 player's representation in UI.
            FadeIn animation1In = new FadeIn(otherPlayers.get(0));
            // Sets its style to the other1 player's color.
            otherPlayers.get(0).getStyleClass().clear();
            otherPlayers.get(0).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4)
                    .getColor().toString().substring(1) + "Circle");
            // Set its information container's style to other1 player's color.
            playerAnchors.get(1).getStyleClass().clear();
            playerAnchors.get(1).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            // Set its name in information container to other1 player's name.
            playerNames.get(1).setText(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4).getName());
            // Set its score in information container to other1 player's score.
            playerScores.get(1).setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4).getScore() * 1.0 / 10);
            animation1In.setSpeed(3);
            animation1In.play();
        });
        animation2.setOnFinished(event ->
        {
            // Initialize in animation for the new other2 player's representation in UI.
            FadeIn animation2In = new FadeIn(otherPlayers.get(1));
            // Sets its style to the other2 player's color.
            otherPlayers.get(1).getStyleClass().clear();
            otherPlayers.get(1).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4)
                    .getColor().toString().substring(1) + "Circle");
            // Set its information container's style to other2 player's color.
            playerAnchors.get(2).getStyleClass().clear();
            playerAnchors.get(2).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            // Set its name in information container to other2 player's name.
            playerNames.get(2).setText(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4).getName());
            // Set its score in information container to other2 player's score.
            playerScores.get(2).setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4).getScore() * 1.0 / 10);
            animation2In.setSpeed(3);
            animation2In.play();
        });
        animation3.setOnFinished(event ->
        {
            // Initialize in animation for the new other3 player's representation in UI.
            FadeIn animation3In = new FadeIn(otherPlayers.get(2));
            // Sets its style to the other3 player's color.
            otherPlayers.get(2).getStyleClass().clear();
            otherPlayers.get(2).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4)
                    .getColor().toString().substring(1) + "Circle");
            // Set its information container's style to other3 player's color.
            playerAnchors.get(3).getStyleClass().clear();
            playerAnchors.get(3).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            // Set its name in information container to other3 player's name.
            playerNames.get(3).setText(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4).getName());
            // Set its score in information container to other3 player's score.
            playerScores.get(3).setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4).getScore() * 1.0/ 10);
            animation3In.setSpeed(3);
            animation3In.play();
        });
    }

    /**
     * This function refreshes/sets up the information of the current player.
     */
    public void setupCurrentPlayer() {
        // Initialize out animation for previous representation of current player with 3x the normal speed.
        FadeOut infoOut = new FadeOut(playerAnchors.get(0));
        infoOut.setSpeed(3);
        infoOut.setOnFinished(event ->
        {
            FadeIn infoIn = new FadeIn(playerAnchors.get(0));
            // Sets current player's container's style to the current player's color.
            playerAnchors.get(0).getStyleClass().clear();
            playerAnchors.get(0).getStyleClass().add(controller.getGame().getCurrentPlayer().getColor().toString().substring(1) + "PlayerBox");
            // Set current player's name in information container to current player's name.
            playerNames.get(0).setText(controller.getGame().getCurrentPlayer().getName());
            // Set current player's score in information container to current player's score.
            playerScores.get(0).setProgress(controller.getGame().getCurrentPlayer().getScore() * 1.0 / 10);
            // Get the resources of the current player.
            int playercurrPlayerResources[] = controller.getGame().getCurrentPlayer().getResources();

            // Set each of the current player's resource representations in UI to current player's actual resource counts.
            for ( int i = 0; i < currPlayerResources.size(); i++)
            {
                currPlayerResources.get(i).setText("" + playercurrPlayerResources[i]);
            }

            infoIn.setSpeed(3);
            infoIn.play();
        });
        infoOut.play();
    }

    /**
     * This function shows the information related to the hovered other player.
     * @param otherIndex is the other player's number/index.
     */
    private void showPlayer(int otherIndex)
    {
        // Initialize in animation for the other player's information box.
        playerAnchors.get(otherIndex).setVisible(true);
        ZoomIn showAnim = new ZoomIn(playerAnchors.get(otherIndex));
        showAnim.play();
    }

    /**
     * This function hides the information related to the hovered other player.
     * @param otherIndex is the other player's number/index.
     */
    private void hidePlayer(int otherIndex)
    {
        // Initialize out animation for the other player's information box.
        ZoomOut hideAnim = new ZoomOut(playerAnchors.get(otherIndex));
        hideAnim.setOnFinished(event ->
        {
            // Make the information container of the other player invisible, set its shown boolean as false.
            playerAnchors.get(otherIndex).setVisible(false);
            switch (otherIndex)
            {
                case 1: other1Shown = false; break;
                case 2: other2Shown = false; break;
                case 3: other3Shown = false; break;
            }
        });
        hideAnim.play();
    }

    /**
     * WORK IN PROGRESS - WILL PROBABLY BE DEPRECATED
     * @param owner
     */
    private void showTradePopup(Node owner)
    {
        try {
            Parent tradeRoot = FXMLLoader.load(getClass().getResource("/UI/TradePopup.fxml"));
            tradeRoot.getStylesheets().add(getClass().getResource("/UI/TradePopup.css").toExternalForm());
            PopOver tradePopup = new PopOver(tradeRoot);
            tradePopup.setTitle("Trade");
            tradePopup.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
            tradePopup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
            tradePopup.show(owner);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    /**
     * WORK IN PROGRESS - WILL PROBABLY BE DEPRECATED
     * @param owner
     */
    private void hideTradePopup(Node owner)
    {

    }

    /**
     * This function scans every player to see if they have longest road card. If one has it, their longest road card
     * is shown in the UI.
     */
    public void setupLongestRoad() {
        // For each player in the game
        for ( int i = 0; i < 4; i++)
        {
            // See if he/she is the current holder of the longest road card.
            if ( controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + i) % 4) == controller.getGame().getLongestRoadPlayer())
            {
                // If he/she is, sets its longest road card image visibility as true and show it via an in animation.
                longestRoads.get(i).setVisible(true);
                FadeIn laIn = new FadeIn(longestRoads.get(i));
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
                // If not, set its longest road card image visibility as false and hide it via an out animation
                FadeOut laOut = new FadeOut(longestRoads.get(i));
                laOut.setSpeed(3);
                int finalI = i;
                laOut.setOnFinished(event ->
                {
                    longestRoads.get(finalI).setVisible(false);
                });
                laOut.play();
            }
        }
        setupOtherPlayers();
    }

    /**
     * This function scans every player to see if they have largest army card. If one has it, their largest army card
     * is shown in the UI.
     */
    public void setupLargestArmy() {
        // For each player in the game
        for ( int i = 0; i < 4; i++)
        {
            // See if he/she is the current holder of the largest army card.
            if ( controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + i) % 4) == controller.getGame().getLargestArmyPlayer())
            {
                // If he/she is, sets its largest army card image visibility as true and show it via an in animation.
                largestArmies.get(i).setVisible(true);
                FadeIn laIn = new FadeIn(largestArmies.get(i));
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
                // If not, set its largest army card image visibility as false and hide it via an out animation
                FadeOut laOut = new FadeOut(largestArmies.get(i));
                laOut.setSpeed(3);
                int finalI = i;
                laOut.setOnFinished(event ->
                {
                    largestArmies.get(finalI).setVisible(false);
                });
                laOut.play();
            }
        }
        setupOtherPlayers();
    }
}
