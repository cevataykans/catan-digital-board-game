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
        this.phase = 0;
        this.order = true;
    }
    endTurn() {
        if (this.phase == 0 && this.order) {
            if (this.turn == 3) {
                this.order = false;
                console.log("Again turn " + this.turn + "but order is " + this.order);
            }
            else {
                this.turn = (this.turn + 1) % 4;
                console.log("turn: " + this.turn + "order: " + this.order);
            }
        }
        else if (this.phase == 0) {
            if (this.turn == 0) {
                this.phase = 1;
                this.order = true;
                console.log("turn: " + this.turn + "order: " + this.order);
            }
            else {
                this.turn = (this.turn - 1) % 4;
                console.log("turn: " + this.turn + "order: " + this.order);
            }
        }
        else {
            this.turn = (this.turn + 1) % 4;
            console.log("turn: " + this.turn + "order: " + this.order);
        }
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