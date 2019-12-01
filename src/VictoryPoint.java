import java.util.concurrent.Flow;

public class VictoryPoint extends Card {
    // Properties
    FlowManager flowManager;

    // Constructor
    public VictoryPoint()
    {
        flowManager = FlowManager.getInstance();
        setName("Victory Point");
        setInformation("This special card increases your score by 1 point.");
    }

    // Methods
    /**
     * This function plays the VictoryPoint card, which gives the current player 1 point.
     */
    @Override
    public void play() {
        flowManager.addMust(10);
    }
}
