package SceneManagement.GameManagement;

import GameFlow.*;
import Player.Player;
import SceneManagement.SingleGameController;
import animatefx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.PopupWindow;
import javafx.stage.StageStyle;
import org.controlsfx.control.PopOver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import External.*;

/**
 * This controller manages all the player information logic. It has association with the Single-GameFlow.Game controller.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class PlayerInfoController {

    // Properties
    private SingleGameController controller;
    private Scene scene;

    private ArrayList<FillProgressIndicator> otherPlayers;
    private ArrayList<Label> currPlayerResources;
    private boolean otherInfoShown = false;
    private AnchorPane otherInfoBox;
    private AnchorPane otherPlayerBox;
    private AnchorPane currentPlayerBox;
    private Label currentPlayerName;
    private Label otherPlayerName;
    private ImageView currentLR;
    private ImageView currentLA;
    private ProgressIndicator curPlayerScore;
    private ImageView otherLR;
    private ImageView otherLA;
    private ImageView otherSettlementImage;
    private ImageView otherRoadImage;
    private ImageView otherCityImage;
    private Label otherSettlementCount;
    private Label otherRoadCount;
    private Label otherCityCount;

    // Trade Popup UI variables
    private Parent tradeRoot;
    private Button trade;
    private Button cancel;
    private Label curPlayerTrade;
    private Label otherPlayerTrade;

    // Spinners of current player in trade popup
    private Spinner<Integer> cLumberSpin;
    private Spinner<Integer> cWoolSpin;
    private Spinner<Integer> cGrainSpin;
    private Spinner<Integer> cBrickSpin;
    private Spinner<Integer> cOreSpin;

    // Spinnders of other player in trade popup
    private Spinner<Integer> oLumberSpin;
    private Spinner<Integer> oWoolSpin;
    private Spinner<Integer> oGrainSpin;
    private Spinner<Integer> oBrickSpin;
    private Spinner<Integer> oOreSpin;

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

        curPlayerScore = (ProgressIndicator) scene.lookup("#currentScore");

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
        try
        {
            tradeRoot = FXMLLoader.load( getClass().getResource("/UI/TradePopup.fxml"));
            tradeRoot.getStylesheets().add(getClass().getResource("/UI/TradePopup.css").toExternalForm());

            Scene tradeScene = new Scene( tradeRoot);
            trade = (Button) tradeScene.lookup("#tradeButton");
            cancel = (Button) tradeScene.lookup("#cancel");

            cLumberSpin = (Spinner) tradeScene.lookup("#cLumberSpin");
            cWoolSpin = (Spinner) tradeScene.lookup("#cWoolSpin");
            cGrainSpin = (Spinner) tradeScene.lookup("#cGrainSpin");
            cBrickSpin = (Spinner) tradeScene.lookup("#cBrickSpin");
            cOreSpin = (Spinner) tradeScene.lookup("#cOreSpin");

            oLumberSpin = (Spinner) tradeScene.lookup("#oLumberSpin");
            oWoolSpin = (Spinner) tradeScene.lookup("#oWoolSpin");
            oGrainSpin = (Spinner) tradeScene.lookup("#oGrainSpin");
            oBrickSpin = (Spinner) tradeScene.lookup("#oBrickSpin");
            oOreSpin = (Spinner) tradeScene.lookup("#oOreSpin");

            curPlayerTrade = (Label) tradeScene.lookup("#offerer");
            otherPlayerTrade = (Label) tradeScene.lookup("#offeree");
        }
        catch ( IOException e)
        {
            System.out.println( e);
        }


        setupOtherPlayers();
        setupCurrentPlayer();
    }

    /**
     * This function refreshes/sets up the information of the other players.
     */
    public void setupOtherPlayers() {

        // Get the game for access
        FlowManager flowManager = new FlowManager();

        // Clear the box that contains the progress representations.
        otherInfoBox.getChildren().clear();

        // Initialize out animation for previous representation of other players with 3x the normal speed.
        FadeOut animation = new FadeOut( otherInfoBox);
        animation.setSpeed(3);
        animation.play();

        /*
         * This process is the same as the one in the initialization method. Please check the comments in there
         * for further information.
         */
        animation.setOnFinished(event ->
        {
            // Initialize in animation for the all other player's representation in UI.
            FadeIn animationIn = new FadeIn( otherInfoBox);

            // Initialize other players's representation in the UI.
            for ( int i = 0; i < 3; i++)
            {
                FillProgressIndicator otherPlayer;
                otherPlayer = new FillProgressIndicator( flowManager.getPlayer(( flowManager.getCurrentPlayerIndex() + i + 1) % 4)
                        .getColor().toString().substring(1));
                otherPlayer.setInnerCircleRadius(10);
                otherPlayer.setProgress( flowManager.getPlayer(( flowManager.getCurrentPlayerIndex() + i + 1) % 4).getScore() * 10);

                if ( i > 0 )
                {
                    otherPlayer.setTranslateY( otherPlayers.get( i - 1).getTranslateY() + 150);
                }

                int index = 1 + i;
                otherPlayer.setOnMouseEntered(event1 ->
                {
                    // If the player information container is not already shown, show it.
                    if ( !otherInfoShown)
                    {
                        otherInfoShown = true;
                        new Pulse( otherPlayer).play();
                        showPlayer( index);
                    }
                });

                otherPlayer.setOnMouseExited(event2 ->
                {
                    if ( otherInfoShown)
                    {
                        // If the player information container is already shown, hide it.
                        hidePlayer();
                    }
                });

                otherPlayer.setOnMouseClicked( event3 ->
                {
                    // Trade can be done only if player is in free turn
                    if ( new FlowManager().checkMust() == Response.MUST_FREE_TURN)
                    {
                        showTradePopup(otherPlayer, index - 1);
                    }
                });

                otherPlayers.set(i, otherPlayer);
                otherInfoBox.getChildren().add(otherPlayer);

            }
            animationIn.setSpeed(3);
            animationIn.play();
        });
    }

    /**
     * This function refreshes/sets up the information of the current player.
     */
    public void setupCurrentPlayer() {

        FlowManager flowManager = new FlowManager();

        // Initialize out animation for previous representation of current player with 3x the normal speed.
        FadeOut infoOut = new FadeOut(currentPlayerBox);
        infoOut.setSpeed(3);
        infoOut.setOnFinished(event ->
        {
            FadeIn infoIn = new FadeIn(currentPlayerBox);
            // Sets current player's container's style to the current player's color.
            currentPlayerBox.getStyleClass().clear();
            currentPlayerBox.getStyleClass().add( flowManager.getCurrentPlayer().getColor().toString().substring(1) + "PlayerBox");
            // Set current player's name in information container to current player's name.
            currentPlayerName.setText( flowManager.getCurrentPlayer().getName());
            // Set current player's score in information container to current player's score.
            curPlayerScore.setProgress( flowManager.getCurrentPlayer().getScore() * 1.0 / 10);
            // Get the resources of the current player.
            int playercurrPlayerResources[] =  flowManager.getCurrentPlayer().getResources();

            // Set each of the current player's resource representations in UI to current player's actual resource counts.
            for ( int i = 0; i < currPlayerResources.size(); i++)
            {
                currPlayerResources.get(i).setText("" + playercurrPlayerResources[i]);
            }

            TitleManager titleMan = new TitleManager();
            if ( flowManager.getCurrentPlayer() == titleMan.getLongestRoadPlayer() )
            {
                currentLR.setVisible( true);
            }
            else
            {
                currentLR.setVisible( false);
            }

            if ( flowManager.getCurrentPlayer() == titleMan.getLargestArmyPlayer() )
            {
                currentLA.setVisible( true);
            }
            else
            {
                currentLA.setVisible(  false);
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
        // Get game for accessing data
        FlowManager flowManager = new FlowManager();

        otherPlayerBox.getStyleClass().clear();
        otherPlayerBox.getStyleClass().add( flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4)
                .getColor().toString().substring(1) + "PlayerBox");
        otherPlayerName.setText( flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4).getName());

        otherSettlementImage.setImage(new Image("/images/settlement" +  flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4).getColor() + ".png"));
        otherRoadImage.setImage(new Image("/images/road" +  flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4).getColor() + ".png"));
        otherCityImage.setImage(new Image("/images/city" +  flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4).getColor() + ".png"));

        otherSettlementCount.setText("" +  flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4).getSettlementCount());

        otherRoadCount.setText("" +  flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4).getRoadCount());

        otherCityCount.setText("" +  flowManager
                .getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4).getCityCount());

        TitleManager titleMan = new TitleManager();
        if ( flowManager.getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4) == titleMan.getLongestRoadPlayer() )
        {
            otherLR.setVisible( true);
        }
        else
        {
            otherLR.setVisible( false);
        }

        if ( flowManager.getPlayer(( flowManager.getCurrentPlayerIndex() + otherIndex) % 4) == titleMan.getLargestArmyPlayer() )
        {
            otherLA.setVisible( true);
        }
        else
        {
            otherLA.setVisible(  false);
        }

        // Initialize in animation for the other player's information box.
        otherPlayerBox.setVisible(true);
        ZoomIn showAnim = new ZoomIn(otherPlayerBox);
        showAnim.setSpeed( 5);
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
        hideAnim.setSpeed( 100);
        hideAnim.play();
    }

    /**
     * Open the trade pop up when clicked on the other player circle.
     * @param owner is the node circle of the clicked player
     * @param playerIndex is the arrow index from player, 0 - top, 1 - middle, 2, bottom
     * If you need to access real players from game, you need to increase this value by one because second player is 0
     * 0 indexed here, last playaer is 2 indexed etc.
     */
    private void showTradePopup(Node owner, int playerIndex)
    {
        // Adjust current player spinners for max resource limit
        ResourceManager resManager = new ResourceManager();
        FlowManager flow = new FlowManager();
        Player curPlayer = flow.getCurrentPlayer();
        Player otherPlayer = flow.getPlayer((flow.getCurrentPlayerIndex() + playerIndex + 1 ) % 4 );

        // Get the current player resources for adjusting
        int[] curResources = curPlayer.getResources();

        // Adjust the limit for all current player resources
        var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curResources[ ResourceManager.LUMBER], 0);
        factory.setWrapAround( true);
        cLumberSpin.setValueFactory( factory);

        factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curResources[ ResourceManager.WOOL], 0);
        factory.setWrapAround( true);
        cWoolSpin.setValueFactory( factory);

        factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curResources[ ResourceManager.GRAIN], 0);
        factory.setWrapAround( true);
        cGrainSpin.setValueFactory( factory);

        factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curResources[ ResourceManager.BRICK], 0);
        factory.setWrapAround( true);
        cBrickSpin.setValueFactory( factory);

        factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, curResources[ ResourceManager.ORE], 0);
        factory.setWrapAround( true);
        cOreSpin.setValueFactory( factory);

        // Create the pop up instance and trade operations
        curPlayerTrade.setText( curPlayer.getName() );
        otherPlayerTrade.setText( otherPlayer.getName() );
        PopOver tradePopup = new PopOver( tradeRoot);
        tradePopup.setTitle("Trade");

        // Depending on the clicked player index, adjust arrow location -> 0 - top, 1 - middle, 2 - bottom
        if ( playerIndex == 0)
        {
            tradePopup.setArrowLocation( PopOver.ArrowLocation.RIGHT_TOP );
        }
        else if ( playerIndex == 2)
        {
            tradePopup.setArrowLocation( PopOver.ArrowLocation.RIGHT_BOTTOM );
        }
        else
        {
            tradePopup.setArrowLocation( PopOver.ArrowLocation.RIGHT_CENTER);
        }
        tradePopup.setArrowSize( 20);
        tradePopup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
        tradePopup.show( owner);

        // Ask for confirmation, then perform trade if accepted.
        trade.setOnMouseClicked( mouseEvent ->
        {
            if ( tradePopup.isShowing() )
            {
                Alert alert = new Alert( Alert.AlertType.CONFIRMATION);
                alert.initStyle( StageStyle.UTILITY);

                // Create a beautiful icon for catan dialog
                ImageView icon = new ImageView("/images/catanIcon.png");
                icon.setFitHeight(48);
                icon.setFitWidth(48);
                alert.getDialogPane().setGraphic( icon);

                // Get the resources for displaying information.
                int[] toGive = {0, 0, 0, 0, 0};
                int[] toTake = {0, 0, 0, 0, 0};

                toGive[ ResourceManager.LUMBER] = cLumberSpin.getValue();
                toGive[ ResourceManager.WOOL] = cWoolSpin.getValue();
                toGive[ ResourceManager.GRAIN] = cGrainSpin.getValue();
                toGive[ ResourceManager.BRICK] = cBrickSpin.getValue();
                toGive[ ResourceManager.ORE] = cOreSpin.getValue();

                toTake[ ResourceManager.LUMBER] = oLumberSpin.getValue();
                toTake[ ResourceManager.WOOL] = oWoolSpin.getValue();
                toTake[ ResourceManager.GRAIN] = oGrainSpin.getValue();
                toTake[ ResourceManager.BRICK] = oBrickSpin.getValue();
                toTake[ ResourceManager.ORE] = oOreSpin.getValue();


                alert.setHeaderText("Trade offer of player: " + curPlayer.getName() );
                alert.setContentText( curPlayer.getName() + " offers:\nLumber: ");

                tradePopup.hide(javafx.util.Duration.seconds(0.2));
                Optional<ButtonType> result = alert.showAndWait();
                if ( result.get() == ButtonType.OK )
                {
                    // From the point of view of current player
                    if (resManager.tradeWithPlayer(curPlayer, otherPlayer, toGive, toTake))
                    {
                        // Trade successfuly made, close pop up, update current player UI
                        this.setupCurrentPlayer();
                    } else
                    {
                        System.out.println("Trade failed");
                    }
                }
                else
                {
                    System.out.println( "Player: " + otherPlayer.getName() + " declined the trade");
                }
            }
        });

        // Close the pop up
        cancel.setOnMouseClicked( mouseEvent ->
        {
            if ( tradePopup.isShowing() )
            {
                tradePopup.hide( javafx.util.Duration.seconds( 0.2));
            }
        });

    }

    /**
     * This function scans every player to see if they have longest road card. If one has it, their longest road card
     * is shown in the UI.
     * DEPRECIATED?
     */
    /*private void setupLongestRoad() {

        // Get game for accessing data
        Game game = Game.getInstance();
        FlowManager flowManager = new FlowManager();
        TitleManager titleManager = new TitleManager();

        // For each player in the game
        for ( int i = 0; i < 4; i++)
        {
            // See if he/she is the current holder of the longest road card.
            if (  flowManager.getPlayer(( flowManager.getCurrentPlayerIndex() + i) % 4) ==  titleManager.getLongestRoadPlayer())
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
    }*/

    /**
     * This function scans every player to see if they have largest army card. If one has it, their largest army card
     * is shown in the UI.
     * DEPRECIATED?
     */
    /*private void setupLargestArmy() {

        // Get game for accessing data
        Game game = Game.getInstance();
        FlowManager flowManager = new FlowManager();
        TitleManager titleManager = new TitleManager();

        // For each player in the game
        for ( int i = 0; i < 4; i++)
        {
            // See if he/she is the current holder of the largest army card.
            if (  flowManager.getPlayer(( flowManager.getCurrentPlayerIndex() + i) % 4) ==  titleManager.getLargestArmyPlayer())
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
    }*/

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
