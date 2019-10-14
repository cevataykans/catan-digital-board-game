package Cards;

import java.awt.image.BufferedImage;

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

    // Methods
    public void playCard() {
        if ( isTurn) {
            int firstResource = GameEngine.requestResource();
            int secondResource = GameEngine.requestResource();
            GameEngine.getCurrentPlayer().addResource(firstResource);
            GameEngine.getCurrentPlayer().addResource(secondResource);
        }
    }
}
