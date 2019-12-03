package DevelopmentCards;

public class PerfectlyBalanced extends Card {
    // Properties

    // Constructor
    public PerfectlyBalanced()
    {
        setName("Perfectly Balanced");
        setInformation("This special card discards half of ALL players' resources.");
    }

    // Methods
    /**
     * This function plays the DevelopmentCards.PerfectlyBalanced card, which discards half of all resources from all players.
     */
    @Override
    public void play() {
        getFlowManager().addMust(9);
    }
}
