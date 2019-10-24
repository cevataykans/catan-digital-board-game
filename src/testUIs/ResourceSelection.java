package testUIs;

import animatefx.animation.BounceIn;
import animatefx.animation.BounceInLeft;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.*;
import javafx.util.Duration;

public class ResourceSelection extends Application {
    boolean exists = false;
    Circle circle = new Circle();
    Rectangle rects[] = new Rectangle[5];
    Group root = new Group();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(root, 1000, 600, Color.AQUA);

        Rectangle b = new Rectangle(100, 100);
        b.setFill(Color.YELLOW);
        b.setTranslateX(50);
        b.setTranslateY(500);
        b.setOnMouseEntered(e-> {
            enableScoreShow(root);
        });
        b.setOnMouseExited(e-> {
            disableScoreShow(root);
        });
        Button button = new Button("SELECT RESOURCE");
        button.setTranslateX(450);
        button.setTranslateY(50);
        button.setOnAction(e-> {
            askResource(root);
        });
        root.getChildren().add(b);

        root.getChildren().add(button);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("RESOURCE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void enableScoreShow(Group root)
    {
        if ( !exists) {
            circle = new Circle();
            circle.setFill(Color.GREEN);
            circle.setRadius(30);
            circle.setTranslateX(110);
            circle.setTranslateY(515);
            root.getChildren().add(circle);
            FadeIn fadeIn = new FadeIn(circle);
            fadeIn.play();

            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.seconds(1));
            transition.setToX(200);
            transition.setToY(515);
            transition.setNode(circle);
            transition.play();
            exists = true;
        }
    }
    private void disableScoreShow(Group root)
    {
        if ( exists) {
            FadeOut fadeOut = new FadeOut(circle);
            fadeOut.play();
            fadeOut.setOnFinished(event -> {
                root.getChildren().remove(circle);
            });
            exists = false;
        }
    }

    private void askResource(Group root) {
        for ( int i = 0; i < rects.length; i++)
        {
            rects[i] = new Rectangle( ( i + 1) * 150, 100, 120, 300);
            switch (i)
            {
                case 0:
                    Image img = new Image("/images/wood.jpg");
                    rects[i].setFill(new ImagePattern(img));
                    rects[i].setOnMousePressed(e-> {
                        System.out.println("YOU CHOSE LUMBER!");
                        closeSelection();
                    });
                    break;
                case 1:
                    Image img2 = new Image("/images/sheep.jpg");
                    rects[i].setFill(new ImagePattern(img2));
                    rects[i].setOnMousePressed(e-> {
                        System.out.println("YOU CHOSE WOOL!");
                        closeSelection();
                    });
                    break;
                case 2:
                    Image img3 = new Image("/images/grain.png");
                    rects[i].setFill(new ImagePattern(img3));
                    rects[i].setOnMousePressed(e-> {
                        System.out.println("YOU CHOSE GRAIN!");
                        closeSelection();
                    });
                    break;
                case 3:
                    Image img4 = new Image("/images/brick.jpg");
                    rects[i].setFill(new ImagePattern(img4));
                    rects[i].setOnMousePressed(e-> {
                        System.out.println("YOU CHOSE BRICK!");
                        closeSelection();
                    });
                    break;
                case 4:
                    Image img5 = new Image("/images/ore.jpg");
                    rects[i].setFill(new ImagePattern(img5));
                    rects[i].setOnMousePressed(e-> {
                        System.out.println("YOU CHOSE ORE!");
                        closeSelection();
                    });
                    break;
            }
            root.getChildren().add(rects[i]);
            new BounceInLeft(rects[i]).play();
        }
    }

    private void closeSelection()
    {
        for ( int p = 0; p < rects.length; p++)
        {
            new FadeOut(rects[p]).play();
            root.getChildren().remove(rects[p]);
        }
    }
}
