public class Knight extends Card{
    // Properties

    // Constructor
    public Knight()
    {
        setName("Knight");
        setInformation("This special card allows you to change the position of the robber and steal" +
                " a card from a neighboring player.");
    }

    // Methods

    /**
     * This function plays the Knight card, which allows player to move the robber and steal one resource from
     * the neighboring players to the robber's hexagon.
     */
    @Override
    public void play() {
        FlowManager.getInstance().addMust(3);
        FlowManager.getInstance().addMust(8);
    }
}
