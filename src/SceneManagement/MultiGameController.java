package SceneManagement;

import DevelopmentCards.Card;
import GameBoard.Harbor;
import GameBoard.RoadTile;
import GameBoard.StartTile;
import GameBoard.Tile;
import GameBoard.BuildingTile;
import GameFlow.*;
import Player.Player;
import SceneManagement.GameManagement.*;
import ServerCommunication.ServerHandler;
import ServerCommunication.ServerInformation;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MultiGameController extends SceneController {
    private HashMap<Point2D, ImageView> settlementMap = new HashMap<>();

    // Properties
    private ArrayList<Player> players;
    private Player localPlayer;
    private AnchorPane gameBox;
    private Button buyDevelopmentCard;
    private Button endTurn;

    // Highlight related properties
    private boolean highlightOn = false;
    private ImageView highImg = null;

    // Sub Controllers
    private MultiPlayerInfoController infoController;
    private MultiStatusController statusController;
    private MultiDevCardController devCardController;
    private MultiSelectionController selectionController;
    private MultiDiceController diceController;
    private MultiHarborController harborController;
    private ChatController chatController;

    // Robber Related Properties
    private ImageView robber;
    private AtomicReference<Double> xRob = new AtomicReference<>((double) 0);
    private AtomicReference<Double> yRob = new AtomicReference<>((double) 0);
    private double distXRob = 0;
    private double distYRob = 0;
    private double initialRobX;
    private double initialRobY;

    // ESC Popup Properties
    private AnchorPane escPopup;
    private Button continueButton;
    private Button mainMenuButton;
    private Button exitButton;

    // Constructor
    public MultiGameController(Stage stage) throws IOException
    {
        JSONObject obj = ServerInformation.getInstance().getInformation();

        this.players = new ArrayList<>();
        try {
            JSONArray playerNames =  (JSONArray) obj.get("players");


            for ( int i = 0; i < playerNames.length(); i++)
            {
                Player player = new Player(playerNames.getString(i), null);
                switch (i)
                {
                    case 0:
                        player.setColor(Color.BLUE);
                        break;
                    case 1:
                        player.setColor(Color.WHITE);
                        break;
                    case 2:
                        player.setColor(Color.ORANGE);
                        break;
                    case 3:
                        player.setColor(Color.BROWN);
                        break;
                }
                if ( playerNames.getString(i).equals(ServerHandler.getInstance().getUserId()))
                {
                    if ( i == 0 )
                    {
                        ServerHandler.getInstance().setStatus(ServerHandler.Status.SENDER);
                    }
                    this.localPlayer = player;
                }
                this.players.add(player);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        for(Player p: players) {
            System.out.println(p.getName());
            System.out.println(p.getColor());
        }
        this.root = FXMLLoader.load(getClass().getResource("/UI/MultiGame.fxml"));
        this.scene = new Scene(root, Color.BLACK);
        this.initialize(stage);
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
        scene.getStylesheets().add(getClass().getResource("/UI/MultiGame.css").toExternalForm());
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
        ResourceManager resourceManager = new ResourceManager();

        gameBox = (AnchorPane) scene.lookup("#gameBox");
        robber = new ImageView("/images/robber.png");

        setupGameBoard( tiles); // Game is configured here
        setupRobber(); // Robber is configured here

        escPopup = (AnchorPane) scene.lookup("#escPopup");
        continueButton = (Button) scene.lookup("#continue");
        mainMenuButton = (Button) scene.lookup("#mainMenu");
        exitButton = (Button) scene.lookup("#exitGame");

        // Initializing all the sub controllers that will handle the game's other logic.
        infoController = new MultiPlayerInfoController(scene, this);
        statusController = new MultiStatusController(scene, this);
        devCardController = new MultiDevCardController(scene, this);
        selectionController = new MultiSelectionController(scene, this);
        diceController = new MultiDiceController(scene, this);
        harborController = new MultiHarborController(scene, this);
        chatController = new ChatController(scene, this);

        // Adding listener to make the game board intractable.
        gameBox.setOnMouseClicked(mouseEvent -> {
            if ( localPlayer == flowManager.getCurrentPlayer()) {
                System.out.println("x: " + mouseEvent.getX() + " y: " + mouseEvent.getY());
                // Allow the action to be processed for game board UI if only game board related must, be done
                Response response = flowManager.checkMust();
                if (response == Response.MUST_FREE_TURN ||
                        response == Response.MUST_ROAD_BUILD ||
                        response == Response.MUST_SETTLEMENT_BUILD ||
                        response == Response.MUST_CITY_BUILD ||
                        response == Response.MUST_INSIDE_TILE_SELECTION) {
                    System.out.println("Checkpoint1");
                    System.out.println(" Hex: " + PixelProcessor.getHexIndex());
                    System.out.println(" Tile: " + PixelProcessor.getTileIndex());
                    System.out.println("mousex: " + mouseEvent.getX() + " mousey: " + mouseEvent.getY());
                    // Getting the mouse coordinates.
                    int x = PixelProcessor.processX(mouseEvent.getX());
                    int y = PixelProcessor.processY(mouseEvent.getY());

                    // Checking if the clicked coordinate is a game tile
                    createDialog(boardManager.checkTile(x, y), x, y);
                } else {
                    if ( response != Response.MUST_FREE_TURN) {
                        statusController.informStatus(flowManager.checkMust());
                    }
                }
            }
        });

        // Adding listener to make the game board highlightable to make the more usable.
        gameBox.setOnMouseMoved(mouseEvent2 ->
        {
            if (localPlayer == flowManager.getCurrentPlayer()) {
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
                    if ( structureCheck == Response.INFORM_ROAD_CAN_BE_BUILT
                            && (flowManager.checkMust() == Response.MUST_FREE_TURN || flowManager.checkMust() == Response.MUST_ROAD_BUILD))
                    {
                        // Get the road image with the current player's color.
                        ImageView roadHighlight = new ImageView("/images/road" + flowManager.getCurrentPlayer().getColor()
                                + ".png");
                        roadHighlight.setOpacity( 0.65);
                        this.highImg = roadHighlight;
                        // Set its rotation depending on the hexagon side and sets its x depending on rotation.
                        setRoadRotation(roadHighlight, x, y);
                        // Set road highlight's y corresponding to the hexagon side.
                        roadHighlight.setY( y * 30 + 35);
                        // Add the road highlight to the UI.
                        gameBox.getChildren().add(roadHighlight);
                        // Set the highlight boolean to true so that user can't view multiple highlights in the same/other
                        // place(s).
                        highlightOn = true;
                    }
                    // Check if the hovered tile is a constructable settlement.
                    else if ( structureCheck == Response.INFORM_SETTLEMENT_CAN_BE_BUILT &&
                            (flowManager.checkMust() == Response.MUST_FREE_TURN || flowManager.checkMust() == Response.MUST_SETTLEMENT_BUILD))
                    {
                        // Get the settlement image with the current player's color.
                        ImageView settlementHighlight = new ImageView("/images/settlement" + flowManager.getCurrentPlayer()
                                .getColor() + ".png");
                        settlementHighlight.setOpacity( 0.65);
                        this.highImg = settlementHighlight;
                        // Set its x depending on the hexagin corner.
                        settlementHighlight.setX( PixelProcessor.getXToDisplay() );
                        // Set road highlight's y corresponding to the hexagon corner.
                        settlementHighlight.setY( PixelProcessor.getYToDisplay( y) );
                        // Add the settlement highlight to the UI.
                        gameBox.getChildren().add(settlementHighlight);
                        // Set the highlight boolean to true so that user can't view multiple highlights in the same/other
                        // place(s).
                        highlightOn = true;
                    }
                }

                // This will definetely (hopefully) destroy an image to prevent hover bug bi making region check
                if ( highlightOn && this.highImg != null )
                {
                    // Location of the mouse
                    Double mouseX = mouseEvent2.getX();
                    Double mouseY = mouseEvent2.getY();

                    // Width and height of the image
                    Double imageWidth = this.highImg.getImage().getWidth();
                    Double imageHeight = this.highImg.getImage().getHeight();

                    // Location of the image
                    Double imageX = this.highImg.getX();
                    Double imageY = this.highImg.getY();

                    // If mouse is not in the region of image, this also almost covers the road with rotated scenario, np
                    if ( !(mouseX >= imageX && mouseY >= imageY && mouseX <= imageX + imageWidth && mouseY <= imageY + imageHeight) )
                    {
                        // Remove the road highlight and set the highlight boolean to false so that the player
                        // can view other highlights.
                        gameBox.getChildren().remove( this.highImg);
                        this.highImg = null;
                        highlightOn = false;
                    }
                }
            }
        });

        // Getting the development card button from the single-game fxml file and adding the buy card logic to its click listener.
        buyDevelopmentCard = (Button) scene.lookup("#buyDevelopmentCard");
        buyDevelopmentCard.setOnMouseClicked(event ->
        {
            if ( localPlayer == flowManager.getCurrentPlayer() && flowManager.checkMust() == Response.MUST_FREE_TURN )
            {
                if( resourceManager.hasEnoughResources(flowManager.getCurrentPlayer(), Card.REQUIREMENTS_FOR_CARD) ) {
                    cardManager.addDevelopmentCard();
                    devCardController.setupDevelopmentCards();
                    infoController.setupCurrentPlayer();
                }
                else
                    statusController.informStatus( Response.ERROR_NO_RESOURCE_FOR_CARD);
            }
            else{
                statusController.informStatus(flowManager.checkMust());
            }
        });

        // Getting the end turn button from the single-game fxml file and adding the end turn logic to its click listener.
        endTurn = (Button) scene.lookup( "#endTurn");
        endTurn.setOnMouseReleased(mouseEvent ->
        {
            if (localPlayer == flowManager.getCurrentPlayer()) {
                performEndTurnButtonEvent();
            }
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
                    case E: if (localPlayer == flowManager.getCurrentPlayer()) {performEndTurnButtonEvent();}
                        break;
                    case H: robber.setImage( new Image("/images/hakan.jpeg", 45, 70, false, false) );
                        break;
                    case G: robber.setImage( new Image("/images/goose.png", 45, 70, false, false) );
                        break;
                    case ESCAPE:
                        if ( escPopup.isVisible()) {
                            escPopup.setVisible(false);
                        }
                        else {
                            escPopup.setVisible(true);
                        }
                        continueButton.setOnMouseClicked(event1 -> {
                            escPopup.setVisible(false);
                        });
                        mainMenuButton.setOnMouseClicked(event1 -> {
                            // Initializing closing animation for game scene.
                            FadeOut animation2 = new FadeOut(root);
                            animation2.setSpeed(3.5);
                            animation2.setOnFinished(event2 ->
                            {
                                try
                                {
                                    // Make this scene invisible and change the controller to main menu from GameEngine.
                                    root.setVisible(false);
                                    ServerHandler.getInstance().logout();
                                    GameEngine.getInstance().setController(0);
                                }
                                catch (IOException e)
                                {
                                    System.out.println(e);
                                }
                            });
                            animation2.play();
                        });
                        exitButton.setOnMouseClicked(event1 -> {
                            Platform.exit();
                            System.exit(0);
                        });
                        break;
                    default:
                        break;
                }
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
                stage.setMaximized(true);
                stage.setResizable(false);
            }
        });
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

                // Set up the harbor images, these image setx, sety values are manually adjusted, be careful!
                // These indexes on the board are selected specially to display harbors!
                if (  board[ i][ j] instanceof BuildingTile && ((BuildingTile) board[ i][ j]).getHarbor() != null )
                {
                    String imgPath = this.getHarborImagePath(((BuildingTile) board[i][j]).getHarbor());
                    ImageView harborImg;
                    if (i == 0 && j == 10) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 105);
                        harborImg.setY(i * 30);
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 10 && j == 0) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 90); // + 105
                        harborImg.setY(i * 30 - 30); // - 15
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 10 && j == 22) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 65); //+ 105
                        harborImg.setY(i * 30 - 25); // -5
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 18 && j == 6) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 105);
                        harborImg.setY(i * 30 + 40); // -5
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 18 && j == 16) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 45);
                        harborImg.setY(i * 30 + 40);
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 2 && j == 6) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 105);
                        harborImg.setY(i * 30 - 5);
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 2 && j == 16) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 45); // 105
                        harborImg.setY(i * 30 - 5);
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 14 && j == 0) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 90);
                        harborImg.setY(i * 30 - 30);
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    } else if (i == 14 && j == 22) {
                        harborImg = new ImageView(new Image(imgPath, 60, 60, false, false));
                        harborImg.setX((j - 2) * 30 + 65);
                        harborImg.setY(i * 30 - 25);
                        gameBox.getChildren().add(harborImg);
                        harborImg.toFront();
                    }
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
            if(localPlayer == flowManager.getCurrentPlayer()){
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
                }
                else
                {
                    statusController.informStatus( flowManager.checkMust() );
                }
            }

        });

        // User drags the robber to another hexagon of choice or simply can put it in the same place to continue blocking the users
        robber.setOnMouseDragged(e ->
        {
            if(localPlayer == flowManager.getCurrentPlayer()){
                System.out.println( "Mouse Dragging, coordinateX: "+ PixelProcessor.processX( e.getX() ) + " y: "
                        + PixelProcessor.processY( e.getY() ) );
                if ( flowManager.checkMust() == Response.MUST_INSIDE_TILE_SELECTION )
                {
                    // source : https://blogs.oracle.com/vaibhav/image-drag-with-mouse-in-javafx
                    robber.setX( e.getX() - distXRob );
                    robber.setY( e.getY() - distYRob );
                    if ( robber.getX() < 0 || robber.getX() > 660 || robber.getY() < 0 || robber.getY() > 600)
                    {
                        // Return the robber if it is out of bounds of game board!
                        e.consume();
                        robber.setX( initialRobX );
                        robber.setY( initialRobY );
                        statusController.informStatus( Response.ERROR_OUTSIDE_GAMEBOARD );
                    }
                }
                else
                {
                    statusController.informStatus( flowManager.checkMust() );
                }
            }
        });

        // Check if user has put the robber onto a valid position
        robber.setOnMouseReleased(e ->
        {
            if(localPlayer == flowManager.getCurrentPlayer()){
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
                else // SUCCESSFUL
                {
                    changeRobber(e.getX(), e.getY());
                }

            }
        });
    }

    public void changeRobber(double mouseX, double mouseY){
        FlowManager flowManager = new FlowManager();
        BoardManager boardManager = new BoardManager();

        int movedX = PixelProcessor.processX( mouseX );
        int movedY = PixelProcessor.processY( mouseY );

        System.out.println( "Moude Released, coordinateX: "+ PixelProcessor.processX( mouseX ) + " y: "
                + PixelProcessor.processY( mouseY ) );

        robber.setX( PixelProcessor.getXToDisplay() );
        robber.setY( PixelProcessor.getYToDisplay( movedY) );
        boardManager.changeRobber( movedX, movedY);
        SoundManager.getInstance().playEffect(SoundManager.Effect.ROBBER);

        if(localPlayer == flowManager.getCurrentPlayer()){ // SENDER
            ServerHandler.getInstance().setupRobber(mouseX, mouseY); // Send signal to the Server
            flowManager.doneMust();
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

    public void selectPlayer(String name){
        for(Player p: players){
            if(p.getName().equals(name))
                selectionController.stealResourceFromPlayer(p);
        }
    }

    /**
     * Sets, builds and shows a settlement on the given coordinate for the current player.
     * @param alert is the dialog prompting confirmation of building the settlement (SENDER ONLY)
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    public void buildSettlement(Alert alert, int x, int y)
    {
        FlowManager flowManager = new FlowManager();

        if ( localPlayer == flowManager.getCurrentPlayer()) {
            alert.setHeaderText("Building a Settlement");
            alert.setContentText("Do you want to build a settlement?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // Check if the user obligated to build a settlement
                if (flowManager.checkMust() == Response.MUST_SETTLEMENT_BUILD) {
                    flowManager.doneMust();
                }
                showSettlement(flowManager, x, y);
                ServerHandler.getInstance().buildSettlement(x, y, PixelProcessor.getHexIndex(), PixelProcessor.getTileIndex());
            }
        }
    }

    public void receiveBuildSettlement(int x, int y)
    {
        FlowManager flowManager = new FlowManager();
        JSONObject obj = ServerInformation.getInstance().getInformation();
        ServerInformation.getInstance().deleteInformation();
        int hexIndex = 0;
        int tileIndex = 0;
        try {
            hexIndex = obj.getInt("hexIndex");
            tileIndex = obj.getInt("tileIndex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PixelProcessor.setHexIndex(hexIndex);
        PixelProcessor.setTileIndex(tileIndex);
        showSettlement(flowManager, x, y);
    }

    /**
     * Shows the settlement belonging to the current player in the UI (called for every client).
     * @param flowManager is the manager that will pass the current player to the controller.
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void showSettlement(FlowManager flowManager, int x, int y)
    {

        // Get the necessary managers
        BoardManager boardManager = new BoardManager();

        // Make the corresponding game tile in the given index a road.
        boardManager.setTile( x, y);

        // Initializing road image to be shown on the UI.
        ImageView structure = new ImageView("/images/settlement" + flowManager.getCurrentPlayer().getColor() + ".png");

        // Setting city image's coordinates.
        structure.setX( PixelProcessor.getXToDisplay() );
        structure.setY( PixelProcessor.getYToDisplay( y) );
        // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
        Platform.runLater(
                () -> {
                    // Playing a zoom in animation for the settlement.
                    new ZoomIn(structure).play();

                    // Adding settlement image to the game area in UI.
                    gameBox.getChildren().add(structure);

                    // Putting the settlement image to the settlement map, a map that is used to switch settlement images with
                    // city images when the player upgrades settlement to city.
                    settlementMap.put(new Point2D(x, y), structure);

                    // Refresh current player information.
                    infoController.setupCurrentPlayer();
                    SoundManager.getInstance().playEffect(SoundManager.Effect.SETTLEMENT_BUILT);
                    checkWinCondition();
                }
        );
    }

    /**
     * Shows the road belonging to the current player in the UI (called for every client).
     * @param alert is the dialog prompting confirmation of building the road
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    public void buildRoad(Alert alert, int x, int y)
    {
        FlowManager flowManager = new FlowManager();

        alert.setHeaderText("Building a Road");
        alert.setContentText("Do you want to build a road?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // Check if the user obligated to build a road
            if (flowManager.checkMust() == Response.MUST_ROAD_BUILD) {
                flowManager.doneMust();
            }

            showRoad(flowManager, x, y);
            ServerHandler.getInstance().buildRoad(x, y, PixelProcessor.getHexIndex(), PixelProcessor.getTileIndex());

            // If its initial state, player has to immediately end the turn.
            if (flowManager.checkMust() == Response.MUST_END_TURN) {
                performEndTurnButtonEvent();
            }
        }

    }

    /**
     * Shows the road belonging to the current player in the UI (called for every client).
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    public void receiveBuildRoad(int x, int y)
    {
        FlowManager flowManager = new FlowManager();


        JSONObject obj = ServerInformation.getInstance().getInformation();
        ServerInformation.getInstance().deleteInformation();
        int hexIndex = 0;
        int tileIndex = 0;
        try {
            hexIndex = obj.getInt("hexIndex");
            tileIndex = obj.getInt("tileIndex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PixelProcessor.setHexIndex(hexIndex);
        PixelProcessor.setTileIndex(tileIndex);
        showRoad(flowManager, x, y);

    }

    /**
     * Shows the road belonging to the current player in the UI (called for every client).
     * @param flowManager is the manager that will pass the current player to the controller.
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void showRoad(FlowManager flowManager, int x, int y)
    {
        // Get the necessary managers
        BoardManager boardManager = new BoardManager();

        // Make the corresponding game tile in the given index a road.
        boardManager.setTile( x, y);
        // Initializing road image to be shown on the UI.
        ImageView structure = new ImageView("/images/road" + flowManager.getCurrentPlayer().getColor() + ".png");
        // Determining its rotation type corresponding to the hexagon side. Setting the road image's coordinates.
        setRoadRotation(structure, x, y);
        structure.setY( y * 30 + 30);
        Platform.runLater(
                () -> {
                    new ZoomIn(structure).play();
                    // Adding the road image to the game area in UI.
                    gameBox.getChildren().add(structure);

                    // Refreshing current player information
                    infoController.setupCurrentPlayer();
                    SoundManager.getInstance().playEffect(SoundManager.Effect.ROAD_BUILD);
                    checkWinCondition();
                }
        );
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
     * Shows the city belonging to the current player in the UI (called for every client).
     * @param alert is the dialog prompting confirmation of upgrading to the city
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    public void buildCity( Alert alert, int x, int y)
    {
        FlowManager flowManager = new FlowManager();

        alert.setHeaderText("Upgrading To City");
        alert.setContentText("Do you want to upgrade your settlement to a city?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // Check if the user obligated to build a city
            if (flowManager.checkMust() == Response.MUST_CITY_BUILD) {
                flowManager.doneMust();
            }

            showCity(flowManager, x, y);
            ServerHandler.getInstance().buildCity(x, y, PixelProcessor.getHexIndex(), PixelProcessor.getTileIndex());
        }

    }

    /**
     * Shows the city belonging to the current player in the UI (called for every client).
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    public void receiveBuildCity( int x, int y)
    {
        FlowManager flowManager = new FlowManager();

        JSONObject obj = ServerInformation.getInstance().getInformation();
        ServerInformation.getInstance().deleteInformation();
        int hexIndex = 0;
        int tileIndex = 0;
        try {
            hexIndex = obj.getInt("hexIndex");
            tileIndex = obj.getInt("tileIndex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PixelProcessor.setHexIndex(hexIndex);
        PixelProcessor.setTileIndex(tileIndex);
        showCity(flowManager, x, y);

    }


    /**
     * Shows the city belonging to the current player in the UI (called for every client).
     * @param flowManager is the manager that will pass the current player to the controller.
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void showCity(FlowManager flowManager, int x, int y)
    {
        // Get the necessary managers
        BoardManager boardManager = new BoardManager();

        // Make the corresponding game tile in the given index a city.
        boardManager.setTile( x, y);

        // Initializing road image to be shown on the UI.
        ImageView structure = new ImageView("/images/city" + flowManager.getCurrentPlayer().getColor() + ".png");

        // Setting city image's coordinates.
        structure.setX( PixelProcessor.getXToDisplay() );
        structure.setY( PixelProcessor.getYToDisplay( y) );

        Platform.runLater(
                () -> {
                    // Switching the corresponding settlement image with city image via an out-in animation.
                    ImageView settlement = settlementMap.get( new Point2D( x, y) );
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
                    checkWinCondition();
                }
        );
    }

    /**
     * End the turn when the end turn button is pressed or player builds a road in the initial phase.
     */
    public void performEndTurnButtonEvent()
    {
        FlowManager flowManager = new FlowManager();
        if(localPlayer == flowManager.getCurrentPlayer()){
            // Check if the user has to do something before ending their turn
            if ( flowManager.checkMust() == Response.MUST_FREE_TURN || flowManager.checkMust() == Response.MUST_END_TURN) {

                // Check if the user ends the turn because of obligation
                if ( flowManager.checkMust() == Response.MUST_END_TURN) {
                    flowManager.doneMust();
                }
                flowManager.endTurn();

                SoundManager.getInstance().playEffect(SoundManager.Effect.END_TURN);
                Platform.runLater(
                        () -> {
                            infoController.setupOtherPlayers();
                            infoController.setupCurrentPlayer();
                            diceController.setupDiceRoll();
                        }
                );
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
                        Platform.runLater(
                                () -> {
                                    if ( Game.getInstance().getGameStatus() == 1) {
                                        statusController.informStatus(Response.INFORM_ROLL_DICE);
                                    }
                                    else {
                                        statusController.informStatus(Response.MUST_SETTLEMENT_BUILD);
                                    }
                                    devCardController.setupDevelopmentCards();
                                }
                        );
                    }
                });
                new Thread(sleeper2).start();
                if(localPlayer == flowManager.getCurrentPlayer()){ // This player is next player
                    Platform.runLater(
                            () -> {
                                statusController.informStatus( flowManager.checkMust());
                            }
                    );
                }
                else{ // This player is not next player
                    flowManager.discardAllMust();
                }
                ServerHandler.getInstance().endTurn();
            } else {
                Platform.runLater(
                        () -> {
                            statusController.informStatus( flowManager.checkMust());
                        }
                );
            }
        }
        else{ // RECEIVER
            flowManager.endTurn();
            SoundManager.getInstance().playEffect(SoundManager.Effect.END_TURN);
            // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
            Platform.runLater(
                    () -> {
                        infoController.setupOtherPlayers();
                        infoController.setupCurrentPlayer();
                        diceController.setupDiceRoll();
                    }
            );

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
                    Platform.runLater(
                            () -> {
                                if ( Game.getInstance().getGameStatus() == 1) {
                                    statusController.informStatus(Response.INFORM_ROLL_DICE);
                                }
                                else {
                                    statusController.informStatus(Response.MUST_SETTLEMENT_BUILD);
                                }
                                devCardController.setupDevelopmentCards();
                            }
                    );
                }
            });
            new Thread(sleeper2).start();

            if(localPlayer == flowManager.getCurrentPlayer()){ // This player is next player
                Platform.runLater(
                        () -> {
                            statusController.informStatus( flowManager.checkMust());
                        }
                );
            }
            else{ // This player is not next player
                flowManager.discardAllMust();
            }
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
        if(resultCode == Response.INFORM_SETTLEMENT_CAN_BE_BUILT ||
                resultCode == Response.INFORM_ROAD_CAN_BE_BUILT ||
                resultCode == Response.INFORM_CITY_CAN_BE_BUILT ||
                resultCode == Response.INFORM_INSIDE_TILE ||
                resultCode == Response.INFORM_SEA_TILE ||
                ( new FlowManager().checkMust() == Response.MUST_ROAD_BUILD &&
                        resultCode != Response.ERROR_NO_CONNECTION_FOR_ROAD &&
                        resultCode != Response.ERROR_OCCUPIED_BY ) ||
                ( new FlowManager().checkMust() == Response.MUST_SETTLEMENT_BUILD &&
                        resultCode != Response.ERROR_NO_CONNECTION_FOR_SETTLEMENT &&
                        resultCode != Response.ERROR_THERE_IS_NEAR_BUILDING_FOR_SETTLEMENT &&
                        resultCode != Response.ERROR_OCCUPIED_BY ) )
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
        else{
            System.out.println( " error is ** " + resultCode + "   "); /***********************************************/
            // handle error
            statusController.informStatus( resultCode);
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
            if (mustCheckCode == Response.MUST_FREE_TURN || mustCheckCode == Response.MUST_ROAD_BUILD) {
                buildRoad(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // Player.Player tries to build a settlement
        else if (resultCode == Response.INFORM_SETTLEMENT_CAN_BE_BUILT) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == Response.MUST_FREE_TURN || mustCheckCode == Response.MUST_SETTLEMENT_BUILD) {
                buildSettlement(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        // Player.Player tries to build a city
        else if (resultCode == Response.INFORM_CITY_CAN_BE_BUILT) {
            // Allow construction only if player is obliged or free to hang arooound lol
            if (mustCheckCode == Response.MUST_FREE_TURN || mustCheckCode == Response.MUST_CITY_BUILD) {
                buildCity(alert, x, y);
            } else {
                statusController.informStatus(mustCheckCode);
            }
        }
        else{
            statusController.informStatus(mustCheckCode);
        }
    }

    public void checkWinCondition()
    {
        class SortByScore implements Comparator<Player>
        {
            public int compare( Player a, Player b)
            {
                if ( a.getScore() <= b.getScore() )
                {
                    return a.getScore();
                }
                else
                {
                    return b.getScore();
                }
            }
        }

        Player curPlayer = new FlowManager().getCurrentPlayer();
        // For test, you can decrease this to 2!
        if ( curPlayer.getScore() >= 10)
        {
            SoundManager.getInstance().playEffect(SoundManager.Effect.VICTORY);
            Alert alert = new Alert( Alert.AlertType.INFORMATION);

            // Create a beautiful icon for catan dialog
            ImageView icon = new ImageView("/images/catanIcon.png");
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            alert.getDialogPane().setGraphic( icon);

            // Showing player rankings
            alert.setTitle( "Catan");
            alert.setHeaderText("Player " + curPlayer.getName() + " has won!" + "\n\nPlayer scores:");
            Collections.sort( players, new SortByScore() );
            alert.setContentText("1-) " + players.get( 3).getName() + " - Score: " + players.get( 3).getScore()
                    +"\n2-) " + players.get( 2).getName() + " - Score: " + players.get( 2).getScore()
                    +"\n3-) " + players.get( 1).getName() + " - Score: " + players.get( 1).getScore()
                    +"\n4-) " + players.get( 0).getName() + " - Score: " + players.get( 0).getScore()
            );
            FlowManager flowManager = new FlowManager();
            if ( getLocalPlayer() == flowManager.getCurrentPlayer())
            {
                ServerHandler.getInstance().finishGame();
            }
            alert.showAndWait();
            try
            {
                new FlowManager().terminateData(); // throws null pointer exception? dont know why
                GameEngine.getInstance().setController(0); // may throw an error
            }
            catch ( Exception e) { System.out.println( e); }
        }
    }

    /**
     * Gets the harbor image path from the given HarborType enum.
     * @param type is the type of the Harbor defined in HarborType enum.
     * @return string image path of the corresponding harbor image.
     */
    private String getHarborImagePath( Harbor.HarborType type)
    {
        // Put the most likely case up
        if ( type == Harbor.HarborType.THREE_TO_ONE)
        {
            return "images/port3-1.png";
        }
        else if ( type == Harbor.HarborType.TWO_TO_ONE_LUMBER )
        {
            return "images/port2-1L.png";
        }
        else if ( type == Harbor.HarborType.TWO_TO_ONE_WOOL )
        {
            return "images/port2-1W.png";
        }
        else if ( type == Harbor.HarborType.TWO_TO_ONE_GRAIN )
        {
            return "images/port2-1G.png";
        }
        else if ( type == Harbor.HarborType.TWO_TO_ONE_BRICK )
        {
            return "images/port2-1B.png";
        }
        else
        {
            return "images/port2-1O.png";
        }
    }

    public void finishTheGameForDisconnection()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert( Alert.AlertType.INFORMATION);

                // Showing player rankings
                alert.setTitle( "Disconnected");
                alert.setHeaderText("Player has disconnected! Please return to main menu");
                alert.showAndWait();
            }
        });


        FlowManager flowManager = new FlowManager();
        if ( getLocalPlayer() == flowManager.getCurrentPlayer())
        {
            ServerHandler.getInstance().finishGame();
        }

        try
        {
            new FlowManager().terminateData(); // throws null pointer exception? dont know why
            GameEngine.getInstance().setController(0); // may throw an error
        }
        catch ( Exception e) { System.out.println( e); }
    }

    public MultiPlayerInfoController getInfoController() {
        return infoController;
    }

    public MultiStatusController getStatusController() {
        return statusController;
    }

    public MultiDevCardController getDevCardController() {
        return devCardController;
    }

    public MultiSelectionController getSelectionController() {
        return selectionController;
    }

    public MultiDiceController getDiceController() {
        return diceController;
    }

    public MultiHarborController getHarborController() {
        return harborController;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }
}
