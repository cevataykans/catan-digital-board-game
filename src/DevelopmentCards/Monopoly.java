package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;

public class Monopoly extends Card {
    // Properties

    // Constructor
    public Monopoly()
    {
        setName("DevelopmentCards.Knight");
        setInformation("This special card allows you to steal ALL of any one resource of your" +
                " choice from all other players.");
    }

    // Methods
    /**
     * This function plays the DevelopmentCards.Monopoly card, which allows player steal ALL of any one resource of choice from all players.
     */
    @Override
    public void play() {
        FlowManager flowManager = new FlowManager();

        flowManager.addMust(4);
    }
}
