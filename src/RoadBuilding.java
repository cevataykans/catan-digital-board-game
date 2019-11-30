public class RoadBuilding extends Card {
    // Properties

    // Constructor
    public RoadBuilding()
    {
        setName("Road Building");
        setInformation("This special card allows you to build 2 roads freely.");
    }

    // Methods
    /**
     * This function plays the RoadBuilding card, which allows player to build 2 roads freely.
     */
    @Override
    public void play() {
        FlowManager.getInstance().addMust(0);
        FlowManager.getInstance().addMust(0);
    }
}
