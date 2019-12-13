package ServerCommunication;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ServerInformation {

    // Singleton instance
    private static ServerInformation serverInformation = null;

    // Properties
    private Queue<JSONObject> information;

    // Constructor
    private ServerInformation(){
        this.information = null;
    }

    public static ServerInformation getInstance(){
        if(serverInformation == null)
            serverInformation = new ServerInformation();
        return serverInformation;
    }

    public void addInformation(JSONObject object){
        this.information.add(object);
    }

    public JSONObject getInformation(){
        return this.information.peek();
    }

    public void deleteInformation(){
        this.information.remove();
    }

    public JSONObject JSONObjectFactory(String[] names, Object[] keys){
        JSONObject resultObject = new JSONObject();
        for(int i = 0 ; i < names.length ; i++){
            try{
                resultObject.put(names[i], keys[i]);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return resultObject;
    }

    public Request buildRequest(String[] names, String[] keys, String address){
        // form parameters
        FormBody.Builder builder = new FormBody.Builder();
        for(int i = 0 ; i < names.length ; i++)
            builder.add(names[i], keys[i]);

        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(address)
                .addHeader("User-Agent", "OkHttp Bot") // Can be discarded???
                .post(formBody)
                .build();
        return request;
    }

}
