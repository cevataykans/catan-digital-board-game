package ServerCommunication;

import DevelopmentCards.Card;
import Player.Player;
import SceneManagement.GameEngine;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;

public class ServerHandler {
    enum Status{
        RECEIVER, SENDER
    }
    private final String ADDRESS = "http://localhost:3000";
    private final OkHttpClient httpClient = new OkHttpClient();

    public static ServerHandler serverHandler;
    // Properties
    private Status status;
    private Socket socket;

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
        return sendPost(names, keys, "/api/user/login");
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
            }
        });
        this.socket.on("build-settlement-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("build-city-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("build-road-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("setup-robber-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("roll-dice-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("end-turn-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
            }
        });
        this.socket.on("select-player-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                setStatus(Status.RECEIVER); // Client acts as receiver. It receives message from the server
                JSONObject obj = (JSONObject) objects[0];
                ServerInformation.getInstance().addInformation(obj); // Put the data to the information queue
                // Call related controller method
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
        this.socket.on("turn-error-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                // Print error!!!
            }
        });
        this.socket.on("validation-error-response", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                // Print error!!!
            }
        });
    }


    public void gameRequest() throws URISyntaxException {
        this.connect();
        socket.emit("game-request");

    }

    public void buildSettlement(int x, int y){
        String[] names = {"x", "y"};
        Integer[] keys = new Integer[2];
        keys[0] = x;
        keys[1] = y;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("build-settlement", data);
    }

    public void buildRoad(int x, int y){
        String[] names = {"x", "y"};
        Integer[] keys = new Integer[2];
        keys[0] = x;
        keys[1] = y;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("build-road", data);
    }

    public void buildCity(int x, int y){
        String[] names = {"x", "y"};
        Integer[] keys = new Integer[2];
        keys[0] = x;
        keys[1] = y;
        JSONObject data = ServerInformation.getInstance().JSONObjectFactory(names, keys);
        socket.emit("build-city", data);
    }

    public void setupRobber(int x, int y){
        String[] names = {"x", "y"};
        Integer[] keys = new Integer[2];
        keys[0] = x;
        keys[1] = y;
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

    public void selectPlayer(Player player){
        String[] names = {"player"};
        Player[] keys = new Player[1];
        keys[0] = player;
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

    public Status getStatus(){
        return this.status;
    }

    public void setStatus(Status status){
        this.status = status;
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
