package com.gaotenglife.pulltozoomscrollview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private PullZoomScrollView mView;

    private RelativeLayout mImageContainer;

    private ImageView mImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mImageContainer = (RelativeLayout)findViewById(R.id.topimagecontainer);
        mView = (PullZoomScrollView)findViewById(R.id.scrollview);
        mImage = (ImageView)findViewById(R.id.topimage);
        mImage.setImageBitmap(ImageCrop(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)));
        mView.mImageView = mImageContainer;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 将图片裁减为宽高比例符合 上部imageview的宽高比例,这个例子中，高度固定为200，这个自己可以调整
     * @param bitmap
     * @return
     */
    public Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX = 0;
        int retY = 0;
        int height = 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float rate =  (float)dp2px(200,this)/(float)displayMetrics.widthPixels;
        height = (int)(h*rate);
        if(h>height){
            retY = (h-height)/2;
            retX = 0;
        }else {
            retY = 0;
            retX = (w-(int)(h/rate))/2;
        }
        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, w, height, null, false);
    }

    public  int dp2px(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
