package Cards;

import java.awt.image.BufferedImage;

/**
 * The Knight class that represents the special card "Knight" in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
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

    // Functions
    /**
     * Plays the "Knight" card that will change the thief's position and make the player steal a card from someone else.
     */
    public void playCard() {
        if ( isTurn) {
            GameController.getCurrentPlayer().increaseLargestArmy();
            int thiefDestination = GameController.requestNumber();
            GameController.moveRobber(thiefDestination);
            Player targetedPlayer = GameController.requestPlayer();
            GameController.stealResource(targetedPlayer);
        }
    }
}
