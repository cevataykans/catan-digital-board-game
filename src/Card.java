import java.awt.image.BufferedImage;

/**
 * The Card class that represents the special cards in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
public class Card
{
    public static final int[] REQUIREMENTS_FOR_CARD = {1, 0, 0, 1, 1};

    // Card Types
    enum CardType {
        KNIGHT, MONOPOLY, ROADBUILDING, VICTORYPOINT, YEAROFPLENTY
    }

    // Attributes
    private CardType type;
    private String name;
    private int[] requirements;
    private String information;
    private BufferedImage image;
    private boolean isPlayable;

    // Constructor
    public Card(CardType type)
    {
        if ( type == CardType.KNIGHT)
        {
            name = "Knight";
            information = "This special card allows you to change the position of the thief and steal" +
                    " a card from a neighbouring player.";
        }
        else if ( type == CardType.MONOPOLY)
        {
            name = "Monopoly";
            information = "\"This special card allows you to steal ALL of any one resource of your" +
                    " choice from all other players.";
        }
        else if ( type == CardType.ROADBUILDING)
        {
            name = "Road-Building";
            information = "This special card allows you to build 2 roads freely.";
        }
        else if ( type == CardType.VICTORYPOINT)
        {
            name = "Victory-Point";
            information = "This special card increases your score by 1 point.";
        }
        else if ( type == CardType.YEAROFPLENTY)
        {
            name = "Year-of-Plenty";
            information = "This special card allows you to gain 2 resources of your choice from the bank";
        }
        this.type = type;
        requirements = new int[5];
        requirements[0] = 0;
        requirements[1] = 1;
        requirements[2] = 1;
        requirements[3] = 0;
        requirements[4] = 1;
        isPlayable = false;
    }

    // Functions
    /**
     * Gets the card's type.
     * @return card's type.
     */
    public CardType getType() {
        return type;
    }

    /**
     * Sets the card type to the given type.
     * @param type The name that will be set as card's type.
     */
    public void setType(CardType type) {
        this.type = type;
    }


    /**
     * Gets the card's name.
     * @return card's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the card name to the given name.
     * @param name The name that will be set as card's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the card's requirements.
     * @return card's requirements.
     */
    public int[] getRequirements() {
        return requirements;
    }

    /**
     * Sets the card requirements to the given requirements.
     * @param requirements The requirements that will be set as card's requirements.
     */
    public void setRequirements(int[] requirements) {
        this.requirements = requirements;
    }

    /**
     * Gets the card's information.
     * @return card's information.
     */
    public String getInformation() {
        return information;
    }

    /**
     * Sets the card information to the given information.
     * @param information The information that will be set as card's information.
     */
    public void setInformation(String information) {
        this.information = information;
    }

    /**
     * Gets the card's image.
     * @return card's image.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the card image to the given image.
     * @param image The image that will be set as card's image.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Gets the card's playability.
     * @return card's playability status.
     */
    public boolean isPlayable() {
        return isPlayable;
    }

    /**
     * Sets the card's playability to the given status.
     * @param playable The status of playability that will be set as card's playable status.
     */
    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }
}
