package DevelopmentCards;

import GameFlow.Game;

public class VictoryPoint extends Card {
    // Properties

    // Constructor
    public VictoryPoint()
    {
        setName("Victory Point");
        setInformation("This special card increases your score by 1 point.");
    }

    // Methods
    /**
     * This function plays the DevelopmentCards.VictoryPoint card, which gives the current player 1 point.
     */
    @Override
    public void play() {

        Game.getInstance().getCurrentPlayer().increaseScore( 1);
    }
}
