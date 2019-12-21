package SceneManagement.GameManagement;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.Response;
import SceneManagement.MultiGameController;
import SceneManagement.SingleGameController;
import ServerCommunication.ServerHandler;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import DevelopmentCards.*;

/**
 * This controller manages all the development card logic. It has association with the Single-GameFlow.Game controller.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class MultiDevCardController {
    // Properties
    private MultiGameController controller;
    private Scene scene;
    private ImageView devCardsHover;
    private Rectangle cardPlayableArea;
    private Label cardDragLabel;
    private AnchorPane cardBox;
    private ArrayList<Card> cards;
    private ArrayList<ImageView> uiCards;

    private double cardBoxHideLocation;
    private double cardBoxShownLocation;

    // Constructor
    public MultiDevCardController(Scene scene, MultiGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    /**
     * This method initializes the UI components (taken from the game's fxml) related to the development cards and it
     * adds the logic as a listener to the components.
     */
    private void initialize() {
        devCardsHover = (ImageView) scene.lookup("#devCardsHover");
        cardPlayableArea = (Rectangle) scene.lookup("#cardPlayArea");
        cardDragLabel = (Label) scene.lookup("#cardDragLabel");
        cardBox = (AnchorPane) scene.lookup("#cardBox");
        cardBoxHideLocation = cardBox.getTranslateY();
        cardBoxShownLocation = cardBoxHideLocation - 75;
        setupDevelopmentCards();
    }

    /**
     * This method adds the development card logic to the UI components as listener. Development cards are contained
     * int a box that is shown when player hovers over it. When the box is shown and player drags a development card to
     * the playable area, the card is played.
     */
    public void setupDevelopmentCards() {
        FlowManager flowManager = new FlowManager();

        // Initialize out animation for the previous player's development card box with 3x the normal speed.
        FadeOut cardBoxOut = new FadeOut(cardBox);
        cardBoxOut.setSpeed(3);
        cardBoxOut.setOnFinished(event ->
        {
            // Clear all the previous UI (important distinction) development cards from the card container.
            cardBox.getChildren().clear();
            // Get the development cards (logical unit ones) from the current player.
            ArrayList<Card> cards = controller.getLocalPlayer().getCards();
            // Initialize an ArrayList for the UI development cards.
            ArrayList<ImageView> cardsInUI = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                // Get the card image corresponding to the current one in the player.
                ImageView temp = new ImageView("/images/" + cards.get(i).getName() + ".png");
                // Place the UI development card in the container.
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
                    // When the card is picked and drag, show where the user can drop the card to play it.
                    cardPlayableArea.setVisible(true);
                    cardDragLabel.setVisible(true);
                    new FadeIn(cardPlayableArea).play();
                    new FadeIn(cardDragLabel).play();
                    x.set(e.getX());
                    y.set(e.getY());
                });
                temp.setOnMouseDragged(e ->
                {
                    // Add drag functionality to the card by adding the delta of the mouse to the card's coordinates.
                    temp.setTranslateX(temp.getTranslateX() + (e.getX() - x.get()));
                    temp.setTranslateY(temp.getTranslateY() + (e.getY() - y.get()));
                });
                int finalI = i;
                temp.setOnMouseReleased(e ->
                {
                    if ( controller.getLocalPlayer() == flowManager.getCurrentPlayer() ) {
                        // When the card is dropped, initialize out animation for the "play the card here" area of the UI.
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
                        // Get the dropped location of the card
                        Bounds rectanglePosition = temp.localToScene(temp.getBoundsInLocal());
                        Bounds playAreaPosition = cardPlayableArea.localToScene(cardPlayableArea.getBoundsInLocal());
                        // Check if the dropped location of the card is inside the playable area, if it is play the card.
                        // If not, send the card to its original location.
                        if (playAreaPosition.contains(rectanglePosition.getCenterX(), rectanglePosition.getCenterY()) ||
                                playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY()) ||
                                playAreaPosition.contains(rectanglePosition.getCenterX(), rectanglePosition.getCenterY() + rectanglePosition.getHeight()) ||
                                playAreaPosition.contains(rectanglePosition.getCenterX() + rectanglePosition.getWidth(), rectanglePosition.getCenterY() + rectanglePosition.getHeight())) {
                            if (cards.get(finalI).isPlayable()) {
                                System.out.println("Current card playable and played");
                                cardBox.getChildren().remove(temp);
                                cards.get(finalI).play();
                                ServerHandler.getInstance().playCard(cards.get(finalI).getName(), finalI);
                                setupDevelopmentCards();
                                controller.getInfoController().setupCurrentPlayer();
                                ServerHandler.getInstance().refreshInfos();
                                if ( cards.get(finalI) instanceof VictoryPoint)
                                {
                                    this.controller.checkWinCondition();
                                }
                                if ( cards.get(finalI) instanceof Knight)
                                {
                                    this.controller.checkWinCondition();
                                }
                                if (cards.get(finalI) instanceof YearOfPlenty) {
                                    controller.getSelectionController().showResourceSelectionForPlenty();
                                } else if (cards.get(finalI) instanceof Monopoly) {
                                    controller.getSelectionController().showResourceSelectionForMonopoly();
                                }
                                cards.remove(cards.get(finalI));
                            } else {
                                temp.setTranslateX(0);
                                temp.setTranslateY(0);
                                controller.getStatusController().informStatus(Response.ERROR_CARD_NOT_PLAYABLE);
                            }
                        } else {
                            temp.setTranslateX(0);
                            temp.setTranslateY(0);
                            controller.getStatusController().informStatus(Response.ERROR_CARD_DRAGGED_OUTSIDE);
                        }
                    }
                    else {
                        temp.setTranslateX(0);
                        temp.setTranslateY(0);
                        controller.getStatusController().informStatus(Response.ERROR_NOT_PLAYER_TURN_CARD);
                    }
                });
                // Add the card image to the ArrayList.
                cardsInUI.add(temp);
            }

            // Add every UI card in the ArrayList to the game's UI.
            for (ImageView card : cardsInUI) {
                cardBox.getChildren().add(card);
            }

            // Play in animation for the current player's development card components in UI.
            FadeIn cardBoxIn = new FadeIn(cardBox);
            cardBoxIn.setSpeed(3);
            cardBoxIn.play();
        });
        cardBoxOut.play();

        devCardsHover.setOnMouseEntered(event ->
        {
            // If current player hovers of the "Development DevelopmentCards.Card" part of the box in UI, show the card container to user.
            if ( cardBox.getTranslateY() == cardBoxHideLocation) {
                TranslateTransition hoverTT = new TranslateTransition(Duration.millis(500), devCardsHover);
                TranslateTransition boxTT = new TranslateTransition(Duration.millis(500), cardBox);
                hoverTT.setByY(-75);
                boxTT.setByY(-75);
                hoverTT.play();
                boxTT.play();
                boxTT.setOnFinished(event1 ->
                {
                    cardBox.setTranslateY( -75);
                });
            }
        });
        cardBox.setOnMouseExited(event ->
        {
            // If the container is already shown and player's mouse exits the container, hide the container.
            if ( cardBox.getTranslateY() == cardBoxShownLocation) {
                TranslateTransition hoverTT = new TranslateTransition(Duration.millis(500), devCardsHover);
                TranslateTransition boxTT = new TranslateTransition(Duration.millis(500), cardBox);
                hoverTT.setByY(75);
                boxTT.setByY(75);
                hoverTT.play();
                boxTT.play();
                boxTT.setOnFinished(event1 ->
                {
                    cardBox.setTranslateY( 0);
                });
            }
        });
    }
}
