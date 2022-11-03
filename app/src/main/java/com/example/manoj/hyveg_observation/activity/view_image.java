package com.example.manoj.hyveg_observation.activity;

import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.manoj.hyveg_observation.Api;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.volley.CustomVolleyRequest;

public class view_image extends AppCompatActivity {

    ImageView img_btn_list;
    Activity activity;
    String img_value;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        img_value = getIntent().getExtras().getString("img_value");
        img_btn_list = findViewById(R.id.img_btn_list);
        if (img_value != null){
            get_img(img_value);
        }

        activity = this;

//        img_btn_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ZoomAnimation zoomAnimation = new ZoomAnimation(activity);
//                zoomAnimation.zoom(v, 600);
//            }
//        });
    }

    private void get_img(String img_value) {
        imageLoader = CustomVolleyRequest.getInstance(view_image.this).getImageLoader();
        imageLoader.get(Api.obd_get_image + img_value, new ImageLoader.ImageListener() {

            public void onErrorResponse(VolleyError arg0) {
                // set an error image if the download fails
            }

            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    img_btn_list.setImageBitmap(response.getBitmap());
                }
            }
        });
    }
}
