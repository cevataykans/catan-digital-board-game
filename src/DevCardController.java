import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class DevCardController {

    // Properties
    private SingleGameController controller;
    private Scene scene;
    private ImageView devCardsHover;
    private Rectangle cardPlayableArea;
    private Label cardDragLabel;
    private AnchorPane cardBox;
    private ArrayList<Card> cards;
    private ArrayList<ImageView> uiCards;

    private boolean movedUp = false;
    private boolean movedDown = true;

    // Constructor
    public DevCardController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    private void initialize() {
        devCardsHover = (ImageView) scene.lookup("#devCardsHover");
        cardPlayableArea = (Rectangle) scene.lookup("#cardPlayArea");
        cardDragLabel = (Label) scene.lookup("#cardDragLabel");
        cardBox = (AnchorPane) scene.lookup("#cardBox");
        setupDevelopmentCards();
    }

    public void setupDevelopmentCards() {
        FadeOut cardBoxOut = new FadeOut(cardBox);
        cardBoxOut.setSpeed(3);
        cardBoxOut.setOnFinished(event ->
        {
            cardBox.getChildren().clear();
            ArrayList<Card> cards = controller.getGame().getCurrentPlayer().getCards();
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
                    cardPlayableArea.setVisible(true);
                    cardDragLabel.setVisible(true);
                    new FadeIn(cardPlayableArea).play();
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
                    FadeOut animation = new FadeOut(cardPlayableArea);
                    FadeOut animation2 = new FadeOut(cardDragLabel);
                    animation.setOnFinished(event1 ->
                    {
                        cardPlayableArea.setVisible(false);
                    });
                    animation2.setOnFinished(event1 ->
                    {
                        cardDragLabel.setVisible(false);
                    });
                    animation.play();
                    animation2.play();
                    Bounds rectanglePosition = temp.localToScene(temp.getBoundsInLocal());
                    Bounds playAreaPosition = cardPlayableArea.localToScene(cardPlayableArea.getBoundsInLocal());
                    if (playAreaPosition.contains( rectanglePosition.getCenterX(), rectanglePosition.getCenterY() ) ||
                            playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY()) ||
                            playAreaPosition.contains(rectanglePosition.getCenterX(), rectanglePosition.getCenterY() + rectanglePosition.getHeight()) ||
                            playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY() + rectanglePosition.getHeight())) {
                        controller.getGame().playDevelopmentCard(cards.get(finalI));
                        controller.infoController.setupLargestArmy();
                        cardBox.getChildren().remove(temp);
                    } else {
                        temp.setTranslateX(0);
                        temp.setTranslateY(0);
                    }
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

        devCardsHover.setOnMouseEntered(event ->
        {
            if ( movedDown && !movedUp) {
                TranslateTransition hoverTT = new TranslateTransition(Duration.millis(500), devCardsHover);
                TranslateTransition boxTT = new TranslateTransition(Duration.millis(500), cardBox);
                hoverTT.setByY(-75);
                boxTT.setByY(-75);
                hoverTT.play();
                boxTT.play();
                movedUp = true;
                movedDown = false;
            }
        });
        cardBox.setOnMouseExited(event ->
        {
            if ( movedUp && !movedDown) {
                TranslateTransition hoverTT = new TranslateTransition(Duration.millis(500), devCardsHover);
                TranslateTransition boxTT = new TranslateTransition(Duration.millis(500), cardBox);
                hoverTT.setByY(75);
                boxTT.setByY(75);
                hoverTT.play();
                boxTT.play();
                movedUp = false;
                movedDown = true;
            }
        });
    }
}
