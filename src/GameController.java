import animatefx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.w3c.dom.css.Rect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Game Controller class which is the C in MVC.
 * @author Cevat Aykan Sevinc
 * @version 09.11.2019
 * Added functions for game board to place structures
 * Added function for end turn button
 *
 * @version 11.11.2019
 * Added must checks to building structures
 *
 * @version 13.11.2019
 * Added must checks to trade and robber, implemented some handlings of development cards
 */
public class GameController extends Application {

    // Properties
    private AnchorPane gameBox;
    private Game game; // game added here for function access
    private Label statusText; // To inform user
    private FlowManager flowManager;

    // For function access of player selection and resource selection, I have put them in here
    private AnchorPane selectionBox;
    private Label selectionLabel;

    // For function access of information of all the players in the game.
    private ArrayList<AnchorPane> anchorPanes;
    private ArrayList<Label> labels;
    private ArrayList<ProgressIndicator> indicators;
    private ArrayList<Label> resources;
    private ArrayList<ImageView> longestRoads;
    private ArrayList<ImageView> largestArmies;

    HashMap<Point2D, Integer> settlementMap = new HashMap<>();

    // To keep hexagon index for a better display!
    private static int hexIndex = -1;
    private static int tileIndex = -1;
    private ImageView diceRollAvailable;
    private ImageView die1Result;
    private ImageView die2Result;
    private Rectangle cardPlayArea;
    private Label cardDragLabel;
    private AnchorPane cardBox;

    // For robber
    AtomicReference<Double> xRob = new AtomicReference<>((double) 0);
    AtomicReference<Double> yRob = new AtomicReference<>((double) 0);
    double distXRob = 0;
    double distYRob = 0;
    double initialRobX;
    double initialRobY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Get the Catan font from the fonts file and initialize it for the game.
            final Font font1 = Font.loadFont(new FileInputStream(new File("").getAbsolutePath()
                    .concat("/src/fonts/MinionPro-Bold.otf")), 40);
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e);
        }
        Parent root = FXMLLoader.load(getClass().getResource("/UI/Intro1.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("CATAN");
        initializePlayerSelection(root, primaryStage, new Scene(root, Color.BLACK));
        primaryStage.show();
    }

    private void initializeIntro1(Parent root, Stage primaryStage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Intro1.fxml"));
        Scene scene = new Scene(root, Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/UI/Intro1.css").toExternalForm());

        FadeIn animation = new FadeIn(root);
        animation.setSpeed(0.5);
        animation.play();
        FadeOut animation2 = new FadeOut(root);
        animation2.setDelay(new Duration(4000));
        animation2.setSpeed(0.5);
        animation2.play();
        Parent finalRoot = root;
        animation2.setOnFinished(event ->
        {
            try {
                initializeIntro2(finalRoot, primaryStage, scene);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });

        primaryStage.setScene(scene);
    }

    private void initializeIntro2(Parent root, Stage primaryStage, Scene scene) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Intro2.fxml"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Intro2.css").toExternalForm());
        scene.setRoot(root);

        Label label = (Label) scene.lookup("#test");
        label.setVisible(false);
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        Parent finalRoot1 = root;
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                label.setVisible(true);
                FadeIn animation = new FadeIn(finalRoot1);
                animation.setSpeed(0.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        FadeOut animation2 = new FadeOut(root);
        animation2.setDelay(new Duration(4000));
        animation2.setSpeed(0.5);
        animation2.play();
        Parent finalRoot = root;
        animation2.setOnFinished(event ->
        {
            try {
                initializeMainMenu(finalRoot, primaryStage, scene);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });
        primaryStage.setScene(scene);
    }

    private void initializeMainMenu(Parent root, Stage primaryStage, Scene scene) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/MainMenu.fxml"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/MainMenu.css").toExternalForm());
        scene.setRoot(root);

        root.setVisible(false);
        Parent finalRoot = root;
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(50);
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
                animation.setSpeed(0.5);
                animation.play();
            }
        });
        new Thread(sleeper).start();

        Button playButton = (Button) scene.lookup("#playButton");
        Button helpButton = (Button) scene.lookup("#helpButton");
        Button exitButton = (Button) scene.lookup("#exitButton");

        playButton.setOnMouseClicked(event -> {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    finalRoot.setVisible(false);
                    initializePlayerSelection(finalRoot, primaryStage, scene);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        helpButton.setOnMouseClicked(event -> {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    finalRoot.setVisible(false);
                    initializeHelp(finalRoot, primaryStage, scene);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        exitButton.setOnMouseClicked(event -> {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(2);
            animation2.setOnFinished(event1 ->
            {
                Platform.exit();
                System.exit(0);
            });
            animation2.play();
        });

        primaryStage.setScene(scene);
    }

    private void initializeHelp(Parent root, Stage primaryStage, Scene scene) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Help.fxml"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Help.css").toExternalForm());
        scene.setRoot(root);

        FadeIn animation = new FadeIn(root);
        animation.setSpeed(3.5);
        animation.play();

        ImageView goBack = (ImageView) scene.lookup("#goBack");
        Parent finalRoot = root;
        goBack.setOnMouseClicked(event ->
        {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    finalRoot.setVisible(false);
                    initializeMainMenu(finalRoot, primaryStage, scene);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setScene(scene);
    }

    private void initializePlayerSelection(Parent root, Stage primaryStage, Scene scene) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/PlayerSelection.fxml"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/PlayerSelection.css").toExternalForm());
        scene.setRoot(root);

        FadeIn animation = new FadeIn(root);
        animation.setSpeed(2);
        animation.play();

        ImageView goBack = (ImageView) scene.lookup("#goBack");
        Parent finalRoot = root;
        goBack.setOnMouseClicked(event ->
        {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(3.5);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    finalRoot.setVisible(false);
                    initializeMainMenu(finalRoot, primaryStage, scene);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        TextField player1Name = (TextField) scene.lookup("#player1Name");
        TextField player2Name = (TextField) scene.lookup("#player2Name");
        TextField player3Name = (TextField) scene.lookup("#player3Name");
        TextField player4Name = (TextField) scene.lookup("#player4Name");
        Button startButton = (Button) scene.lookup("#startButton");
        startButton.setOnMouseClicked(event ->
        {
            FadeOut animation2 = new FadeOut(finalRoot);
            animation2.setSpeed(2);
            animation2.setOnFinished(event1 ->
            {
                try
                {
                    ArrayList<Player> players = new ArrayList<>();
                    players.add(new Player(player1Name.getText(), Color.BLUE));
                    players.add(new Player(player2Name.getText(), Color.WHITE));
                    players.add(new Player(player3Name.getText(), Color.ORANGE));
                    players.add(new Player(player4Name.getText(), Color.BROWN));
                    initializeGame(finalRoot, primaryStage, players, scene);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        primaryStage.setScene(scene);
    }

    private void initializeGame(Parent root, Stage primaryStage, ArrayList<Player> players, Scene scene) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Game.fxml"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/UI/Game.css").toExternalForm());
        scene.setRoot(root);

        Parent finalRoot = root;
        finalRoot.setVisible(false);
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(50);
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
                animation.play();
            }
        });
        new Thread(sleeper).start();

        game = new Game(players);
        flowManager = FlowManager.getInstance();
        gameBox = (AnchorPane) scene.lookup("#gameBox");
        ImageView robber = new ImageView("/images/robber.png");
        setupGameBoard(robber);

        //**************************************************************************************************************
        // Configure game Board functions

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
                informError( flowManager.checkMust() );
            }
        });

        statusText = (Label) scene.lookup("#statusText");

        //**************************************************************************************************************
        //*********************************************************************
        //Initialize player boxes
        AnchorPane currentPlayerBox = (AnchorPane) scene.lookup("#currentPlayerInf");
        AnchorPane otherPlayer1Box = (AnchorPane) scene.lookup("#otherPlayer1Box");
        AnchorPane otherPlayer2Box = (AnchorPane) scene.lookup("#otherPlayer2Box");
        AnchorPane otherPlayer3Box = (AnchorPane) scene.lookup("#otherPlayer3Box");
        anchorPanes = new ArrayList<>();
        anchorPanes.add(currentPlayerBox);
        anchorPanes.add(otherPlayer1Box);
        anchorPanes.add(otherPlayer2Box);
        anchorPanes.add(otherPlayer3Box);

        Label currentPlayer = (Label) scene.lookup("#currentPlayer");
        Label otherPlayer1 = (Label) scene.lookup("#otherPlayer1");
        Label otherPlayer2 = (Label) scene.lookup("#otherPlayer2");
        Label otherPlayer3 = (Label) scene.lookup("#otherPlayer3");
        labels = new ArrayList<>();
        labels.add(currentPlayer);
        labels.add(otherPlayer1);
        labels.add(otherPlayer2);
        labels.add(otherPlayer3);

        ProgressIndicator currentPlayerIndicator = (ProgressIndicator) scene.lookup("#currentPlayerProgress");
        ProgressIndicator otherPlayer1Progress = (ProgressIndicator) scene.lookup("#otherPlayer1Progress");
        ProgressIndicator otherPlayer2Progress = (ProgressIndicator) scene.lookup("#otherPlayer2Progress");
        ProgressIndicator otherPlayer3Progress = (ProgressIndicator) scene.lookup("#otherPlayer3Progress");
        indicators = new ArrayList<>();
        indicators.add(currentPlayerIndicator);
        indicators.add(otherPlayer1Progress);
        indicators.add(otherPlayer2Progress);
        indicators.add(otherPlayer3Progress);

        Label lumberCount = (Label) scene.lookup("#lumberCount");
        Label woolCount = (Label) scene.lookup("#woolCount");
        Label grainCount = (Label) scene.lookup("#grainCount");
        Label brickCount = (Label) scene.lookup("#brickCount");
        Label oreCount = (Label) scene.lookup("#oreCount");
        resources = new ArrayList<>();
        resources.add(lumberCount);
        resources.add(woolCount);
        resources.add(grainCount);
        resources.add(brickCount);
        resources.add(oreCount);

        ImageView currentLR = (ImageView) scene.lookup("#currentLR");
        ImageView other1LR = (ImageView) scene.lookup("#other1LR");
        ImageView other2LR = (ImageView) scene.lookup("#other2LR");
        ImageView other3LR = (ImageView) scene.lookup("#other3LR");
        longestRoads = new ArrayList<>();
        longestRoads.add(currentLR);
        longestRoads.add(other1LR);
        longestRoads.add(other2LR);
        longestRoads.add(other3LR);

        ImageView currentLA = (ImageView) scene.lookup("#currentLA");
        ImageView other1LA = (ImageView) scene.lookup("#other1LA");
        ImageView other2LA = (ImageView) scene.lookup("#other2LA");
        ImageView other3LA = (ImageView) scene.lookup("#other3LA");
        largestArmies = new ArrayList<>();
        largestArmies.add(currentLA);
        largestArmies.add(other1LA);
        largestArmies.add(other2LA);
        largestArmies.add(other3LA);

        setupPlayerBoxes();
        setupRobber(robber);
        //**********************************************************************

        // Development Cards
        cardPlayArea = (Rectangle) scene.lookup("#cardPlayArea");
        cardDragLabel = (Label) scene.lookup("#cardDragLabel");
        cardBox = (AnchorPane) scene.lookup("#cardBox");
        setupDevelopmentCards(cardPlayArea, cardDragLabel, cardBox);
        //----------------------------------------------

        // Dice Roll
        diceRollAvailable = (ImageView) scene.lookup("#diceRollAvailable");
        die1Result = (ImageView) scene.lookup("#die1Result");
        die2Result = (ImageView) scene.lookup("#die2Result");

        setupDiceRoll(diceRollAvailable, die1Result, die2Result);
        //-----------------------------------------------


        // Selection
        selectionBox = (AnchorPane) scene.lookup("#selectionBox");
        selectionLabel = (Label) scene.lookup("#selectionLabel");
        //-----------------------------------------------

        // Trade
        Button otherPlayer1Trade = (Button) scene.lookup("#otherPlayer1Trade");
        Button otherPlayer2Trade = (Button) scene.lookup("#otherPlayer2Trade");
        Button otherPlayer3Trade = (Button) scene.lookup("#otherPlayer3Trade");

        otherPlayer1Trade.setOnMouseClicked(event ->
        {
            setupTrade(game.getPlayer((game.getCurrentPlayerIndex() + 1) % 4), resources);
        });

        otherPlayer2Trade.setOnMouseClicked(event ->
        {
            setupTrade(game.getPlayer((game.getCurrentPlayerIndex() + 2) % 4), resources);
        });

        otherPlayer3Trade.setOnMouseClicked(event ->
        {
            setupTrade(game.getPlayer((game.getCurrentPlayerIndex() + 3) % 4), resources);
        });
        //-----------------------------------------------

        Button buyDevelopmentCard = (Button) scene.lookup("#buyDevelopmentCard");
        buyDevelopmentCard.setOnMouseClicked(event ->
        {
            if ( flowManager.checkMust() == -1) {
                game.addDevelopmentCard();
                setupDevelopmentCards(cardPlayArea, cardDragLabel, cardBox);
            }
            else
            {
                informError(flowManager.checkMust());
            }
        });

        Button endTurnButton = (Button) scene.lookup( "#endTurn");
        endTurnButton.setOnMouseReleased(mouseEvent ->
        {
            performEndTurnButtonEvent();

        });
        primaryStage.setScene(scene);
    }

    /**
     * Sets up the game board, its hexagons, numbers and their positions
     */
    private void setupGameBoard(ImageView robber) {
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
                        robber.setX((j-2) * 30 + 90);
                        robber.setY(i * 30 + 45);
                        gameBox.getChildren().add( robber);
                    }
                    hexagon = new ImageView(new Image(imgPath));
                    hexagon.setX((j - 2) * 30 + 30);
                    hexagon.setY(i * 30 + 15);
                    gameBox.getChildren().add(hexagon);
                    robber.toFront();
                }
            }
        }
    }

    private void setupPlayerBoxes()
    {
        FadeOut animation0 = new FadeOut(anchorPanes.get(0));
        FadeOut animation1 = new FadeOut(anchorPanes.get(1));
        FadeOut animation2 = new FadeOut(anchorPanes.get(2));
        FadeOut animation3 = new FadeOut(anchorPanes.get(3));
        animation0.setSpeed(3);
        animation1.setSpeed(3);
        animation2.setSpeed(3);
        animation3.setSpeed(3);
        animation0.play();
        animation1.play();
        animation2.play();
        animation3.play();
        animation0.setOnFinished(event ->
        {
            FadeIn animation0In = new FadeIn(anchorPanes.get(0));
            anchorPanes.get(0).getStyleClass().clear();
            anchorPanes.get(0).getStyleClass().add(game.getCurrentPlayer().getColor().toString().substring(1) + "PlayerBox");
            setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
            labels.get(0).setText(game.getCurrentPlayer().getName());
            animation0In.setSpeed(3);
            animation0In.play();
        });
        animation1.setOnFinished(event ->
        {
            FadeIn animation1In = new FadeIn(anchorPanes.get(1));
            anchorPanes.get(1).getStyleClass().clear();
            anchorPanes.get(1).getStyleClass().add(game.getPlayer((game.getCurrentPlayerIndex() + 1) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            labels.get(1).setText(game.getPlayer((game.getCurrentPlayerIndex() + 1) % 4).getName());
            indicators.get(1).setProgress(game.getPlayer((game.getCurrentPlayerIndex() + 1) % 4).getScore() * 1.0 / 10);
            animation1In.setSpeed(3);
            animation1In.play();
        });
        animation2.setOnFinished(event ->
        {
            FadeIn animation2In = new FadeIn(anchorPanes.get(2));
            anchorPanes.get(2).getStyleClass().clear();
            anchorPanes.get(2).getStyleClass().add(game.getPlayer((game.getCurrentPlayerIndex() + 2) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            labels.get(2).setText(game.getPlayer((game.getCurrentPlayerIndex() + 2) % 4).getName());
            indicators.get(2).setProgress(game.getPlayer((game.getCurrentPlayerIndex() + 2) % 4).getScore() * 1.0 / 10);
            animation2In.setSpeed(3);
            animation2In.play();
        });
        animation3.setOnFinished(event ->
        {
            FadeIn animation3In = new FadeIn(anchorPanes.get(3));
            anchorPanes.get(3).getStyleClass().clear();
            anchorPanes.get(3).getStyleClass().add(game.getPlayer((game.getCurrentPlayerIndex() + 3) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            labels.get(3).setText(game.getPlayer((game.getCurrentPlayerIndex() + 3) % 4).getName());
            indicators.get(3).setProgress(game.getPlayer((game.getCurrentPlayerIndex() + 3) % 4).getScore() * 1.0/ 10);
            animation3In.setSpeed(3);
            animation3In.play();
        });
    }

    private void setupCurrentPlayerInfo(AnchorPane infoBox, ProgressIndicator score, ArrayList<Label> resources)
    {
        FadeOut infoOut = new FadeOut(infoBox);
        infoOut.setSpeed(3);
        infoOut.setOnFinished(event ->
        {
            score.setProgress(game.getCurrentPlayer().getScore() * 1.0 / 10);
            int playerResources[] = game.getCurrentPlayer().getResources();

            for ( int i = 0; i < resources.size(); i++)
            {
                resources.get(i).setText("" + playerResources[i]);
            }
            FadeIn infoIn = new FadeIn(infoBox);
            infoIn.setSpeed(3);
            infoIn.play();
        });
        infoOut.play();
    }

    /**
     * Sets up the development card area and their playable area.
     * @param cardPlayArea is the only area that accepts a development card to be played. If card is dropped here,
     *                     it will be played and then it will be removed.
     * @param cardDragLabel is the information given to the user so that he/she can know where to play.
     * @param cardBox is the layout that contains the development cards.
     */
    private void setupDevelopmentCards(Rectangle cardPlayArea, Label cardDragLabel, AnchorPane cardBox)
    {
        FadeOut cardBoxOut = new FadeOut(cardBox);
        cardBoxOut.setSpeed(3);
        cardBoxOut.setOnFinished(event ->
        {
            cardBox.getChildren().clear();
            ArrayList<Card> cards = game.getCurrentPlayer().getCards();
            ArrayList<ImageView> cardsInUI = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                ImageView temp = new ImageView("/images/" + cards.get(i).getName() + ".png");
                if (i == 0) {
                    temp.setX(50);
                } else {
                    temp.setX(cardsInUI.get(i - 1).getX() + 40);
                }

                temp.setY(10);
                AtomicReference<Double> x = new AtomicReference<>((double) 0);
                AtomicReference<Double> y = new AtomicReference<>((double) 0);
                temp.setOnMousePressed(e ->
                {
                    cardPlayArea.setVisible(true);
                    cardDragLabel.setVisible(true);
                    new FadeIn(cardPlayArea).play();
                    new FadeIn(cardDragLabel).play();
                    x.set(e.getX());
                    y.set(e.getY());
                });
                temp.setOnMouseDragged(e ->
                {
                    temp.setTranslateX(temp.getTranslateX() + (e.getX() - x.get()));
                    temp.setTranslateY(temp.getTranslateY() + (e.getY() - y.get()));
                });
                int finalI = i;
                temp.setOnMouseReleased(e ->
                {
                    FadeOut animation = new FadeOut(cardPlayArea);
                    FadeOut animation2 = new FadeOut(cardDragLabel);
                    animation.setOnFinished(event1 ->
                    {
                        cardPlayArea.setVisible(false);
                    });
                    animation2.setOnFinished(event1 ->
                    {
                        cardDragLabel.setVisible(false);
                    });
                    animation.play();
                    animation2.play();
                    Bounds rectanglePosition = temp.localToScene(temp.getBoundsInLocal());
                    Bounds playAreaPosition = cardPlayArea.localToScene(cardPlayArea.getBoundsInLocal());
                 /*   if (playAreaPosition.contains( rectanglePosition.getCenterX(), rectanglePosition.getCenterY() ) ||
                            playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY()) ||
                            playAreaPosition.contains(rectanglePosition.getCenterX(), rectanglePosition.getCenterY() + rectanglePosition.getHeight()) ||
                            playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY() + rectanglePosition.getHeight())) {
                        game.playDevelopmentCard(cards.get(finalI));
                        setupLargestArmy();
                        cardBox.getChildren().remove(temp);
                    } else {
                        temp.setTranslateX(0);
                        temp.setTranslateY(0);
                    } */
                });
                cardsInUI.add(temp);
            }

            for (ImageView card : cardsInUI) {
                cardBox.getChildren().add(card);
            }

            FadeIn cardBoxIn = new FadeIn(cardBox);
            cardBoxIn.setSpeed(3);
            cardBoxIn.play();
        });
        cardBoxOut.play();
    }

    /**
     * Sets the dice roll available gif and dice result images, it also implements the ui logic of a dice roll by
     * communicating with the game model.
     * @param diceRollAvailable is gif that shows user that dice is avaible for roll and if the user clicks it dice will
     *                          be rolled.
     * @param die1Result is the first die result.
     * @param die2Result is the second die result.
     */
    private void setupDiceRoll(ImageView diceRollAvailable, ImageView die1Result, ImageView die2Result)
    {
        FadeOut die1Out = new FadeOut(die1Result);
        FadeOut die2Out = new FadeOut(die2Result);
        die1Out.setSpeed(2);
        die2Out.setSpeed(2);
        die1Out.setOnFinished(event ->
        {
            die1Result.setVisible(false);
            die2Result.setVisible(false);
            diceRollAvailable.setVisible(true);
            FadeIn rollAvailableIn = new FadeIn(diceRollAvailable);
            rollAvailableIn.setSpeed(2);
            rollAvailableIn.play();
        });
        die1Out.play();
        die2Out.play();
        diceRollAvailable.setOnMouseClicked(event ->
        {
            // Dice could only be rolled at the beginning of a turn
            if ( flowManager.checkMust() == 7 )
            {
                FadeOut animation = new FadeOut(diceRollAvailable);
                animation.setSpeed(2);
                animation.setOnFinished(event1 ->
                {
                    Task<Void> sleeper = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                            }
                            return null;
                        }
                    };
                    sleeper.setOnSucceeded(event2 -> {
                        diceRollAvailable.setVisible(false);
                        FadeIn die1Anim = new FadeIn(die1Result);
                        FadeIn die2Anim = new FadeIn(die2Result);
                        die1Anim.play();
                        die2Anim.play();
                        die1Result.setVisible(true);
                        die2Result.setVisible(true);
                        setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
                    });
                    new Thread(sleeper).start();
                });
                animation.play();

                //***** Logic to roll the dice and collect resources, collecting resources could be made in the dice method of game class! *****
                flowManager.doneMust();
                ArrayList<Integer> results = game.rollDice();
                //game.collectResources();

                die1Result.setImage(new Image("/images/die" + results.get(0) + ".png"));
                die2Result.setImage(new Image("/images/die" + results.get(1) + ".png"));
            }
            else
            {
                informError( flowManager.checkMust() );
            }
        });
    }

    /**
     * Sets the UI display of the robber while asserting certain functions to it when it moves
     * @param robber is the ImageView representation of the robber for displaying it on the UI
     */
    private void setupRobber(ImageView robber)
    {

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
                informError( flowManager.checkMust() );
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
                informError( flowManager.checkMust() );
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
                    informError( resultCode);
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
                        askForPlayerCevatImplementation(neighbors);
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

    private void setupLongestRoad()
    {
        for ( int i = 0; i < 4; i++)
        {
            if ( game.getPlayer((game.getCurrentPlayerIndex() + i) % 4) == game.getLongestRoadPlayer())
            {
                longestRoads.get(i).setVisible(true);
                FadeIn laIn = new FadeIn(longestRoads.get(i));
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
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
        setupPlayerBoxes();
    }

    private void setupLargestArmy()
    {
        for ( int i = 0; i < 4; i++)
        {
            if ( game.getPlayer((game.getCurrentPlayerIndex() + i) % 4) == game.getLargestArmyPlayer())
            {
                largestArmies.get(i).setVisible(true);
                FadeIn laIn = new FadeIn(largestArmies.get(i));
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
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
        setupPlayerBoxes();
    }

    //******************************************************************************************************************
    //
    // FUNCTIONS RELATED TO GAME BOARD
    //
    //******************************************************************************************************************

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
            informError( resultCode);
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
     * Inform error function updates the status bar to display user error, this function could be evolved into pop up
     * and a sound may be played regarding the error.
     * @param resultCode is the error code given by the game controller
     *
     *        GameBoard related:
     *        -1 = there is no connection for road to build
     *        -2 = there is no connection for city to build
     *        -3 = there is a building near
     *        -4 = this tile is occupied by a road, city or other players structure, in this case there is no need to explain anything
     *        -5 = there is no enough resource for road
     *        -6 = there is no enough resource for settlement
     *        -7 = there is no enough resource for city
     *
     *        Must related:
     *         0 = road need to be built
     *         1 = settlement need to be built
     *         2 = city need to be built
     *         3 = inside tile selection ( for robber selection )
     *         4 = resource selection (for monopoly card)
     *         5 = resource selection (for year of plenty card)
     *         6 = end turn ( we will end the turn automatically, do not wait player to end )
     *         7 = roll dice
     *         8 = get neighbor players ( after robber is places )
     */
    private void informError( int resultCode )
    {
        FadeOutRight animation = new FadeOutRight(statusText);
        animation.setSpeed(3);
        animation.setOnFinished(event ->
        {
            if ( resultCode == -1 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", there is no connection for a road to build");
            }
            else if ( resultCode == -2 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", there is no connection for a settlement to build");
            }
            else if ( resultCode == -3 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", there is another building near");
            }
            else if ( resultCode == -4 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", this spot is occupied by a player");
            }
            else if ( resultCode == -5 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", not enough resources for a road");
            }
            else if ( resultCode == -6 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", not enough resources for a settlement");
            }
            else if ( resultCode == -7 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", not enough resources for a city");
            }
            else if ( resultCode == -8 )
            {
                statusText.setText( "Other player does not have enough resources");
            }
            else if ( resultCode == -9)
            {
                statusText.setText( "-");
            }
            else if ( resultCode == 0 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", build road first!");
            }
            else if ( resultCode == 1 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", build settlement first!");
            }
            else if ( resultCode == 2 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", build city first!");
            }
            else if ( resultCode == 3 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", move the robber first by clicking and dragging!");
            }
            else if ( resultCode == 4 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", select a resource for monopoly card!");
            }
            else if ( resultCode == 5 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", select two resources for year of plenty card!");
            }
            else if ( resultCode == 6 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", END YOUR TURN RIGHT NOW!!!!!");
            }
            else if ( resultCode == 7 )
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", but first, lets roll the dice!");
            }
            else if ( resultCode == 8)
            {
                statusText.setText( game.getCurrentPlayer().getName() + ", choose a neighbor player first to steal a resource!");
            }
            else
            {
                statusText.setText( "Function could not detect the error, lol, exploded!");
            }
            FadeInLeft animation2 = new FadeInLeft(statusText);
            animation2.setSpeed(3);
            animation2.play();
        });
        animation.play();
    }

    /**
     * Creates the dialog corresponding to the user action on the game board.
     * @param alert is the dialog to display the user event
     * @param resultCode is the positive result gotten from the controller
     * @param x is the x coordinate of to perform action on the game board
     * @param y is the y coordinate of to perform action on the game board
     */
    private void informBoardSelection( Alert alert, int resultCode, int x, int y )
    {
        int mustCheckCode = flowManager.checkMust();

        // Player tries to build a road
        if ( resultCode == 0 )
        {
            // Allow construction only if player is obliged or free to hang arooound lol
            if ( mustCheckCode == -1 || mustCheckCode == 0)
            {
                buildRoad(alert, x, y);
            }
            else
            {
                informError( mustCheckCode );
            }
        }
        // Player tries to build a settlement
        else if ( resultCode == 1)
        {
            // Allow construction only if player is obliged or free to hang arooound lol
            if ( mustCheckCode == -1 || mustCheckCode == 1)
            {
                buildSettlement(alert, x, y);
            }
            else
            {
                informError( mustCheckCode );
            }
        }
        // Player tries to build a city
        else if ( resultCode == 2)
        {
            // Allow construction only if player is obliged or free to hang arooound lol
            if ( mustCheckCode == -1 || mustCheckCode == 2)
            {
                buildCity( alert, x, y);
            }
            else
            {
                informError( mustCheckCode);
            }
        }
    }

    /**
     * Prompts to ask if the user really wants to build a settlement.
     * @param alert is the dialog prompting confirmation of building the settlement
     * @param x is the x coordinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void buildSettlement( Alert alert, int x, int y)
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

            setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
            setupLongestRoad();
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

            setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
            setupLongestRoad();
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

            setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
        }
    }

    //******************************************************************************************************************
    //
    // FUNCTIONS RELATED TO DEVELOPMENT CARDS
    //
    //******************************************************************************************************************

    private void askForResource(AnchorPane selectionBox, Label selectionLabel)
    {
        statusText.setText("Choose a resource to select");
        selectionLabel.setText("Choose Your Resource");
        ArrayList<ImageView> resources = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            switch (i) {
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
            resources.get(i).getStyleClass().add("resourceBox");
            selectionBox.getChildren().add(resources.get(i));
        }
        new FadeInLeft(selectionBox).play();
        selectionBox.setVisible(true);
    }

    private void askForPlayer(AnchorPane selectionBox, Label selectionLabel)
    {
        statusText.setText("Choose a player to steal from");
        selectionLabel.setText("Choose Your Player");
        ArrayList<Rectangle> players = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    Rectangle otherPlayer1 = new Rectangle(150, 100, 200, 400);
                    otherPlayer1.setFill(game.getPlayer((game.getCurrentPlayerIndex() + 1) % 4).getColor());
                    otherPlayer1.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    players.add(otherPlayer1);
                    break;
                case 1:
                    Rectangle otherPlayer2 = new Rectangle(450, 100, 200, 400);
                    otherPlayer2.setFill(game.getPlayer((game.getCurrentPlayerIndex() + 2) % 4).getColor());
                    otherPlayer2.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    players.add(otherPlayer2);
                    break;
                case 2:
                    Rectangle otherPlayer3 = new Rectangle(750, 100, 200, 400);
                    otherPlayer3.setFill(game.getPlayer((game.getCurrentPlayerIndex() + 3) % 4).getColor());
                    otherPlayer3.setOnMousePressed(e -> {
                        new FadeOutRight(selectionBox).play();
                        selectionBox.setVisible(false);
                    });
                    players.add(otherPlayer3);
            }
            players.get(i).getStyleClass().add("resourceBox");
            selectionBox.getChildren().add(players.get(i));
        }
        new FadeInLeft(selectionBox).play();
        selectionBox.setVisible(true);
    }

    /**
     * This function displays player boxes for the current player to choose a player to steal a random resource from them.
     * @param playersToSelect is the players arraylist to display available players.
     */
    private void askForPlayerCevatImplementation( ArrayList<Player> playersToSelect)
    {
        statusText.setText( "Choose a player to steal from");
        selectionLabel.setText( "Choose Your Player");
        ArrayList<Rectangle> players = new ArrayList<>();
        for ( int i = 0; i < playersToSelect.size(); i++ )
        {
            // According to the index of the array list, configure the player information
            Rectangle otherPlayer = new Rectangle(i * 300 + 150, 100, 200, 400);
            otherPlayer.setFill( playersToSelect.get( i).getColor() );
            int finalI = i;
            otherPlayer.setOnMousePressed(e -> {

                if ( flowManager.checkMust() == 8 )
                {
                    flowManager.doneMust();
                }
                stealResourceFromPlayer( playersToSelect.get( finalI) );
                setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
            });
            players.add(otherPlayer);

            players.get(i).getStyleClass().add("resourceBox");
            selectionBox.getChildren().add(players.get(i));
        }
        new FadeInLeft(selectionBox).play();
        selectionBox.setVisible(true);
    }

    /**
     * Helper function of the askForPlayerCevatImplementation, to avoid code duplication. This function allows the
     * current player to steal a resource and close the resource selection event, while doing the must.
     * @
     */
    private void stealResourceFromPlayer( Player stealingFrom)
    {
        // Resource stealing for the selected player must
        if ( flowManager.checkMust() == 8 )
        {
            flowManager.doneMust();

            game.getCurrentPlayer().stealResourceFromPlayer( stealingFrom );
        }
        new FadeOutRight( selectionBox).play();
        selectionBox.setVisible(false);
    }


    //******************************************************************************************************************
    //
    // FUNCTIONS RELATED TO TRADE BUTTONS
    //
    //******************************************************************************************************************
    /**
     * Function to trade with a player when a player offers a trade to another player by clicking the trade button.
     * @param playerToTrade the player who current player wants to trade with
     * @param resources resources is the resource labels of the current player which will be updated after trade.
     */
    private void setupTrade(Player playerToTrade, ArrayList<Label> resources)
    {
        // Tradings can only be done when free of obligations
        if ( flowManager.checkMust() == -1)
        {
            try {
                // Initialize the trade popup, its a new stage.
                Stage tradeStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("/UI/TradePopup.fxml"));
                Scene scene = new Scene(root, Color.BLACK);
                scene.getStylesheets().add(getClass().getResource("/UI/TradePopup.css").toExternalForm());

                // Get the current player of the game and set the offeror and offeree labels to related players.
                Player currentPlayer = game.getCurrentPlayer();
                Label offeror = (Label) scene.lookup("#offeror");
                offeror.setText(currentPlayer.getName());
                Label offeree = (Label) scene.lookup("#offeree");
                offeree.setText(playerToTrade.getName());

                // Initialize spinners for each offering resource type and add it to root.
                ArrayList<Spinner<Integer>> offerings = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    Spinner<Integer> offer = new Spinner<>(0, currentPlayer.getResources()[0], 0);
                    offer.setPrefWidth(50);
                    offer.setPrefHeight(30);
                    offer.setTranslateX(i * 60 + 20);
                    offer.setTranslateY(130);
                    offerings.add(offer);
                    ((AnchorPane) root).getChildren().add(offer);
                }

                // Initialize spinners for each wanted resource type and add it to root.
                ArrayList<Spinner<Integer>> wanteds = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    Spinner<Integer> wanted = new Spinner<>(0, 7, 0);
                    wanted.setPrefWidth(50);
                    wanted.setPrefHeight(30);
                    wanted.setTranslateX(i * 60 + 20);
                    wanted.setTranslateY(240);
                    wanteds.add(wanted);
                    ((AnchorPane) root).getChildren().add(wanted);
                }

                // Initialize the trade button. It creates a confirmation popup.
                Button tradeButton = (Button) scene.lookup("#tradeButton");
                tradeButton.setOnMouseClicked(event ->
                {
                    // Initialize resource arrays for offerings and wanteds.
                    int[] offeringResources = new int[5];
                    int[] wantedResources = new int[5];

                    // Get each offered resource from their spinners.
                    for ( int i = 0; i < offerings.size(); i++)
                    {
                        offeringResources[i] = offerings.get(i).getValue();
                    }

                    // Get each wanted resource from their spinners.
                    for ( int i = 0; i < offerings.size(); i++)
                    {
                        wantedResources[i] = wanteds.get(i).getValue();
                    }

                    // Create a confirmation popup where you ask the offeree if they accept the trade.
                    Alert alert = new Alert( Alert.AlertType.CONFIRMATION);
                    alert.initStyle( StageStyle.UTILITY);

                    ImageView icon = new ImageView("/images/catanIcon.png");
                    icon.setFitHeight(48);
                    icon.setFitWidth(48);
                    alert.getDialogPane().setGraphic( icon);

                    alert.setHeaderText("Trade Confirmation");
                    alert.setContentText(playerToTrade.getName() + ", do you confirm the trade?");

                    // If accepted, check if the offeree has enough resources, if not make new status, if do make the trade.
                    Optional<ButtonType> result = alert.showAndWait();
                    if ( result.get() == ButtonType.OK){
                        if ( !game.tradeWithPlayer(currentPlayer, playerToTrade, wantedResources, offeringResources))
                        {
                            informError(-8);
                        }
                        setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
                        tradeStage.close();
                    }
                });

                // Initialize cancel button. It closes the trade popup.
                Button cancel = (Button) scene.lookup("#cancel");
                cancel.setOnMouseClicked(event ->
                {
                    tradeStage.close();
                });

                // Show the trade popup in the game.
                tradeStage.initStyle(StageStyle.UTILITY);
                tradeStage.setTitle("Player Trade");
                tradeStage.setResizable(false);
                tradeStage.setScene(scene);
                tradeStage.show();
            } catch (Exception e) {
                System.out.println(e);
            }
       }
       else
       {
           informError( flowManager.checkMust() );
       }
    }

    //******************************************************************************************************************
    //
    // FUNCTIONS RELATED TO END TURN BUTTON
    //
    //******************************************************************************************************************

    /**
     * End the turn when the end turn button is pressed or player builds a road in the initial phase.
     */
    private void performEndTurnButtonEvent()
    {
        // Check if the user has to do something before ending their turn
        if ( flowManager.checkMust() == -1 || flowManager.checkMust() == 6 )
        {
            // Check if the user ends the turn because of obligation
            if ( flowManager.checkMust() == 6 )
            {
                // Done the must, yeey :)
                flowManager.doneMust();
            }
            game.endTurn();
            setupPlayerBoxes();
            setupLargestArmy();
            setupLongestRoad();
            setupDiceRoll( diceRollAvailable, die1Result, die2Result);
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
                    informError(-9);
                    setupCurrentPlayerInfo(anchorPanes.get(0), indicators.get(0), resources);
                    setupDevelopmentCards( cardPlayArea, cardDragLabel, cardBox);
                }
            });
            new Thread(sleeper2).start();
        }
        else
        {
            informError( flowManager.checkMust() );
        }
    }
}
