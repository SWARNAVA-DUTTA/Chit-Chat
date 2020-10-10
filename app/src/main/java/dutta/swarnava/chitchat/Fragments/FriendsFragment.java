package dutta.swarnava.chitchat.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dutta.swarnava.chitchat.ChatActivity;
import dutta.swarnava.chitchat.JavaClasses.FindFriendsAdapter;
import dutta.swarnava.chitchat.R;


public class FriendsFragment extends Fragment {

    private View FriendsView;
    private RecyclerView friendList;
    private DatabaseReference friendsRef,usersRef;
    FirebaseAuth mAuth;
    String currentUserId;
    TextView nofriends;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FriendsView= inflater.inflate(R.layout.fragment_friends, container, false);
        friendList=(RecyclerView)FriendsView.findViewById(R.id.friendList);

        friendList.setLayoutManager(new LinearLayoutManager(getContext()));
        nofriends=(TextView) FriendsView.findViewById(R.id.nofriends);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        friendsRef= FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        return FriendsView;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<FindFriendsAdapter>()
                .setQuery(friendsRef,FindFriendsAdapter.class)
                .build();
        FirebaseRecyclerAdapter<FindFriendsAdapter,FriendsViewHolder> adapter=new FirebaseRecyclerAdapter<FindFriendsAdapter, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull FindFriendsAdapter model)
            {
                final String usersIDs = getRef(position).getKey();
                final String[] retImage = {"default_image"};

                usersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.child("userState").hasChild("state"))
                            {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                if (state.equals("online"))
                                {
                                    holder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else if (state.equals("offline"))
                                {
                                    holder.onlineIcon.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                holder.onlineIcon.setVisibility(View.GONE);
                            }

//                            nofriends.setVisibility(View.GONE);
                            if (dataSnapshot.hasChild("imageUrl"))
                            {
                                retImage[0] = dataSnapshot.child("imageUrl").getValue().toString();
                                Picasso.get().load(retImage[0]).into(holder.user_profile_image);
                            }

                            final String retName = dataSnapshot.child("Name").getValue().toString();
                            final String retAbout = dataSnapshot.child("About").getValue().toString();

                            holder.user_profile_name.setText(retName);
                            holder.user_about.setText(retAbout);


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
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                FriendsViewHolder viewHolder=new FriendsViewHolder(view);
                return viewHolder;
            }
        };
        friendList.setAdapter(adapter);
        adapter.startListening();
        nofriends.setVisibility(View.GONE);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView user_profile_image;
        TextView user_profile_name,user_about;
        ImageView onlineIcon;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile_name=itemView.findViewById(R.id.user_profile_name);
            user_about=itemView.findViewById(R.id.user_about);
            user_profile_image=itemView.findViewById(R.id.user_profile_image);
            onlineIcon=itemView.findViewById(R.id.onlineIcon);
        }
    }
}