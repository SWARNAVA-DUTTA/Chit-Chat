package dutta.swarnava.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

public class UserCredential extends AppCompatActivity {
    CircleImageView profile_picture;
    EditText name;
    Button save;
    StorageReference userProfileImage;
    AlertDialog.Builder builder;
    private boolean checkPermission=false;
    FirebaseAuth mAuth;
    ProgressDialog loading;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credential);
        profile_picture=(CircleImageView) findViewById(R.id.profile_picture);
        name=(EditText)findViewById(R.id.name);
        save=(Button)findViewById(R.id.save);
        loading=new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);
        mAuth = FirebaseAuth.getInstance();
        userProfileImage = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        retrieveUserinfo();
    }
    public void onBackPressed(){
        SharedPreferences preferences = getSharedPreferences("Reg",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Phone_Number");
        editor.commit();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);


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
                            String retrieveUserImage=snapshot.child("imageUrl").getValue().toString();
                            name.setText(retrieveUserName);
//                            Glide.with(profile_picture.getContext())
//                                    .load(retrieveUserImage)
//                                    .placeholder(R.drawable.loading)
//                                    .into(profile_picture);
                            Picasso.get().load(retrieveUserImage).into(profile_picture);

                        }
                        else if(((snapshot.exists()) && (snapshot.hasChild("Name"))))
                        {
                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                            name.setText(retrieveUserName);
                        }
                        else
                            Toast.makeText(UserCredential.this, "Enter your Details", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void change_profile_picture(View view)
    {
        Dexter.withActivity(UserCredential.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {checkPermission=true;pick_profile_picture();}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {checkPermission=false;}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {token.continuePermissionRequest();}
                }).check();
    }
    private void pick_profile_picture()
    {
        ImagePicker.Companion.with(UserCredential.this)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                Uri fileUri = data.getData();
//                profile_picture.setImageURI(fileUri);
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
                                        flag=flag+1;
                                        Toast.makeText(UserCredential.this, "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserCredential.this, "Failed to upload Profile Picture", Toast.LENGTH_SHORT).show();
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


    public void save_details(View view) {
        if (flag == 0) {
            String getuserName = name.getText().toString();
            if (TextUtils.isEmpty(getuserName)) {
                builder.setMessage("Please enter your name.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                name.setFocusable(true);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("");
                alert.show();
            } else {
                String currentUserId = mAuth.getCurrentUser().getUid();
                loading.setMessage("Saving your details...");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("About").setValue("Can't Talk, ChitChat Only");
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Name").setValue(getuserName);
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("imageUrl").setValue("https://firebasestorage.googleapis.com/v0/b/chit-chat-fc68f.appspot.com/o/Profile%20Pictures%2Fprofile.jpg?alt=media&token=4c5f70d5-dd38-4dab-9c23-bad36e4e30a3");
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Uid").setValue(currentUserId)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                flag++;
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserCredential.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    loading.dismiss();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                } else {
                                    String errormessage = task.getException().toString();
                                    Toast.makeText(UserCredential.this, "Error " + errormessage, Toast.LENGTH_SHORT).show();
                                    loading.dismiss();
                                }
                            }
                        });
            }
        }
        else {
        String getuserName = name.getText().toString();
        if (TextUtils.isEmpty(getuserName)) {
            builder.setMessage("Please enter your name.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            name.setFocusable(true);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("");
            alert.show();
        } else {
            String currentUserId = mAuth.getCurrentUser().getUid();
            loading.setMessage("Saving your details...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("About").setValue("Can't Talk, ChitChat Only");
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Name").setValue(getuserName);
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Uid").setValue(currentUserId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            flag++;
                            if (task.isSuccessful()) {
                                Toast.makeText(UserCredential.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            } else {
                                String errormessage = task.getException().toString();
                                Toast.makeText(UserCredential.this, "Error " + errormessage, Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
        }
    }
    }
}