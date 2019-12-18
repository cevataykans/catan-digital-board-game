import {Game} from "./Game";
import {GameQueue} from "./GameQueue";
import { UserDBService } from "../User/UserDBService";

export class GameEventController{

    private gameQueue: GameQueue; 
    private games; // game id to game
    private players; // socket id to game id
    private userIds; // socket id to user id
    private freeGameIds: number[]; // free game list
    private userDBService: UserDBService;

    constructor(){
        this.userDBService = new UserDBService();
        this.gameQueue = new GameQueue();
        this.games = {};
        this.players = {};
        this.userIds = {};
        this.freeGameIds = [];
        for(let i = 0 ; i < 1000; i++){
            this.freeGameIds.push(i);
        }
    }

    public setNewGame(socketIds: string[]): number{
        if(this.freeGameIds.length == 0)
            return -1;
        let newGameId: number = this.freeGameIds.pop();
        let newGame: Game = new Game(socketIds, newGameId);
        this.games[newGameId] = newGame;
        socketIds.forEach((item) => {
            this.players[item] = newGameId;
        });
        return newGameId;
    }

    public saveUserId(client, data){
        if(data == null || data.userId == null)
            return;
        this.userIds[client.id] = data.userId;
        console.log("saved");
    }

    public async disconnectPlayer(socket, client): Promise<void> {
        const user = {
            userId: this.userIds[client.id],
            password: ""
        };
        delete this.userIds[client.id];
        console.log(user + " has disconnected!");
        await this.userDBService.logout(user);
        // If diconnected player was waiting for a game, discard from the queue
        let result = this.gameQueue.deletePlayerFromQueue(client.id);
        if(result){
            this.updateWaitingPlayers(socket);
            return;
        }
        // If disconnected player was playing a game, finish the game
        const gameId = this.players[client.id];
        if(gameId == null) // Was not playing a game
            return;
        // Was playing a game
        const players = this.finishGame(client, gameId);
        players.forEach((item) => {
            socket.to(item).emit("disconnect-response", {"message": "Player has disconnected"});
            delete this.players[item];
        });
        delete this.players[client.id];
    }

    public requestUserId(socket, client){
        console.log("request");
        socket.to(client.id).emit("userId-request");
    }

    public finish(socket, client): void {
        // Message is well formed
        const gameId: number = this.players[client.id];
        if(gameId == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        this.finishGame(client, gameId);
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        otherPlayers.forEach((item) => {
            socket.to(item).emit("finish-game-response");
        });
    }

    public finishGame(client, gameId: number){
        let game: Game = this.games[gameId]; // find game
        if(game == null)
            return null;
        const players = this.games[gameId].getPlayersSockets(client.id);
        delete this.games[gameId];
        this.freeGameIds.push(gameId);
        return players;
    }

    private shuffle(array){
        let counter = array.length;
    
        // While there are elements in the array
        while (counter > 0) {
            // Pick a random index
            let index = Math.floor(Math.random() * counter);
    
            // Decrease counter by 1
            counter--;
    
            // And swap the last element with it
            let temp = array[counter];
            array[counter] = array[index];
            array[index] = temp;
        }
    
        return array;
    }

    public findOtherPlayers(socketId: string): string[] {
        let gameId: string = this.players[socketId]; // find gameId
        let game: Game = this.games[gameId]; // find game
        if(game == null)
            return null;
        return game.getPlayersSockets(socketId);
    }

    public gameRequest(socket, client, data): void {
        const format: boolean = data != null && data.userId != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        const result: any[] = this.gameQueue.addPlayer(client.id, data.userId);
        if(result == null){
            this.updateWaitingPlayers(socket);
        }
        else{ // It means gameQueue.addPlayer returns players for a game.
            // shuffle the players
            const shuffledPlayers = this.shuffle(result);
            let playerIds: string[] = [];
            let socketIds: string[] = [];
            for(let i = 0 ; i < shuffledPlayers.length ; i++){
                playerIds.push(shuffledPlayers[i].userId);
                socketIds.push(shuffledPlayers[i].socketId);
                socket.to(socketIds[i]).emit("found-player-response", {"number": 4});
            }
            const gameId: number = this.setNewGame(socketIds);
            if(gameId < 0){
                socket.emit("games-full-response", {"message": "No avaliable game room!"});
                return;
            }

            // There is avaliable room for that game

            // Shuffle dice numbers, resources, ports
            let diceNumbers = [2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12];
            diceNumbers = this.shuffle(diceNumbers);
            let resources = [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4];
            resources = this.shuffle(resources);
            let ports = [0, 0, 0, 0, 1, 2, 3, 4, 5];
            ports = this.shuffle(ports);
            const cardNumbers = [14, 2, 2, 2, 5];
            let cards = []
            for(let i = 0 ; i < 25; i++){
                for(let j = 0; j < cardNumbers[i]; j++){
                    cards.push(i);
                }
            }
            cards = this.shuffle(cards);
            const data = {
                "diceNumbers" : diceNumbers,
                "resources": resources,
                "ports": ports,
                "players": playerIds,
                "cards": cards,
                "gameId": gameId
            };

            console.log("Last sockets: " + socketIds);
            socketIds.forEach((item) => { // for each players
                console.log(data + " to " + item);
                socket.to(item).emit("game-request-response", data); // send message for starting the  game
            });
        }
    }

    private updateWaitingPlayers(socket): void {
        let waitingPlayers: string[] = this.gameQueue.getWaitingPlayers();
        const data = {
            "number": waitingPlayers.length
        }
        waitingPlayers.forEach((item) => {
            socket.to(item).emit("found-player-response", data);
        })
    }

    public rollDice(socket, client, data): void {
        const result: boolean = data != null && data.firstDice != null && data.secondDice != null; // validation check
        if(!result){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        
        otherPlayers.forEach((item) => {
            socket.to(item).emit("roll-dice-response", data);
        });
        
    }

    public sendCard(socket, client, data): void {
        const result: boolean = data != null && data.cardName != null; // validation check
        if(!result){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        
        otherPlayers.forEach((item) => {
            socket.to(item).emit("send-card-response", data);
        });
        
    }

    public playCard(socket, client, data): void {
        const result: boolean = data != null && data.cardName != null && data.cardIndex != null; // validation check
        if(!result){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        
        otherPlayers.forEach((item) => {
            socket.to(item).emit("play-card-response", data);
        });
        
    }

    public sendMonopoly(socket, client, data): void {
        const result: boolean = data != null && data.material != null; // validation check
        if(!result){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        
        otherPlayers.forEach((item) => {
            socket.to(item).emit("send-monopoly-response", data);
        });
        
    }

    public sendYearOfPlenty(socket, client, data): void {
        const result: boolean = data != null && data.material != null; // validation check
        if(!result){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        
        otherPlayers.forEach((item) => {
            socket.to(item).emit("send-plenty-response", data);
        });
        
    }

    public sendPerfectlyBalanced(socket, client, data): void {
        const result: boolean = data != null && data.indexes != null; // validation check
        if(!result){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        
        otherPlayers.forEach((item) => {
            socket.to(item).emit("send-balanced-response", data);
        });
        
    }

    public buildSettlement(socket, client, data): void {
        const format: boolean = data != null && data.x != null && data.y != null && data.hexIndex != null && data.tileIndex != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            console.log("FORMAT ERROR");
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            console.log("player ERROR");
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }

        otherPlayers.forEach((item) => {
            socket.to(item).emit("build-settlement-response", data);
        });
    }

    public buildCity(socket, client, data): void {
        const format: boolean = data != null && data.x != null && data.y != null && data.hexIndex != null && data.tileIndex != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }

        otherPlayers.forEach((item) => {
            socket.to(item).emit("build-city-response", data);
        });
    }

    public buildRoad(socket, client, data): void {
        const format: boolean = data != null && data.x != null && data.y != null && data.hexIndex != null && data.tileIndex != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }

        otherPlayers.forEach((item) => {
            socket.to(item).emit("build-road-response", data);
        });
    }

    public setupRobber(socket, client, data): void {
        const format: boolean = data != null && data.mouseX != null && data.mouseY != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        otherPlayers.forEach((item) => {
            socket.to(item).emit("setup-robber-response", data);
        });
    }

    public selectResource(socket, client, data): void {
        const format: boolean = data != null && data.resource != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        const newData = {
            "resource": data.resource
        };
        otherPlayers.forEach((item) => {
            socket.to(item).emit("select-resource-response", newData);
        });
    }

    public selectPlayer(socket, client, data): void {
        const format: boolean = data != null && data.player != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        otherPlayers.forEach((item) => {
            socket.to(item).emit("select-player-response", data);
        });
    }

    public sendTrade(socket, client, data): void {
        const format: boolean = data != null && data.toGive != null && data.toTake != null && data.otherPlayer != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        otherPlayers.forEach((item) => {
            socket.to(item).emit("send-trade-response", data);
        });
    }

    public confirmTrade(socket, client, data): void {
        const format: boolean = data != null && data.toGive != null && data.toTake != null && data.otherPlayer != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        otherPlayers.forEach((item) => {
            socket.to(item).emit("confirm-trade-response", data);
        });
    }

    public refuseTrade(socket, client, data): void {
        const format: boolean = data != null && data.otherPlayer != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        otherPlayers.forEach((item) => {
            socket.to(item).emit("refuse-trade-response", data);
        });
    }

    public harborTrade(socket, client, data): void {
        const format: boolean = data != null && data.harborType != null && data.giveResIndex != null && data.takeResIndex != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        otherPlayers.forEach((item) => {
            socket.to(item).emit("harbor-trade-response", data);
        });
    }

    public refreshInfos(socket, client): void {
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        // There is game
        otherPlayers.forEach((item) => {
            socket.to(item).emit("refresh-infos-response");
        });
    }
    
    public endTurn(socket, client): void {
        // Message is well formed
        const others: string[] = this.findOtherPlayers(client.id);
        if(others == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        const game: Game = this.games[this.players[client.id]];
        game.endTurn();
        const currentPlayer = game.getCurrentPlayer();
        const allPlayers = game.getAllPlayers();
        // There is game
        allPlayers.forEach((item) => { 
            if(item == currentPlayer){
                socket.to(item).emit("end-turn-response", {"status": 1});
            }
            else{
                socket.to(item).emit("end-turn-response", {"status": 0});
            }
        });
    }

    public sendMessage(socket, client, data): void {
        const format: boolean = data != null && data.userId != null && data.message != null; // validation check
        if(!format){ // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", {"message": "Wrong format"}); 
            return;
        }
        // Message is well formed
        const otherPlayers: string[] = this.findOtherPlayers(client.id);
        if(otherPlayers == null){
            client.emit("no-game-error", {"message": "You are not in a game"});
            return;
        }
        otherPlayers.forEach((item) => {
            socket.to(item).emit("send-message-response", data);
        });
    }
}