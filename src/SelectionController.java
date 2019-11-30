import animatefx.animation.FadeInLeft;
import animatefx.animation.FadeOutRight;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class SelectionController {
    // Properties
    SingleGameController controller;
    Scene scene;
    AnchorPane selectionBox;
    Label selectionLabel;

    // Constructor
    public SelectionController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    private void initialize()
    {
        selectionBox = (AnchorPane) scene.lookup("#selectionBox");
        selectionLabel = (Label) scene.lookup("#selectionLabel");
    }

    public void showPlayerSelection(ArrayList<Player> playersToSelect) {
        controller.statusController.informStatus(8);
        selectionLabel.setText( "Choose Your Player");
        ArrayList<Rectangle> players = new ArrayList<>();
        for ( int i = 0; i < playersToSelect.size(); i++ )
        {
            // According to the index of the array list, configure the player information
            Rectangle otherPlayer = new Rectangle(i * 300 + 150, 100, 200, 400);
            otherPlayer.setFill( playersToSelect.get( i).getColor() );
            int finalI = i;
            otherPlayer.setOnMousePressed(e -> {

                if ( controller.flowManager.checkMust() == 8 )
                {
                    controller.flowManager.doneMust();
                }
                stealResourceFromPlayer( playersToSelect.get( finalI) );
                controller.infoController.setupCurrentPlayer();
            });
            players.add(otherPlayer);

            players.get(i).getStyleClass().add("resourceBox");
            selectionBox.getChildren().add(players.get(i));
        }
        new FadeInLeft(selectionBox).play();
        selectionBox.setVisible(true);
    }

    private void stealResourceFromPlayer( Player stealingFrom)
    {
        // Resource stealing for the selected player must
        if ( controller.flowManager.checkMust() == 8 )
        {
            controller.flowManager.doneMust();

            controller.game.getCurrentPlayer().stealResourceFromPlayer( stealingFrom );
        }
        new FadeOutRight( selectionBox).play();
        selectionBox.setVisible(false);
    }

    public void showResourceSelection(String id) {
        if ( id == "MONOPOLY")
        {
            controller.statusController.informStatus(5);
        }
        else if ( id == "YEAROFPLENTY")
        {
            controller.statusController.informStatus(6);
        }
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
}
