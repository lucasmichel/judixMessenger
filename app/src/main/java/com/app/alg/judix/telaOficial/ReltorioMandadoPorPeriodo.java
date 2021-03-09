package com.app.alg.judix.telaOficial;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.alg.judix.R;

public class ReltorioMandadoPorPeriodo extends AppCompatActivity {

    private WebView webbViewPeriodo;
    ProgressDialog prDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reltorio_mandado_por_periodo);


        WebView webbViewPeriodo = (WebView) findViewById(R.id.webbViewPeriodo);
        loadWebViewLoad(webbViewPeriodo);

    }


    private void loadWebViewLoad(WebView webview) {



        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.setWebViewClient(new MyWebViewClient());
        //webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient(){


            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                //webImage.setImageBitmap(icon);
                //webView.loadUrl("preencheCPF('042.028.794-90')");
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //progressBar.setProgress(newProgress);
            }


            @Override
            public void onShowCustomView(android.view.View view, android.webkit.WebChromeClient.CustomViewCallback callback){

            }

            public void onPageFinished(WebView view, String url) {
                //super.onPageFinished(view, url);
                /*webView.loadUrl(
                        "javascript:(function () { " +
                                "var element = document.getElementById('hplogo');"
                                + "element.parentNode.removeChild(element);" +
                                "})()");*/

                //webView.loadUrl("preencheCPF('042.028.794-90')");
            }


        });


        SharedPreferences pref = getSharedPreferences("PreferenciaJudix", 0);
        //USU_ID = Integer.parseInt(pref.getString("USU_ID", null));
        String USU_CPF = pref.getString("USU_CPF", null);


        webview.loadUrl(this.uriBuilder());
        webview.loadUrl("https://judix.com.br/modulos/ws/relatorioMandadosPorPeriodoMobile.php?PES_CPF="+USU_CPF);

        //webView.loadUrl("preencheCPF('042.028.794-90')");



/*
        // Enablejavascript
        WebSettings ws = webview.getSettings();
        ws.setJavaScriptEnabled(true);
// Add the interface to record javascript events
        webview.addJavascriptInterface(valid, "valid");
        webview.addJavascriptInterface(refuse, "refuse");*/


    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            prDialog = new ProgressDialog(ReltorioMandadoPorPeriodo .this);
            prDialog.setMessage("Aguarde, carregando...");
            prDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(prDialog!=null){
                prDialog.dismiss();
            }
            //webView.loadUrl("preencheCPF('042.028.794-90')");
        }
    }

    private String uriBuilder() {
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.example.com")
                .appendPath("search")
                .appendQueryParameter("id", "123")
                .appendQueryParameter("category", "student");
        String myUrl = builder.build().toString();*/
        //https://api.example.com/search?id=123&category=student



        //https://judix.com.br/modulos/ws/relatorioProdutividadeMobile.php?PES_CPF=
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("judix.com.br/modulos/ws/relatorioMandadosPorPeriodoMobile.php")
                //.appendPath("/modulos/ws/relatorioProdutividadeMobile.php")
                .appendQueryParameter("PES_CPF", "042.028.794-90");
        //.appendQueryParameter("category", "student");
        String myUrl = builder.build().toString();

        return myUrl;
    }

}
