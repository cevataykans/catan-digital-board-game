package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.Response;
import SceneManagement.SoundManager;

public class YearOfPlenty extends Card {
    // Properties

    // Constructor
    public YearOfPlenty()
    {
        setName("Year-of-Plenty");
        setInformation("This special card allows you to gain 2 resources of your choice from the bank");
    }

    // Methods
    /**
     * This function plays the DevelopmentCards.YearOfPlenty card, which allows player to gain 2 resources of their choice.
     */
    @Override
    public void play() {
        FlowManager flowManager = new FlowManager();

        flowManager.addMust(Response.MUST_RESOURCE_SELECTION_PLENTY);
        SoundManager.getInstance().playEffect(SoundManager.Effect.YEAR_OF_PLENTY);
    }
}
