package SceneManagement.GameManagement;

import External.FillProgressIndicator;
import GameFlow.*;
import Player.Player;
import SceneManagement.MultiGameController;
import SceneManagement.SceneController;
import ServerCommunication.ServerHandler;
import ServerCommunication.ServerInformation;
import animatefx.animation.*;
import javafx.application.Platform;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class MultiPlayerInfoController {

    // Properties
    private MultiGameController controller;
    private Scene scene;
    private ArrayList<FillProgressIndicator> otherPlayerInfos;
    private ArrayList<Player> otherPlayerOrders;
    private Pulse currentPlayerCircleAnim;
    private Pulse currentPlayerBoxAnim;
    private ArrayList<Label> localPlayerResources;
    private boolean otherInfoShown = false;
    private AnchorPane otherInfoBox;
    private AnchorPane otherPlayerBox;
    private AnchorPane localPlayerBox;
    private ProgressIndicator localScore;
    private Label localPlayerName;
    private Label otherPlayerName;
    private ImageView currentLR;
    private ImageView currentLA;
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

    // Spinners of other player in trade popup
    private Spinner<Integer> oLumberSpin;
    private Spinner<Integer> oWoolSpin;
    private Spinner<Integer> oGrainSpin;
    private Spinner<Integer> oBrickSpin;
    private Spinner<Integer> oOreSpin;

    // Constructor
    public MultiPlayerInfoController(Scene scene, SceneController controller)
    {
        this.scene = scene;
        this.controller = (MultiGameController) controller;
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
        otherPlayerInfos = new ArrayList<>();
        otherPlayerInfos.add(null);
        otherPlayerInfos.add(null);
        otherPlayerInfos.add(null);

        FlowManager flowManager = new FlowManager();
        otherPlayerOrders = new ArrayList<>();
        for ( int i = 0; i < 4; i++)
        {
            if ( flowManager.getPlayer(i) != controller.getLocalPlayer())
            {
                otherPlayerOrders.add(flowManager.getPlayer(i));
            }
        }

        // Get the UI representations of other players' containers from the player information controller's fxml file.
        localPlayerBox = (AnchorPane) scene.lookup("#currentPlayerInf");
        otherPlayerBox = (AnchorPane) scene.lookup("#otherBox");
        currentPlayerBoxAnim = new Pulse(localPlayerBox);
        localScore = (ProgressIndicator) scene.lookup("#currentScore");

        // Get the UI representations of other players' names from the player information controller's fxml file.
        localPlayerName = (Label) scene.lookup("#currentName");
        localPlayerName.setAlignment(Pos.CENTER);
        otherPlayerName = (Label) scene.lookup("#otherName");
        otherPlayerName.setAlignment(Pos.CENTER);

        // Get the UI representations of current player's resources from the player information controller's fxml file.
        Label lumberCount = (Label) scene.lookup("#lumberCount");
        Label woolCount = (Label) scene.lookup("#woolCount");
        Label grainCount = (Label) scene.lookup("#grainCount");
        Label brickCount = (Label) scene.lookup("#brickCount");
        Label oreCount = (Label) scene.lookup("#oreCount");
        localPlayerResources = new ArrayList<>();
        localPlayerResources.add(lumberCount);
        localPlayerResources.add(woolCount);
        localPlayerResources.add(grainCount);
        localPlayerResources.add(brickCount);
        localPlayerResources.add(oreCount);

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

        // For initial start of the game, refresh/show all of the information related to all players.
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                otherInfoBox.getChildren().clear();
            }
        });

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

            for ( int i = 0; i < 3; i++)
            {
                // Initialize other players' representation in the UI.
                FillProgressIndicator otherPlayer;
                otherPlayer = new FillProgressIndicator( otherPlayerOrders.get(i)
                        .getColor().toString().substring(1));
                otherPlayer.setInnerCircleRadius(10);
                otherPlayer.setProgress( otherPlayerOrders.get(i).getScore() * 10);

                if ( i > 0 )
                {
                    otherPlayer.setTranslateY( otherPlayerInfos.get( i - 1).getTranslateY() + 150);
                }

                int finalI = i;
                otherPlayer.setOnMouseEntered(event1 ->
                {
                    // If the player information container is not already shown, show it.
                    if ( !otherInfoShown)
                    {
                        otherInfoShown = true;
                        showPlayer(finalI);
                        //showTradePopup(other1);
                    }
                });
                otherPlayer.setOnMouseExited(event2 ->
                {
                    if ( otherInfoShown)
                    {
                        // If the player information container is already shown, hide it.
                        hidePlayer();
                        //hideTradePopup(other1);
                    }
                });

                otherPlayer.setOnMouseClicked( event3 ->
                {
                    if ( controller.getLocalPlayer() == flowManager.getCurrentPlayer()) {
                        // Trade can be done only if player is in free turn
                        if ( new FlowManager().checkMust() == Response.MUST_FREE_TURN)
                        {
                            showTradePopup(otherPlayer, finalI);
                        }
                        else
                        {
                            controller.getStatusController().informStatus(flowManager.checkMust());
                        }
                    }
                });

                otherPlayerInfos.set(i, otherPlayer);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        otherInfoBox.getChildren().add(otherPlayer);
                    }
                });
                if ( otherPlayerOrders.get(i) == flowManager.getCurrentPlayer())
                {
                    currentPlayerCircleAnim = new Pulse(otherPlayerInfos.get(i));
                    currentPlayerCircleAnim.setSpeed(0.5);
                    currentPlayerCircleAnim.setCycleCount(AnimationFX.INDEFINITE);
                    currentPlayerCircleAnim.play();
                }
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
        FadeOut infoOut = new FadeOut(localPlayerBox);
        infoOut.setSpeed(3);
        infoOut.setOnFinished(event ->
        {
            FadeIn infoIn = new FadeIn(localPlayerBox);
            // Sets local player's container's style to the current player's color.
            localPlayerBox.getStyleClass().clear();
            localPlayerBox.getStyleClass().add( controller.getLocalPlayer().getColor().toString().substring(1) + "PlayerBox");
            // Set local player's name in information container to current player's name.
            localPlayerName.setText( controller.getLocalPlayer().getName());
            // Set local player's score in information container to current player's score.
            localScore.setProgress( controller.getLocalPlayer().getScore() * 1.0 / 10);
            // Get the resources of the local player.
            int logicLocalPlayerResources[] =  controller.getLocalPlayer().getResources();

            // Set each of the local player's resource representations in UI to current player's actual resource counts.
            for ( int i = 0; i < localPlayerResources.size(); i++)
            {
                localPlayerResources.get(i).setText("" + logicLocalPlayerResources[i]);
            }

            if ( controller.getLocalPlayer() == flowManager.getCurrentPlayer())
            {
                currentPlayerBoxAnim.setSpeed(0.5);
                currentPlayerBoxAnim.setCycleCount(AnimationFX.INDEFINITE);
                currentPlayerBoxAnim.play();
            }
            else
            {
                currentPlayerBoxAnim.stop();
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
        FlowManager flowManager = new FlowManager();

        otherPlayerBox.getStyleClass().clear();
        otherPlayerBox.getStyleClass().add( otherPlayerOrders.get(otherIndex)
                .getColor().toString().substring(1) + "PlayerBox");
        otherPlayerName.setText( otherPlayerOrders.get(otherIndex).getName());

        otherSettlementImage.setImage(new Image("/images/settlement" +  otherPlayerOrders.get(otherIndex).getColor() + ".png"));
        otherRoadImage.setImage(new Image("/images/road" +  otherPlayerOrders.get(otherIndex).getColor() + ".png"));
        otherCityImage.setImage(new Image("/images/city" +  otherPlayerOrders.get(otherIndex).getColor() + ".png"));

        otherSettlementCount.setText("" +  otherPlayerOrders.get(otherIndex).getSettlementCount());

        otherRoadCount.setText("" +  otherPlayerOrders.get(otherIndex).getRoadCount());

        otherCityCount.setText("" +  otherPlayerOrders.get(otherIndex).getCityCount());

        TitleManager titleMan = new TitleManager();
        if ( otherPlayerOrders.get(otherIndex) == titleMan.getLongestRoadPlayer() )
        {
            otherLR.setVisible( true);
        }
        else
        {
            otherLR.setVisible( false);
        }

        if ( otherPlayerOrders.get(otherIndex) == titleMan.getLargestArmyPlayer() )
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
        FlowManager flow = new FlowManager();
        Player curPlayer = flow.getCurrentPlayer();
        Player otherPlayer = otherPlayerOrders.get(playerIndex);
        System.out.println(curPlayer.getName());
        System.out.println(otherPlayer.getName());

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

        oLumberSpin.getValueFactory().setValue( 0);
        oWoolSpin.getValueFactory().setValue( 0);
        oGrainSpin.getValueFactory().setValue( 0);
        oBrickSpin.getValueFactory().setValue( 0);
        oOreSpin.getValueFactory().setValue( 0);

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
                // Get the resources for displaying information.
                int[] toGive = {0, 0, 0, 0, 0};
                int[] toTake = {0, 0, 0, 0, 0};

                // These values are set in a way that they cannot be set to an amount that player does not have, do not worry!
                toGive[ ResourceManager.LUMBER] = cLumberSpin.getValue();
                toGive[ ResourceManager.WOOL] = cWoolSpin.getValue();
                toGive[ ResourceManager.GRAIN] = cGrainSpin.getValue();
                toGive[ ResourceManager.BRICK] = cBrickSpin.getValue();
                toGive[ ResourceManager.ORE] = cOreSpin.getValue();

                // In tradeWithPlayer function, resource check is made!
                toTake[ ResourceManager.LUMBER] = oLumberSpin.getValue();
                toTake[ ResourceManager.WOOL] = oWoolSpin.getValue();
                toTake[ ResourceManager.GRAIN] = oGrainSpin.getValue();
                toTake[ ResourceManager.BRICK] = oBrickSpin.getValue();
                toTake[ ResourceManager.ORE] = oOreSpin.getValue();
                ServerHandler.getInstance().sendTrade(toGive, toTake, otherPlayer.getName());
                controller.getStatusController().informStatus(Response.INFORM_WAIT_FOR_TRADE_RESPONSE);
                flow.addMust(Response.MUST_WAITING_FOR_TRADE);
                tradePopup.hide(javafx.util.Duration.seconds(0.2));
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

    public void receiveTradeOffer() {
        JSONObject obj = ServerInformation.getInstance().getInformation();
        ServerInformation.getInstance().deleteInformation();
        String otherPlayerName = null;
        try {
            otherPlayerName = obj.getString("otherPlayer");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(otherPlayerName);
        System.out.println(controller.getLocalPlayer().getName());
        if(!controller.getLocalPlayer().getName().equals(otherPlayerName)){
            controller.getStatusController().informStatus(Response.INFORM_WAIT_FOR_TRADE_RESPONSE);
        }
        if ( controller.getLocalPlayer().getName().equals(otherPlayerName)) {
            JSONArray toGive = null;
            JSONArray toTake = null;
            try {
                toGive = obj.getJSONArray("toGive");
                toTake = obj.getJSONArray("toTake");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray finalToGive = toGive;
            JSONArray finalToTake = toTake;
            String finalName = otherPlayerName;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initStyle(StageStyle.UTILITY);

                    // Create a beautiful icon for catan dialog
                    ImageView icon = new ImageView("/images/catanIcon.png");
                    icon.setFitHeight(48);
                    icon.setFitWidth(48);
                    alert.getDialogPane().setGraphic(icon);

                    ResourceManager resManager = new ResourceManager();
                    FlowManager flowManager = new FlowManager();
                    Player curPlayer = flowManager.getCurrentPlayer();
                    Player localPlayer = controller.getLocalPlayer();

                    alert.setHeaderText("Trade Offer by:   " + curPlayer.getName() + "   to:   " + localPlayer.getName());
                    try {
                        alert.setContentText("Offers: \t\t\t Wants: "
                                + "\nLumber: " + finalToGive.get(ResourceManager.LUMBER) + "\t\t Lumber: " + finalToTake.get(ResourceManager.LUMBER)
                                + "\nWool: " + finalToGive.get(ResourceManager.WOOL) + "\t\t\t Wool: " + finalToTake.get(ResourceManager.WOOL)
                                + "\nGrain: " + finalToGive.get(ResourceManager.GRAIN) + "\t\t Grain: " + finalToTake.get(ResourceManager.GRAIN)
                                + "\nBrick: " + finalToGive.get(ResourceManager.BRICK) + "\t\t\t Brick: " + finalToTake.get(ResourceManager.BRICK)
                                + "\nOre: " + finalToGive.get(ResourceManager.ORE) + "\t\t\t Ore: " + finalToTake.get(ResourceManager.ORE)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        int[] toGiveArray = new int[5];
                        int[] toTakeArray = new int[5];

                        for ( int i = 0; i < 5; i++)
                        {
                            try {
                                toGiveArray[i] = finalToGive.getInt(i);
                                toTakeArray[i] = finalToTake.getInt(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // From the point of view of current player
                        if (resManager.tradeWithPlayer(curPlayer, localPlayer, toGiveArray, toTakeArray)) {
                            // Trade successfuly made, close pop up, update current player UI
                            setupCurrentPlayer();
                            ServerHandler.getInstance().confirmTrade(toGiveArray, toTakeArray, controller.getLocalPlayer().getName());
                        } else {
                            ServerHandler.getInstance().refuseTrade(finalName);
                            controller.getStatusController().informStatus(Response.ERROR_PLAYER_REFUSED_TRADE);
                        }
                    } else {
                        ServerHandler.getInstance().refuseTrade(finalName);

                    }
                }
            });
        }
    }

    public void confirmTrade() {
        FlowManager flowManager = new FlowManager();
        JSONObject obj = ServerInformation.getInstance().getInformation();
        try {
            String otherPlayerName = obj.getString("otherPlayer");
            System.out.println("updated");
            JSONArray toGive = obj.getJSONArray("toGive");
            JSONArray toTake = obj.getJSONArray("toTake");

            int[] toGiveArray = new int[5];
            int[] toTakeArray = new int[5];
            for ( int i = 0; i < 5; i++)
            {
                toGiveArray[i] = toGive.getInt(i);
                toTakeArray[i] = toTake.getInt(i);
            }

            ResourceManager resManager = new ResourceManager();
            Player otherPlayer = null;
            for ( int i = 0; i < 4; i++)
            {
                if ( flowManager.getPlayer(i).getName().equals(otherPlayerName))
                {
                    otherPlayer = flowManager.getPlayer(i);
                }
            }
            resManager.tradeWithPlayer(flowManager.getCurrentPlayer(), otherPlayer, toGiveArray, toTakeArray);
            setupCurrentPlayer();
            if(controller.getLocalPlayer() == flowManager.getCurrentPlayer())
                flowManager.doneMust();
            controller.getStatusController().informStatus(Response.INFORM_ACCEPT_TRADE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refuseTrade(){
        FlowManager flowManager = new FlowManager();
        if(controller.getLocalPlayer() == flowManager.getCurrentPlayer()){
            flowManager.doneMust();
        }
        controller.getStatusController().informStatus(Response.INFORM_REFUSE_TRADE);
    }

    /**
     * This function scans every player to see if they have longest road card. If one has it, their longest road card
     * is shown in the UI.
     */
    /*public void setupLongestRoad() {

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
     */
    /*public void setupLargestArmy() {

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
}
