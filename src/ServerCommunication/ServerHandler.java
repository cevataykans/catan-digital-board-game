package ServerCommunication;

import DevelopmentCards.Card;
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
    private final String ADDRESS = "http://localhost:3000";
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
        this.socket = IO.socket("http://localhost:3000");
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

                    controller.buildSettlement(null, x, y);
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

                    controller.buildCity(null, x, y);
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

                    controller.buildRoad(null, x, y);
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

    public void playCard(Card card){
        String[] names = {"card"};
        Card[] keys = new Card[1];
        keys[0] = card;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("play-card", data);
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
