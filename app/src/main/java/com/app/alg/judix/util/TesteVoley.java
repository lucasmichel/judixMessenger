package com.app.alg.judix.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by lucas on 22/07/17.
 */

public class TesteVoley {

    private String TAG = "SO_TEST";
    private String url = "http://pokeapi.co/api/v2/pokemon-form/1/";


    public JSONObject fetchModules(Context ctx, JSONObject jsonEnviar){
        JSONObject response = null;
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonEnviar, future, future);
        requestQueue.add(request);

        try {
            response = future.get(3, TimeUnit.SECONDS); // Blocks for at most 10 seconds.
        } catch (InterruptedException e) {
            Log.d(TAG,"interrupted");
        } catch (ExecutionException e) {
            Log.d(TAG,"execution");
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d(TAG,response.toString());

        return response;
    }

}
