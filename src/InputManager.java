import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InputManager extends Application {

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
        initializeIntro1(root, primaryStage);
        primaryStage.show();
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


        primaryStage.setScene(scene);
    }
}
