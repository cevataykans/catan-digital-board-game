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

        client.on('send-card', data => {
            this.eventController.sendCard(socket, client, data);
        });

        client.on('play-card', data => {
            this.eventController.playCard(socket, client, data);
        });

        client.on('send-monopoly', data => {
            this.eventController.sendMonopoly(socket, client, data);
        });

        client.on('send-plenty', data => {
            this.eventController.sendYearOfPlenty(socket, client, data);
        });

        client.on('send-balanced', data => {
            this.eventController.sendPerfectlyBalanced(socket, client, data);
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

        client.on('refuse-trade', data => {
            this.eventController.refuseTrade(socket, client, data);
        });

        client.on('harbor-trade', data => {
            this.eventController.harborTrade(socket, client, data);
        });

        client.on('refresh-infos', data => {
            this.eventController.refreshInfos(socket, client);
        });

        client.on('end-turn', data => {
            this.eventController.endTurn(socket, client);
        })

        client.on('send-message', data => {
            this.eventController.sendMessage(socket, client, data);
        })

        client.on('disconnect', data => {
            this.eventController.disconnectPlayer(socket, client);
        })

        client.on('finish', data => {
            this.eventController.finish(socket, client, data);
        })
    }
}