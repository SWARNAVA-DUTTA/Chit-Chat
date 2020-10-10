package dutta.swarnava.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class NewGroup extends AppCompatActivity {
    Toolbar toolbar_group_create;
    CircleImageView new_group_icon;
    EditText new_group_name;
    TextView message;
    StorageReference groupIcon;
    FirebaseAuth mAuth;
    ProgressDialog loading;
    FloatingActionButton tick;
    int flag=0;
    private boolean checkPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        toolbar_group_create = (Toolbar) findViewById(R.id.toolbar_group_create);
        setSupportActionBar(toolbar_group_create);
        getSupportActionBar().setTitle("New Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        new_group_icon = (CircleImageView) findViewById(R.id.new_group_icon);
        new_group_name = (EditText) findViewById(R.id.new_group_name);
        message = (TextView) findViewById(R.id.message);
        loading = new ProgressDialog(this);
        tick = findViewById(R.id.tick);
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getGroupName = new_group_name.getText().toString();
                if (TextUtils.isEmpty(getGroupName)) {
                    new_group_name.setError("Please enter a group name");

                } else {
                    loading.setMessage("Creating group...");
                    loading.setCanceledOnTouchOutside(false);
                    loading.show();
                    createNewGroup(getGroupName);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createNewGroup(String getGroupName) {
//        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Groups").child(getGroupName).setValue("")
        FirebaseDatabase.getInstance().getReference().child("Groups").child(getGroupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(NewGroup.this, "Group Created", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                            finish();
                        }
                    }
                });
    }


}