package com.example.connect;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

//beacon import
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;

// Streaming import
import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.video.VideoQuality;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity implements OnClickListener, Session.Callback, SurfaceHolder.Callback{
    EditText edTxt;
    MyThread myThread;
    TextView txt;
    TextView txt_PredictPosition;
    TextView txt_RSSI_UUID;
    TextView txt_FacingToward;
    TextView txt_WarningLeftRight;
    TextView txt_SettingMapView;
    int[][] UpdateMap = null;


    private BeaconManager beaconManager;
    private BeaconRegion region;

    private final static String TAG = "MainActivity";

    private Button mButton1, mButton2;
    private SurfaceView mSurfaceView;
    private EditText mEditText;
    private Session mSession;



    //newly added
    private Button Testmove;
    private String carServer = "http://" + "/" + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.gostraight);
        mediaPlayer.start();
        int status;
        try {
            Bundle bundle = MainActivity.this.getIntent().getExtras();
            //map = (int[][]) bundle.getSerializable("map");
            // map = bundle.getIntArray("map");
            ///txt1 = findViewById(R.id.txt1);

            Object[] objectArray = (Object[]) bundle.getSerializable("map");
            if(objectArray!=null){ UpdateMap = new int[objectArray.length][]; for(int i=0;i<objectArray.length;i++){ UpdateMap[i]=(int[]) objectArray[i]; }
            }

            ///txt1.setText(map_image);

        }catch(Exception ex){
            status = -1; //Or some error status //
        }
        //之後audio照這個形式寫
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.gostraight);
//        mediaPlayer.start();


//        edTxt = findViewById(R.id.edTxt);
//        txt = findViewById(R.id.txt); //目標終點
//        txt_PredictPosition = findViewById(R.id.txt_PredictPosition);//predict
//        txt_RSSI_UUID = findViewById(R.id.txt_RSSI_UUID);//UUID +RSSI
//        txt_FacingToward = findViewById(R.id.txt_FacingToward);//面向
//        txt_WarningLeftRight = findViewById(R.id.txt_WarningLeftRight);//偏移
//        txt_SettingMapView = findViewById(R.id.txt_SettingMapView);

        myThread = new MyThread();//Thread 對應到 run()
        new Thread(myThread).start();


        beaconManager = new BeaconManager(MainActivity.this);
        //beaconManager.setBackgroundScanPeriod(1000,1000);

        beaconManager.setForegroundScanPeriod(2000,0);

        //beaconManager.setForegroundScanPeriod(2000,1000); 之後是看看


        region = new BeaconRegion("ranged region", null, null, null);

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {

            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> beacons) {
                String text_str = "";
                int txt_count=0;
                int k=0;
                int[] check = {0,0,0,0,0,0};
                int [] RSSI_temp={-65,-65,-65,-65,-65,-65};
                Log.d("test","end");

                if (!beacons.isEmpty()) {
                    for (final Beacon beacon : beacons) {
                        Log.d("test", " onBeaconsDiscovered: " + beacon.getMacAddress() + " " + beacon.getProximityUUID() + " " + beacon.getRssi());
                        String  UUID = String.valueOf(beacon.getProximityUUID());

                        // revised section
                        String Coordinate = "test";
                        String  RSSI = String.valueOf(beacon.getRssi()) ;
                        RSSI = RSSI.concat(Coordinate);



                        ///txt_RSSI_UUID = findViewById(R.id.txt_RSSI_UUID);
                        //text_str+="UUID: "+UUID+" RSSI: "+RSSI+"\n";
                        //txt3.setText(text_str);

                        String index = UUID.substring(UUID.length()-1);

                        myThread.sendMsgParam(index+","+RSSI);
                        k=Integer.parseInt(index);
                        if(k==7)
                        {k=6;}

                        check[k-1]=1;
                        RSSI_temp[k-1]=Integer.parseInt(RSSI);

                        //希望用UUID 來看要放在陣列的哪個位子  RSSI_List=[ , , , , , , ]
                        // i=UUID[:-1];

                        // RSSI_List[i]=Integer.parseInt(RSSI)
                        //成功後  透過 socket傳陣列
                    }



                    for(int i=0;i<6;i++){//beacon有時候會沒傳值，為了不讓他搶到下一次RSSI的值，直接補一個值給他
                        if(check[i]==0){
                            if(i==5){
                                String j= Integer.toString(6+1);

                                myThread.sendMsgParam(j+","+Integer.toString(RSSI_temp[5]));
                            }
                            else {
                                String j = Integer.toString(i+1);

                                myThread.sendMsgParam(j  + "," + Integer.toString(RSSI_temp[i]));
                            }
                        }
                        check[i]=0;

                    }


                }
            }
        });


        TextView txt1;
        String map_image="";



        Button Btn_SettingBlindBrick;
        ///Btn_SettingBlindBrick = findViewById(R.id.Btn_SettingBlindBrick);
//        Btn_SettingBlindBrick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, setting.class);
//                startActivity(intent);
//            }
//        });


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mEditText = (EditText) findViewById(R.id.editText1);

        //newly added
        Testmove = (Button) findViewById(R.id.Testmove);
        Testmove.setOnClickListener(this);
        //

        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(90)
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setVideoQuality(new VideoQuality(320,240,20,500000))
                .build();

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);

        mSurfaceView.getHolder().addCallback(this);
    }

    private class MyThread implements Runnable {

        private volatile String msg = "";



        Socket socket;
        DataOutputStream dos;
        DataInputStream inputStream;
        String Str_PredictPosition = "";
        String Str_UserDestination = "601";
        String temp_str="";
        String map="";
        int des_row=6;
        int des_col=1;
        int now_col=0;
        int now_row=0;

        @Override

        public void run() {


            try {
                socket = new Socket("140.116.72.75", 8080);//不用加http
                if (socket.isConnected()) {
                   /// txt = findViewById(R.id.txt);
                    //txt.setText("connect!");
                }
                dos = new DataOutputStream(socket.getOutputStream());//創建輸出流對象

                dos.writeUTF(msg);

                InputStream inputStream = socket.getInputStream();

                byte[] buf = new byte[1024];
                int length = inputStream.read(buf);
                Str_PredictPosition = new String(buf, 0, length);
                Log.d("number",Str_PredictPosition);
                ///txt_PredictPosition = findViewById(R.id.txt_PredictPosition);
                txt_PredictPosition.setText(Str_PredictPosition);//收到的位置
                int[][] Default_Lab_Environment = {
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
                };//不能走的地方為-1  可以走 還沒被設為路徑的為0  被設為路徑的為1 2為終點


                int[][] pos;
                Default_Lab_Environment=UpdateMap;
                pos=Default_Lab_Environment;


                Pathfinder(pos,Str_PredictPosition, Str_UserDestination);

                Show_MapView(pos,map);

                Warning_FacingToward(pos,now_row,now_col);


                WarningLeftRight(pos, now_row, now_col,Str_PredictPosition);

                pos=Default_Lab_Environment;
//

                //dos.writeUTF(RSSI);
                //dos.writeUTF(UUID);
                dos.close();
                dos.flush();//重新整理
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }



                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void sendMsgParam(String msg) {
            this.msg = msg;
            run();
        }
        public void send_des(String Str_UserDestination) {
            this.Str_UserDestination = Str_UserDestination;
            run();
        }

        public void Pathfinder(int[][]pos,String Str_PredictPosition,String Str_UserDestination){

            String temp_str =Str_PredictPosition;

            temp_str=temp_str.replaceAll("[^0-9]","");

            if(Str_UserDestination.length()>=2)
            { des_row=Integer.parseInt(Str_UserDestination.substring(Str_UserDestination.length()-2));;//給使用者決定
                des_col=Integer.parseInt(Str_UserDestination.substring(0, Str_UserDestination.length()-2));
                txt.setText("目標終點: "+ Str_UserDestination.substring(0, Str_UserDestination.length()-2)+ Str_UserDestination.substring(Str_UserDestination.length()-2)+"\n");
            }

            if(pos[des_row][des_col]!=-1){
                pos[des_row][des_col]=2;
            }
            if(temp_str.length()>=2)
            { Log.d("temp",temp_str);
                now_col=Integer.parseInt(temp_str.substring(0,temp_str.length()-2));
                now_row=Integer.parseInt(temp_str.substring(temp_str.length()-2));//之後算位置 轉成這個

            }

            if(des_row==now_row&&des_col==now_col){
                txt_FacingToward.setText("抵達!"+"\n");
            }
            if((11-now_row+11-des_row)>(now_row-1+des_row-1))//代表走上面那條路的距離比較遠 所以要走下面
            {
                for(int i=now_row;i>0;i--){
                    pos[i][now_col]=1;
                }
                for(int i=des_row;i>0;i--){
                    pos[i][des_col]=1;
                }
                if(des_col>now_col) {
                    for(int i=now_col;i<des_col+1;i++){ //橫的部分要看 des_col 和 now_col哪個大
                        pos[1][i]=1;
                    }
                }
                else {
                    for(int i=now_col;i>des_col-1;i--){ //橫的部分要看 des_col 和 now_col哪個大
                        pos[1][i]=1;
                    }
                }
            }
            else{

                for(int i=now_row;i<12;i++){
                    pos[i][now_col]=1;
                }
                for(int i=des_row;i<12;i++){
                    pos[i][des_col]=1;
                }
                if(des_col>now_col) {
                    for(int i=now_col;i<des_col+1;i++){ //橫的部分要看 des_col 和 now_col哪個大
                        pos[11][i]=1;
                    }
                }
                else {
                    for(int i=now_col;i>des_col-1;i--){ //橫的部分要看 des_col 和 now_col哪個大
                        pos[11][i]=1;
                    }
                }
            }
            if(pos[des_row][des_col]!=-1){
                pos[des_row][des_col]=2;
            }
            if(pos[now_row][now_col]!=-1){
                pos[now_row][now_col]=3;
            }
        }


    }


    public void SaveDestination(View v) {
        String des_str = edTxt.getText().toString();
//        myThread.sendMsgParam(msg);
        myThread.send_des(des_str);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.stopRanging(region);
        Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSession.isStreaming()) {
            mButton1.setText(R.string.stop);
        } else {
            mButton1.setText(R.string.start);
        }
        //Timmy Beacon
        SystemRequirementsChecker.checkWithDefaultDialogs(this);//ask for permission
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                    beaconManager.startRanging(region);
            }
        });

        Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
        //Timmy
    }
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_LONG).show();
    }

    public void Show_MapView(int[][] pos,String map){

        for(int i=pos.length-1;i>=0;i--){
            map += "  \n";
            for(int j=0;j<pos[i].length;j++) {
                if (pos[i][j] == 0) {
                    map += "□";
                }
                if (pos[i][j] == -1) {
                    map += "■";
                }
                if (pos[i][j] == 1) {
                    map += "。";
                }
                if (pos[i][j] == 2) {
                    map += "★";
                }
                if (pos[i][j] == 3) {
                    map += "☺";
                }
            }

        }

        txt_SettingMapView.setText(map);
        map="";

    }

    public void Warning_FacingToward(int[][]pos,int now_row,int now_col){

        if(pos[now_row+1][now_col]==1){
            txt_FacingToward.setText("請面向大門(北)"+"\n");
        }
        else if(pos[now_row][now_col+1]==1){
            txt_FacingToward.setText("請面向東"+"\n");
        }
        else if(pos[now_row-1][now_col]==1){
            txt_FacingToward.setText("請面向南"+"\n");
        }
        else if(pos[now_row][now_col-1]==1){
            txt_FacingToward.setText("請面向西"+"\n");
        }


    }
    public void WarningLeftRight(int[][]pos,int now_row,int now_col,String Str_PredictPosition){

        if(pos[now_row+1][now_col]==1){
            //L就警示往右 R就警示往左
            if(Str_PredictPosition.contains("L")){
                txt_WarningLeftRight.setText("請往右稍微移動，回到路中間，再保持直走"+"\n");
            }
            if(Str_PredictPosition.contains("R")){
                txt_WarningLeftRight.setText("請往左稍微移動，回到路中間，再保持直走"+"\n");

            }
            else {
                txt_WarningLeftRight.setText("保持直走"+"\n");
            }
        }
        else if(pos[now_row-1][now_col]==1){
            //L就警示往左 R就警示往右
            if(Str_PredictPosition.contains("L")){
                txt_WarningLeftRight.setText("請往左稍微移動，回到路中間，再保持直走"+"\n");
            }
            else if(Str_PredictPosition.contains("R")){
                txt_WarningLeftRight.setText("請往右稍微移動，回到路中間，再保持直走"+"\n");
            }
            else {
                txt_WarningLeftRight.setText("保持直走"+"\n");
            }
        }
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mSession.isStreaming()) {
//            mButton1.setText(R.string.stop);
//        } else {
//            mButton1.setText(R.string.start);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSession.release();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {

            System.out.println("active BT button1");
            // Starts/stops streaming
            mSession.setDestination(mEditText.getText().toString());
            if (!mSession.isStreaming()) {
                mSession.configure();
                mSession.switchCamera();
                mSession.switchCamera();
            } else {
                mSession.stop();
            }
            mButton1.setEnabled(false);
        }

        //newly added
        else if(v.getId() == R.id.Testmove){
            Intent intent = new Intent(this, car_camera.class);
            startActivity(intent);

            System.out.println("active BT testmove");

        }


        else {
            // Switch between the two cameras
            mSession.switchCamera();
        }

    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        Log.d(TAG,"Bitrate: "+bitrate);
    }

    @Override
    public void onSessionError(int message, int streamType, Exception e) {
        mButton1.setEnabled(true);
        if (e != null) {
            logError(e.getMessage());
        }
    }

    @Override

    public void onPreviewStarted() {
        Log.d(TAG,"Preview started.");
    }

    @Override
    public void onSessionConfigured() {
        Log.d(TAG,"Preview configured.");
        // Once the stream is configured, you can get a SDP formated session description
        // that you can send to the receiver of the stream.
        // For example, to receive the stream in VLC, store the session description in a .sdp file
        // and open it with VLC while streming.
        Log.d(TAG, mSession.getSessionDescription());
        mSession.start();
    }

    @Override
    public void onSessionStarted() {
        Log.d(TAG,"Session started.");
        mButton1.setEnabled(true);
        mButton1.setText(R.string.stop);
    }

    @Override
    public void onSessionStopped() {
        Log.d(TAG,"Session stopped.");
        mButton1.setEnabled(true);
        mButton1.setText(R.string.start);
    }

    /** Displays a popup to report the eror to the user */
    private void logError(final String msg) {
        final String error = (msg == null) ? "Error unknown" : msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(error).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSession.stop();
    }


    //new

    public void switchActivity(View view){

    }


}