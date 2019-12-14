"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class GameDistributor {
    listenEvents(client) {
        client.on('event', data => {
            console.log("event by" + client.id);
            // validation
            // call related function
        });
    }
}
exports.GameDistributor = GameDistributor;
//# sourceMappingURL=GameDistributor.js.map