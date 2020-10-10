package dutta.swarnava.chitchat.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dutta.swarnava.chitchat.ChatActivity;
import dutta.swarnava.chitchat.JavaClasses.Contacts;
import dutta.swarnava.chitchat.R;

public class ChatsFragment extends Fragment {
    private View PrivateChatsView;
    private RecyclerView chatList;
    private boolean checkPermission=true;
    TextView noconvo;
    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID="";

    public ChatsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        noconvo=(TextView)PrivateChatsView.findViewById(R.id.noconvo);
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatList=(RecyclerView)PrivateChatsView.findViewById(R.id.chatList);

        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return PrivateChatsView;
    }


    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatsRef, Contacts.class)
                        .build();
        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model)
                    {
                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    noconvo.setVisibility(View.GONE);
                                    if (dataSnapshot.hasChild("imageUrl"))
                                    {
                                        retImage[0] = dataSnapshot.child("imageUrl").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                    }

                                    final String retName = dataSnapshot.child("Name").getValue().toString();
                                    final String retStatus = dataSnapshot.child("About").getValue().toString();

                                    holder.userName.setText(retName);


                                    if (dataSnapshot.child("userState").hasChild("state"))
                                    {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if (state.equals("online"))
                                        {
                                            holder.userStatus.setText("online");
                                        }
                                        else if (state.equals("offline"))
                                        {
                                            holder.userStatus.setText("Last Seen: " + date + " " + time);
                                        }
                                    }
                                    else
                                    {
                                        holder.userStatus.setText("offline");
                                    }
                                    holder.profileImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                            LayoutInflater inflater = LayoutInflater.from(getContext());
                                            View register_layout = inflater.inflate(R.layout.user_profile_image_dialog, null);
                                        }
                                    });

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("messageReceiverId", usersIDs);
                                            chatIntent.putExtra("messageReceiverName", retName);
                                            chatIntent.putExtra("messageReceiverImage", retImage[0]);
                                            startActivity(chatIntent);
                                        }
                                    });
                                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
//
                                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                            builder.setTitle("Delete Chat with "+retName+" ?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    ChatsRef.child(usersIDs).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            Toast.makeText(holder.itemView.getContext(), "Chat Deleted Successfully", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            builder.show();
                                            return true;
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class  ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus, userName;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.user_profile_image);
            userStatus = itemView.findViewById(R.id.user_about);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
//    private void updateUserStatus(String state)
//    {
//        String saveCurrentTime, saveCurrentDate;
//
//        Calendar calendar = Calendar.getInstance();
//
//        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
//        saveCurrentDate = currentDate.format(calendar.getTime());
//
//        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
//        saveCurrentTime = currentTime.format(calendar.getTime());
//
//        HashMap<String, Object> onlineStateMap = new HashMap<>();
//        onlineStateMap.put("time", saveCurrentTime);
//        onlineStateMap.put("date", saveCurrentDate);
//        onlineStateMap.put("state", state);
//
//        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("userState")
//                .updateChildren(onlineStateMap);
//
//    }

}