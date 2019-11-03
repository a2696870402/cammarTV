package com.example.cammartv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener,SurfaceHolder.Callback,RadioGroup.OnCheckedChangeListener
{
    private Button start,stop,card;
    private RadioGroup radioGroup1,radioGroup2;
    private RadioButton rb1,rb2,rb3,rb4,rb5,rb6,rb7,rb8;
    private static MediaRecorder mediaRecorder; //录制视频的类
    private SurfaceView surfaceView; //显示视频的控件
    private String resoultion_str;
    String pose,cameara,Indoor,idcard;
    private SurfaceHolder surfaceHolder;
    AlertDialog alert=null;
    @SuppressWarnings({"deprecation","unused"})
    private static Camera myCamera=null;
    @SuppressWarnings({"deprecation","unused"})
    private Camera.Parameters myParamerters;
    @SuppressWarnings({"deprecation","unused"})
    private Camera.AutoFocusCallback mAutoFocusCallback=null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化屏幕设置
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置横屏显示

        setContentView(R.layout.activity_main);
        init();
    }
    @SuppressWarnings("deprecation")
    private void init() {
        start = (Button) this.findViewById(R.id.start);
        stop = (Button) this.findViewById(R.id.stop);
        card = (Button) this.findViewById(R.id.card);

        radioGroup1 = (RadioGroup) this.findViewById(R.id.radiogroup1);
        radioGroup2 = (RadioGroup) this.findViewById(R.id.radiogroup2);

        radioGroup1.setOnCheckedChangeListener(this);
        radioGroup2.setOnCheckedChangeListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        card.setOnClickListener(this);

        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);

        SurfaceHolder holder=surfaceView.getHolder();
        //添加回调接口
        holder.addCallback(this);
        //设置setType
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if(null==myCamera){
            //打开前置摄像头
            int cameraCount=0;
            @SuppressWarnings("unused")
            Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
            cameraCount=Camera.getNumberOfCameras();

            for(int camIdex=0; camIdex<cameraCount; camIdex++){
                Camera.getCameraInfo(camIdex,cameraInfo);
                if(cameraInfo.facing== CameraInfo.CAMERA_FACING_BACK){
                    //代表摄像头方位 前置和后置
                    try {
                        myCamera=Camera.open(camIdex);
                    }catch (RuntimeException e){
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb1:
                pose = "warpCopperPhoto";
                Log.i("tag","warpCopperPhoto");
                break;
            case R.id.rb2:
                Log.i("tag","warpA4Photo");
                pose = "warpA4Photo";
                break;
            case R.id.rb3:
                pose = "cutEyeCopperPhoto";
                Log.i("tag","cutEyeCopperPhoto");
                break;
            case R.id.rb4:
                pose = "moveEyeA4Photo";
                Log.i("tag","moveEyeA4Photo");
                break;
            case R.id.rb5:
                pose = "netVideo";
                Log.i("tag","netVideo");
                break;
            case R.id.rb6:
                pose = "netPicture";
                Log.i("tag","netPicture");
                break;
            case R.id.rb7:
                pose = "netA4Photo";
                Log.i("tag","netA4Photo");
                break;
            case R.id.rb8:
                pose = "netCopperPhoto";
                Log.i("tag","netCopperPhoto");
                break;
            case R.id.rb11:
                resoultion_str= "LR";
                Log.i("tag","LR");
                break;
            case R.id.rb22:
                resoultion_str= "NR";
                Log.i("tag","NR");
                break;
            case R.id.rb33:
                resoultion_str= "HR";
                Log.i("tag","HR");
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        String filename="";
        switch (v.getId()){
            case R.id.start:
                if(null==myCamera){
                    int cameraCount=0;
                    @SuppressWarnings("unused")
                    Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
                    cameraCount=Camera.getNumberOfCameras();
                    for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {
                        Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo
                        if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK ) {
                            // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                            try {
                                myCamera = Camera.open( camIdx );
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                }
                myCamera.unlock();
                if(null==mediaRecorder){
                    mediaRecorder=new MediaRecorder();
                }
                mediaRecorder.setCamera(myCamera);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                //录制完毕 封装为mp4
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                //视频编码为h264
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

                //设置视频的分辨率 注意 必须放在设置编码和格式后面
                mediaRecorder.setVideoSize(176,144);

                //设置录制的视频帧率
                mediaRecorder.setVideoFrameRate(20);

                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

                //存放视频
                String name="fake"+resoultion_str+"_"+"Phone"+"_"+"Indoor"+pose+"_"+idcard+".mp4";
                File file=new File("/sdcard/video/");
                file.mkdirs();
                filename="/sdcard/video/"+name;
                //设置视频文件输出的路径
                mediaRecorder.setOutputFile(filename);
                try {
                    mediaRecorder.prepare(); //准备录制
                    mediaRecorder.start(); //开始
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
            case R.id.stop:
                if(mediaRecorder!=null){
                    mediaRecorder.stop(); //停止录制
                    mediaRecorder.release(); //释放资源
                    mediaRecorder=null;
                    Toast.makeText(getApplicationContext(),"录制结束",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.card:
                final EditText filenames=new EditText(this);
                filenames.setInputType(EditorInfo.TYPE_CLASS_PHONE); //数字键盘
                Builder alerbuidler=new Builder(this);
                alerbuidler
                        .setTitle("请输入工作编号")
                        .setView(filenames)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        idcard=filenames.getText().toString();
                                    }
                                });
                alert=alerbuidler.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
                break;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder=holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceView=null;
        surfaceHolder=null;
        mediaRecorder=null;
    }

}
