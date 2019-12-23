package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.Response;
import SceneManagement.SoundManager;
import ServerCommunication.ServerHandler;

public class Monopoly implements Playable {
    // Properties

    // Constructor
    public Monopoly()
    {
    }

    // Methods
    /**
     * This function plays the DevelopmentCards.Monopoly card, which allows player steal ALL of any one resource of choice from all players.
     */
    @Override
    public void play() {
        FlowManager flowManager = new FlowManager();

        if (ServerHandler.getInstance().getStatus() != ServerHandler.Status.RECEIVER) {
            flowManager.addMust(Response.MUST_RESOURCE_SELECTION_MONOPOLY);
        }
        SoundManager.getInstance().playEffect(SoundManager.Effect.MONOPOLY);
    }
}
