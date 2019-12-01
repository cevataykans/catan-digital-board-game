import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.concurrent.Flow;

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
    private void initialize() {
        diceAvailable = (ImageView) scene.lookup("#diceRollAvailable");
        die1 = (ImageView) scene.lookup("#die1Result");
        die2 = (ImageView) scene.lookup("#die2Result");
        setupDiceRoll();
    }

    public void setupDiceRoll() {
        FadeOut die1Out = new FadeOut(die1);
        FadeOut die2Out = new FadeOut(die2);
        die1Out.setSpeed(2);
        die2Out.setSpeed(2);
        die1Out.setOnFinished(event ->
        {
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
                FadeOut animation = new FadeOut(diceAvailable);
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
                        diceAvailable.setVisible(false);
                        FadeIn die1Anim = new FadeIn(die1);
                        FadeIn die2Anim = new FadeIn(die2);
                        die1Anim.play();
                        die2Anim.play();
                        die1.setVisible(true);
                        die2.setVisible(true);
                        controller.infoController.setupCurrentPlayer();
                    });
                    new Thread(sleeper).start();
                });
                animation.play();

                //***** Logic to roll the dice and collect resources, collecting resources could be made in the dice method of game class! *****
                FlowManager.getInstance().doneMust();
                ArrayList<Integer> results = controller.getGame().rollDice();
                //game.collectResources();

                die1.setImage(new Image("/images/die" + results.get(0) + ".png"));
                die2.setImage(new Image("/images/die" + results.get(1) + ".png"));
            }
            else
            {
                controller.statusController.informStatus( controller.flowManager.checkMust() );
            }
        });
    }
}
