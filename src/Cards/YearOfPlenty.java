package Cards;

import java.awt.image.BufferedImage;

/**
 * The YearOfPlenty class that represents the special card "YearOfPlenty" in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
public class YearOfPlenty extends Card
{
    // Attributes
    BufferedImage image = new BufferedImage(5, 5, 5);
    int[] requirements = {0, 0, 0, 0, 0};
    //Constructor
    public YearOfPlenty()
    {
        super("YearOfPlenty", "This special card allows you to gain 2 resources of your choice from the bank");
        setImage(image);
    }

    // Functions
    /**
     * Plays the "Monopoly" card that will allow player to gain 2 resources of their choice.
     */
    public void playCard() {
        if ( isTurn) {
            int firstResource = GameEngine.requestResource();
            int secondResource = GameEngine.requestResource();
            GameEngine.getCurrentPlayer().addResource(firstResource);
            GameEngine.getCurrentPlayer().addResource(secondResource);
        }
    }
}
