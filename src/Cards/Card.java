package Cards;

import java.awt.image.BufferedImage;

public abstract class Card
{
    // Attributes
    String name;
    int[] requirements;
    String information;
    BufferedImage image;
    boolean isTurn;

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
        this.isTurn = false;
    }

    // Methods
    abstract void playCard();

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
