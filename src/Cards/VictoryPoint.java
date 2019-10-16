package Cards;

import java.awt.image.BufferedImage;

/**
 * The VictoryPoint class that represents the special card "VictoryPoint" in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
public class VictoryPoint extends Card
{
    // Attributes
    BufferedImage image = new BufferedImage(5, 5, 5);

    //Constructor
    public VictoryPoint()
    {
        super("VictoryPoint", "This special card increases your score by 1 point.");
        setImage(image);
    }

    // Functions
    /**
     * Plays the "VictoryPoint" card that will increase the player's point(s) by 1.
     */
    public void playCard() {
        if ( isTurn) {
            GameController.getCurrentPlayer().increaseScore(1);
        }
    }
}
