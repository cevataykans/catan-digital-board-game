package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.Response;
import Player.Player;
import SceneManagement.SoundManager;
import ServerCommunication.ServerHandler;

public class ChangeOfFortune implements Playable {
    // Properties

    // Constructor
    public ChangeOfFortune()
    {
    }

    // Methods
    /**
     * This function plays the Change of Fortune card, which allows player to re-roll the dice after its initially rolled.
     */
    @Override
    public void play() {
        FlowManager flowManager = new FlowManager();

        if (ServerHandler.getInstance().getStatus() != ServerHandler.Status.RECEIVER) {
            flowManager.addMust(Response.MUST_ROLL_DICE);
        }
        SoundManager.getInstance().playEffect(SoundManager.Effect.CHANGE_OF_FORTUNE);
    }
}
