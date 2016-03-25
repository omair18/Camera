package com.example.weezn.camera;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2  {
private static final String TAG="MainActivity";

    private int mathed;
    private Mat mat;
    private CameraBridgeViewBase bridgeViewBase;
    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            Log.i(TAG,"BaseLoaderCallback");
            super.onManagerConnected(status);
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    bridgeViewBase.enableView();
                    Log.i(TAG, "opencv load succcessfully");
//                    System.loadLibrary("process_frame");

                }break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "oncreat");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        bridgeViewBase= (CameraBridgeViewBase) findViewById(R.id.camera_view);
        bridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        bridgeViewBase.setCvCameraViewListener(this);

        /**
         * 将RGB4通道图像转化为灰度图像
         */
        Button btn_gray= (Button) findViewById(R.id.gray);
        btn_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mathed=1;
            }
        });

        /**
         * 边缘检测
         */
        Button  btn_Canny= (Button) findViewById(R.id.canny);
        btn_Canny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mathed=2;
            }
        });

        /**
         *肤色检测
         */
        Button btn_skin= (Button) findViewById(R.id.skin);
        btn_skin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mathed = 3;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!=bridgeViewBase){
            bridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, loaderCallback);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if(null!=bridgeViewBase){
            bridgeViewBase.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.i(TAG,"onCameraViewStarted");
        mat =new Mat(width,height,CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        Log.i(TAG, "oncameraviewstopped");
        mat.release();
    }

    /**
     * 处理相机读入帧数 inputFrame  返回Mat作为显示在手机上的图像
     * @param inputFrame
     * @return
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Log.i(TAG, "onCameraFrame()");
        final Mat input=inputFrame.rgba();

        switch (mathed){
            case 1:
                Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2GRAY);
                break;
            case 2:
                Imgproc.Canny(input,mat,100,100);
                break;
            case 3:

            default:
                mat=input;
                break;
        }

        return mat;
    }

    Mat skin(Mat input){
        Mat mat=new Mat(input.size(),CvType.CV_8UC1);

        return mat;
    }

}
