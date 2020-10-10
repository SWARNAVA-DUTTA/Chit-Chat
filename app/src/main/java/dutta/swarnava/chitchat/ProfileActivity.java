package dutta.swarnava.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
CircleImageView profile_picture;
ImageView icon1,icon2,icon3,edit1,edit2;
TextView name,about,phone;
FloatingActionButton fab_camera;
    ProgressDialog loading;
    Toolbar toolbar_profile;
    String mImg;
    CoordinatorLayout coordinatorLayout;
    StorageReference userProfileImage;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_picture=findViewById(R.id.profile_picture);
        icon1=findViewById(R.id.icon1);
        icon2=findViewById(R.id.icon2);
        icon3=findViewById(R.id.icon3);
        edit1=findViewById(R.id.edit1);
        edit2=findViewById(R.id.edit2);
        name=findViewById(R.id.name);
        about=findViewById(R.id.about);
        phone=findViewById(R.id.phone);
        loading=new ProgressDialog(this);
        fab_camera=findViewById(R.id.fab_camera);
        toolbar_profile = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar_profile);
        getSupportActionBar().setTitle("Your Profile");
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
                        if((snapshot.exists()) && (snapshot.hasChild("Name")) && (snapshot.hasChild("imageUrl")) && (snapshot.hasChild("Phone")))
                        {
                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                            String retrieveUserImage=snapshot.child("imageUrl").getValue().toString();
                            String retrieveUserPhone=snapshot.child("Phone").getValue().toString();
                            String newPhone="";
                            for(int i=3;i<retrieveUserPhone.length();i++)
                            {
                               newPhone=newPhone+retrieveUserPhone.charAt(i);
                            }
                            name.setText(retrieveUserName);
                            about.setText(retrieveUserAbout);
                            phone.setText("+91 "+newPhone);

//                            Glide.with(profile_picture.getContext())
//                                    .load(retrieveUserImage)
//                                    .placeholder(R.drawable.loading)
//                                    .into(profile_picture);

                            Picasso.get().load(retrieveUserImage).into(profile_picture);
                        }
                        else if(((snapshot.exists()) && (snapshot.hasChild("Name"))) && (snapshot.hasChild("About")) && (snapshot.hasChild("Phone")))
                        {
                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                            String retrieveUserPhone=snapshot.child("Phone").getValue().toString();
                            String newPhone="";
                            for(int i=3;i<retrieveUserPhone.length();i++)
                            {
                                newPhone=newPhone+retrieveUserPhone.charAt(i);
                            }
                            name.setText(retrieveUserName);
                            about.setText(retrieveUserAbout);
                            phone.setText("+91 "+newPhone);
                        }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void editname(View view) {
        String uid = mAuth.getCurrentUser().getUid();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View changing_name = inflater.inflate(R.layout.name_change_alert, null);
        final TextView msg=changing_name.findViewById(R.id.msg);
        final EditText ed_name = changing_name.findViewById(R.id.ed_name);

        dialog.setView(changing_name);
        ed_name.setSelectAllOnFocus(true);
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("Name"))) {
                            String retrieveUserNamefromdb = snapshot.child("Name").getValue().toString();
                            ed_name.setText(retrieveUserNamefromdb);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String getuserName = ed_name.getText().toString();
                if (TextUtils.isEmpty(getuserName)) {
                    Toast.makeText(ProfileActivity.this, "Name cannot remain empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String currentUserId = mAuth.getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Name").setValue(getuserName);
                }
            }

    })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        dialog.show();
    }

    public void editabout(View view) {
        String uid = mAuth.getCurrentUser().getUid();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View changing_name = inflater.inflate(R.layout.about_change_alert, null);
        final TextView msg=changing_name.findViewById(R.id.msg);
        final EditText ed_about = changing_name.findViewById(R.id.ed_about);

        dialog.setView(changing_name);
        ed_about.setSelectAllOnFocus(true);
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("About"))) {
                            String retrieveUserAboutfromdb = snapshot.child("About").getValue().toString();
                            ed_about.setText(retrieveUserAboutfromdb);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String getuserName = ed_about.getText().toString();
                if (TextUtils.isEmpty(getuserName)) {
                    Toast.makeText(ProfileActivity.this, "About can't remain empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String currentUserId = mAuth.getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("About").setValue(getuserName);
                }
            }

        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        dialog.show();
    }


    public void change_profile_picture(View view) {
        ImagePicker.Companion.with(ProfileActivity.this)
                .crop()//Crop image(Optional), Check Customization for more option
                .compress(1024)
                .galleryOnly()//// Final image size will be less than 1 MB(Optional)
                //.saveDir(Utils.getRootDirPath(MainActivity.this))
                //.saveDir(tempDir)
                .crop(1f, 1f)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                Uri fileUri = data.getData();
                loading.setMessage("Uploading profile picture...");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                String currentUserId = mAuth.getCurrentUser().getUid();
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Profile Pictures")
                        .child(currentUserId+".jpg");
//                StorageReference filePath=userProfileImage.child(currentUserId+".jpg");
                storageReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlImage = uriTask.getResult();
                        String ImageUrl = urlImage.toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("imageUrl").setValue(ImageUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ProfileActivity.this, "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this, "Failed to upload Profile Picture", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                });

                    }
                });
            }
            else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, "ImagePicker.getError(data)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }


}