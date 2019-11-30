public class Earthquake extends Card {
    // Properties

    // Constructor
    public Earthquake()
    {
        setName("Earthquake");
        setInformation("This special card allows you destroy a settlement belonged to another player");
    }

    // Methods
    /**
     * This function plays the Knight card, which allows player to select one enemy settlement and destroy it.
     */
    @Override
    public void play() {
        FlowManager.getInstance().addMust(10);
    }
}
