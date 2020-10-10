package dutta.swarnava.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {
CircleImageView user_profile_image;
TextView user_profile_name,user_about;
    Toolbar toolbar_settings;
    StorageReference userProfileImage;
    LinearLayout settingsprofile;
    FirebaseAuth mAuth;
    LinearLayout ln_account,ln_chats,ln_notification,ln_data,ln_help,ln_invite_friend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user_profile_image=findViewById(R.id.user_profile_image);
        user_profile_name=findViewById(R.id.user_profile_name);
        user_about=findViewById(R.id.user_about);
        toolbar_settings = (Toolbar) findViewById(R.id.toolbar_settings);
        settingsprofile=findViewById(R.id.settingsprofile);
        ln_account=(LinearLayout)findViewById(R.id.ln_account);
        ln_chats=(LinearLayout)findViewById(R.id.ln_chats);
        ln_notification=(LinearLayout)findViewById(R.id.ln_notification);
        ln_data=(LinearLayout)findViewById(R.id.ln_data);
        ln_help=(LinearLayout)findViewById(R.id.ln_help);
        ln_invite_friend=(LinearLayout)findViewById(R.id.ln_invite_friend);
        setSupportActionBar(toolbar_settings);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        retrieveUserinfo();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void retrieveUserinfo()
    {
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if((snapshot.exists()) && (snapshot.hasChild("Name")) && (snapshot.hasChild("imageUrl")))
                        {
                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                            String retrieveUserImage=snapshot.child("imageUrl").getValue().toString();
                            user_profile_name.setText(retrieveUserName);
                            user_about.setText(retrieveUserAbout);
//                            Glide.with(user_profile_image.getContext())
//                                    .load(retrieveUserImage)
//                                    .placeholder(R.drawable.loading)
//                                    .into(user_profile_image);
                            Picasso.get().load(retrieveUserImage).into(user_profile_image);
                        }
                        else if(((snapshot.exists()) && (snapshot.hasChild("Name"))) && (snapshot.hasChild("About")))
                        {
                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                            user_profile_name.setText(retrieveUserName);
                            user_about.setText(retrieveUserAbout);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void gotoprofile(View view) {

        Intent intent = new Intent(Settings.this, ProfileActivity.class);
        startActivity(intent);
    }
}