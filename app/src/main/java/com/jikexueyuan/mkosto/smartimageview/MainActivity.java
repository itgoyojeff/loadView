package com.jikexueyuan.mkosto.smartimageview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    protected static final int SUCCESS = 0;  //定义了这两个 常量, 用来区分 是成功还是 失败
    protected static final int ERROR = 1;

    EditText  ed_path;
    ImageView iv_pic;  //用来显 示图 片的imageview 控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化 控件
        ed_path = (EditText) findViewById(R.id.ed_path);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);

    }

    //大家, 以后 自己在 学习 android 技术时, 可以经常去翻翻源代码....

    private Handler handler = new Handler(){

        // 处理消息
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SUCCESS:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    iv_pic.setImageBitmap(bitmap);
                    break;
                case ERROR:
                    // 需要弹 土司提示
                    Toast.makeText(MainActivity.this, "对不起, 出错了.  ", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        };

    };

    String path;

    // 点击之后, 显示 图片
    public void display(View v){


        //当前这些代码运行在主线程中
//    	Thread.currentThread().getName()  --- >> main

        //获得 路径
        path = ed_path.getText().toString().trim();

        if(TextUtils.isEmpty(path)){
            Toast.makeText(this, "路径不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

//		连接网络
        new Thread(){

            public void run() {


                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    //设置连接的超时 时间
                    conn.setConnectTimeout(5000);

                    //设置 请求的方式
                    conn.setRequestMethod("GET");  // 必须要大写

                    int code = conn.getResponseCode();
                    if(code==200){

                        //获得 图片的流的数据
                        InputStream in = conn.getInputStream();
                        //这个流是 一个 图片的数据, 那么需要将 流 转换为 图片
                        Bitmap bitmap = BitmapFactory.decodeStream(in);

                        //在 子线程 中直接去 更新了   非 子线程中创建的 控件 .

                        //如何解决呢 ? ---->>> Handler的 技术
//						iv_pic.setImageBitmap(bitmap);

//						Message msg = new Message();

                        //这个代码内部 , 进行了 优化
                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        //成功
                        msg.what=SUCCESS;
                        handler.sendMessage(msg);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Message msg = Message.obtain();

                    //失败
                    msg.what= ERROR;

                    handler.sendMessage(msg);
                }

            };
        }.start();


    }


}

