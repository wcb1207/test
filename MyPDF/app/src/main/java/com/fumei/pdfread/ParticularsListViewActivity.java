package com.fumei.pdfread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fumei.pdfread.adapter.MyListViewAdapter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by WCB on 2016/7/8.
 */
public class ParticularsListViewActivity extends Activity {
    private ListView listView;
    private int number;
    private List<Integer> numberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particulars_listview_activity);
        listView = (ListView) findViewById(R.id.list_view);

        String path = Environment.getExternalStorageDirectory().getPath() + "/myPdf/";
        File file = new File(path);
        getFileAndDirectory(file);

    }


     public void getFileAndDirectory(File file){
        int countDirctory = 0;
        int countFile = 0;
        if(file.isDirectory()){
            File []files = file.listFiles();
            for(File fileIndex:files){
                if(fileIndex.isDirectory()){
                    countDirctory++;

                    getFileAndDirectory(fileIndex);

                }else {
                    countFile++;
                }
            }
        }
        Log.i("aaaaaa","目录文件数目为："+countDirctory);
        Log.i("aaaaaa","普通文件数目为："+countFile);
    }
    public void getData(){
        Intent intent = getIntent();
        number = intent.getIntExtra("name",0);
        for (int i = 0; i <= number ; i++){
            numberList.add(i);
        }
        listView.setAdapter(new MyListViewAdapter(this,numberList));
    }
    public void getonClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ParticularsListViewActivity.this,MyActivity.class);
                intent.putExtra("abc",i);
                startActivity(intent);
                finish();
            }
        });
    }
}
