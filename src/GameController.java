import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Game Controller class which is the C in MVC.
 * @author Cevat Aykan Sevinc
 * @version 09.11.2019
 * Added functions for game board to place structures
 * Added function for end turn button
 */
public class GameController extends Application {

    // Properties
    private Game game; // game added here for function access
    private Label statusBar; // this and the below three added for pop up dislyaing for info
    private Popup myPopUp;
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            final Font font1 = Font.loadFont(new FileInputStream(new File("C:\\Users\\USER\\Desktop\\Project_Catan\\CS319-3C-CA\\src\\fonts\\MinionPro-Bold.otf")), 40);
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e);
        }

        Parent root = FXMLLoader.load(getClass().getResource("/UI/Test1.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("CATAN");
        initializeGame(root, primaryStage);
        primaryStage.show();

        stage = primaryStage; // I added here to display pop ups for info
    }

    private void initializeIntro1(Parent root, Stage primaryStage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Test1.fxml"));
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
                initializeIntro2(finalRoot, primaryStage);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });

        primaryStage.setScene(scene);
    }

    private void initializeIntro2(Parent root, Stage primaryStage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Test2.fxml"));
        Scene scene = new Scene(root, Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/UI/Intro2.css").toExternalForm());
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
                initializeMainMenu(finalRoot, primaryStage);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });

        primaryStage.setScene(scene);
    }

    private void initializeMainMenu(Parent root, Stage primaryStage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/MainMenu.fxml"));
        Scene scene = new Scene(root, Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/UI/MainMenu.css").toExternalForm());
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
                    initializePlayerSelection(finalRoot, primaryStage);
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
                    initializeHelp(finalRoot, primaryStage);
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

    private void initializeHelp(Parent root, Stage primaryStage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Help.fxml"));
        Scene scene = new Scene(root, Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/UI/Help.css").toExternalForm());

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
                    initializeMainMenu(finalRoot, primaryStage);
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

    private void initializePlayerSelection(Parent root, Stage primaryStage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/PlayerSelection.fxml"));
        Scene scene = new Scene(root, Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/UI/PlayerSelection.css").toExternalForm());

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
                    initializeMainMenu(finalRoot, primaryStage);
                }
                catch (IOException e)
                {
                    System.out.println(e);
                }
            });
            animation2.play();
        });

        Button startButton = (Button) scene.lookup("#startButton");
        startButton.setOnMouseClicked(event ->
        {
            try {
                initializeGame(finalRoot, primaryStage);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        });

        primaryStage.setScene(scene);
    }

    private void initializeGame(Parent root, Stage primaryStage) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("/UI/Game.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/UI/Game.css").toExternalForm());

        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Talha", Color.BLUE));
        players.add(new Player("Talha", Color.ORANGE));
        players.add(new Player("Talha", Color.WHITE));
        players.add(new Player("Talha", Color.BROWN));
        this.game = new Game(players);
        AnchorPane gameBox = (AnchorPane) scene.lookup("#gameBox");
        setupGameBoard(game, gameBox);

        //**************************************************************************************************************
        // Configure game Board functions

        gameBox.setOnMouseClicked( new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                int x = processX( mouseEvent.getX() );
                int y = processY( mouseEvent.getY() );

                System.out.print( "X is: " + mouseEvent.getX() + " | Y is: " + mouseEvent.getY());
                System.out.println( " X' is: " + x + " | Y' is: " + y );

                createDialog( game.checkTile( x, y ), x, y );
            }
        } );

        this.statusBar = new Label( "Hello World!");
        this.myPopUp = new Popup();
        this.myPopUp.getContent().add( this.statusBar);

        Button endTurnButton = (Button) scene.lookup( "#endTurn");
        endTurnButton.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {

                        System.out.println( "You wanna finish your turn huh :)");
                        game.endTurn();
                    }
                }
        );

        //**************************************************************************************************************

        Rectangle cardPlayArea = (Rectangle) scene.lookup("#cardPlayArea");
        Label cardDragLabel = (Label) scene.lookup("#cardDragLabel");
        AnchorPane cardBox = (AnchorPane) scene.lookup("#cardBox");
        setupDevelopmentCards(cardPlayArea, cardDragLabel, cardBox);

        ProgressIndicator currentPlayerIndicator = (ProgressIndicator) scene.lookup("#currentPlayerProgress");
        ProgressIndicator otherPlayer1Progress = (ProgressIndicator) scene.lookup("#otherPlayer1Progress");
        ProgressIndicator otherPlayer2Progress = (ProgressIndicator) scene.lookup("#otherPlayer2Progress");
        ProgressIndicator otherPlayer3Progress = (ProgressIndicator) scene.lookup("#otherPlayer3Progress");

        ImageView diceRollAvailable = (ImageView) scene.lookup("#diceRollAvailable");
        ImageView die1Result = (ImageView) scene.lookup("#die1Result");
        ImageView die2Result = (ImageView) scene.lookup("#die2Result");

        setupDiceRoll(diceRollAvailable, die1Result, die2Result, game);

        primaryStage.setScene(scene);
    }

    /**
     * Sets up the game board, its hexagons, numbers and their positions
     * @param game is the game model that will give the game board so that ui can draw it.
     * @param gameBox is the layout that will contain the game board.
     */
    private void setupGameBoard(Game game, AnchorPane gameBox) {
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
                if ( board[ i][ j].isItStartPoint() )
                {
                    int diceNum = board[i][j].getDiceNumber();
                    int resource = board[ i][ j].getResource();

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
                    }
                    hexagon = new ImageView(new Image(imgPath));
                    System.out.println( hexagon.getFitWidth() + "   " + hexagon.getFitHeight() );
                    hexagon.setX((j - 2) * 30 + 30);
                    hexagon.setY(i * 30 + 15);
                    gameBox.getChildren().add(hexagon);
                }
            }
        }
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

        ArrayList<Rectangle> rects = new ArrayList<>();
        Map<Rectangle, Point2D> originalPositions = new HashMap<>();
        rects.add(new Rectangle(cardBox.getTranslateX() + 125, 10, 50, 75));
        originalPositions.put(rects.get(0), new Point2D(rects.get(0).getX(), rects.get(0).getY()));
        for(int i = 1; i < 10; i++)
        {
            Rectangle temp = new Rectangle(rects.get(i - 1).getX() + 40, 10, 50, 75);
            originalPositions.put(temp, new Point2D(temp.getX(), temp.getY()));
            switch (i)
            {
                case 1:
                    temp.setFill(Color.BLUE);
                    break;
                case 2:
                    temp.setFill(Color.RED);
                    break;
                case 3:
                    temp.setFill(Color.GREEN);
                    break;
                case 4:
                    temp.setFill(Color.YELLOW);
                    break;
                case 5:
                    temp.setFill(Color.PURPLE);
                    break;
                case 6:
                    temp.setFill(Color.PINK);
                    break;
                case 7:
                    temp.setFill(Color.ORANGE);
                    break;
                case 8:
                    temp.setFill(Color.BROWN);
                    break;
                case 9:
                    temp.setFill(Color.VIOLET);
                    break;
            }
            AtomicReference<Double> x = new AtomicReference<>((double) 0);
            AtomicReference<Double> y = new AtomicReference<>((double) 0);
            temp.setOnMousePressed(e->
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
            temp.setOnMouseReleased(e->
            {
                FadeOut animation = new FadeOut(cardPlayArea);
                FadeOut animation2 = new FadeOut(cardDragLabel);
                animation.setOnFinished(event ->
                {
                    cardPlayArea.setVisible(false);
                });
                animation2.setOnFinished(event ->
                {
                    cardDragLabel.setVisible(false);
                });
                animation.play();
                animation2.play();
               /* Bounds rectanglePosition = temp.localToScene(temp.getBoundsInLocal());
                Bounds playAreaPosition = cardPlayArea.localToScene(cardPlayArea.getBoundsInLocal());
                System.out.println( rectanglePosition.getCenterX() + " " + playAreaPosition.getCenterX());
                if ( playAreaPosition.contains( rectanglePosition.getCenterX(), rectanglePosition.getCenterY()) ||
                        playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY()) ||
                        playAreaPosition.contains( rectanglePosition.getCenterX(), rectanglePosition.getCenterY() + rectanglePosition.getHeight()) ||
                        playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY() + rectanglePosition.getHeight()))
                {
                    cardBox.getChildren().remove(temp);
                }
                else
                {
                    temp.setTranslateX(0);
                    temp.setTranslateY(0);
                } */
            });
            rects.add(temp);
        }

        for ( Rectangle rectangle : rects)
        {
            cardBox.getChildren().add(rectangle);
        }
    }

    /**
     * Sets the dice roll available gif and dice result images, it also implements the ui logic of a dice roll by
     * communicating with the game model.
     * @param diceRollAvailable is gif that shows user that dice is avaible for roll and if the user clicks it dice will
     *                          be rolled.
     * @param die1Result is the first die result.
     * @param die2Result is the second die result.
     * @param game is the game model that will give the dice roll result to the controller.
     */
    private void setupDiceRoll(ImageView diceRollAvailable, ImageView die1Result, ImageView die2Result, Game game)
    {
        diceRollAvailable.setOnMouseClicked(event ->
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
                sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        diceRollAvailable.setVisible(false);
                        FadeIn die1Anim = new FadeIn(die1Result);
                        FadeIn die2Anim = new FadeIn(die2Result);
                        die1Anim.play();
                        die2Anim.play();
                        die1Result.setVisible(true);
                        die2Result.setVisible(true);
                    }
                });
                new Thread(sleeper).start();
            });
            animation.play();

            ArrayList<Integer> results = game.rollDice();
            die1Result.setImage(new Image("/images/die" + results.get(0) + ".png"));
            die2Result.setImage(new Image("/images/die" + results.get(1) + ".png"));
        });
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
        if ( x > 15 && x < 45 )
        {
            return 0;
        }
        else if ( x > 650 && x < 672 )
        {
            return 22;
        }
        return ( (int) x / 30 );
    }

    /**
     * Processes the mouse click event for the y coordinate of game board
     * @param y is the y coordinate given by the mouse event
     * @return an integer index, the processed result corresponding the y index for the game board
     */
    private int processY( double y)
    {
        //y = y - 15;
        return (int) y / 30;
    }

    /**
     * The function to handle user intereaction for the game board.
     * @param resultCode is the code returned from the game controller
     * @param x is the x corrdinate in the game board
     * @param y is the y coordinate in the game board
     */
    private void createDialog(  int resultCode, int x, int y )
    {

        // If the controller returns minus integer, there is an error!
        if ( resultCode < 0 )
        {
            System.out.println( " error is ** " + resultCode + "   ");
            // handle error
            informError( resultCode);
        }
        else
        {
            // The clicked tile is game tile, inform the user about the event regarding the resultCode gotten from controller
            Alert alert = new Alert( Alert.AlertType.CONFIRMATION);
            alert.initStyle( StageStyle.UTILITY);

            // User icon could be used
            ImageView icon = new ImageView();
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            alert.getDialogPane().setGraphic( icon);

            System.out.println( "result is ** " + resultCode + "   ");
            // Handle the intended user action could have a dedicated function for it!
            informResult( alert, resultCode, x, y);
        }
        System.out.println();
    }

    /**
     * Inform error function updates the status bar to display user error, this function could be evolved into pop up
     * and a sound may be played regarding the error.
     * @param resultCode is the error code given by the game controller
     */
    private void informError( int resultCode )
    {
        if ( resultCode == -1 )
        {
            this.statusBar.setText( "There is no connection for road to build for player:" +
                    this.game.getCurrentPlayer().getName() );
        }
        else if ( resultCode == -2 )
        {
            this.statusBar.setText( "There is no connection for city to build for player:" +
                    this.game.getCurrentPlayer().getName() );
        }
        else if ( resultCode == -3 )
        {
            this.statusBar.setText( "There is a building near for player:" +
                    this.game.getCurrentPlayer().getName() );
        }
        else if ( resultCode == -4 )
        {
            this.statusBar.setText( "This spot is occupied by other players for player: " +
                    this.game.getCurrentPlayer().getName() );
        }
        else
        {
            this.statusBar.setText( "Inform Error function could not detect the error??");
        }

        this.myPopUp.show( this.stage); // We need a sttaus label ! pop up is not good
    }

    /**
     * Creates the dialog corresponding to the user action on the game board.
     * @param alert is the dialog to display the user event
     * @param resultCode is the positive result gotten from the controller
     * @param x is the x coordinate of to perform action on the game board
     * @param y is the y coordinate of to perform action on the game board
     */
    private void informResult( Alert alert, int resultCode, int x, int y )
    {
        if ( resultCode == 0 )
        {
            buildRoad( alert, x, y);
        }
        else if ( resultCode == 1)
        {
            buildSettlement( alert, x ,y);
        }
        else if ( resultCode == 2)
        {
            buildCity( alert, x, y);
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
        alert.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText("Build settlement?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK){
            System.out.println( "Building Setlement...");
            this.game.setTile( x, y, Structure.Type.SETTLEMENT);
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
        alert.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText("Build road?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK){
            System.out.println( "Building road...");
            this.game.setTile( x, y, Structure.Type.ROAD);
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
        alert.setHeaderText("");
        alert.setContentText("Upgrade city?");

        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == ButtonType.OK){
            System.out.println( "Upgrading to city");
            this.game.setTile( x, y, Structure.Type.CITY);
        }
    }

    //******************************************************************************************************************
    //
    // FUNCTIONS RELATED TO TRADE BUTTONS
    //
    //******************************************************************************************************************

    // Insert function for trade buttons here


    //******************************************************************************************************************
    //
    // FUNCTIONS RELATED TO END TURN BUTTON
    //
    //******************************************************************************************************************

    // Insert function for end button here

}
