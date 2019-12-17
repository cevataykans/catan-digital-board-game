package ServerCommunication;

import DevelopmentCards.*;
import GameFlow.CardManager;
import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.ResourceManager;
import Player.Player;
import SceneManagement.GameEngine;
import SceneManagement.MultiGameController;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ServerHandler {
    public enum Status{
        RECEIVER, SENDER
    }
    private final String ADDRESS = "http://139.179.210.161:3000";
    private final OkHttpClient httpClient = new OkHttpClient();

    public static ServerHandler serverHandler;

    // Properties
    private Status status;
    private Socket socket;
    private MultiGameController controller;
    private int gameId;
    private String userId;

    // Constructor
    private ServerHandler(){
        this.status = null;
        this.socket = null;
    }

    public static ServerHandler getInstance(){
        if(serverHandler == null)
            serverHandler = new ServerHandler();
        return serverHandler;
    }

    // Methods
    public boolean login(String userId, String password){
        String[] names = {"userId", "password"};
        String[] keys = new String[2];
        keys[0] = userId;
        keys[1] = password;
        boolean result = sendPost(names, keys, "/api/user/login");
        if ( result == true)
        {
            this.userId = userId;
        }
        return result;
    }


    public boolean register(String userId, String password){
        String[] names = {"userId", "password"};
        String[] keys = new String[2];
        keys[0] = userId;
        keys[1] = password;
        return sendPost(names, keys, "/api/user/register");
    }

    public boolean changePassword(String userId, String oldPsw, String newPsw){
        String[] names = {"userId", "oldPassword", "newPassword"};
        String[] keys = new String[3];
        keys[0] = userId;
        keys[1] = oldPsw;
        keys[2] = newPsw;
        return sendPost(names, keys, "/api/user/changePassword");
    }

    private void connect() throws URISyntaxException{
        this.socket = IO.socket(ADDRESS);
        this.socket.connect();
        listenEvents();
    }

    public void listenEvents() {
        this.socket.on("game-request-response", new Emitter.Listener() { // Start message from the server
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
                try {
                    GameEngine.getInstance().setController(7);
                    controller = (MultiGameController) GameEngine.getInstance().getController();
                    gameId = (int) obj.get("gameId");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("build-settlement-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
                try {
                    int x = obj.getInt("x");
                    int y = obj.getInt("y");

                    controller.receiveBuildSettlement(x, y);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("build-city-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
                try {
                    int x = obj.getInt("x");
                    int y = obj.getInt("y");

                    controller.receiveBuildCity(x, y);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("build-road-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
                try {
                    int x = obj.getInt("x");
                    int y = obj.getInt("y");

                    controller.receiveBuildRoad(x, y);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("setup-robber-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                try{
                    double mouseX = obj.getDouble("mouseX");
                    double mouseY = obj.getDouble("mouseY");
                    controller.changeRobber(mouseX, mouseY);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("roll-dice-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                controller.getDiceController().rollDice();
            }
        });
        this.socket.on("end-turn-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                if(getStatus() != Status.SENDER){
                    controller.performEndTurnButtonEvent();
                }
                try{
                    int status = obj.getInt("status");
                    System.out.println("Status: " + status);
                    if(status == 0)
                        setStatus(Status.RECEIVER);
                    else
                        setStatus(Status.SENDER);
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
        this.socket.on("send-card-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                try{
                    String cardName = obj.getString("cardName");
                    FlowManager flowManager = new FlowManager();
                    System.out.println("Receiver Card Bought");
                    switch (cardName)
                    {
                        case "knight":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Knight());
                            break;
                        case "monopoly":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Monopoly());
                            break;
                        case "Road-Building":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new RoadBuilding());
                            break;
                        case "Victory-Point":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new VictoryPoint());
                            break;
                        case "Year-of-Plenty":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new YearOfPlenty());
                            break;
                        case "fortune":
                            break;
                        case "balanced":
                            break;
                    }
                    Game.getInstance().getCardStack().pop();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("play-card-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                try{
                    String cardName = obj.getString("cardName");
                    Integer cardIndex = obj.getInt("cardIndex");
                    FlowManager flowManager = new FlowManager();
                    System.out.println("Receiver Card");
                    switch (cardName)
                    {
                        case "knight":
                            new Knight().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                        case "monopoly":
                            new Monopoly().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                        case "Road-Building":
                            new RoadBuilding().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                        case "Victory-Point":
                            new VictoryPoint().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                        case "Year-of-Plenty":
                            new YearOfPlenty().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                        case "fortune":
                            break;
                        case "balanced":
                            break;
                    }
                    Game.getInstance().getCardStack().pop();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("send-monopoly-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                try{
                    CardManager cardManager = new CardManager();
                    int material = obj.getInt("material");
                    cardManager.playMonopoly(material);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("send-plenty-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                try{
                    CardManager cardManager = new CardManager();
                    int material = obj.getInt("material");
                    cardManager.playYearOfPlenty(material);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("send-balanced-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                try{
                    ResourceManager resourceManager = new ResourceManager();
                    resourceManager.discardHalfOfResourcesWithoutCondition();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("select-player-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                try{
                    String playerName = obj.getString("player");
                    controller.selectPlayer(playerName);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        this.socket.on("select-resource-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("send-trade-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
                System.out.println("received");
                controller.getInfoController().receiveTradeOffer();
            }
        });
        this.socket.on("confirm-trade-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
                System.out.println("confirmed");
                controller.getInfoController().confirmTrade();
            }
        });
        this.socket.on("harbor-trade-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                controller.getHarborController().receiveHarborTradeMessage();
            }
        });
        this.socket.on("refresh-infos-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                // Call related controller method
                controller.getInfoController().setupCurrentPlayer();
                controller.getInfoController().setupOtherPlayers();
            }
        });
        this.socket.on("play-card-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("send-message-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                JSONObject obj = (JSONObject) objects[0];
                // Make related things
                try {
                    String userId = obj.getString("userId");
                    String message = obj.getString("message");

                    controller.getChatController().getChatMessage(userId, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        this.socket.on("turn-error-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                // Print error!!!
                System.out.println("TURN ERROR");
            }
        });
        this.socket.on("validation-error-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                // Print error!!!
                System.out.println("VALIDATION ERROR");
            }
        });
        this.socket.on("games-full-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                // Print error!!!
                System.out.println("GAMES FULL ERROR");
            }
        });

        this.socket.on("disconnect-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                // Finish the game and return to matchmaking screen
            }
        })
    }


    public void gameRequest() throws URISyntaxException {
        this.connect();
        String[] names = {"userId"};
        String[] keys = new String[1];
        keys[0] = this.userId;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("game-request", data);
    }

    public void buildSettlement(int x, int y, int hexIndex, int tileIndex){
        String[] names = {"x", "y", "hexIndex", "tileIndex"};
        Integer[] keys = new Integer[4];
        keys[0] = x;
        keys[1] = y;
        keys[2] = hexIndex;
        keys[3] = tileIndex;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("build-settlement", data);
    }

    public void buildRoad(int x, int y, int hexIndex, int tileIndex){
        String[] names = {"x", "y", "hexIndex", "tileIndex"};
        Integer[] keys = new Integer[4];
        keys[0] = x;
        keys[1] = y;
        keys[2] = hexIndex;
        keys[3] = tileIndex;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("build-road", data);
    }

    public void buildCity(int x, int y, int hexIndex, int tileIndex){
        String[] names = {"x", "y", "hexIndex", "tileIndex"};
        Integer[] keys = new Integer[4];
        keys[0] = x;
        keys[1] = y;
        keys[2] = hexIndex;
        keys[3] = tileIndex;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("build-city", data);
    }

    public void setupRobber(double mouseX, double mouseY){
        String[] names = {"mouseX", "mouseY"};
        Object[] keys = new Object[2];
        keys[0] = mouseX;
        keys[1] = mouseY;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("setup-robber", data);
    }

    public void endTurn(){
        socket.emit("end-turn");
    }

    public void rollDice(int firstDice, int secondDice){
        String[] names = {"firstDice", "secondDice"};
        Integer[] keys = new Integer[2];
        keys[0] = firstDice;
        keys[1] = secondDice;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("roll-dice", data);
    }

    public void rollDice(int firstDice, int secondDice, ArrayList<Integer>[] discarded){
        String[] names = {"firstDice", "secondDice", "discarded"};
        Object[] keys = new Object[3];
        keys[0] = firstDice;
        keys[1] = secondDice;
        keys[2] = discarded;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("roll-dice", data);
    }

    public void sendDevCard(String cardName)
    {
        System.out.println("Sender Bought Card");
        String[] names = {"cardName"};
        String[] keys = new String[1];
        keys[0] = cardName;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-card", data);
    }

    public void playCard(String cardName, int cardIndex){
        System.out.println("Sender Play Card");
        String[] names = {"cardName", "cardIndex"};
        Object[] keys = new Object[2];
        keys[0] = cardName;
        keys[1] = cardIndex;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("play-card", data);
    }

    public void sendMonopoly(int material){
        String[] names = {"material"};
        Integer[] keys = new Integer[1];
        keys[0] = material;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-monopoly", data);
    }

    public void sendYearOfPlenty(int material){
        String[] names = {"material"};
        Integer[] keys = new Integer[1];
        keys[0] = material;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-plenty", data);
    }

    public void sendPerfectlyBalanced(ArrayList<Integer> indexes){
        String[] names = {"indexes"};
        Object[] keys = new Object[1];
        keys[0] = indexes;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-balanced", data);
    }

    public void selectPlayer(Player player, int index){
        String[] names = {"player", "index"};
        Object[] keys = new Object[2];
        keys[0] = player.getName();
        keys[1] = index;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("select-player", data);
    }

    public void selectResource(int resource){
        String[] names = {"resource"};
        Integer[] keys = new Integer[1];
        keys[0] = resource;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("select-resource", data);
    }

    public void sendTrade(int[] toGive, int[] toTake, String otherPlayer) {
        String[] names = {"toGive", "toTake", "otherPlayer"};
        Object[] keys = new Object[3];
        keys[0] = toGive;
        keys[1] = toTake;
        keys[2] = otherPlayer;
        System.out.println("sending");
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-trade", data);
    }

    public void confirmTrade(int[] toGive, int[] toTake, String otherPlayer) {
        String[] names = {"toGive", "toTake", "otherPlayer"};
        Object[] keys = new Object[3];
        keys[0] = toGive;
        keys[1] = toTake;
        keys[2] = otherPlayer;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("confirm-trade", data);
    }

    public void harborTrade(int harborType, int giveResIndex, int takeResIndex)
    {
        String[] names = {"harborType", "giveResIndex", "takeResIndex"};
        Integer[] keys = new Integer[3];
        keys[0] = harborType;
        keys[1] = giveResIndex;
        keys[2] = takeResIndex;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("harbor-trade", data);
    }

    public void refreshInfos() {
        socket.emit("refresh-infos", null);
    }

    public void sendMessage(String userId, String message){
        String[] names = {"userId", "message"};
        String[] keys = new String[2];
        keys[0] = userId;
        keys[1] = message;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-message", data);
    }

    public Status getStatus(){
        return this.status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    private boolean sendPost(String[] names, String[] keys, String apiURL){
        Request request = ServerInformation.getInstance().buildRequest(names, keys, ADDRESS + apiURL);
        boolean result = false;
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            result = response.isSuccessful();
        } catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }
}
