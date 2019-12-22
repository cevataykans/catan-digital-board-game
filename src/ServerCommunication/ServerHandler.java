package ServerCommunication;

import DevelopmentCards.*;
import GameFlow.CardManager;
import GameFlow.FlowManager;
import GameFlow.Game;
import GameFlow.ResourceManager;
import Player.Player;
import SceneManagement.GameEngine;
import SceneManagement.MatchmakingController;
import SceneManagement.MultiGameController;
import SceneManagement.SoundManager;
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
    private final String ADDRESS = "http://139.179.210.100:3000";
    private final OkHttpClient httpClient = new OkHttpClient();

    public static ServerHandler serverHandler;

    // Properties
    private Status status;
    private Socket socket;
    private MultiGameController controller;
    private String token;
    private String userId;
    private boolean connected;

    // Constructor
    private ServerHandler(){
        this.status = null;
        this.socket = null;
        this.token = "";
        this.connected = false;
        this.userId = "";
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
        String result = sendPost(names, keys, "/api/user/login");
        if ( result.equals("Failed") )
        {
            // Error message
            return false;
        }
        this.userId = userId;
        System.out.println(result.split("token")[1]);
        String token = result.split("token")[1].substring(3).split("\"")[0];
        this.token = token;
        System.out.println(token);
        connect();
        return true;
    }


    public boolean register(String userId, String password){
        String[] names = {"userId", "password"};
        String[] keys = new String[2];
        keys[0] = userId;
        keys[1] = password;
        String result = sendPost(names, keys, "/api/user/register");
        if(result.equals("Failed"))
            return false;
        return true;
    }

    public boolean changePassword(String oldPsw, String newPsw){
        String[] names = {"userId", "oldPassword", "newPassword", "token"};
        String[] keys = new String[3];
        keys[0] = this.userId;
        keys[1] = oldPsw;
        keys[2] = newPsw;
        keys[3] = this.token;
        String result = sendPost(names, keys, "/api/user/changePassword");
        if(result.equals("Failed"))
            return false;
        return true;
    }

    public boolean logout(){
        String[] names = {"userId", "token"};
        String[] keys = new String[2];
        keys[0] = this.userId;
        keys[1] = this.token;
        String result = sendPost(names, keys, "/api/user/logout");
        if(result.equals("Failed"))
            return false;
        this.token = "";
        this.userId = "";
        this.connected = false;
        this.socket.disconnect();
        this.status = null;
        this.serverHandler = null;
        return true;
    }

    private void connect(){
        if(connected)
            return;
        try{
            this.socket = IO.socket(ADDRESS);
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
        this.socket.connect();
        this.connected = true;
        listenEvents();
    }

    private void sendUserId(){
        System.out.println("send");
        String[] names = {"userId", "token"};
        String[] keys = new String[2];
        keys[0] = this.userId;
        keys[1] = this.token;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory();
        this.socket.emit("userId-response", data);
    }

    public void listenEvents() {
        this.socket.on("userId-request", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("request");
                sendUserId();
            }
        });

        this.socket.on("found-player-response", new Emitter.Listener() { // Start message from the server
            @Override
            public void call(Object... objects) {
                JSONObject obj = (JSONObject) objects[0];
                try {
                    int count = obj.getInt("number");
                    MatchmakingController controller = (MatchmakingController) GameEngine.getInstance().getController();
                    controller.foundPlayerCount(count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    switch (cardName)
                    {
                        case "knight":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Card(new Knight(),"knight"));
                            break;
                        case "monopoly":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Card(new Monopoly(), "monopoly"));
                            break;
                        case "Road-Building":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Card(new RoadBuilding(), "Road-Building"));
                            break;
                        case "Victory-Point":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Card(new VictoryPoint(), "Victory-Point"));
                            break;
                        case "Year-of-Plenty":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Card(new YearOfPlenty(), "Year-of-Plenty"));
                            break;
                        case "Change-of-Fortune":
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Card(new ChangeOfFortune(), "Change-of-Fortune"));
                            break;
                        case "Perfectly-Balanced":
                            System.out.println("Perfectly balanced acquired for current player");
                            flowManager.getCurrentPlayer().buyDevelopmentCard(Card.REQUIREMENTS_FOR_CARD, new Card(new PerfectlyBalanced(), "Perfectly-Balanced"));
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
                    switch (cardName)
                    {
                        case "knight":
                            new Knight().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            controller.checkWinCondition();
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
                            controller.checkWinCondition();
                            break;
                        case "Year-of-Plenty":
                            new YearOfPlenty().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                        case "Change-of-Fortune":
                            new ChangeOfFortune().play();
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                        case "Perfectly-Balanced":
                            System.out.println("Perfectly balanced played");
                            flowManager.getCurrentPlayer().getCards().remove(flowManager.getCurrentPlayer().getCards().get(cardIndex));
                            break;
                    }
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
                ServerInformation.getInstance().addInformation(obj);
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
                ServerInformation.getInstance().addInformation(obj);
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
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
                System.out.println("confirmed");
                controller.getInfoController().confirmTrade();
            }
        });

        this.socket.on("refuse-trade-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj);
                // Call related controller method
                controller.getInfoController().refuseTrade();
            }
        });
        this.socket.on("harbor-trade-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj);
                // Call related controller method
                controller.getHarborController().receiveHarborTradeMessage();
            }
        });
        this.socket.on("refresh-infos-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
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
        this.socket.on("finish-game-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                controller.checkWinCondition();
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
                if(connected){
                    System.out.println("disconnect response");
                    boolean result = logout();
                    if(result)
                        System.out.println("logout successfully");
                    controller.finishTheGameForDisconnection();
                }
            }
        });
    }


    public void gameRequest(){
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
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory();
        socket.emit("end-turn", data);
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
        String[] names = {"cardName"};
        String[] keys = new String[1];
        keys[0] = cardName;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-card", data);
    }

    public void playCard(String cardName, int cardIndex){
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

    public void sendPerfectlyBalanced(ArrayList<ArrayList<Integer>> indexes){
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

    public void refuseTrade(String otherPlayer){
        String[] names = {"otherPlayer"};
        Object[] keys = new Object[1];
        keys[0] = otherPlayer;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("refuse-trade", data);
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
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory();
        socket.emit("refresh-infos", data);
    }

    public void sendMessage(String userId, String message){
        String[] names = {"userId", "message"};
        String[] keys = new String[2];
        keys[0] = userId;
        keys[1] = message;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("send-message", data);
    }

    public void finishGame() {
        System.out.println("outside if");
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory();
        if(socket != null) {
            socket.emit("finish", data);
            System.out.println("inside if");
        }
    }

    public void terminateServerHandler(){
        if(connected){
            logout();
        }
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

    private String sendPost(String[] names, String[] keys, String apiURL){
        Request request = ServerInformation.getInstance().buildRequest(names, keys, ADDRESS + apiURL);
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch(Exception e){
            System.out.println("Failed");
            return "Failed";
        }
    }

    public String getToken(){
        return this.token;
    }

}
