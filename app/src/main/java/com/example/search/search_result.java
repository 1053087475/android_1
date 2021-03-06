package com.example.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class search_result extends AppCompatActivity {
    WebView webv;
    private String[] data={};
    int page=1,aim_uri_num=50,data_count=data.length,len=0,count=0;
    String head,body,inputSearch;
    @Override
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        webv = findViewById(R.id.webv);
        webv.getSettings().setJavaScriptEnabled(true);
        webv.getSettings().setDomStorageEnabled(true);
        webv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        Intent intent=getIntent();
        inputSearch = intent.getStringExtra("inputSearch");
        System.out.println(inputSearch);
//        Uri uri=null;
//        try {
//            uri = Uri.parse("http://www.baidu.com/s?&ie=utf-8&oe=UTF-8&wd=" + URLEncoder.encode(inputSearch,"UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        webv.addJavascriptInterface(new InJavaScriptLocalObj(),"local_obj");
        head = "https://m.sogou.com/web";
        body = "/search/searchList.jsp?keyword=";
        webv.setWebViewClient(new MyWebViewClient());
        webv.loadUrl(head + body + inputSearch +"&p=" + page++);
        initView();
    }

    private void initView(){
        Button btnSearch = findViewById(R.id.finduri);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aim_uri_num>data.length){
                    webv.loadUrl(head + body + inputSearch +"&p=" + page++);
                }else{
                    Intent intent  = new Intent(search_result.this,getlink.class);
                    intent.putExtra("link",data);
                    startActivity(intent);
                }
                System.out.println("data.length==="+data.length);
                for(int i=0;i<data.length;i++){System.out.println(data[i]); } //??????Data
            }
        });
    }


    class MyWebViewClient extends WebViewClient{
        @Override  //WebView??????????????????WebView
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //??????????????????WebView??????????????????
            String url = request.getUrl().toString();
//            view.loadUrl(request.getUrl().toString());
//            return true;
            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            } catch (Exception e){
                return false;
            }

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            Log.d("WebView","??????????????????");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("WebView","??????????????????");
            // ?????????????????????????????????
            // ??????????????????
//          view.loadUrl("javascript:window.local_obj.showSource( document.getElementsByTagName('html')[0].innerHTML);");
            //????????????
            view.loadUrl("javascript:window.local_obj.getLength(document.getElementsByClassName('vr-tit').length);");
        }

//        @Override
//        public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest request) {
//            // ???????????????????????????????????????????????????????????????
//            return super.shouldInterceptRequest(view, request);
//        }
        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.indexOf("http://www.example.com") != -1 && view != null) {
                view.stopLoading();
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        }
    }

    //?????????????????????????????????
     public static String[] insert(String[] arr, String str) {
        int size = arr.length;  //??????????????????
        String[] tmp = new String[size + 1];  //????????????????????????????????????????????????????????????
        for (int i = 0; i < size; i++){  //????????????????????????????????????????????????????????????????????????
            tmp[i] = arr[i];
        }
        tmp[size] = str;  //???????????????????????????????????????
        return tmp;  //????????????????????????????????????
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            html = html.replaceFirst(".",head);
//            System.out.println("====>html=" + html);
            data = insert(data,data.length+":"+html);
            data_count++;
            //???????????????????????????????????????????????????
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(count==len){
                        webv.loadUrl(head + body + inputSearch +"&p=" + page++);
                        count=0;
                    }
                    if(aim_uri_num==data.length){
                        Intent intent  = new Intent(search_result.this,getlink.class);
                        intent.putExtra("link",data);
                        startActivity(intent);
                        //??????Data
                        System.out.println("data.length==="+data.length);
                        for(int i=0;i<data.length;i++){System.out.println(data[i]); } //??????Data
                    }
                }
            });
        }

        @JavascriptInterface
        public void getLength(String length) {
            System.out.println("====>length=" + length);
            len = Integer.valueOf(length);
            //????????????href??????
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(data_count<aim_uri_num) {
                        for (int i=0;i<len;i++){
//                        System.out.println("x==="+x+"len==="+len);
                            count++;
                            webv.loadUrl("javascript:window.local_obj.showSource(document.getElementsByClassName('vr-tit')[" + i + "].getElementsByTagName('a')[0].getAttribute('href'));");
                        }
                    }
                }
            });
        }
    }

}
