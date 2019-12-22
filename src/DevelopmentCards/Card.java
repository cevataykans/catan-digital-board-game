package DevelopmentCards;

/**
 * The DevelopmentCards.Card class that represents the special cards in catan gameboard.
 * @author Talha Şen
 * @version 15.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 */
public class Card
{
    public static final int[] REQUIREMENTS_FOR_CARD = {0, 1, 1, 0, 1};


    /*
        setName("Perfectly-Balanced");
        setInformation("This development card discards half of ALL players' resources.");

        setName("Change-of-Fortune");
        setInformation("This special card allows you to re-roll the dice after it is rolled, if you did not like the" +
                " previous results");

        setName("knight");
        setInformation("This development card allows you to change the position of the robber and steal" +
                " a card from a neighboring player.");

        setName("monopoly");
        setInformation("This development card allows you to steal ALL of any one resource of your" +
                " choice from all other players.");

        setName("Road-Building");
        setInformation("This special card allows you to build 2 roads freely.");

        setName("Victory-Point");
        setInformation("This special card increases your score by 1 point.");

        setName("Year-of-Plenty");
        setInformation("This development card allows you to gain 2 of a resource of your choice.");
     */


    // Attributes
    private String name;
    private String information;
    private boolean isPlayable;
    private Playable cardPlay;

    // Constructor
    public Card( Playable cardPlay, String name )
    {
        isPlayable = false;
        this.cardPlay = cardPlay;
        this.name = name;
    }

    // Functions
    public void play(){
        cardPlay.play();
    }

    /**
     * Gets the card's name.
     * @return card's name.
     */
    public String getName() {
        return name;
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
