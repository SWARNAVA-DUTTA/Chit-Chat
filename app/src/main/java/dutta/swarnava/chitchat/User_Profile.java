package dutta.swarnava.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

public class User_Profile extends AppCompatActivity {
CircleImageView user_profile_image;
TextView user_profile_name,user_about;
Button send_message_request,decline_message_request;
    String receiveUserId,currentState,senderUserId;
    String name,about,img;
    FirebaseAuth mAuth;
    DatabaseReference chatRequest,contactsRef,NotificationRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);
        user_profile_image=findViewById(R.id.user_profile_image);
        user_profile_name=findViewById(R.id.user_profile_name);
        user_about=findViewById(R.id.user_about);
        send_message_request=findViewById(R.id.send_message_request);
        decline_message_request=findViewById(R.id.decline_message_request);
        mAuth = FirebaseAuth.getInstance();
        chatRequest=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        receiveUserId=getIntent().getExtras().get("messageReceiverId").toString();
        currentState="new";
        senderUserId = mAuth.getCurrentUser().getUid();
            retrieveUserinfo();
    }
    private void retrieveUserinfo()
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(receiveUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if((snapshot.exists()) && (snapshot.hasChild("Name")) && (snapshot.hasChild("imageUrl")) && (snapshot.hasChild("Cover_Pic")))
                        {
                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                            String retrieveUserImage=snapshot.child("imageUrl").getValue().toString();

                            user_profile_name.setText(retrieveUserName);
                            user_about.setText(retrieveUserAbout);
                            Picasso.get().load(retrieveUserImage).placeholder(R.drawable.loading).into(user_profile_image);
                            ManageChatRequests();
                        }
                        else if(((snapshot.exists()) && (snapshot.hasChild("Name"))) && (snapshot.hasChild("About")))
                        {
                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                            user_profile_name.setText(retrieveUserName);
                            user_about.setText(retrieveUserAbout);
                            ManageChatRequests();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void ManageChatRequests()
    {
        chatRequest.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(receiveUserId))
                        {
                            String request_type = dataSnapshot.child(receiveUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                currentState = "request_sent";
                                send_message_request.setText("Decline");
                            }
                            else if (request_type.equals("received"))
                            {
                                currentState = "request_received";
                                send_message_request.setText("Accept");

                                decline_message_request.setVisibility(View.VISIBLE);
                                decline_message_request.setEnabled(true);

                                decline_message_request.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        cancelChatRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            contactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.hasChild(receiveUserId))
                                            {
                                                currentState = "friends";
                                                send_message_request.setText("Unfriend");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        if (!senderUserId.equals(receiveUserId))
        {
            send_message_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    send_message_request.setEnabled(false);

                    if (currentState.equals("new"))
                    {
                        sendChatRequest();
                    }
                    if (currentState.equals("request_sent"))
                    {
                        cancelChatRequest();
                    }
                    if (currentState.equals("request_received"))
                    {
                        acceptChatRequest();
                    }
                    if (currentState.equals("friends"))
                    {
                        unfriend();
                    }
                }
            });
        }
        else
        {
            send_message_request.setVisibility(View.GONE);
        }
    }

    private void unfriend()
    {
        contactsRef.child(senderUserId).child(receiveUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            contactsRef.child(receiveUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                send_message_request.setEnabled(true);
                                                currentState = "new";
                                                send_message_request.setText("Send Request");

                                                decline_message_request.setVisibility(View.GONE);
                                                decline_message_request.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptChatRequest()
    {
        contactsRef.child(senderUserId).child(receiveUserId)
                .child("Friends").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            contactsRef.child(receiveUserId).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                chatRequest.child(senderUserId).child(receiveUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    chatRequest.child(receiveUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    send_message_request.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    send_message_request.setText("Unfriend");

                                                                                    decline_message_request.setVisibility(View.GONE);
                                                                                    decline_message_request.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void sendChatRequest()
    {
        chatRequest.child(senderUserId).child(receiveUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            chatRequest.child(receiveUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserId);
                                                chatNotificationMap.put("type", "request");

                                                NotificationRef.child(receiveUserId).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    send_message_request.setEnabled(true);
                                                                    currentState = "request_sent";
                                                                    send_message_request.setText("Cancel Request");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest()
    {
        chatRequest.child(senderUserId).child(receiveUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            chatRequest.child(receiveUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                send_message_request.setEnabled(true);
                                                currentState = "new";
                                                send_message_request.setText("Send Request");

                                                decline_message_request.setVisibility(View.GONE);
                                                decline_message_request.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}