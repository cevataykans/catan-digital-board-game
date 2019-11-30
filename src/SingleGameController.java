import animatefx.animation.FadeIn;
import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SingleGameController implements Controller {
    private static int hexIndex = -1;
    private static int tileIndex = -1;
    HashMap<Point2D, Integer> settlementMap = new HashMap<>();

    // Properties
    Parent root;
    Scene scene;
    FlowManager flowManager;
    ArrayList<Player> players;
    Game game;
    AnchorPane gameBox;
    Button endTurn;

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
    @Override
    public void initialize(Stage stage) throws IOException {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Game.css").toExternalForm());
        scene.setRoot(root);

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
                finalRoot.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot);
                animation.setSpeed(3.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        game = new Game(players);
        flowManager = FlowManager.getInstance();
        gameBox = (AnchorPane) scene.lookup("#gameBox");
        robber = new javafx.scene.image.ImageView("/images/robber.png");
        setupGameBoard();
        setupRobber();

        infoController = new PlayerInfoController(scene, this);
        statusController = new StatusController(scene, this);
        devCardController = new DevCardController(scene, this);
        selectionController = new SelectionController(scene, this);
        diceController = new DiceController(scene, this);

        gameBox.setOnMouseClicked(mouseEvent -> {

            // Allow the action to be processed for game board UI if only game board related must, be done
            if ( flowManager.checkMust() < 4 )
            {
                int x = processX(mouseEvent.getX());
                int y = processY(mouseEvent.getY());

                System.out.print("X is: " + mouseEvent.getX() + " | Y is: " + mouseEvent.getY()); /********************************************************/
                System.out.println(" X' is: " + x + " | Y' is: " + y); /********************************************************/

                createDialog(game.checkTile(x, y), x, y);
            }
            else
            {
                statusController.informStatus( flowManager.checkMust() );
            }
        });

        endTurn = (Button) scene.lookup( "#endTurn");
        endTurn.setOnMouseReleased(mouseEvent ->
        {
            performEndTurnButtonEvent();
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case E: performEndTurnButtonEvent();
            }
        });

        stage.setScene(scene);
    }

    public void setupGameBoard() {
        // Initialize the controller
        game.configureGame();
        Tile[][] board = game.getBoard();
        ImageView hexagon;
        // For each Tile in the game board ( this method could be optimized with increased i, j index increment)
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
                if ( board[ i][ j] instanceof StartTile )
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
                    robber.setY( movedY * 30 );
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

            game.setTile( x, y);
            ImageView structure = new ImageView("/images/settlement" + game.getCurrentPlayer().getColor() + ".png");
            structure.setX( getXToDisplay() );
            structure.setY( y * 30);
            new ZoomIn(structure).play();
            gameBox.getChildren().add(structure);
            settlementMap.put(new Point2D(x, y), gameBox.getChildren().lastIndexOf(structure));

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

            game.setTile( x, y);
            ImageView structure = new ImageView("/images/road" + game.getCurrentPlayer().getColor() + ".png");
            RoadTile.RotationType rotationType = game.rotationType(x, y);
            switch (rotationType)
            {
                case HORIZONTAL:
                    structure.setRotate(structure.getRotate() + 105);
                    structure.setX( getXToDisplay() + 5);
                    break;
                case UPPER_LEFT_VERTICAL:
                    structure.setRotate(structure.getRotate() - 15);
                    structure.setX( getXToDisplay());
                    break;
                case UPPER_RIGHT_VERTICAL:
                    structure.setRotate(structure.getRotate() + 225);
                    structure.setX( getXToDisplay() - 5);
                    break;
            }
            structure.setY( y * 30 - 5);
            new ZoomIn(structure).play();
            gameBox.getChildren().add(structure);

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

            game.setTile( x, y);
            ImageView structure = new ImageView("/images/city" + game.getCurrentPlayer().getColor() + ".png");
            structure.setX( getXToDisplay() );
            structure.setY( y * 30);
            ImageView settlement = (ImageView) gameBox.getChildren().get(settlementMap.get(new Point2D(x, y)));
            ZoomOut settlementOut = new ZoomOut(settlement);
            settlementOut.setOnFinished(event ->
            {
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
        if (  x < 20 )
        {
            return -1;
        }
        else
        {
            x = x - 30; // Omit threshold

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
        // How beautiful everything is when each tile has the same height ;/
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
            x += 20;
        }
        else if ( tileIndex == 1 )
        {
            x += 40;
        }
        else if ( tileIndex == 2 )
        {
            x += 60;
        }
        else if ( tileIndex == 3 )
        {
            x += 90;
        }
        else if ( tileIndex == 4 )
        {
            x += 130;
        }
        else if ( tileIndex == 5)

        {
            x += 160;
        }
        else
        {
            x += 180;
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

        // Player tries to build a road
        if (resultCode == 0) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == -1 || mustCheckCode == 0) {
                buildRoad(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // Player tries to build a settlement
        else if (resultCode == 1) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == -1 || mustCheckCode == 1) {
                buildSettlement(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // Player tries to build a city
        else if (resultCode == 2) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == -1 || mustCheckCode == 2) {
                buildCity(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
    }
}
