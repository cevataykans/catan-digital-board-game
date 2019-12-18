import server from "./App";
import {GameEventListener} from "./Game/GameEventListener";

class Socket{
    socket = null;
    gameEventListener: GameEventListener;

    constructor(){
        this.socket = require('socket.io')(server);
        this.gameEventListener = new GameEventListener();
    }

    public listen(PORT: number): void {
        this.socket.on('connection', client => {
            console.log('connected ' + client.id);
            this.gameEventListener.listenEvents(this.socket, client);
        })

        server.listen(PORT, "139.179.103.162", () => {
            console.log("Server is ready on " + PORT);
        })
    }
}

export default new Socket();