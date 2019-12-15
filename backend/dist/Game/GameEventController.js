"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Game_1 = require("./Game");
const GameQueue_1 = require("./GameQueue");
class GameEventController {
    constructor() {
        this.gameQueue = new GameQueue_1.GameQueue();
        this.games = {};
        this.players = {};
        this.freeGameIds = [];
        for (let i = 0; i < 1000; i++) {
            this.freeGameIds.push(i);
        }
    }
    setNewGame(socketIds) {
        if (this.freeGameIds.length == 0)
            return -1;
        let newGameId = this.freeGameIds.pop();
        let newGame = new Game_1.Game(socketIds, newGameId);
        this.games[newGameId] = newGame;
        socketIds.forEach((item) => {
            this.players[item] = newGameId;
        });
        return newGameId;
    }
    finishGame(gameId) {
        this.freeGameIds.push(gameId);
    }
    shuffle(array) {
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
    findOtherPlayers(socketId) {
        let gameId = this.players[socketId]; // find gameId
        let game = this.games[gameId]; // find game
        if (game == null)
            return null;
        return game.getPlayersSockets(socketId);
    }
    disconnect(socket, client, data) {
        // If diconnected player was waiting for a game, discard from the queue
        let result = this.gameQueue.deletePlayerFromQueue(client.id);
        if (result)
            return;
        // If disconnected player was playing a game, finish the game
        const gameId = this.players[client.id];
        if (gameId == null) // Was not playing a game
            return;
        // Was playing a game
        this.finishGame(gameId);
        const players = this.games[gameId].getPlayersSockets(client.id);
        players.forEach((item) => {
            socket.to(item).emit("disconnect-response", { "message": "Player has disconnected" });
        });
    }
    gameRequest(socket, client, data) {
        const format = data != null && data.userId != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        const result = this.gameQueue.addPlayer(client.id, data.userId);
        if (result != null) { // It means gameQueue.addPlayer returns players for a game.
            // shuffle the players
            const shuffledPlayers = this.shuffle(result);
            let playerIds = [];
            let socketIds = [];
            for (let i = 0; i < shuffledPlayers.length; i++) {
                playerIds.push(shuffledPlayers[i].userId);
                socketIds.push(shuffledPlayers[i].socketId);
            }
            const gameId = this.setNewGame(socketIds);
            if (gameId < 0) {
                socket.emit("games-full-response", { "message": "No avaliable game room!" });
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
            let cards = [];
            for (let i = 0; i < 25; i++) {
                for (let j = 0; j < cardNumbers[i]; j++) {
                    cards.push(i);
                }
            }
            cards = this.shuffle(cards);
            const data = {
                "diceNumbers": diceNumbers,
                "resources": resources,
                "ports": ports,
                "players": playerIds,
                "cards": cards,
                "gameId": gameId
            };
            console.log("Last sockets: " + socketIds);
            socketIds.forEach((item) => {
                console.log(data + " to " + item);
                socket.to(item).emit("game-request-response", data); // send message for starting the  game
            });
        }
        console.log('gamerequest done');
    }
    rollDice(socket, client, data) {
        const result = data != null && data.firstDice != null && data.secondDice != null; // validation check
        if (!result) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        otherPlayers.forEach((item) => {
            socket.to(item).emit("roll-dice-response", data);
        });
    }
    buildSettlement(socket, client, data) {
        const format = data != null && data.x != null && data.y != null && data.hexIndex != null && data.tileIndex != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            console.log("FORMAT ERROR");
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            console.log("player ERROR");
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        otherPlayers.forEach((item) => {
            socket.to(item).emit("build-settlement-response", data);
        });
    }
    buildCity(socket, client, data) {
        const format = data != null && data.x != null && data.y != null && data.hexIndex != null && data.tileIndex != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        otherPlayers.forEach((item) => {
            socket.to(item).emit("build-city-response", data);
        });
    }
    buildRoad(socket, client, data) {
        const format = data != null && data.x != null && data.y != null && data.hexIndex != null && data.tileIndex != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        otherPlayers.forEach((item) => {
            socket.to(item).emit("build-road-response", data);
        });
    }
    setupRobber(socket, client, data) {
        const format = data != null && data.x != null && data.y != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        // There is game
        const newData = {
            "x": data.x,
            "y": data.y
        };
        otherPlayers.forEach((item) => {
            socket.to(item).emit("setup-robber-response", newData);
        });
    }
    selectResource(socket, client, data) {
        const format = data != null && data.resource != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
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
    selectPlayer(socket, client, data) {
        const format = data != null && data.player != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        // There is game
        const newData = {
            "player": data.player
        };
        otherPlayers.forEach((item) => {
            socket.to(item).emit("select-player-response", newData);
        });
    }
    endTurn(socket, client) {
        // Message is well formed
        const others = this.findOtherPlayers(client.id);
        if (others == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        const game = this.games[this.players[client.id]];
        game.endTurn();
        const currentPlayer = game.getCurrentPlayer();
        const allPlayers = game.getAllPlayers();
        // There is game
        allPlayers.forEach((item) => {
            if (item == currentPlayer) {
                socket.to(item).emit("end-turn-response", { "status": 1 });
            }
            else {
                socket.to(item).emit("end-turn-response", { "status": 0 });
            }
        });
    }
    sendMessage(socket, client, data) {
        const format = data != null && data.userId != null && data.message != null; // validation check
        if (!format) { // Message received by the server is not well formed!!!
            client.emit("invalid-information-error", { "message": "Wrong format" });
            return;
        }
        // Message is well formed
        const otherPlayers = this.findOtherPlayers(client.id);
        if (otherPlayers == null) {
            client.emit("no-game-error", { "message": "You are not in a game" });
            return;
        }
        otherPlayers.forEach((item) => {
            socket.to(item).emit("send-message-response", data);
        });
    }
}
exports.GameEventController = GameEventController;
//# sourceMappingURL=GameEventController.js.map