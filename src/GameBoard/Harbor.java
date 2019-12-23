package GameBoard;

/**
 * The port class that represents the ports that are using to make trades
 * @author Hakan Sivuk
 * @version 23.10.2019
 * Class is created and functions are implemented.
 */

public class Harbor{

	public enum HarborType{
	    THREE_TO_ONE, TWO_TO_ONE_LUMBER, TWO_TO_ONE_WOOL, TWO_TO_ONE_GRAIN, TWO_TO_ONE_BRICK, TWO_TO_ONE_ORE
    }

    // Attributes
    private HarborType harborType;

    // Constructor
    public Harbor( HarborType harborType){
	    this.harborType = harborType;
    }

    // Methods
    /**
     * Returns the port type.
     * @return the type of the port.
     */
    public HarborType getHarborType(){
	    return this.harborType;
    }
}
