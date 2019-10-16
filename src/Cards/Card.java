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
