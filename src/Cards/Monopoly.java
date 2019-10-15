package Cards;

import java.awt.image.BufferedImage;

/**
 * The Monopoly class that represents the special card "Monopoly" in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
public class Monopoly extends Card
{
    // Attributes
    BufferedImage image = new BufferedImage(5, 5, 5);

    //Constructor
    public Monopoly()
    {
        super("Monopoly", "This special card allows you to steal ALL of any one resource of your " +
                "choice from all other players.");
        setImage(image);
    }

    // Functions
    /**
     * Plays the "Monopoly" card that will allow player to steal all of any one resource of player's choice from all other players.
     */
    public void playCard() {
        if ( isTurn) {
            int theResource = GameEngine.requestResource();
            for (int i = 0; i < 4; i++) {
                if (GameEngine.getPlayer(i) != GameEngine.currentPlayer()) {
                    for (int p = 0; p < GameEngine.getPlayer(i).getResources()[theResource]; p++) {
                        GameEngine.getCurrentPlayer().addResource(theResource);
                        GameEngine.getPlayer(p).removeResource(theResource);
                    }
                }
            }
        }
    }
}
