package Cards;

import java.awt.image.BufferedImage;

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

    // Methods
    public void playCard() {
        if ( isTurn) {
            GameEngine.getCurrentPlayer().increaseScore(1);
        }
    }
}
