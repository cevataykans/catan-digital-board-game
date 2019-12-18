import {GameEventController} from "./GameEventController";
import { TokenService } from "../User/TokenService";

export class GameEventListener{

    private eventController: GameEventController;
    private tokenService: TokenService;

    constructor(){
        this.eventController = new GameEventController();
        this.tokenService = new TokenService();
    }

    public listenEvents(socket, client): void {
        client.on('game-request', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.gameRequest(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }

        });

        client.on('roll-dice', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.rollDice(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('build-settlement', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.buildSettlement(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        })

        client.on('build-city', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.buildCity(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('build-road', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.buildRoad(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('setup-robber', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.setupRobber(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('send-card', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.sendCard(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('play-card', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.playCard(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('send-monopoly', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.sendMonopoly(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('send-plenty', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.sendYearOfPlenty(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('send-balanced', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.sendPerfectlyBalanced(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('select-resource', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.selectResource(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('select-player', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.selectPlayer(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('send-trade', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.sendTrade(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('confirm-trade', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.confirmTrade(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('refuse-trade', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.refuseTrade(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('harbor-trade', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.harborTrade(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('refresh-infos', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.refreshInfos(socket, client);
            }
            catch(error){
                console.log("Authentication Error");
            }
        });

        client.on('end-turn', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.endTurn(socket, client);
            }
            catch(error){
                console.log("Authentication Error");
            }
        })

        client.on('send-message', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.sendMessage(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        })

        client.on('disconnect', data => {
            this.eventController.disconnectPlayer(socket, client);
        })

        client.on('finish', data => {
            try{
                this.tokenService.checkTokenForGame(data);
                this.eventController.finish(socket, client, data);
            }
            catch(error){
                console.log("Authentication Error");
            }
        })
    }
}