"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Game {
    constructor(socketIds, id) {
        this.turn = 0;
        this.players = [];
        socketIds.forEach((item) => {
            this.players.push(item);
        });
        this.gameId = id;
    }
    endTurn() {
        this.turn = (this.turn + 1) % 4;
    }
    isTurnOf(userId) {
        if (this.players[this.turn] == userId)
            return true;
        return false;
    }
    getId() {
        return this.gameId;
    }
    getPlayersSockets(socketId) {
        let result = [];
        let index = 0;
        this.players.forEach((item) => {
            if (item != socketId) {
                result[index] = item;
                index++;
            }
        });
        if (index == 4)
            return null;
        return result;
    }
    getAllPlayers() {
        return this.players;
    }
    getCurrentPlayer() {
        return this.players[this.turn];
    }
}
exports.Game = Game;
//# sourceMappingURL=Game.js.map