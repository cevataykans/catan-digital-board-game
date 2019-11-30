import animatefx.animation.FadeInLeft;
import animatefx.animation.FadeOutRight;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class StatusController {
    // Properties
    SingleGameController controller;
    Scene scene;
    Label statusText;

    // Constructor
    public StatusController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    private void initialize()
    {
        statusText = (Label) scene.lookup("#statusText");
    }

    public void informStatus(int resultCode)
    {
        FadeOutRight animation = new FadeOutRight(statusText);
        animation.setSpeed(3);
        animation.setOnFinished(event ->
        {
            if ( resultCode == -1 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", there is no connection for a road to build");
            }
            else if ( resultCode == -2 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", there is no connection for a settlement to build");
            }
            else if ( resultCode == -3 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", there is another building near");
            }
            else if ( resultCode == -4 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", this spot is occupied by a player");
            }
            else if ( resultCode == -5 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", not enough resources for a road");
            }
            else if ( resultCode == -6 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", not enough resources for a settlement");
            }
            else if ( resultCode == -7 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", not enough resources for a city");
            }
            else if ( resultCode == -8 )
            {
                statusText.setText( "Other player does not have enough resources");
            }
            else if ( resultCode == -9)
            {
                statusText.setText( "-");
            }
            else if ( resultCode == 0 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", build road first!");
            }
            else if ( resultCode == 1 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", build settlement first!");
            }
            else if ( resultCode == 2 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", build city first!");
            }
            else if ( resultCode == 3 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", move the robber first by clicking and dragging!");
            }
            else if ( resultCode == 4 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", select a resource for monopoly card!");
            }
            else if ( resultCode == 5 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", select two resources for year of plenty card!");
            }
            else if ( resultCode == 6 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", END YOUR TURN RIGHT NOW!!!!!");
            }
            else if ( resultCode == 7 )
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", but first, lets roll the dice!");
            }
            else if ( resultCode == 8)
            {
                statusText.setText( controller.game.getCurrentPlayer().getName() + ", choose a neighbor player first to steal a resource!");
            }
            else
            {
                statusText.setText( "Function could not detect the error, lol, exploded!");
            }
            FadeInLeft animation2 = new FadeInLeft(statusText);
            animation2.setSpeed(3);
            animation2.play();
        });
        animation.play();
    }
}
