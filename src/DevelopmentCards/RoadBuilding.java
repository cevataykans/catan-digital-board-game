package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.ResourceManager;
import GameFlow.Response;
import SceneManagement.SoundManager;
import ServerCommunication.ServerHandler;

public class RoadBuilding implements Playable {
    // Properties

    // Constructor
    public RoadBuilding()
    {
    }

    // Methods
    /**
     * This function plays the DevelopmentCards.RoadBuilding card, which allows player to build 2 roads freely.
     */
    @Override
    public void play() {
        FlowManager flowManager = new FlowManager();

        // Add 2 road resources to the player.
        flowManager.getCurrentPlayer().collectMaterial(ResourceManager.LUMBER, 2);
        flowManager.getCurrentPlayer().collectMaterial(ResourceManager.BRICK, 2);

        if (ServerHandler.getInstance().getStatus() != ServerHandler.Status.RECEIVER) {
            // Make the player build 2 roads, give musts.
            flowManager.addMust(Response.MUST_ROAD_BUILD);
            flowManager.addMust(Response.MUST_ROAD_BUILD);
        }

        SoundManager.getInstance().playEffect(SoundManager.Effect.ROAD_BUILD);
    }
}
