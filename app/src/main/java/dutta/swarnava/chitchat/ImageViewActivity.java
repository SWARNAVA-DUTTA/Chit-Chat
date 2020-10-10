package dutta.swarnava.chitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {
ImageView image_viewer;
String img="";
Toolbar toolbar_imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        image_viewer=findViewById(R.id.image_viewer);
        toolbar_imageview = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_imageview);
        setSupportActionBar(toolbar_imageview);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        img=getIntent().getStringExtra("url");

        Glide.with((FragmentActivity) this).load( img)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into( image_viewer);
//        Picasso.get()
//                .load(img)
//                .fit()
//                .noFade()
//                .into(image_viewer,new Callback() {
//            @Override
//            public void onSuccess() {
//                supportStartPostponedEnterTransition();
//            }
//
//            @Override
//            public void onError(Exception e)
//            {
//                supportStartPostponedEnterTransition();
//            }
//
//        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}