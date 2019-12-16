package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.Response;
import SceneManagement.SoundManager;

public class Knight extends Card{
    // Properties

    // Constructor
    public Knight()
    {
        setName("knight");
        setInformation("This special card allows you to change the position of the robber and steal" +
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

        flowManager.addMust(Response.MUST_INSIDE_TILE_SELECTION);
        flowManager.addMust(Response.MUST_GET_NEIGHBOR);
        SoundManager.getInstance().playEffect(SoundManager.Effect.KNIGHT);
    }
}
