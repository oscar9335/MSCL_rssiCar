package com.example.connect;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;



public class Menu extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Button to_car;
        to_car = findViewById(R.id.tocar);

        to_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_Gocar = new Intent(Menu.this,car_camera.class);
                startActivity(intent_Gocar);

                System.out.println("active BT testmove");
            }
        });

        Button btn_Setting;
        Button btn_Navigation;
        btn_Setting=findViewById(R.id.btn_Setting);
        btn_Navigation=findViewById(R.id.btn_Navigation);
        Intent intent_GoSetting=new Intent(Menu.this,setting.class);
        Intent intent_GoNavigation=new Intent(Menu.this,MainActivity.class);
        btn_Setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                startActivity(intent_GoSetting);


            }



        });

        btn_Navigation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                startActivity(intent_GoNavigation);


            }



        });





    }
}
