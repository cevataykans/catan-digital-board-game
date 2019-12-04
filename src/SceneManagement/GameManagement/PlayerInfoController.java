package SceneManagement.GameManagement;

import SceneManagement.SingleGameController;
import animatefx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.PopupWindow;
import org.controlsfx.control.PopOver;
import java.io.IOException;
import java.util.ArrayList;
import External.*;

/**
 * This controller manages all the player information logic. It has association with the Single-GameFlow.Game controller.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class PlayerInfoController {
    // Properties
    SingleGameController controller;
    Scene scene;

    ArrayList<FillProgressIndicator> otherPlayers;
    ArrayList<Label> currPlayerResources;
    boolean otherInfoShown = false;
    AnchorPane otherInfoBox;
    AnchorPane otherPlayerBox;
    AnchorPane currentPlayerBox;
    Label currentPlayerName;
    Label otherPlayerName;
    ImageView currentLR;
    ImageView currentLA;
    ImageView otherLR;
    ImageView otherLA;
    ImageView otherSettlementImage;
    ImageView otherRoadImage;
    ImageView otherCityImage;
    Label otherSettlementCount;
    Label otherRoadCount;
    Label otherCityCount;

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
        // Add the other player representations as special progress indicators to the list.
        otherInfoBox = (AnchorPane) scene.lookup("#otherInfoBox");
        otherPlayers = new ArrayList<>();

        otherPlayers.add(null);
        otherPlayers.add(null);
        otherPlayers.add(null);

        // Get the UI representations of other players' containers from the player information controller's fxml file.
        currentPlayerBox = (AnchorPane) scene.lookup("#currentPlayerInf");
        otherPlayerBox = (AnchorPane) scene.lookup("#otherBox");

        // Get the UI representations of other players' names from the player information controller's fxml file.
        currentPlayerName = (Label) scene.lookup("#currentName");
        currentPlayerName.setAlignment(Pos.CENTER);
        otherPlayerName = (Label) scene.lookup("#otherName");
        otherPlayerName.setAlignment(Pos.CENTER);

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
        currentLR = (ImageView) scene.lookup("#currentLR");
        otherLR = (ImageView) scene.lookup("#otherLR");

        // Get the UI representations of other players' largest armies from the player information controller's fxml file.
        currentLA = (ImageView) scene.lookup("#currentLA");
        otherLA = (ImageView) scene.lookup("#otherLA");

        // Get the UI representations of the shown other player's structure images and counts.
        otherSettlementImage = (ImageView) scene.lookup("#otherSettlementImage");
        otherRoadImage = (ImageView) scene.lookup("#otherRoadImage");
        otherCityImage = (ImageView) scene.lookup("#otherCityImage");
        otherSettlementCount = (Label) scene.lookup("#otherSettlementCount");
        otherRoadCount = (Label) scene.lookup("#otherRoadCount");
        otherCityCount = (Label) scene.lookup("#otherCityCount");

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
        // Clear the box that contains the progress representations.
        otherInfoBox.getChildren().clear();

        // Initialize out animation for previous representation of other players with 3x the normal speed.
        FadeOut animation = new FadeOut(otherInfoBox);
        animation.setSpeed(3);
        animation.play();

        /**
         * This process is the same as the one in the initialization method. Please check the comments in there
         * for further information.
         */
        animation.setOnFinished(event ->
        {
            // Initialize in animation for the all other player's representation in UI.
            FadeIn animationIn = new FadeIn(otherInfoBox);
            // Initialize other1 player's representation in the UI.
            FillProgressIndicator otherPlayer1;
            otherPlayer1 = new FillProgressIndicator(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4)
                    .getColor().toString().substring(1));
            otherPlayer1.setInnerCircleRadius(10);
            otherPlayer1.setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4).getScore() * 10);
            otherPlayer1.setOnMouseEntered(event1 ->
            {
                // If the player information container is not already shown, show it.
                if ( !otherInfoShown)
                {
                    otherInfoShown = true;
                    new Pulse(otherPlayer1).play();
                    showPlayer(1);
                    //showTradePopup(other1);
                }
            });
            otherPlayer1.setOnMouseExited(event2 ->
            {
                if ( otherInfoShown)
                {
                    // If the player information container is already shown, hide it.
                    hidePlayer();
                    //hideTradePopup(other1);
                }
            });
            otherPlayers.set(0, otherPlayer1);
            otherInfoBox.getChildren().add(otherPlayer1);

            // Initialize other2 player's representation in the UI.
            FillProgressIndicator otherPlayer2;
            otherPlayer2 = new FillProgressIndicator(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4)
                    .getColor().toString().substring(1));
            otherPlayer2.setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4).getScore() * 10);
            otherPlayer2.setTranslateY(otherPlayers.get(0).getTranslateY() + 150);
            otherPlayer2.setOnMouseEntered(event1 ->
            {
                if ( !otherInfoShown)
                {
                    // If the player information container is not already shown, show it.
                    otherInfoShown = true;
                    new Pulse(otherPlayer2).play();
                    showPlayer(2);
                    //showTradePopup(other2);
                }
            });
            otherPlayer2.setOnMouseExited(event2 ->
            {
                // If the player information container is already shown, hide it.
                if ( otherInfoShown) {
                    hidePlayer();
                    //hideTradePopup(other2);
                }
            });
            otherPlayers.set(1, otherPlayer2);
            otherInfoBox.getChildren().add(otherPlayer2);

            // Initialize other3 player's representation in the UI.
            FillProgressIndicator otherPlayer3;
            otherPlayer3 = new FillProgressIndicator(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4)
                    .getColor().toString().substring(1));
            otherPlayer3.setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4).getScore() * 10);
            otherPlayer3.setTranslateY(otherPlayers.get(1).getTranslateY() + 150);
            otherPlayer3.setOnMouseEntered(event1 ->
            {
                if ( !otherInfoShown)
                {
                    // If the player information container is not already shown, show it.
                    otherInfoShown = true;
                    new Pulse(otherPlayer3).play();
                    showPlayer(3);
                    //showTradePopup(other3);
                }
            });
            otherPlayer3.setOnMouseExited(event2 ->
            {
                // If the player information container is already shown, hide it.
                if ( otherInfoShown) {
                    hidePlayer();
                    //hideTradePopup(other3);
                }
            });
            otherPlayers.set(2, otherPlayer3);
            otherInfoBox.getChildren().add(otherPlayer3);

            animationIn.setSpeed(3);
            animationIn.play();
        });
    }

    /**
     * This function refreshes/sets up the information of the current player.
     */
    public void setupCurrentPlayer() {
        // Initialize out animation for previous representation of current player with 3x the normal speed.
        FadeOut infoOut = new FadeOut(currentPlayerBox);
        infoOut.setSpeed(3);
        infoOut.setOnFinished(event ->
        {
            FadeIn infoIn = new FadeIn(currentPlayerBox);
            // Sets current player's container's style to the current player's color.
            currentPlayerBox.getStyleClass().clear();
            currentPlayerBox.getStyleClass().add(controller.getGame().getCurrentPlayer().getColor().toString().substring(1) + "PlayerBox");
            // Set current player's name in information container to current player's name.
            currentPlayerName.setText(controller.getGame().getCurrentPlayer().getName());
            // Set current player's score in information container to current player's score.
            //playerScores.get(0).setProgress(controller.getGame().getCurrentPlayer().getScore() * 1.0 / 10);
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
        otherPlayerBox.getStyleClass().clear();
        otherPlayerBox.getStyleClass().add(controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4)
                .getColor().toString().substring(1) + "PlayerBox");
        otherPlayerName.setText(controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4).getName());

        otherSettlementImage.setImage(new Image("/images/settlement" + controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4).getColor() + ".png"));
        otherRoadImage.setImage(new Image("/images/road" + controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4).getColor() + ".png"));
        otherCityImage.setImage(new Image("/images/city" + controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4).getColor() + ".png"));

        otherSettlementCount.setText("" + controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4).getSettlementCount());

        otherRoadCount.setText("" + controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4).getRoadCount());

        otherCityCount.setText("" + controller.getGame()
                .getPlayer((controller.getGame().getCurrentPlayerIndex() + otherIndex) % 4).getCityCount());

        // Initialize in animation for the other player's information box.
        otherPlayerBox.setVisible(true);
        ZoomIn showAnim = new ZoomIn(otherPlayerBox);
        showAnim.play();
    }

    /**
     * This function hides the information related to the hovered other player.
     */
    private void hidePlayer()
    {
        // Initialize out animation for the other player's information box.
        ZoomOut hideAnim = new ZoomOut(otherPlayerBox);
        hideAnim.setOnFinished(event ->
        {
            // Make the information container of the other player invisible, set its shown boolean as false.
            otherPlayerBox.setVisible(false);
            otherInfoShown = false;
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
                otherLR.setVisible(true);
                FadeIn laIn = new FadeIn(otherLR);
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
                // If not, set its longest road card image visibility as false and hide it via an out animation
                FadeOut laOut = new FadeOut(otherLR);
                laOut.setSpeed(3);
                int finalI = i;
                laOut.setOnFinished(event ->
                {
                    otherLR.setVisible(false);
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
                otherLA.setVisible(true);
                FadeIn laIn = new FadeIn(otherLA);
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
                // If not, set its largest army card image visibility as false and hide it via an out animation
                FadeOut laOut = new FadeOut(otherLA);
                laOut.setSpeed(3);
                int finalI = i;
                laOut.setOnFinished(event ->
                {
                    otherLA.setVisible(false);
                });
                laOut.play();
            }
        }
        setupOtherPlayers();
    }

    /**
     * This function plays an animation for every resource types and amounts every player gains after rolling the dice,
     * its purpose is to highlight the resource distributions after a dice roll.
     */
    public void highlightResourceCollection() {

    }

    /**
     * This function plays an animation for the resource types and amounts current player gains after an action involving
     * resource collection/discarding.
     */
    public void highlightCurrentPlayerResourceGain() {

    }
}
