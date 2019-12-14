"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const App_1 = require("./App");
const GameEventListener_1 = require("./Game/GameEventListener");
class Socket {
    constructor() {
        this.socket = null;
        this.socket = require('socket.io')(App_1.default);
        this.gameEventListener = new GameEventListener_1.GameEventListener();
    }
    listen(PORT) {
        this.socket.on('connection', client => {
            console.log('connected ' + client.id);
            this.gameEventListener.listenEvents(this.socket, client);
        });
        App_1.default.listen(PORT, () => {
            console.log("Server is ready on " + PORT);
        });
    }
}
exports.default = new Socket();
//# sourceMappingURL=Socket.js.map