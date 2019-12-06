package SceneManagement.GameManagement;

import SceneManagement.SingleGameController;
import SceneManagement.SoundManager;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import GameFlow.FlowManager;

/**
 * This controller manages all the dice logic. It has association with the Single-GameFlow.Game controller.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class DiceController {
    // Properties
    SingleGameController controller;
    Scene scene;
    ImageView die1;
    ImageView die2;
    ImageView diceAvailable;

    // Constructor
    public DiceController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
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
            // Dice could only be rolled at the beginning of a turn
            if ( FlowManager.getInstance().checkMust() == 7 )
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
                FlowManager.getInstance().doneMust();
                ArrayList<Integer> results = controller.getGame().rollDice();

                // Set die result images taken from the logic.
                die1.setImage(new Image("/images/die" + results.get(0) + ".png"));
                die2.setImage(new Image("/images/die" + results.get(1) + ".png"));
            }
            else
            {
                // If the current action should not be rolling dice, inform the current player.
                controller.getStatusController().informStatus( FlowManager.getInstance().checkMust() );
            }
        });
    }
}
