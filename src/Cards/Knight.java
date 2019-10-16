package Cards;

import java.awt.image.BufferedImage;

public class Knight extends Card
{
    // Attributes
    BufferedImage image = new BufferedImage(5, 5, 5);

    //Constructor
    public Knight()
    {
        super("Knight", "This special card allows you to change the position of the thief and steal" +
                " a card from a neighbouring player.");
        setImage(image);
    }

    public void playCard() {
        if ( isTurn) {
            GameEngine.getCurrentPlayer().increaseKnights();
            int thiefDestination = GameEngine.requestNumber();
            GameEngine.moveThief(thiefDestination);
            Player targetedPlayer = GameEngine.requestPlayer();
            GameEngine.stealResource(targetedPlayer);
        }
    }
}
