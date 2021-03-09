package com.app.alg.judix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

/**
 * Created by lucas on 22/07/17.
 */

public class MyVolleyAsyncTask extends AsyncTask<String,String, JSONObject> {

    private Context ctx;
    private JSONObject objJson;

    public MyVolleyAsyncTask(Context hostContext, JSONObject jsonEnviar)
    {
        ctx = hostContext;
        objJson = jsonEnviar;
    }


    /*@Override
    protected void onPreExecute(){

        ProgressDialog load = ProgressDialog.show(ctx, "Por favor Aguarde ...",
                "Exec async ...");
    }*/

    protected void onPreExecute (){
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        //return null;

        // Method runs on a separate thread, make all the network calls you need
        TesteVoley tester = new TesteVoley();

        return tester.fetchModules(ctx,  objJson);

    }

    protected void onProgressUpdate(Integer...a){
        super.onProgressUpdate();
    }

    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

    }

}
