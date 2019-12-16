import {GameEventController} from "./GameEventController";

export class GameEventListener{

    private eventController: GameEventController;

    constructor(){
        this.eventController = new GameEventController();
    }

    public listenEvents(socket, client): void {
        client.on('game-request', data => {
            this.eventController.gameRequest(socket, client, data);
        });

        client.on('roll-dice', data => {
            this.eventController.rollDice(socket, client, data);
        });

        client.on('build-settlement', data => {
            this.eventController.buildSettlement(socket, client, data);
        })

        client.on('build-city', data => {
            this.eventController.buildCity(socket, client, data);
        });

        client.on('build-road', data => {
            this.eventController.buildRoad(socket, client, data);
        });

        client.on('setup-robber', data => {
            this.eventController.setupRobber(socket, client, data);
        });

        client.on('select-resource', data => {
            this.eventController.selectResource(socket, client, data);
        });

        client.on('select-player', data => {
            this.eventController.selectPlayer(socket, client, data);
        });
        client.on('send-trade', data => {
            this.eventController.sendTrade(socket, client, data);
        });
        client.on('confirm-trade', data => {
            this.eventController.confirmTrade(socket, client, data);
        });
        client.on('harbor-trade', data => {
            this.eventController.harborTrade(socket, client, data);
        });
        client.on('end-turn', data => {
            this.eventController.endTurn(socket, client);
        })

        client.on('send-message', data => {
            this.eventController.sendMessage(socket, client, data);
        })

        client.on('disconnect', data => {
            console.log('disconnected' + client.id);
        })
    }
}