"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class GameQueue {
    constructor() {
        this.NUMBER_OF_PLAYERS = 4;
        this.queue = [];
    }
    addPlayer(socketId, name) {
        if (this.queue.length < this.NUMBER_OF_PLAYERS - 1) {
            const player = {
                "userId": name,
                "socketId": socketId
            };
            this.queue.push(player);
            return null;
        }
        let result = [];
        for (let i = 0; i < this.NUMBER_OF_PLAYERS - 1; i++) {
            const player = this.queue.shift();
            result.push(player);
        }
        const newPlayer = {
            "userId": name,
            "socketId": socketId
        };
        result.push(newPlayer);
        return result;
    }
    deletePlayerFromQueue(socketId) {
        this.queue.forEach((item, index) => {
            if (item.socketId == socketId) {
                this.queue.slice(index, 1);
                return true;
            }
        });
        return false;
    }
}
exports.GameQueue = GameQueue;
//# sourceMappingURL=GameQueue.js.map