package com.example.search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    EditText etSearch;
    SearchView sv;
    String head,body;
    String[] data={};
    int page=1,aim_uri_num=50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    //设置监听事件
    private void initView(){
        etSearch = findViewById(R.id.etSearch);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnRequest = findViewById(R.id.btnRequest);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doGetWebInfo();
                    }
                }).start();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    doSearch();
            }
        });
    }

    private void doGetWebInfo() {
        String inputSearch = etSearch.getText().toString();
        if(inputSearch.equals("")){
            return;
        }
        try {
            //https://m.sogou.com/web/search/searchList.jsp?keyword= 被拦截
            head = "https://www.sogou.com/";
            body = "web?query=";
            URL url = new URL(head + body + inputSearch + "&page=" + page++);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer();
            String temp = null,href;
            boolean found = false;
            while((temp = bufferedReader.readLine())!=null){
                buffer.append(temp+"\n");
//                System.out.println(temp);
                //手机版网页
                if(temp.contains("resultLink") & temp.contains("<a ")){
                    href = getHref(temp);
                    if(href.charAt(0)=='.'){
                        href = href.replaceFirst(".","https://m.sogou.com/web");
                        href = href.replaceAll("&amp;","&");
                    }
                    if(href.charAt(0)=='h'){
                        data = search_result.insert(data,href);
                    }
                    if(data.length==aim_uri_num)break;
                    continue;
                }
                //电脑版网页
                if(temp.contains("class=\"vr-title") || temp.contains("class=\"vrTitle\"")){
                    found = true;
                }
                if(found && temp.contains("<a ")){
                    System.out.println(temp);
                    found = false;
                    href = getHref(temp);
                    //System.out.println(href);
                    if(href.charAt(0)=='/'){
                        href = href.replaceFirst("/",head+"/");
                    }
                    if(href.charAt(0)=='h'){
                        data = search_result.insert(data,href);
                    }
                    if(data.length==aim_uri_num)break;
                }
            }
            bufferedReader.close();
            reader.close();
            inputStream.close();
//            不满足数量  循环
            if(data.length<aim_uri_num && data.length != 0)doGetWebInfo();
//            System.out.println(buffer);
            else{
                System.out.println("data.length==="+data.length);
                for(int i=0;i<data.length;i++){System.out.println(i+":"+data[i]); } //验证Data
                Intent intent  = new Intent(MainActivity.this,getlink.class);
                intent.putExtra("link",data);
                startActivity(intent);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doSearch(){
        String inputSearch = etSearch.getText().toString();
        if(inputSearch.equals("")){
            Toast toast=Toast.makeText(this,"搜索框不能为空！",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
//        startActivity(new Intent(MainActivity.this,search_result.class));
//        finish();

//        Uri uri=null;
//        try {
//            uri = Uri.parse("http://www.baidu.com/s?&ie=utf-8&oe=UTF-8&wd=" + URLEncoder.encode(inputSearch,"UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        Intent intent  = new Intent(MainActivity.this,search_result.class);
//        System.out.println(uri);
//        i.setData(uri);

        intent.putExtra("inputSearch",inputSearch);
        startActivity(intent);
    }

    private String getHref(String str){
        String[] begin = str.split("href=\"");
        str = begin[1].split("\"")[0];
        return str;
    }
}