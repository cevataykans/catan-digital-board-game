package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.Response;
import GameFlow.TitleManager;
import SceneManagement.SoundManager;
import ServerCommunication.ServerHandler;

public class Knight extends Card implements Playable{
    // Properties

    // Constructor
    public Knight()
    {
        setName("knight");
        setInformation("This development card allows you to change the position of the robber and steal" +
                " a card from a neighboring player.");
    }

    // Methods

    /**
     * This function plays the DevelopmentCards.Knight card, which allows player to move the robber and steal one resource from
     * the neighboring players to the robber's hexagon.
     */
    @Override
    public void play() {
        FlowManager flowManager = new FlowManager();
        TitleManager titleManager = new TitleManager();

        flowManager.getCurrentPlayer().incrementLargestArmy();
        titleManager.updateLargestArmy();
        if ( ServerHandler.getInstance().getStatus() != ServerHandler.Status.RECEIVER) {
            flowManager.addMust(Response.MUST_INSIDE_TILE_SELECTION);
            flowManager.addMust(Response.MUST_GET_NEIGHBOR);
        }
        SoundManager.getInstance().playEffect(SoundManager.Effect.KNIGHT);

    }
}
