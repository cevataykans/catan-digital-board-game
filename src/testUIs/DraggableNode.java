package testUIs;

import animatefx.animation.GlowText;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DraggableNode extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    double x, y;
    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        Rectangle destination = new Rectangle(50, 100);
        destination.setTranslateX(200);
        destination.setTranslateY(100);
        destination.setStroke(Color.BLACK);
        destination.setFill(Color.LIGHTGRAY);

        Rectangle rect = new Rectangle(50, 100);
        rect.setTranslateX(200);
        rect.setTranslateY(300);
        rect.setStroke(Color.YELLOW);
        rect.setFill(Color.BLUE);
        rect.setOnMousePressed(e->
        {
            x = e.getX();
            y = e.getY();
        });
        rect.setOnMouseDragged(e ->
        {
            rect.setTranslateX(rect.getTranslateX() + (e.getX() - x));
            rect.setTranslateY(rect.getTranslateY() + (e.getY() - y));
        });
        rect.setOnMouseReleased(e->
        {
            destination.setFill(Color.RED);
            System.out.println("Rect x: " + rect.getTranslateY() + " Dest x: " + destination.getTranslateY());
            if ( rect.contains(destination.getX(), destination.getY()))
            {
                System.out.println("YOU PLAYED THE CARD!");
                root.getChildren().remove(rect);
            }
        });
        root.getChildren().add(destination);
        root.getChildren().add(rect);

        Scene scene = new Scene(root, 500, 500);
        scene.setFill(Color.CADETBLUE);
        primaryStage.setTitle("DRAG TEST");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
