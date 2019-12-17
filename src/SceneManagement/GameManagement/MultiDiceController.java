package SceneManagement.GameManagement;

import DevelopmentCards.Card;
import DevelopmentCards.ChangeOfFortune;
import GameFlow.FlowManager;
import GameFlow.Response;
import SceneManagement.MultiGameController;
import SceneManagement.SceneController;
import SceneManagement.SingleGameController;
import SceneManagement.SoundManager;
import ServerCommunication.ServerHandler;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Flow;

public class MultiDiceController {

    // Properties
    private MultiGameController controller;
    private Scene scene;
    private ImageView die1;
    private ImageView die2;
    private ImageView diceAvailable;

    // Constructor
    public MultiDiceController(Scene scene, SceneController controller)
    {
        this.scene = scene;
        this.controller = (MultiGameController) controller;
        initialize();
    }

    // Methods
    /**
     * This method initializes the UI components (taken from the game's fxml) related to the dice and it
     * adds the logic as a listener to the components.
     */
    private void initialize() {
        diceAvailable = (ImageView) scene.lookup("#diceRollAvailable");
        die1 = (ImageView) scene.lookup("#die1Result");
        die2 = (ImageView) scene.lookup("#die2Result");
        setupDiceRoll();
    }

    /**
     * This method adds the dice logic to the UI components as listener. Dice and 2 of separate dies are independent
     * gif/images. When the roll function is available (or the dice needs to be rolled) the dies are hidden and dice gif
     * is shown. When the dice gif is clicked, roll function is called and the 2 die results are shown as images in UI.
     */
    public void setupDiceRoll() {
        FlowManager flowManager = new FlowManager();
        // Initialize out animation for the previous die results with 2x the normal speed.
        FadeOut die1Out = new FadeOut(die1);
        FadeOut die2Out = new FadeOut(die2);
        die1Out.setSpeed(2);
        die2Out.setSpeed(2);
        die1Out.setOnFinished(event ->
        {
            // When the animation finishes, hide the previous results, make the roll gif available via an in animation
            // with 2x the normal speed.
            die1.setVisible(false);
            die2.setVisible(false);
            diceAvailable.setVisible(true);
            FadeIn rollAvailableIn = new FadeIn(diceAvailable);
            rollAvailableIn.setSpeed(2);
            rollAvailableIn.play();
        });
        die1Out.play();
        die2Out.play();
        diceAvailable.setOnMouseClicked(event ->
        {
            if(controller.getLocalPlayer() == flowManager.getCurrentPlayer()){
                rollDice();
            }
        });
    }

    public void rollDice(){
        FlowManager flowManager = new FlowManager();
        // Dice could only be rolled at the beginning of a turn
        if ( flowManager.checkMust() == Response.MUST_ROLL_DICE || controller.getLocalPlayer() != flowManager.getCurrentPlayer())
        {
            // Initialize an out animation for the roll gif when its clicked with 2x the normal speed.
            FadeOut animation = new FadeOut(diceAvailable);
            animation.setSpeed(2);
            animation.setOnFinished(event1 ->
            {
                // Wait 50 milliseconds before showing the die results in the UI.
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
                    // Initialize in animations for the die results, also refresh the current player information.
                    diceAvailable.setVisible(false);
                    FadeIn die1Anim = new FadeIn(die1);
                    FadeIn die2Anim = new FadeIn(die2);
                    die1Anim.play();
                    die2Anim.play();
                    die1.setVisible(true);
                    die2.setVisible(true);
                    controller.getInfoController().setupCurrentPlayer();
                });
                new Thread(sleeper).start();
            });
            animation.play();
            SoundManager.getInstance().playEffect(SoundManager.Effect.ROLL_DICE);

            // Clear the roll action from the Flow Manager as it is already done, roll the dice in the logic itself
            // and distribute resources to the players that needs to collect resources from the hexagons.
            if(controller.getLocalPlayer() == flowManager.getCurrentPlayer()) {
                flowManager.doneMust();
                ArrayList<Integer> results = flowManager.rollDice();

                // Set die result images taken from the logic.
                die1.setImage(new Image("/images/die" + results.get(0) + ".png"));
                die2.setImage(new Image("/images/die" + results.get(1) + ".png"));
                boolean fortunePlayed = checkChangeOfFortune(results);
                if ( fortunePlayed == true){
                    setupDiceRoll();
                }
                else {
                    flowManager.collectResourcesForDice(results);
                }
            }
        }
        else
        {
            // If the current action should not be rolling dice, inform the current player.
            controller.getStatusController().informStatus( flowManager.checkMust() );
        }
    }

    private boolean checkChangeOfFortune(ArrayList<Integer> results) {
        FlowManager flowManager = new FlowManager();
        ArrayList<Card> devCards = flowManager.getCurrentPlayer().getCards();

        for ( Card card : devCards) {
            if ( card instanceof ChangeOfFortune) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initStyle(StageStyle.UTILITY);

                // Create a beautiful icon for catan dialog
                ImageView icon = new ImageView("/images/catanIcon.png");
                icon.setFitHeight(48);
                icon.setFitWidth(48);
                alert.getDialogPane().setGraphic(icon);

                alert.setHeaderText("Change Of Fortune");
                alert.setContentText("Die results are:\nDie1: " + results.get(0) + "\nDie2: " + results.get(1) +
                        "\nDo you want to play the Change Of Fortune card to re-roll the dice?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    card.play();
                    devCards.remove(card);
                    controller.getDevCardController().setupDevelopmentCards();
                    ServerHandler.getInstance().playCard(card.getName(), devCards.indexOf(card));
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }
}
