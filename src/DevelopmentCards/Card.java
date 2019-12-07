package DevelopmentCards;

/**
 * The DevelopmentCards.Card class that represents the special cards in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
public abstract class Card
{
    public static final int[] REQUIREMENTS_FOR_CARD = {0, 1, 1, 0, 1};

    // Attributes
    private String name;
    private String information;
    private boolean isPlayable;

    // Constructor
    public Card()
    {
        isPlayable = false;
    }

    // Functions
    public abstract void play();

    /**
     * Gets the card's name.
     * @return card's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the card's name to the given name.
     * @param name is card's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the card's information.
     * @return card's information.
     */
    public String getInformation() {
        return information;
    }

    /**
     * Sets the card's information to the given information.
     * @param information is card's information.
     */
    public void setInformation(String information) {
        this.information = information;
    }

    /**
     * Gets the card's playability.
     * @return card's playability status.
     */
    public boolean isPlayable() {
        return isPlayable;
    }

    /**
     * Makes the card playable, isPlayable = true
     */
    public void makePlayable() {
        isPlayable = true;
    }

}
