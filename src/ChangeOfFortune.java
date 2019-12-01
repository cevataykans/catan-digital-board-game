public class ChangeOfFortune extends Card {
    // Properties

    // Constructor
    public ChangeOfFortune()
    {
        setName("Change Of Fortune");
        setInformation("This special card allows you to re-roll the dice after it is rolled, if you did not like the" +
                " previous results");
    }

    // Methods
    /**
     * This function plays the Change of Fortune card, which allows player to re-roll the dice after its initially rolled.
     */
    @Override
    public void play() {
        getFlowManager().addMust(7);
    }
}
