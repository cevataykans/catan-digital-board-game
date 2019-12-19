import server from "./App";
import {GameEventListener} from "./Game/GameEventListener";
import { GameEventController } from "./Game/GameEventController";

class Socket{
    socket = null;
    gameEventListener: GameEventListener;
    gameEventController: GameEventController;

    constructor(){
        this.socket = require('socket.io')(server);
        this.gameEventListener = new GameEventListener();
        this.gameEventController = new GameEventController();
    }

    public listen(PORT: number): void {
        this.socket.on('connection', client => {
            console.log("connected");
            this.gameEventListener.listenEvents(this.socket, client);
        })

        server.listen(PORT, "139.179.200.68", () => {
            console.log("Server is ready on " + PORT);
        })
    }
}

export default new Socket();