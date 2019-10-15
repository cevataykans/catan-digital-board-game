package Cards;

import java.awt.image.BufferedImage;

/**
 * The RoadBuilding class that represents the special card "RoadBuilding" in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
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

    // Functions
    /**
     * Plays the "RoadBuilding" card that will allow player to build 2 roads freely to any available space of theirs
     */
    public void playCard() {
        if ( isTurn) {
            GameEngine.buildRoad(true);
            GameEngine.buildRoad(true);
        }
    }
}
