package Cards;

import java.awt.image.BufferedImage;

/**
 * The Card class that represents the special cards in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
public abstract class Card
{
    // Attributes
    String name;
    int[] requirements;
    String information;
    BufferedImage image;
    boolean isPlayable;

    // Constructor
    public Card( String name, String information)
    {
        this.name = name;
        this.information = information;
        requirements = new int[5];
        requirements[0] = 0;
        requirements[1] = 1;
        requirements[2] = 1;
        requirements[3] = 0;
        requirements[4] = 1;
        isPlayable = false;
    }

    // Functions
    abstract void playCard();

    /**
     * Sets the card image to the given image.
     * @param image The image that will be set as card's image.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
