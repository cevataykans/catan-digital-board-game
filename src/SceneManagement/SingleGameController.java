package SceneManagement;

import Player.Player;
import SceneManagement.GameManagement.*;
import animatefx.animation.*;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import GameFlow.*;
import GameBoard.*;
import org.controlsfx.dialog.Wizard;

/**
 * This scene controller manages all of the Single-GameFlow.Game screen and all of its logic with itself and its sub-controllers.
 * @author Talha Şen
 * @version 29.11.2019
 */
public class SingleGameController extends SceneController
{
    private HashMap<Point2D, Integer> settlementMap = new HashMap<>();

    // Properties
    private ArrayList<Player> players;
    private Game game;
    private AnchorPane gameBox;
    private Button buyDevelopmentCard;
    private Button endTurn;
    private boolean highlightOn = false;

    // Sub Controllers
    private PlayerInfoController infoController;
    private StatusController statusController;
    private DevCardController devCardController;
    private SelectionController selectionController;
    private DiceController diceController;

    // Robber Related Properties
    private ImageView robber;
    private AtomicReference<Double> xRob = new AtomicReference<>((double) 0);
    private AtomicReference<Double> yRob = new AtomicReference<>((double) 0);
    private double distXRob = 0;
    private double distYRob = 0;
    private double initialRobX;
    private double initialRobY;


    // Constructor
    public SingleGameController(Stage stage, ArrayList<Player> players) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/SingleGame.fxml"));
        scene = new Scene(root, Color.BLACK);
        this.players = players;
        initialize(stage);
    }

    // Methods
    /**
     * This initialize method initializes every component of the scene and logic of components directly corrolated with the
     * game box (or game board area).
     * @param stage is the primary stage that will take the controller's scene.
     * @throws IOException is the file-not-found exception.
     */
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/SingleGame.css").toExternalForm());
        scene.setRoot(root);

        // Wait 10 milliseconds to load the scene. After that, play the scene animation.
        root.setVisible(false);
        Parent finalRoot = root;
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                // Make the root visible and play the animation on with 3.5x the normal speed.
                finalRoot.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot);
                animation.setSpeed(3.5);
                animation.play();
                SoundManager.getInstance().playBacktrack(SoundManager.Backtrack.GAME);
            }
        });
        new Thread(sleeper).start();

        // Initializing game and Flow Manager. Getting playable game area and robber from single-game's fxml file.
        // Calling game board and robber setup functions that will add logic to game board and robber.

        //**************************************************************************************************************
        //
        // MANAGERS AND GAME
        //
        //**************************************************************************************************************
        Tile[][] tiles = Game.getInstance( players);
        BoardManager boardManager = new BoardManager();
        CardManager cardManager = new CardManager();
        FlowManager flowManager = new FlowManager();

        gameBox = (AnchorPane) scene.lookup("#gameBox");
        robber = new ImageView("/images/robber.png");

        setupGameBoard( tiles); // Game is configured here
        setupRobber(); // Robber is configured here

        // Initializing all the sub controllers that will handle the game's other logic.
        infoController = new PlayerInfoController(scene, this);
        statusController = new StatusController(scene, this);
        devCardController = new DevCardController(scene, this);
        selectionController = new SelectionController(scene, this);
        diceController = new DiceController(scene, this);

        // Adding listener to make the game board intractable.
        gameBox.setOnMouseClicked(mouseEvent -> {
            // Allow the action to be processed for game board UI if only game board related must, be done
            Response response = flowManager.checkMust();
            if ( response == Response.MUST_EMPTY ||
                    response == Response.MUST_ROAD_BUILD ||
                    response == Response.MUST_SETTLEMENT_BUILD ||
                    response == Response.MUST_CITY_BUILD ||
                    response == Response.MUST_INSIDE_TILE_SELECTION)
            {
                System.out.println("Checkpoint1");
                System.out.println("mousex: " + mouseEvent.getX() + " mousey: " + mouseEvent.getY());
                // Getting the mouse coordinates.
                int x = PixelProcessor.processX(mouseEvent.getX());
                int y = PixelProcessor.processY(mouseEvent.getY());
                System.out.println("Checkpoint2");
                System.out.println("x: " + x + " y: " + y);

                // Checking if the clicked coordinate is a game tile
                createDialog( boardManager.checkTile(x, y), x, y);
            }
            else
            {
                statusController.informStatus( flowManager.checkMust() );
            }
        });

        // Adding listener to make the game board highlightable to make the more usable.
        gameBox.setOnMouseMoved(mouseEvent2 ->
        {
            // Allow the action to be processed for game board UI if only game board related must, be done
            Response response = flowManager.checkMust();
            if ( ( response == Response.MUST_ROAD_BUILD ||
                    response == Response.MUST_SETTLEMENT_BUILD ||
                    response == Response.MUST_CITY_BUILD ||
                    response == Response.MUST_INSIDE_TILE_SELECTION )
                    && !highlightOn)
            {
                // Getting the mouse coordinates.
                int x = PixelProcessor.processX(mouseEvent2.getX());
                int y = PixelProcessor.processY(mouseEvent2.getY());

                // Checking if the hovered coordinate is a game tile.
                Response structureCheck = boardManager.checkTile(x, y);
                // Check if the hovered tile is a constructable road.
                if ( structureCheck == Response.INFORM_ROAD_CAN_BE_BUILT)
                {
                    // Get the road image with the current player's color.
                    ImageView roadHighlight = new ImageView("/images/road" + flowManager.getCurrentPlayer().getColor()
                                                            + ".png");
                    // Set its rotation depending on the hexagon side and sets its x depending on rotation.
                    setRoadRotation(roadHighlight, x, y);
                    // Set road highlight's y corresponding to the hexagon side.
                    roadHighlight.setY( y * 30 + 35);
                    roadHighlight.setOnMouseExited(event ->
                    {
                        // When user exits the road highlight, remove it from UI with an fade out animation.
                        FadeOut highlightOut = new FadeOut(roadHighlight);
                        highlightOut.setSpeed(2);
                        highlightOut.setOnFinished(event1 ->
                        {
                            // Remove the road highlight and set the highlight boolean to false so that the player
                            // can view other highlights.
                            gameBox.getChildren().remove(roadHighlight);
                            highlightOn = false;
                        });
                        highlightOut.play();
                    });
                    // Add the road highlight to the UI.
                    gameBox.getChildren().add(roadHighlight);
                    // Initialize an in animation for the road highlight with 2x the normal speed.
                    FadeIn highlightIn = new FadeIn(roadHighlight);
                    highlightIn.setSpeed(2);
                    highlightIn.play();
                    // Set the highlight boolean to true so that user can't view multiple highlights in the same/other
                    // place(s).
                    highlightOn = true;
                }
                // Check if the hovered tile is a constructable settlement.
                else if ( structureCheck == Response.INFORM_SETTLEMENT_CAN_BE_BUILT)
                {
                    // Get the settlement image with the current player's color.
                    ImageView settlementHighlight = new ImageView("/images/settlement" + flowManager.getCurrentPlayer()
                            .getColor() + ".png");
                    // Set its x depending on the hexagin corner.
                    settlementHighlight.setX( PixelProcessor.getXToDisplay() );
                    // Set road highlight's y corresponding to the hexagon corner.
                    settlementHighlight.setY( PixelProcessor.getYToDisplay( y) );
                    settlementHighlight.setOnMouseExited(event ->
                    {
                        // When user exits the settlement highlight, remove it from UI with an fade out animation.
                        FadeOut highlightOut = new FadeOut(settlementHighlight);
                        highlightOut.setSpeed(2);
                        highlightOut.setOnFinished(event1 ->
                        {
                            // Remove the settlement highlight and set the highlight boolean to false so that the player
                            // can view other highlights.
                            gameBox.getChildren().remove(settlementHighlight);
                            highlightOn = false;
                        });
                        highlightOut.play();
                    });
                    // Add the settlement highlight to the UI.
                    gameBox.getChildren().add(settlementHighlight);
                    // Initialize an in animation for the settlement highlight with 2x the normal speed.
                    FadeIn highlightIn = new FadeIn(settlementHighlight);
                    highlightIn.setSpeed(2);
                    highlightIn.play();
                    // Set the highlight boolean to true so that user can't view multiple highlights in the same/other
                    // place(s).
                    highlightOn = true;
                }
            }
        });

        // Getting the development card button from the single-game fxml file and adding the buy card logic to its click listener.
        buyDevelopmentCard = (Button) scene.lookup("#buyDevelopmentCard");
        buyDevelopmentCard.setOnMouseClicked(event ->
        {
            cardManager.addDevelopmentCard();
        });

        // Getting the end turn button from the single-game fxml file and adding the end turn logic to its click listener.
        endTurn = (Button) scene.lookup( "#endTurn");
        endTurn.setOnMouseReleased(mouseEvent ->
        {
            performEndTurnButtonEvent();
        });

        /**
         *  This is a key listener used to add shortcuts to the game, done via keyboard.
         *  ---- CURRENT SHORTCUTS ----
         *  Key E -> End Turn
         *  ---- PLANNED SHORTCUTS ----
         *  Key D or C -> Buy Development DevelopmentCards.Card
         *  If you want to add a shortcut, add the key to the switch case with its functionality.
        */
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case E: performEndTurnButtonEvent();
                        break;
                case H: robber.setImage( new Image("/images/hakan.jpeg", 45, 70, false, false) );
            }
        });

        stage.setScene(scene);
    }

    /**
     * This functions sets up the game board in the game are (anchor) in UI, its robber and all the game board logic.
     */
    public void setupGameBoard( Tile[][] board)
    {
        // Initialize the controller
        //game.configureGame();
        //Tile[][] board = game.getTileBoard();
        ImageView hexagon;

        // For each GameBoard.Tile in the game board ( this method could be optimized with increased i, j index increment)
        for ( int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                /*
                 *      0-index = LUMBER
                 *      1-index = WOOL
                 *      2-index = GRAIN
                 *      3-index = BRICK
                 *      4-index = ORE
                 *      çöl -> will be assigned automatically when dice is 7
                 */
                if ( board[ i][ j] instanceof StartTile)
                {
                    int diceNum = ((StartTile)board[i][j]).getDiceNumber();
                    int resource = ((StartTile)board[ i][ j]).getResource();

                    String resourceStr;
                    if ( resource  == 0 )
                    {
                        resourceStr = "lumber" + diceNum;
                    }
                    else if ( resource == 1)
                    {
                        resourceStr = "wool" + diceNum;
                    }
                    else if ( resource == 2 )
                    {
                        resourceStr = "grain" + diceNum;
                    }
                    else if ( resource == 3)
                    {
                        resourceStr = "brick" + diceNum;
                    }
                    else if ( resource == 4)
                    {
                        resourceStr = "ore" + diceNum;
                    }
                    else
                    {
                        resourceStr = "";
                    }

                    // Create the img path for image drawing
                    String imgPath;
                    if ( diceNum != 7 )
                    {
                        imgPath = "/images/" + resourceStr + ".png";
                    }
                    else
                    {
                        imgPath = "/images/desert2.png";
                        robber.setX((j-2) * 30 + 110);
                        robber.setY(i * 30 + 80);
                        gameBox.getChildren().add( robber);
                    }
                    hexagon = new ImageView(new Image(imgPath));
                    hexagon.setX((j - 2) * 30 + 50);
                    hexagon.setY(i * 30 + 50);
                    gameBox.getChildren().add(hexagon);
                    robber.toFront();
                }
            }
        }
    }

    /**
     * This function sets up the robber logic and adds the robber logic as a listener. Robber works as a drag and drop
     * unit when dice is 7 or DevelopmentCards.Knight is played. If the dropped hexagon is valid with valid players, a steal scenario happens.
     */
    public void setupRobber() {
        FlowManager flowManager = new FlowManager();

        // User clicks to robber to move it
        robber.setOnMousePressed(e ->
        {
            if ( flowManager.checkMust() == Response.MUST_INSIDE_TILE_SELECTION )
            {
                // Save the initial value so that if dropped place is invalid, return to this position
                initialRobX = robber.getX();
                initialRobY = robber.getY();

                // Source: https://blogs.oracle.com/vaibhav/image-drag-with-mouse-in-javafx
                xRob.set( e.getX());
                yRob.set( e.getY());
                distXRob = xRob.get() - robber.getX();
                distYRob = yRob.get() - robber.getY();
                SoundManager.getInstance().playEffect(SoundManager.Effect.ROBBER);
            }
            else
            {
                statusController.informStatus( flowManager.checkMust() );
            }

        });

        // User drags the robber to another hexagon of choice or simply can put it in the same place to continue blocking the users
        robber.setOnMouseDragged(e ->
        {
            System.out.println( "Mouse Dragging, coordinateX: "+ PixelProcessor.processX( e.getX() ) + " y: "
                    + PixelProcessor.processY( e.getY() ) );
            if ( flowManager.checkMust() == Response.MUST_INSIDE_TILE_SELECTION )
            {
                //robber.setTranslateX(robber.getTranslateX() + (e.getX() - xRob.get()));
                //robber.setTranslateY(robber.getTranslateY() + (e.getY() - yRob.get()));

                // source : https://blogs.oracle.com/vaibhav/image-drag-with-mouse-in-javafx
                robber.setX( e.getX() - distXRob );
                robber.setY( e.getY() - distYRob );
            }
            else
            {
                statusController.informStatus( flowManager.checkMust() );
            }
        });

        // Check if user has put the robber onto a valid position
        robber.setOnMouseReleased(e ->
        {
            System.out.println( "Moude Released, coordinateX: "+ PixelProcessor.processX( e.getX() ) + " y: "
                    + PixelProcessor.processY( e.getY() ) );
            // When user releases the robber, if the robber should not play, this function must not work!
            if ( flowManager.checkMust() == Response.MUST_INSIDE_TILE_SELECTION)
            {
                // Create a board manager
                BoardManager boardManager = new BoardManager();

                // Get the coordinate and process it (processing and checking tile couldbe made in one line!)
                int movedX = PixelProcessor.processX( e.getX() );
                int movedY = PixelProcessor.processY( e.getY() );
                System.out.println("MovedX: " + movedX + " MovedY: " + movedY); /***********************************************/

                Response resultCode = boardManager.checkTile( movedX, movedY);
                if ( resultCode != Response.INFORM_INSIDE_TILE) // Inside tile
                {
                    System.out.println("Not inside tile"); /***********************************************/
                    //robber.setTranslateX(0);
                    //robber.setTranslateY(0);
                    robber.setX( initialRobX );
                    robber.setY( initialRobY );
                    statusController.informStatus( resultCode);
                }
                else
                {
                    // It is known that place is a inside tile, do must!
                    flowManager.doneMust();
                    robber.setX( PixelProcessor.getXToDisplay() );
                    robber.setY( PixelProcessor.getYToDisplay( movedY) );
                    boardManager.changeRobber( movedX, movedY);
                    SoundManager.getInstance().playEffect(SoundManager.Effect.ROBBER);

                    // Now get the neighbors of that hexagon and display player selection to do the must
                    ArrayList<Player> neighbors = boardManager.getNeighborPlayers( movedX, movedY);
                    if ( neighbors.size() != 0)
                    {
                        selectionController.showPlayerSelection(neighbors);
                    }
                    else
                    {
                        // As there is not other player, do the must
                        flowManager.doneMust();
                    }
                }
            }
        });
    }

    /**
     * Prompts to ask if the user really wants to build a settlement.
     * @param alert is the dialog prompting confirmation of building the settlement
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void buildSettlement(Alert alert, int x, int y)
    {
        System.out.println("Checkpoint3");
        System.out.println(" X index: " + x + " Y index: " + y);
        FlowManager flowManager = new FlowManager();

        alert.setHeaderText("Building a Settlement");
        alert.setContentText("Do you want to build a settlement?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK)
        {
            // Get the necessary managers
            BoardManager boardManager = new BoardManager();

            // Check if the user obligated to build a settlement
            if ( flowManager.checkMust() == Response.MUST_SETTLEMENT_BUILD )
            {
                flowManager.doneMust();
            }

            System.out.println("Checkpoint4");
            System.out.println(" X index: " + x + " Y index: " + y);
            // Make the corresponding game tile in the given index a road.
            boardManager.setTile( x, y);

            // Initializing road image to be shown on the UI.
            ImageView structure = new ImageView("/images/settlement" + flowManager.getCurrentPlayer().getColor() + ".png");

            System.out.println("Checkpoint5");
            System.out.println(" X Display: " + PixelProcessor.getXToDisplay());
            System.out.println(" Y Display: " + PixelProcessor.getYToDisplay(y));
            // Setting city image's coordinates.
            structure.setX( PixelProcessor.getXToDisplay() );
            structure.setY( PixelProcessor.getYToDisplay( y) );

            // Playing a zoom in animation for the settlement.
            new ZoomIn(structure).play();

            // Adding settlement image to the game area in UI.
            gameBox.getChildren().add(structure);

            // Putting the settlement image to the settlement map, a map that is used to switch settlement images with
            // city images when the player upgrades settlement to city.
            settlementMap.put(new Point2D(x, y), gameBox.getChildren().lastIndexOf(structure));

            // Refresh current player information.
            infoController.setupCurrentPlayer();
            infoController.setupLongestRoad();
            SoundManager.getInstance().playEffect(SoundManager.Effect.SETTLEMENT_BUILT);
        }
    }

    /**
     * A custom dialog to confirmation of road building
     * @param alert is the dialog prompting confirmation of building the road
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void buildRoad(Alert alert, int x, int y)
    {
        FlowManager flowManager = new FlowManager();

        alert.setHeaderText("Building a Road");
        alert.setContentText("Do you want to build a road?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK)
        {
            // Get the necessary managers
            BoardManager boardManager = new BoardManager();

            // Check if the user obligated to build a road
            if ( flowManager.checkMust() == Response.MUST_ROAD_BUILD )
            {
                flowManager.doneMust();
            }

            // Make the corresponding game tile in the given index a road.
            boardManager.setTile( x, y);
            // Initializing road image to be shown on the UI.
            ImageView structure = new ImageView("/images/road" + flowManager.getCurrentPlayer().getColor() + ".png");
            // Determining its rotation type corresponding to the hexagon side. Setting the road image's coordinates.
            setRoadRotation(structure, x, y);
            structure.setY( y * 30 + 30);
            new ZoomIn(structure).play();
            // Adding the road image to the game area in UI.
            gameBox.getChildren().add(structure);

            // Refreshing current player information
            infoController.setupCurrentPlayer();
            infoController.setupLongestRoad();
            SoundManager.getInstance().playEffect(SoundManager.Effect.ROAD_BUILD);

            // If its initial state, player has to immediately end the turn.
            if ( flowManager.checkMust() == Response.MUST_END_TURN )
            {
                performEndTurnButtonEvent();
            }
        }
    }

    /**
     * This function takes a road image and by looking at its position in gameBoard, it determines the road's
     * rotation.
     * @param road is the road that will be build/highlighted.
     * @param x is the x index of the road.
     * @param y is the y index of the road.
     */
    private void setRoadRotation(ImageView road, int x, int y)
    {
        // Get the necessary managers
        BoardManager boardManager = new BoardManager();

        RoadTile.RotationType rotationType = boardManager.rotationType(x, y);
        switch (rotationType)
        {
            case HORIZONTAL:
                road.setRotate(road.getRotate() + 105);
                road.setX( PixelProcessor.getXToDisplay() + 5);
                break;
            case UPPER_LEFT_VERTICAL:
                road.setRotate(road.getRotate() - 15);
                road.setX( PixelProcessor.getXToDisplay());
                break;
            case UPPER_RIGHT_VERTICAL:
                road.setRotate(road.getRotate() + 225);
                road.setX( PixelProcessor.getXToDisplay() - 5);
                break;
        }
    }

    /**
     * A custom dialog to confirmation of city upgrading
     * @param alert is the dialog prompting confirmation of upgrading to the city
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void buildCity( Alert alert, int x, int y)
    {
        FlowManager flowManager = new FlowManager();

        alert.setHeaderText("Upgrading To City");
        alert.setContentText("Do you want to upgrade your settlement to a city?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK)
        {
            // Get the necessary managers
            BoardManager boardManager = new BoardManager();

            // Check if the user obligated to build a city
            if ( flowManager.checkMust() == Response.MUST_CITY_BUILD )
            {
                flowManager.doneMust();
            }

            // Make the corresponding game tile in the given index a city.
            boardManager.setTile( x, y);

            // Initializing road image to be shown on the UI.
            ImageView structure = new ImageView("/images/city" + flowManager.getCurrentPlayer().getColor() + ".png");

            // Setting city image's coordinates.
            structure.setX( PixelProcessor.getXToDisplay() );
            structure.setY( PixelProcessor.getYToDisplay( y) );

            // Switching the corresponding settlement image with city image via an out-in animation.
            ImageView settlement = (ImageView) gameBox.getChildren().get(settlementMap.get(new Point2D(x, y)));
            // Initializing and playing a zoom out animation for the settlement image.
            ZoomOut settlementOut = new ZoomOut(settlement);
            settlementOut.setOnFinished(event ->
            {
                // When the settlement out animation finishes, a zoom in animation for city is played.
                gameBox.getChildren().remove(settlement);
                ZoomIn cityIn = new ZoomIn(structure);
                cityIn.play();
                gameBox.getChildren().add(structure);
            });
            settlementOut.play();

            infoController.setupCurrentPlayer();
            SoundManager.getInstance().playEffect(SoundManager.Effect.CITY_BUILD);
        }
    }

    /**
     * End the turn when the end turn button is pressed or player builds a road in the initial phase.
     */
    private void performEndTurnButtonEvent()
    {
        FlowManager flowManager = new FlowManager();

        // Check if the user has to do something before ending their turn
        if ( flowManager.checkMust() == Response.MUST_EMPTY || flowManager.checkMust() == Response.MUST_END_TURN) {

            // Check if the user ends the turn because of obligation
            if ( flowManager.checkMust() == Response.MUST_END_TURN) {
                flowManager.doneMust();
            }
            flowManager.endTurn();

            SoundManager.getInstance().playEffect(SoundManager.Effect.END_TURN);
            infoController.setupOtherPlayers();
            infoController.setupCurrentPlayer();
            infoController.setupLargestArmy();
            infoController.setupLongestRoad();
            diceController.setupDiceRoll();
            Task<Void> sleeper2 = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                    return null;
                }
            };

            sleeper2.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    statusController.informStatus(Response.EKSIDOKUZ);
                    devCardController.setupDevelopmentCards();
                }
            });
            new Thread(sleeper2).start();
        } else {
            statusController.informStatus( flowManager.checkMust());
        }
    }

    /**
     * The function to handle user intereaction for the game board.
     * @param resultCode is the code returned from the game
     * @param x is the x corrdinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void createDialog(  Response resultCode, int x, int y )
    {

        // If the controller returns minus integer, there is an error!
        if ( resultCode == Response.ERROR_NO_CONNECTION_FOR_ROAD ||
                resultCode == Response.ERROR_NO_CONNECTION_FOR_SETTLEMENT ||
                resultCode == Response.ERROR_THERE_IS_NEAR_BUILDING_FOR_SETTLEMENT ||
                resultCode == Response.ERROR_OCCUPIED_BY ||
                resultCode == Response.ERROR_NO_RESOURCE_FOR_ROAD ||
                resultCode == Response.ERROR_NO_RESOURCE_FOR_SETTLEMENT ||
                resultCode == Response.ERROR_NO_RESOURCE_FOR_CITY)
        {
            System.out.println( " error is ** " + resultCode + "   "); /***********************************************/
            // handle error
            statusController.informStatus( resultCode);
        }
        else
        {
            // The clicked tile is game tile, inform the user about the event regarding the resultCode gotten from controller
            Alert alert = new Alert( Alert.AlertType.CONFIRMATION);
            alert.initStyle( StageStyle.UTILITY);

            // Create a beautiful icon for catan dialog

            ImageView icon = new ImageView("/images/catanIcon.png");
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            alert.getDialogPane().setGraphic( icon);

            System.out.println( "result is ** " + resultCode + "   "); /***********************************************/
            // Handle the intended user action could have a dedicated function for it!
            informBoardSelection( alert, resultCode, x, y);
        }
    }

    /**
     * Creates the dialog corresponding to the user action on the game board.
     * @param alert is the dialog to display the user event
     * @param resultCode is the positive result gotten from the controller
     * @param x is the x coordinate of to perform action on the game board
     * @param y is the y coordinate of to perform action on the game board
     */
    private void informBoardSelection( Alert alert, Response resultCode, int x, int y ) {
        FlowManager flowManager = new FlowManager();

        Response mustCheckCode = flowManager.checkMust();

        // Player.Player tries to build a road
        if (resultCode == Response.INFORM_ROAD_CAN_BE_BUILT) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == Response.MUST_EMPTY || mustCheckCode == Response.MUST_ROAD_BUILD) {
                buildRoad(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // Player.Player tries to build a settlement
        else if (resultCode == Response.INFORM_SETTLEMENT_CAN_BE_BUILT) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == Response.MUST_EMPTY || mustCheckCode == Response.MUST_SETTLEMENT_BUILD) {
                buildSettlement(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // Player.Player tries to build a city
        else if (resultCode == Response.INFORM_CITY_CAN_BE_BUILT) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == Response.MUST_EMPTY || mustCheckCode == Response.MUST_CITY_BUILD) {
                buildCity(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
    }

    public PlayerInfoController getInfoController() {
        return infoController;
    }

    public StatusController getStatusController() {
        return statusController;
    }

    public DevCardController getDevCardController() {
        return devCardController;
    }

    public SelectionController getSelectionController() {
        return selectionController;
    }

    public DiceController getDiceController() {
        return diceController;
    }
}
