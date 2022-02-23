package com.example.ml_d6;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//
//import org.tensorflow.lite.DataType;
//import org.tensorflow.lite.support.image.TensorImage;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import com.example.ml_d6.ml.DensenetPort;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private ImageView imgView;
    private Button select, predict;
    private TextView tv;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.textView);
        select = (Button) findViewById(R.id.button);
        predict = (Button) findViewById(R.id.button2);
        predict.setEnabled(false);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
                tv.setText("Now Click on Predict Button");
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(getBaseContext(), "Your answer is correct!" , Toast.LENGTH_SHORT ).show();
                img = Bitmap.createScaledBitmap(img, 300, 300, true);
                try {
                    DensenetPort model = DensenetPort.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 300, 300, 3}, DataType.FLOAT32);
                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();



                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    DensenetPort.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();
                    int maxAt = 0;

                    for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                        maxAt = outputFeature0.getFloatArray()[i] > outputFeature0.getFloatArray()[maxAt] ? i : maxAt;
                    }
                    if(maxAt==0)
                    {
                        tv.setText("Cyclone");
                    }
                    else
                        if(maxAt==1)
                        {
                            tv.setText("Earthquake");
                        }
                        else
                        if(maxAt==2)
                        {
                            tv.setText("Flood");
                        }
                        else
                        {
                            tv.setText("Wildfire");
                        }


                } catch (IOException e) {
                    // TODO Handle the exception
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100)
        {
            imgView.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                predict.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}