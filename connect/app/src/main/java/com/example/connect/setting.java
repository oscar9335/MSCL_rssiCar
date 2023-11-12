package com.example.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class setting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        ///////////////////////////////////////////////////////////variable
        Button button2;
        Button btn_save;
        Button btn_reset;
        TextView text1;
        //button2 = findViewById(R.id.button2);
        btn_save = findViewById(R.id.save);
        btn_reset = findViewById(R.id.reset);
        text1=findViewById(R.id.text1);
        Spinner spinner1 = findViewById(R.id.spinnerEX1);
        Intent intent2 = new Intent(setting.this, MainActivity.class);
          int [][] map=
                {
                        {-1, -1, -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                        {-1, 0, 0,0,0,0,0,0,0,0,0,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, -1,-1,-1,-1,0,-1,-1,-1,-1,0,-1},
                        {-1, 0, 0,0,0,0,0,0,0,0,0,0,-1},
                        {-1, -1, -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//  x,y  y是row
                };


////button onclick function//////////////////////////////////////////////////////////////////
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                startActivity(intent2);
//            }
//        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            String map_image="";
            @Override
            public void onClick(View view) {



                Bundle bundle=new Bundle();

                //bundle.putIntArray("map",map);
                bundle.putSerializable("map", map);
                intent2.putExtras(bundle);

                startActivity(intent2);

            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            String map_image="";
            @Override
            public void onClick(View view) {

                text1.setText(map_image);
                map_image="";
            }
        });

//////////////////////////////////////////////////////////////////


        ArrayList arrayList = new ArrayList<Integer>();
        for (int i = 0;i<11;i++){
            arrayList.add(i);
        }//製作陣列

        ArrayList PossiblePoint =new ArrayList<String>();
        for (int j=0;j<13;j++){
            for(int i=0;i<13;i++){
                if(map[i][j]!=-1){
                    PossiblePoint.add(Integer.toString(j)+"-"+Integer.toString(i));
                }

            }

        }

////////////////////////////////////////////////////////////////////////
        ArrayAdapter adapter1 = new ArrayAdapter(this
                ,android.R.layout.simple_dropdown_item_1line,PossiblePoint);
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(0, true);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(view.getContext(),parent.getSelectedItem().toString()/*這行可直接取得選中內容*/,Toast.LENGTH_SHORT).show();

                //Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();

                String SelectedPoint=parent.getSelectedItem().toString();
                Toast.makeText(view.getContext(), "增加 "+SelectedPoint+" 為導盲磚!", Toast.LENGTH_SHORT).show();
                String[] tokens = SelectedPoint.split("-");
                ArrayList TempRowCol =new ArrayList<String>();
                for(String token:tokens){
                    TempRowCol.add(token);
                }
                int MapRow=Integer.parseInt((String) TempRowCol.get(0));
                int MapCol=Integer.parseInt((String) TempRowCol.get(1));
                map[MapRow][MapCol]=1;

//                String map_image="";
//                for(int i=12;i>=0;i--){
//                    for(int j=0;j<=12;j++){
//                        if(map[i][j]==0){map_image+="□";}
//                        if(map[i][j]==-1){map_image+="■";}
//                        if(map[i][j]==1){map_image+="⊞";}
//                    }
//                    map_image+="\n";
//                }
//                text1.setText(map_image);
//                int select_road=(Integer) parent.getSelectedItem();
//                ma[select_road]=1;
//                String msg =  Integer.toString(select_road);
//                Toast.makeText(view.getContext(), "增加第 "+msg+" 點為導盲磚!", Toast.LENGTH_SHORT).show();





            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
///////////////////////////////////////////////////////////////////////////////////////////////////


    }

}