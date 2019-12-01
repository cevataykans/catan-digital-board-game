import java.util.LinkedList;
import java.util.Queue;

/**
 * Flow manager is used to control required moves for players.
 * Sigleton class
 * @author Yusuf Nevzat Şengün
 * @version 27.11.2019
 */

public class FlowManager {
    //Singleton instance
    private static FlowManager flowManager = null;
    //properties
    private Queue<Integer> must;

    //constructor
    private FlowManager(){
        must = new LinkedList<>();
    }

    /**
     * returns the singleton instance
     * @return the singleton instance
     */
    public static FlowManager getInstance(){
        if( flowManager == null )
            flowManager = new FlowManager();
        return flowManager;
    }

    /**
     * adds a new must
     * @param m must type
     */
    public void addMust( int m){
        must.add( m);
    }

    /**
     * return if there is any must operation for this player and its type
     * @return -1 = there is no must
     *          0 = road need to be built
     *          1 = settlement need to be built
     *          2 = city need to be built
     *          3 = inside tile selection
     *          4 = resource selection (for monopoly card)
     *          5 = resource selection (for year of plenty card)
     *          6 = end turn
     *          7 = roll dice
     *          8 = get neighbor players ( after robber is placed )
     *          9 = get half resources from all players (for perfectly balanced card)
     *          10 = player gets a point (for victory point card)
     */
    public int checkMust(){
        if( must.size() == 0 )
            return -1;
        return must.peek();
    }

    /**
     * last-must has been completed
     */
    public void doneMust(){
        must.remove();
    }

}
