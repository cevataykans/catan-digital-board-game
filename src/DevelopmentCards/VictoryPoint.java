package DevelopmentCards;

import GameFlow.FlowManager;
import GameFlow.Game;
import SceneManagement.SoundManager;
import org.controlsfx.dialog.Wizard;

public class VictoryPoint extends Card {
    // Properties

    // Constructor
    public VictoryPoint()
    {
        setName("Victory-Point");
        setInformation("This special card increases your score by 1 point.");
    }

    // Methods
    /**
     * This function plays the DevelopmentCards.VictoryPoint card, which gives the current player 1 point.
     */
    @Override
    public void play() {
        FlowManager flowManager = new FlowManager();
        flowManager.getCurrentPlayer().increaseScore( 1);

        SoundManager.getInstance().playEffect(SoundManager.Effect.VICTORY_POINT);
    }
}
