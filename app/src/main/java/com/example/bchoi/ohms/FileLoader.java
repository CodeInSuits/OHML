package com.example.bchoi.ohms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by bchoi on 8/22/15.
 */
public class FileLoader extends Activity {
    TextToSpeech t1;

    Button nextBtn;
    Button cancelBtn;
    boolean proceed_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_loader);
        loadImage();
        nxtClick();
        cancelClick();
        getExtras();

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    t1.speak("Is this picture of your car okay?", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    public void nxtClick() {
        nextBtn = (Button) findViewById(R.id.next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed_next = true;
                getIntent().putExtra("proceed_next", proceed_next);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
    }

    public void cancelClick() {
        cancelBtn = (Button) findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed_next = false;
                getIntent().putExtra("proceed_next", proceed_next);
                setResult(RESULT_OK,getIntent());
                finish();
            }
        });
    }

    public void getExtras() {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            proceed_next = extras.getBoolean("procees_next");
        }
    }

    public void loadImage() {
        Bitmap bmp = BitmapFactory.decodeFile("/sdcard/ohms/abc.jpg");
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setImageBitmap(bmp);
    }


}
