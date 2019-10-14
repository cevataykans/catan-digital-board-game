package Cards;

import java.awt.image.BufferedImage;

public class RoadBuilding extends Card
{
    // Attributes
    BufferedImage image = new BufferedImage(5, 5, 5);

    //Constructor
    public RoadBuilding()
    {
        super("RoadBuilding", "This special card allows you to build 2 roads freely.");
        setImage(image);
    }

    // Methods
    // Note: buildRoad function takes a boolean, which determines if it is called by normally or by a special card.
    // If it is true, then the buildRoad function builds the road to the location chosen by the player without asking
    // for the resources.
    public void playCard() {
        if ( isTurn) {
            GameEngine.buildRoad(true);
            GameEngine.buildRoad(true);
        }
    }
}
