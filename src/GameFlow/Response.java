package GameFlow;
/*
        *checktile
        *         0 = road can be built here
        *         1 = settlement can be built here
        *         2 = city can be built here
        *         3 = this tile is an inside tile
        *         4 = this tile is a sea tile
        *         errors
        *         -1 = there is no connection for road to build
        *         -2 = there is no connection for city to build
        *         -3 = there is a building near
        *         -4 = this tile is occupied by a road, city or other players structure, in this case there is no need to explain anything
        *         -5 = there is no enough resource for road
        *         -6 = there is no enough resource for settlement
        *         -7 = there is no enough resource for city
        *
        *must
        *         -1 = there is no must
        *          0 = road need to be built
        *          1 = settlement need to be built
        *          2 = city need to be built
        *          3 = inside tile selection
        *          4 = resource selection (for monopoly card)
        *          5 = resource selection (for year of plenty card)
        *          6 = end turn
        *          7 = roll dice
        *          8 = get neighbor players ( after robber is placed )
        *         --- 9 = get half resources from all players (for perfectly balanced card) *** can be implemented in card play function
        *         --- 10 = player gets a point (for victory point card) *** can be implemented in card play function
 */
public enum Response {
    INFORM_ROAD_CAN_BE_BUILT,                       // 0
    INFORM_SETTLEMENT_CAN_BE_BUILT,                 // 1
    INFORM_CITY_CAN_BE_BUILT,                       // 2
    INFORM_INSIDE_TILE,                             // 3
    INFORM_SEA_TILE,                                // 4
    ERROR_NO_CONNECTION_FOR_ROAD,                   // -1
    ERROR_NO_CONNECTION_FOR_SETTLEMENT,             // -2
    ERROR_THERE_IS_NEAR_BUILDING_FOR_SETTLEMENT,    // -3
    ERROR_OCCUPIED_BY,                              // -4
    ERROR_NO_RESOURCE_FOR_ROAD,                     // -5
    ERROR_NO_RESOURCE_FOR_SETTLEMENT,               // -6
    ERROR_NO_RESOURCE_FOR_CITY,                     // -7
    ERROR_OUTSIDE_GAMEBOARD,                        // -100
    MUST_EMPTY,                                     // -1
    MUST_ROAD_BUILD,                                // 0
    MUST_SETTLEMENT_BUILD,                          // 1
    MUST_CITY_BUILD,                                // 2
    MUST_INSIDE_TILE_SELECTION,                     // 3
    MUST_RESOURCE_SELECTION_MONOPOLY,               // 4
    MUST_RESOURCE_SELECTION_PLENTY,                 // 5
    MUST_END_TURN,                                  // 6
    MUST_ROLL_DICE,                                 // 7
    MUST_GET_NEIGHBOR,                              // 8
    MUST_GET_HALF_RESOURCE_PERFECT_BALANCE,         // 9
    MUST_PLAYER_GETS_POINT,                         // 10
    EKSISEKIZ,                                      // todo change here
    EKSIDOKUZ                                       // todo change here
}
