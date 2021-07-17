package com.example.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class getlink extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        Intent intent=getIntent();
        String link[] = intent.getStringArrayExtra("link");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getlink.this,android.R.layout.simple_list_item_1,link);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);//把构造好的适配器对象传进去


    }
}
