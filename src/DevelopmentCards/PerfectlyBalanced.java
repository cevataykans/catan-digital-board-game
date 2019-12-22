package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.ResourceManager;
import Player.Player;
import SceneManagement.SoundManager;
import ServerCommunication.ServerHandler;

import java.util.ArrayList;
import java.util.concurrent.Flow;

public class PerfectlyBalanced implements Playable {
    // Properties

    // Constructor
    public PerfectlyBalanced()
    {
    }

    // Methods
    /**
     * This function plays the PerfectlyBalanced card, which discards half of all resources from all players.
     */
    @Override
    public void play() {
        ResourceManager resourceManager = new ResourceManager();

        ArrayList<Integer> indexes = resourceManager.discardHalfOfResourcesWithoutCondition();
        if (ServerHandler.getInstance().getStatus() == ServerHandler.Status.SENDER) {
            ServerHandler.getInstance().sendPerfectlyBalanced(indexes);
        }
        SoundManager.getInstance().playEffect(SoundManager.Effect.PERFECTLY_BALANCED);
    }
}
