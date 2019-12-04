package SceneManagement;

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

/**
 * This scene controller manages all of the Single-GameFlow.Game screen and all of its logic with itself and its sub-controllers.
 * @author Talha Şen
 * @version 29.11.2019
 */


public class SingleGameController extends SceneController {
    private static int hexIndex = -1;
    private static int tileIndex = -1;
    HashMap<Point2D, Integer> settlementMap = new HashMap<>();

    // Properties
    FlowManager flowManager;
    ArrayList<Player> players;
    Game game;
    AnchorPane gameBox;
    Button buyDevelopmentCard;
    Button endTurn;
    boolean highlightOn = false;

    // Sub Controllers
    PlayerInfoController infoController;
    StatusController statusController;
    DevCardController devCardController;
    SelectionController selectionController;
    DiceController diceController;

    // Robber Related Properties
    ImageView robber;
    AtomicReference<Double> xRob = new AtomicReference<>((double) 0);
    AtomicReference<Double> yRob = new AtomicReference<>((double) 0);
    double distXRob = 0;
    double distYRob = 0;
    double initialRobX;
    double initialRobY;


    // Constructor
    public SingleGameController(Stage stage, ArrayList<Player> players) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Game.fxml"));
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
        scene.getStylesheets().add(getClass().getResource("/UI/Game.css").toExternalForm());
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
            }
        });
        new Thread(sleeper).start();

        // Initializing game and Flow Manager. Getting playable game area and robber from single-game's fxml file.
        // Calling game board and robber setup functions that will add logic to game board and robber.
        game = new Game(players);
        flowManager = FlowManager.getInstance();
        gameBox = (AnchorPane) scene.lookup("#gameBox");
        robber = new ImageView("/images/robber.png");
        setupGameBoard();
        setupRobber();

        // Initializing all the sub controllers that will handle the game's other logic.
        infoController = new PlayerInfoController(scene, this);
        statusController = new StatusController(scene, this);
        devCardController = new DevCardController(scene, this);
        selectionController = new SelectionController(scene, this);
        diceController = new DiceController(scene, this);

        // Adding listener to make the game board intractable.
        gameBox.setOnMouseClicked(mouseEvent -> {
            System.out.println("x: " + mouseEvent.getX() + " y: " + mouseEvent.getY());
            // Allow the action to be processed for game board UI if only game board related must, be done
            if ( flowManager.checkMust() < 4 )
            {
                // Getting the mouse coordinates.
                int x = processX(mouseEvent.getX());
                int y = processY(mouseEvent.getY());

                System.out.print("X is: " + mouseEvent.getX() + " | Y is: " + mouseEvent.getY()); /********************************************************/
                System.out.println(" X' is: " + x + " | Y' is: " + y); /********************************************************/

                // Checking if the clicked coordinate is a game tile
                createDialog(game.checkTile(x, y), x, y);
            }
            else
            {
                statusController.informStatus( flowManager.checkMust() );
            }
        });

        // Adding listener to make the game board highlightable to make the more usable.
        gameBox.setOnMouseMoved(mouseEvent2 -> {
            // Allow the action to be processed for game board UI if only game board related must, be done
            if ( flowManager.checkMust() < 4 && !highlightOn)
            {
                // Getting the mouse coordinates.
                int x = processX(mouseEvent2.getX());
                int y = processY(mouseEvent2.getY());

                // Checking if the hovered coordinate is a game tile.
                int structureCheck = game.checkTile(x, y);
                // Check if the hovered tile is a constructable road.
                if ( structureCheck == 0)
                {
                    // Get the road image with the current player's color.
                    ImageView roadHighlight = new ImageView("/images/road" + game.getCurrentPlayer().getColor()
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
                else if ( structureCheck == 1)
                {
                    // Get the settlement image with the current player's color.
                    ImageView settlementHighlight = new ImageView("/images/settlement" + game.getCurrentPlayer()
                            .getColor() + ".png");
                    // Set its x depending on the hexagin corner.
                    settlementHighlight.setX( getXToDisplay() );
                    // Set road highlight's y corresponding to the hexagon corner.
                    settlementHighlight.setY( y * 30 + 35);
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
            game.addDevelopmentCard();
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
                case H: robber.setImage(new Image("/images/hakan.jpeg"));
            }
        });

        stage.setScene(scene);
    }

    /**
     * This functions sets up the game board in the game are (anchor) in UI, its robber and all the game board logic.
     */
    public void setupGameBoard() {
        // Initialize the controller
        game.configureGame();
        Tile[][] board = game.getBoard();
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
        // User clicks to robber to move it
        robber.setOnMousePressed(e ->
        {
            if ( flowManager.checkMust() == 3 )
            {
                // Save the initial value so that if dropped place is invalid, return to this position
                initialRobX = robber.getX();
                initialRobY = robber.getY();

                // Source: https://blogs.oracle.com/vaibhav/image-drag-with-mouse-in-javafx
                xRob.set( e.getX());
                yRob.set( e.getY());
                distXRob = xRob.get() - robber.getX();
                distYRob = yRob.get() - robber.getY();
            }
            else
            {
                statusController.informStatus( flowManager.checkMust() );
            }

        });

        // User drags the robber to another hexagon of choice or simply can put it in the same place to continue blocking the users
        robber.setOnMouseDragged(e ->
        {
            System.out.println( "Mouse Dragging, coordinateX: "+ processX( e.getX() ) + " y: "+ processY( e.getY() ) );
            if ( flowManager.checkMust() == 3 )
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
            System.out.println( "Moude Released, coordinateX: "+ processX( e.getX() ) + " y: "+ processY( e.getY() ) );
            // When user releases the robber, if the robber should not play, this function must not work!
            if ( flowManager.checkMust() == 3)
            {
                // Get the coordinate and process it (processing and checking tile couldbe made in one line!)
                int movedX = processX( e.getX() );
                int movedY = processY( e.getY() );
                System.out.println("MovedX: " + movedX + " MovedY: " + movedY); /***********************************************/

                int resultCode = game.checkTile( movedX, movedY);
                if ( resultCode != 3) // Inside tile
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
                    robber.setX( getXToDisplay() );
                    robber.setY( movedY * 30 + 35 );
                    game.changeRobber( movedX, movedY);

                    // Now get the neighbors of that hexagon and display player selection to do the must
                    ArrayList<Player> neighbors = game.getNeighborPlayers( movedX, movedY);
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
        alert.setHeaderText("Building a Settlement");
        alert.setContentText("Do you want to build a settlement?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK){

            // Check if the user obligated to build a settlement
            if ( flowManager.checkMust() == 1 )
            {
                flowManager.doneMust();
            }

            // Make the corresponding game tile in the given index a road.
            game.setTile( x, y);
            // Initializing road image to be shown on the UI.
            ImageView structure = new ImageView("/images/settlement" + game.getCurrentPlayer().getColor() + ".png");
            // Setting city image's coordinates.
            structure.setX( getXToDisplay() );
            structure.setY( y * 30 + 35);
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
        alert.setHeaderText("Building a Road");
        alert.setContentText("Do you want to build a road?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK){

            // Check if the user obligated to build a road
            if ( flowManager.checkMust() == 0 )
            {
                flowManager.doneMust();
            }

            // Make the corresponding game tile in the given index a road.
            game.setTile( x, y);
            // Initializing road image to be shown on the UI.
            ImageView structure = new ImageView("/images/road" + game.getCurrentPlayer().getColor() + ".png");
            // Determining its rotation type corresponding to the hexagon side. Setting the road image's coordinates.
            setRoadRotation(structure, x, y);
            structure.setY( y * 30 + 30);
            new ZoomIn(structure).play();
            // Adding the road image to the game area in UI.
            gameBox.getChildren().add(structure);

            // Refreshing current player information
            infoController.setupCurrentPlayer();
            infoController.setupLongestRoad();

            // If its initial state, player has to immediately end the turn.
            if ( flowManager.checkMust() == 6 )
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
        RoadTile.RotationType rotationType = game.rotationType(x, y);
        switch (rotationType)
        {
            case HORIZONTAL:
                road.setRotate(road.getRotate() + 105);
                road.setX( getXToDisplay() + 5);
                break;
            case UPPER_LEFT_VERTICAL:
                road.setRotate(road.getRotate() - 15);
                road.setX( getXToDisplay());
                break;
            case UPPER_RIGHT_VERTICAL:
                road.setRotate(road.getRotate() + 225);
                road.setX( getXToDisplay() - 5);
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
        alert.setHeaderText("Upgrading To City");
        alert.setContentText("Do you want to upgrade your settlement to a city?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK){

            // Check if the user obligated to build a city
            if ( flowManager.checkMust() == 2 )
            {
                flowManager.doneMust();
            }

            // Make the corresponding game tile in the given index a city.
            game.setTile( x, y);

            // Initializing road image to be shown on the UI.
            ImageView structure = new ImageView("/images/city" + game.getCurrentPlayer().getColor() + ".png");

            // Setting city image's coordinates.
            structure.setX( getXToDisplay() );
            structure.setY( y * 30 + 35);

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
        }
    }

    /**
     * Processes the mouse click event for the x coordinate of game board
     * @param x is the x coordinate given by the mouse event
     * @return an integer index, the processed result corresponding the x index for the game board
     */
    private int processX( double x)
    {
        // Threshold for eliminating 0 index bug
        if (  x < 35 )
        {
            return -1;
        }
        else
        {
            x = x - 45; // Omit threshold

            hexIndex = 0;
            while ( (int) (x / 170) > 0 ) // Find the right index of the hexagon.
            {
                ++hexIndex;
                x = x - 120; // Discard the first hexagon, shift every hexagon to left
            }
            System.out.println( "Hexindex is: " + hexIndex); /********************************************************/

            // Find the right index of the tile in the hexagon
            // PLEASE DONT JUDGE ME IT IS 01.33 AM AND I AM TIRED! i will update it with a while loop i am aware pls.
            if ( (int) (x / 10) == 0 )
            {
                tileIndex = 0;
            }
            else
            {
                x = x - 10;
                if ( (int) (x / 20) == 0 )
                {
                    tileIndex = 1;
                }
                else
                {
                    x = x - 20;
                    if ( (int) (x / 30) == 0 )
                    {
                        tileIndex = 2;
                    }
                    else
                    {
                        x = x - 30;
                        if ( (int) (x / 40) == 0 )
                        {
                            tileIndex = 3;
                        }
                        else
                        {
                            x = x - 40;
                            if ( (int) (x / 30) == 0 )
                            {
                                tileIndex = 4;
                            }
                            else
                            {
                                x = x - 30;
                                if ( (int) (x / 20) == 0 )
                                {
                                    tileIndex = 5;
                                }
                                else
                                {
                                    tileIndex = 6;
                                }
                            }
                        }
                    }
                }
            }
            int realTileIndex = 0;
            int tempHexIndex = hexIndex;
            while ( tempHexIndex > 0)
            {
                tempHexIndex--;
                realTileIndex += 4;
            }

            return realTileIndex + tileIndex;
        }
    }

    /**
     * Processes the mouse click event for the y coordinate of game board
     * @param y is the y coordinate given by the mouse event
     * @return an integer index, the processed result corresponding the y index for the game board
     */
    private int processY( double y)
    {
        if ( y < 35)
        {
            return -1;
        }
        // How beautiful everything is when each tile has the same height ;/
        y -= 35;
        return (int) y / 30;
    }

    /**
     * Gets the real value of x pixel coordinate for displaying structures to the players.
     * @return the corresponding x pixel value on the screen regarding parameters hexIndex and tileIndex
     */
    private int getXToDisplay()
    {
        // Process the hexIndex and tileIndex to access the x pixel on the screen for display.
        int x = hexIndex * 120;
        if ( tileIndex == 0 )
        {
            x += 40;
        }
        else if ( tileIndex == 1 )
        {
            x += 60;
        }
        else if ( tileIndex == 2 )
        {
            x += 80;
        }
        else if ( tileIndex == 3 )
        {
            x += 110;
        }
        else if ( tileIndex == 4 )
        {
            x += 150;
        }
        else if ( tileIndex == 5)

        {
            x += 180;
        }
        else
        {
            x += 200;
        }

        return x;
    }

    /**
     * End the turn when the end turn button is pressed or player builds a road in the initial phase.
     */
    private void performEndTurnButtonEvent() {
        // Check if the user has to do something before ending their turn
        if (flowManager.checkMust() == -1 || flowManager.checkMust() == 6) {
            // Check if the user ends the turn because of obligation
            if (flowManager.checkMust() == 6) {
                // Done the must, yeey :)
                flowManager.doneMust();
            }
            game.endTurn();
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
                    statusController.informStatus(-9);
                    devCardController.setupDevelopmentCards();
                }
            });
            new Thread(sleeper2).start();
        } else {
            statusController.informStatus(flowManager.checkMust());
        }
    }

    /**
     * The function to handle user intereaction for the game board.
     * @param resultCode is the code returned from the game
     * @param x is the x corrdinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void createDialog(  int resultCode, int x, int y )
    {

        // If the controller returns minus integer, there is an error!
        if ( resultCode < 0 )
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
    private void informBoardSelection( Alert alert, int resultCode, int x, int y ) {
        int mustCheckCode = flowManager.checkMust();

        // GameFlow.Player tries to build a road
        if (resultCode == 0) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == -1 || mustCheckCode == 0) {
                buildRoad(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // GameFlow.Player tries to build a settlement
        else if (resultCode == 1) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == -1 || mustCheckCode == 1) {
                buildSettlement(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // GameFlow.Player tries to build a city
        else if (resultCode == 2) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == -1 || mustCheckCode == 2) {
                buildCity(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
    }

    /**
     * Returns the game.
     * @return the game.
     */
    public Game getGame() {
        return game;
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
