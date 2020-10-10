package dutta.swarnava.chitchat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import dutta.swarnava.chitchat.Adapters.MessageAdapter;
import dutta.swarnava.chitchat.JavaClasses.Messages;

public class ChatActivity extends AppCompatActivity {
Toolbar chat_toolbar;
RecyclerView private_chat_list;
TextView custom_profile_name,userLastSeen;
CircleImageView custom_profile_image;
EditText input_message;
ImageButton send_files_btn,btn_emoji,send_image;
FloatingActionButton send_btn;
RelativeLayout chat_linear_layout;
//CardView layout_actions;
LinearLayout btn_doc,btn_camera_x,btn_gallery,btn_audio,btn_location,btn_contact;
    private boolean isActionShown = false;
    private static final int REQUEST_CORD_PERMISSION = 332;

RecordButton record_button;
    RecordView recordView;
    ProgressDialog loading;
    private String saveCurrentTime, saveCurrentDate;
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;
    Uri fileUri;
    StorageTask uploadTask;
    String messageSenderId,messageReceiverId,messageReceiverName,messageReceiverImage,myUrl="",checker="",currentUserID="";
    DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    public boolean checkPermission=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        loading = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        messageReceiverName = getIntent().getExtras().get("messageReceiverName").toString();
        messageReceiverImage = getIntent().getExtras().get("messageReceiverImage").toString();
        messageReceiverId = getIntent().getExtras().get("messageReceiverId").toString();

        Initialize();
        custom_profile_name.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).into(custom_profile_image);


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(ChatActivity.this)
                        .crop()//Crop image(Optional), Check Customization for more option
                        .compress(1024)
                        .cameraOnly()
                        //.saveDir(Utils.getRootDirPath(MainActivity.this))
                        //.saveDir(tempDir)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
        send_files_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile();
            }
        });
        DisplayLastSeen();
        rootRef.child("Messages")
                .child(messageSenderId)
                .child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                        Messages messages=snapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        private_chat_list.smoothScrollToPosition(private_chat_list.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
        @SuppressLint("DefaultLocale")
        private String getHumanTimeText(long milliseconds) {
            return String.format("%02d",
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
        }






    private void DisplayLastSeen()
    {
        rootRef.child("Users").child(messageReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen: " + date + " " + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void Initialize()
    {

        chat_toolbar =  findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);
        chat_toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_chat_bar);
        View actionBarView=getSupportActionBar().getCustomView();
        getSupportActionBar().setCustomView(actionBarView);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        custom_profile_image=findViewById(R.id.custom_profile_image);
        custom_profile_name=findViewById(R.id.custom_profile_name);

        messageAdapter=new MessageAdapter(messagesList,ChatActivity.this);
        private_chat_list=findViewById(R.id.private_chat_list);
        linearLayoutManager=new LinearLayoutManager(this);
        private_chat_list.setLayoutManager(linearLayoutManager);
        private_chat_list.setAdapter(messageAdapter);
        private_chat_list.hasFixedSize();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        chat_linear_layout=findViewById(R.id.chat_linear_layout);
        saveCurrentTime = currentTime.format(calendar.getTime());
        input_message=findViewById(R.id.input_message);
        send_btn=findViewById(R.id.send_btn);
        send_files_btn=findViewById(R.id.send_files_btn);
        btn_emoji=findViewById(R.id.btn_emoji);
        send_image=findViewById(R.id.send_image);
        record_button=findViewById(R.id.record_button);
        recordView = (RecordView) findViewById(R.id.record_view);
        record_button.setRecordView(recordView);

        input_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(input_message.getText().toString())){
                    send_btn.setVisibility(View.INVISIBLE);
                    record_button.setVisibility(View.VISIBLE);
                } else {
                    send_btn.setVisibility(View.VISIBLE);
                    record_button.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recordView.setSoundEnabled(false);
        record_button.setListenForRecord(true);

        //ListenForRecord must be false ,otherwise onClick will not be called
        record_button.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Press and Hold to Record Audio", Toast.LENGTH_SHORT).show();
            }
        });
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                Dexter.withContext(ChatActivity.this)
                        .withPermissions(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        btn_emoji.setVisibility(View.INVISIBLE);
                        send_files_btn.setVisibility(View.INVISIBLE);
                        send_image.setVisibility(View.INVISIBLE);
                        input_message.setVisibility(View.INVISIBLE);

                        startRecord();
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null) {
                            vibrator.vibrate(100);
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
                    {token.continuePermissionRequest();}
                }).check();
            }

            @Override
            public void onCancel() {
                try {
                    mediaRecorder.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime) {
                btn_emoji.setVisibility(View.VISIBLE);
                send_files_btn.setVisibility(View.VISIBLE);
                send_image.setVisibility(View.VISIBLE);
                input_message.setVisibility(View.VISIBLE);
                loading.setMessage("Sending Audio");
                loading.setCanceledOnTouchOutside(false);
                loading.show();


                //Stop Recording..
                try {
                    sTime = getHumanTimeText(recordTime);
                    stopRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onLessThanSecond() {
                btn_emoji.setVisibility(View.VISIBLE);
                send_files_btn.setVisibility(View.VISIBLE);
                send_image.setVisibility(View.VISIBLE);
                input_message.setVisibility(View.VISIBLE);
            }
        });
        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                btn_emoji.setVisibility(View.VISIBLE);
                send_files_btn.setVisibility(View.VISIBLE);
                send_image.setVisibility(View.VISIBLE);
                input_message.setVisibility(View.VISIBLE);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==123 && data!=null && data.getData()!=null)
        {
            fileUri=data.getData();
            if(checker.equals("pdf"))
            {
                loading.setMessage("Sending File");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document Files");
                final String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
                final String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;

                DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                        .child(messageSenderId)
                        .child(messageReceiverId).push();
                final String messagePushId=userMessageKeyRef.getKey();

                StorageReference filePath=storageReference.child(messagePushId+"."+checker);
                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            Uri downloadUri=task.getResult();
                            myUrl=downloadUri.toString();
                            Map messagePdf=new HashMap();
                            messagePdf.put("message",myUrl);
                            messagePdf.put("name",fileUri.getLastPathSegment());
                            messagePdf.put("type",checker);
                            messagePdf.put("from",messageSenderId);
                            messagePdf.put("to", messageReceiverId);
                            messagePdf.put("messageID", messagePushId);

                            Map messagePdfDetails=new HashMap();
                            messagePdfDetails.put(messageSenderRef+"/"+messagePushId,messagePdf);
                            messagePdfDetails.put(messageReceiverRef+"/"+messagePushId,messagePdf);
                            rootRef.updateChildren(messagePdfDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        loading.dismiss();
                                        Toast.makeText(ChatActivity.this, "Document sent", Toast.LENGTH_SHORT).show();
//                                                     input_message.setText("");
                                    }
                                    else
                                    {
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        loading.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            if(checker.equals("image"))
            {
                loading.setMessage("Sending Image");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");
                final String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
                final String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;

                DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                        .child(messageSenderId)
                        .child(messageReceiverId).push();
                final String messagePushId=userMessageKeyRef.getKey();

                StorageReference filePath=storageReference.child(messagePushId+"."+"jpg");
                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            Uri downloadUri=task.getResult();
                            myUrl=downloadUri.toString();

                            Map messageImage=new HashMap();
                            messageImage.put("message",myUrl);
                            messageImage.put("name",fileUri.getLastPathSegment());
                            messageImage.put("type",checker);
                            messageImage.put("from",messageSenderId);
                            messageImage.put("to", messageReceiverId);
                            messageImage.put("messageID", messagePushId);


                            Map messageImageDetails=new HashMap();
                            messageImageDetails.put(messageSenderRef+"/"+messagePushId,messageImage);
                            messageImageDetails.put(messageReceiverRef+"/"+messagePushId,messageImage);
                            rootRef.updateChildren(messageImageDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        loading.dismiss();
                                        Toast.makeText(ChatActivity.this, "Image sent", Toast.LENGTH_SHORT).show();
//                                                     input_message.setText("");
                                    }
                                    else
                                    {
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                        int currentprogress = (int) progress;
                        loading.setProgress(currentprogress);
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                });
            }
            if(checker.equals("audio"))
            {
                loading.setMessage("Sending Audio");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Audio Files");
                final String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
                final String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;

                DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                        .child(messageSenderId)
                        .child(messageReceiverId).push();
                final String messagePushId=userMessageKeyRef.getKey();

                StorageReference filePath=storageReference.child(messagePushId+"."+checker);
                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            Uri downloadUri=task.getResult();
                            myUrl=downloadUri.toString();
                            Map messagePdf=new HashMap();
                            messagePdf.put("message",myUrl);
                            messagePdf.put("name",fileUri.getLastPathSegment());
                            messagePdf.put("type",checker);
                            messagePdf.put("from",messageSenderId);
                            messagePdf.put("to", messageReceiverId);
                            messagePdf.put("messageID", messagePushId);

                            Map messagePdfDetails=new HashMap();
                            messagePdfDetails.put(messageSenderRef+"/"+messagePushId,messagePdf);
                            messagePdfDetails.put(messageReceiverRef+"/"+messagePushId,messagePdf);
                            rootRef.updateChildren(messagePdfDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        loading.dismiss();
                                        Toast.makeText(ChatActivity.this, "Audio sent", Toast.LENGTH_SHORT).show();
//                                                     input_message.setText("");
                                    }
                                    else
                                    {
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        loading.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                        int currentprogress = (int) progress;
                        loading.setProgress(currentprogress);
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                });
            }


        }
        {

        }
    }
    private void sendFile()
    {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.send_file_layout, null);

        final AlertDialog alertD = new AlertDialog.Builder(this).create();
        btn_doc=promptView.findViewById(R.id.btn_doc);
        btn_camera_x=promptView.findViewById(R.id.btn_camera_x);
        btn_gallery=promptView.findViewById(R.id.btn_gallery);
        btn_audio=promptView.findViewById(R.id.btn_audio);
        btn_location=promptView.findViewById(R.id.btn_location);
        btn_contact=promptView.findViewById(R.id.btn_contact);
        btn_location.setEnabled(false);
        btn_contact.setEnabled(false);
        btn_doc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checker = "pdf";
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent.createChooser(intent,"Select file"),123);
                alertD.cancel();
            }
        });
        btn_camera_x.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checker = "image";
                ImagePicker.Companion.with(ChatActivity.this)
                        .crop()//Crop image(Optional), Check Customization for more option
                        .compress(1024)
                        .cameraOnly()
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start(132);
                alertD.cancel();
            }
        });
        btn_gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checker = "image";
                ImagePicker.Companion.with(ChatActivity.this)
                        .crop()//Crop image(Optional), Check Customization for more option
                        .compress(1024)
                        .galleryOnly()
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start(123);
                alertD.cancel();
            }
        });
        btn_audio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checker="audio";
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent.createChooser(intent,"Select Audio"),123);
                alertD.cancel();
            }
        });
//        btn_location.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                alertD.cancel();
//            }
//        });
//        btn_contact.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                checker="contact";
//                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                startActivityForResult(intent, 321);
//                alertD.cancel();
//            }
//        });

        WindowManager.LayoutParams wmlp = alertD.getWindow().getAttributes();
        alertD.getWindow().setGravity(Gravity.NO_GRAVITY);
        alertD.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertD.setView(promptView);
        alertD.setCanceledOnTouchOutside(true);
        alertD.setCancelable(true);
        alertD.show();

//        CharSequence option[]=new CharSequence[]
//                {
//                  "Image",
//                  "Document"
//                };
//        AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
//        builder.setTitle("Select a file");
//        builder.setItems(option, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                if(which==0)
//                {
//                    checker = "image";
//                    Intent intent=new Intent();
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    startActivityForResult(intent.createChooser(intent,"Select image"),438);
//                }
//                if(which==1)
//                {
//                    checker = "pdf";
//                    Intent intent=new Intent();
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    intent.setType("application/pdf");
//                    startActivityForResult(intent.createChooser(intent,"Select file"),438);
//                }
//            }
//        });
//        builder.show();
    }
    private void sendMessage()
    {
        String messageText=input_message.getText().toString().trim();
        if(TextUtils.isEmpty(messageText))
        {
        }
        else
        {
            String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
            String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;

            DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                                                        .child(messageSenderId)
                                                        .child(messageReceiverId).push();
            String messagePushId=userMessageKeyRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);
            messageTextBody.put("to", messageReceiverId);
            messageTextBody.put("messageID", messagePushId);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagePushId,messageTextBody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagePushId,messageTextBody);
            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        input_message.setText("");
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }


@Override
public void onStart()
{
    super.onStart();
    updateUserStatus("online");

}

    @Override
    protected void onStop()
    {
        super.onStop();
        updateUserStatus("offline");
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        updateUserStatus("offline");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

    private void startRecord(){
        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            //  Toast.makeText(InChatActivity.this, "Recording...", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ChatActivity.this, "Recording Error , Please restart your app ", Toast.LENGTH_LONG).show();
        }

    }

    private void stopRecord(){
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                //sendVoice();
                sendVoice(audio_path);

            } else {
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Stop Recording Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpMediaRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        } catch (Exception e) {
            Toast.makeText(this, "setUpMediaRecord: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendVoice(String audioPath){
        final Uri uriAudio = Uri.fromFile(new File(audioPath));
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Voice Recordings");
        final String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
        final String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;

        DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                .child(messageSenderId)
                .child(messageReceiverId).push();
        final String messagePushId=userMessageKeyRef.getKey();

        StorageReference filePath=storageReference.child(messagePushId+"."+checker);
        uploadTask=filePath.putFile(uriAudio);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception
            {
                if(!task.isSuccessful())
                {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if(task.isSuccessful())
                {
                    Uri downloadUri=task.getResult();
                    myUrl=downloadUri.toString();
                    Map messageAudio=new HashMap();
                    messageAudio.put("message",myUrl);
                    messageAudio.put("name",messageSenderId);
                    messageAudio.put("type","audio");
                    messageAudio.put("from",messageSenderId);
                    messageAudio.put("to", messageReceiverId);
                    messageAudio.put("messageID", messagePushId);

                    Map messageAudioDetails=new HashMap();
                    messageAudioDetails.put(messageSenderRef+"/"+messagePushId,messageAudio);
                    messageAudioDetails.put(messageReceiverRef+"/"+messagePushId,messageAudio);
                    rootRef.updateChildren(messageAudioDetails).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                loading.dismiss();

                            }
                            else
                            {
                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                loading.dismiss();
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}