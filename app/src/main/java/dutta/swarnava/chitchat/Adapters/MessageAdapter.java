package dutta.swarnava.chitchat.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dutta.swarnava.chitchat.ImageViewActivity;
import dutta.swarnava.chitchat.JavaClasses.AudioService;
import dutta.swarnava.chitchat.JavaClasses.Messages;
import dutta.swarnava.chitchat.MainActivity;
import dutta.swarnava.chitchat.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private AudioService audioService;
    public MessageAdapter(List<Messages> userMessagesList,Context context) {
        this.userMessagesList = userMessagesList;
        this.audioService = new AudioService(context);

    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView senderMessageText,receiverMessageText;
//        CircleImageView receiverProfileImage;
        ImageButton sender_btn_play_chat,receiver_btn_play_chat;
        View sender_v_length,receiver_v_length;
        Chronometer sender_tv_duration,receiver_tv_duration;
public ImageView messageSenderPicture, messageReceiverPicture;
LinearLayout receiver_layout_voice,sender_layout_voice;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText=(TextView)itemView.findViewById(R.id.sender_message_text);
            receiverMessageText=(TextView)itemView.findViewById(R.id.receiver_message_text);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            sender_btn_play_chat=itemView.findViewById(R.id.sender_btn_play_chat);
            sender_v_length=itemView.findViewById(R.id.sender_v_length);
            sender_tv_duration=itemView.findViewById(R.id.sender_tv_duration);
            receiver_btn_play_chat=itemView.findViewById(R.id.receiver_btn_play_chat);
            receiver_v_length=itemView.findViewById(R.id.receiver_v_length);
            receiver_tv_duration=itemView.findViewById(R.id.receiver_tv_duration);
            sender_layout_voice=itemView.findViewById(R.id.sender_layout_voice);
            receiver_layout_voice=itemView.findViewById(R.id.receiver_layout_voice);
//            receiverProfileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout,viewGroup,false);
        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        String messageSenderId= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Messages messages=userMessagesList.get(position);
        String fromUserId=messages.getFrom();
        String fromMessageType=messages.getType();

        usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
              if(snapshot.hasChild("imageUrl"))
              {
                  String receiverImage= Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
//                  Picasso.get().load(receiverImage).placeholder(R.drawable.profile).into(holder.receiverProfileImage);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.sender_btn_play_chat.setVisibility(View.GONE);
        holder.sender_v_length.setVisibility(View.GONE);
        holder.sender_tv_duration.setVisibility(View.GONE);
        holder.receiver_btn_play_chat.setVisibility(View.GONE);
        holder.receiver_v_length.setVisibility(View.GONE);
        holder.receiver_tv_duration.setVisibility(View.GONE);
        holder.sender_layout_voice.setVisibility(View.GONE);
        holder.receiver_layout_voice.setVisibility(View.GONE);
        if(fromMessageType.equals("text"))
        {
            if(fromUserId.equals(messageSenderId))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(messages.getMessage()+ "\n \n" + messages.getTime() + " - " + messages.getDate());

            }
            else
            {
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessage()+ "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
        }
        else if(fromMessageType.equals("image"))
        {
            if(fromUserId.equals(messageSenderId))
            {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            Intent i=new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                            i.putExtra("url",userMessagesList.get(position).getMessage());
                            ActivityOptions options = ActivityOptions
                                    .makeSceneTransitionAnimation((Activity) holder.itemView.getContext(),(View)holder.messageSenderPicture, "url");
                            holder.itemView.getContext().startActivity(i, options.toBundle());

                        }
                        else
                        {
                            Intent i=new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                            i.putExtra("url",userMessagesList.get(position).getMessage());
                            holder.itemView.getContext().startActivity(i);
                        }

                    }
                });

            }
            else
            {
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent i=new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                        i.putExtra("url",userMessagesList.get(position).getMessage());
                        holder.itemView.getContext().startActivity(i);

                    }
                });
            }
        }
        else if(fromMessageType.equals("pdf"))
        {
            if(fromUserId.equals(messageSenderId))
            {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);

                    }
                });

            }
            else
            {
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//                Picasso.get()
//                        .load("https://firebasestorage.googleapis.com/v0/b/chit-chat-fc68f.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=ea680add-9fb1-48c4-b0bb-e80dc3ef5a32")
//                        .into(holder.messageReceiverPicture);
                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }

        else if(fromMessageType.equals("audio"))
        {
            if(fromUserId.equals(messageSenderId))
            {
                holder.sender_btn_play_chat.setVisibility(View.VISIBLE);
                holder.sender_v_length.setVisibility(View.VISIBLE);
                holder.sender_tv_duration.setVisibility(View.VISIBLE);
                holder.sender_layout_voice.setVisibility(View.VISIBLE);

                holder.sender_btn_play_chat.setImageResource(R.drawable.ic_baseline_play_arrow_24);

                holder.sender_btn_play_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        holder.sender_btn_play_chat.setImageResource(R.drawable.ic_baseline_pause_24);
                        audioService.playAudioFromUrl(messages.getMessage(), new AudioService.OnPlayCallBack() {
                            @Override
                            public void onFinished() {
                                holder.sender_btn_play_chat.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                            }
                        });

                    }
                });

            }
            else
            {
                holder.receiver_btn_play_chat.setVisibility(View.VISIBLE);
                holder.receiver_v_length.setVisibility(View.VISIBLE);
                holder.receiver_tv_duration.setVisibility(View.VISIBLE);
                holder.receiver_layout_voice.setVisibility(View.VISIBLE);

               holder.receiver_btn_play_chat.setImageResource(R.drawable.ic_baseline_play_arrow_24);

                holder.receiver_btn_play_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        holder.receiver_btn_play_chat.setImageResource(R.drawable.ic_baseline_pause_24);
                        audioService.playAudioFromUrl(messages.getMessage(), new AudioService.OnPlayCallBack() {
                            @Override
                            public void onFinished() {
                                holder.receiver_btn_play_chat.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                            }
                        });

                    }
                });
            }
        }
//        if(fromUserId.equals(messageSenderId))
//        {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v)
//                {
//                    if(userMessagesList.get(position).getType().equals("pdf"))
//                    {
//                        CharSequence options[]=new CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Download",
//                                        "Cancel",
//                                        "Delete for Everyone"
//                                };
//                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if(which==0)
//                                {
//                                    deleteSentMessage(position,holder);
//                                }
//                                else if(which==1)
//                                {
//                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//
//                                else if(which==3)
//                                {
//                                    deleteMessageforEveryone(position,holder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if(userMessagesList.get(position).getType().equals("text"))
//                    {
//                        CharSequence options[]=new CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Cancel",
//                                        "Delete for Everyone"
//                                };
//                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if(which==0)
//                                {
//                                    deleteSentMessage(position,holder);
//                                }
//                                else if(which==2)
//                                {
//                                    deleteMessageforEveryone(position,holder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if(userMessagesList.get(position).getType().equals("image"))
//                    {
//                        CharSequence options[]=new CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "View this Image",
//                                        "Cancel",
//                                        "Delete for Everyone"
//                                };
//                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if(which==0)
//                                {
//                                    deleteSentMessage(position,holder);
//                                    Intent i=new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(i);
//                                }
//                                else if(which==1)
//                                {
//                                    Intent i=new Intent(holder.itemView.getContext(), ImageViewActivity.class);
//                                    i.putExtra("url",userMessagesList.get(position).getMessage());
//                                    holder.itemView.getContext().startActivity(i);
//                                }
//                                else if(which==3)
//                                {
//                                    deleteMessageforEveryone(position,holder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
//        }
//        else
//        {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v)
//                {
//                    if(userMessagesList.get(position).getType().equals("pdf"))
//                    {
//                        CharSequence options[]=new CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Download",
//                                        "Cancel"
//                                };
//                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if(which==0)
//                                {
//                                    deleteReceiveMessage(position,holder);
//                                }
//                                else if(which==1)
//                                {
//                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if(userMessagesList.get(position).getType().equals("text"))
//                    {
//                        CharSequence options[]=new CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "Cancel",
//                                };
//                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if(which==0)
//                                {
//                                    deleteReceiveMessage(position,holder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                    else if(userMessagesList.get(position).getType().equals("image"))
//                    {
//                        CharSequence options[]=new CharSequence[]
//                                {
//                                        "Delete for me",
//                                        "View this Image",
//                                        "Cancel",
//                                };
//                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message?");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if(which==0)
//                                {
//                                    deleteReceiveMessage(position,holder);
//                                    Intent i=new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(i);
//                                }
//                                else if(which==1)
//                                {
//                                    Intent i=new Intent(holder.itemView.getContext(), ImageViewActivity.class);
//                                    i.putExtra("url",userMessagesList.get(position).getMessage());
//                                    holder.itemView.getContext().startActivity(i);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//            });
//        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    private void deleteSentMessage(final int position,final MessageViewHolder messageViewHolder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(messageViewHolder.itemView.getContext(), "Message Deleted", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void deleteReceiveMessage(final int position,final MessageViewHolder messageViewHolder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(messageViewHolder.itemView.getContext(), "Message Deleted", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void deleteMessageforEveryone(final int position,final MessageViewHolder messageViewHolder)
    {
        final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    rootRef.child("Messages").child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue();

                }
            }
        });
    }

}
