/**
 * The port class that represents the ports that are using to make trades
 * @author Hakan Sivuk
 * @version 23.10.2019
 * Class is created and functions are implemented.
 */

public class PORT{

	enum PortTYPE{
	    THREE_TO_ONE, TWO_TO_ONE_LUMBER, TWO_TO_ONE_WOOL, TWO_TO_ONE_GRAIN, TWO_TO_ONE_BRICK, TWO_TO_ONE_ORE
    }

    // Attributes
    private PortType portType;

    // Constructor
    public Port(PortType portType){
	    this.portType = portType;
    }

    // Methods
    /**
     * Returns the port type.
     * @return the type of the port.
     */
    public PortType getPortType(){
	    return this.portType;
    }
}
