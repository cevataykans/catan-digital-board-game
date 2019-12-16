package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.ResourceManager;
import Player.Player;
import SceneManagement.SoundManager;

import java.util.ArrayList;
import java.util.concurrent.Flow;

public class PerfectlyBalanced extends Card {
    // Properties

    // Constructor
    public PerfectlyBalanced()
    {
        setName("knight");
        setInformation("This special card discards half of ALL players' resources.");
    }

    // Methods
    /**
     * This function plays the PerfectlyBalanced card, which discards half of all resources from all players.
     */
    @Override
    public void play() {
        ResourceManager resourceManager = new ResourceManager();

        resourceManager.discardHalfOfResourcesWithoutCondition();
        SoundManager.getInstance().playEffect(SoundManager.Effect.PERFECTLY_BALANCED);
    }
}
