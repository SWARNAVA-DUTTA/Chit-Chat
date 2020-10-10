package dutta.swarnava.chitchat.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import dutta.swarnava.chitchat.JavaClasses.Requests;
import dutta.swarnava.chitchat.R;

public class RequestsFragment extends Fragment {

    private View requestsView;
    private RecyclerView requestList;
    private DatabaseReference requestsRef,usersRef,friendsRef;
    FirebaseAuth mAuth;
    String currentUserId;
    TextView norequests;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestsView=inflater.inflate(R.layout.fragment_requests, container, false);
        requestList=(RecyclerView)requestsView.findViewById(R.id.requestList);

        requestList.setLayoutManager(new LinearLayoutManager(getContext()));
        norequests=(TextView) requestsView.findViewById(R.id.norequests);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        requestsRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(currentUserId);
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        friendsRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        return requestsView;
    }
    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(requestsRef, Requests.class)
                .build();
        FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestsViewHolder> adapter=new FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsFragment.RequestsViewHolder holder, int position, @NonNull Requests model) {
                String userIDs = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists()) {
                            String type = snapshot.getValue().toString();

                            if (type.equals("received"))
                            {
                                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        String requestUserName=snapshot.child("Name").getValue().toString();
                                        if((snapshot.exists()) && (snapshot.hasChild("Name")) && (snapshot.hasChild("imageUrl")))
                                        {
//                            norequests.setVisibility(View.GONE);
                                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                                            String retrieveUserImage=snapshot.child("imageUrl").getValue().toString();
                                            holder.user_profile_name.setText(retrieveUserName);
                                            Picasso.get().load(retrieveUserImage).placeholder(R.drawable.loading).into(holder.user_profile_image);
                                        }
                                        else if(((snapshot.exists()) && (snapshot.hasChild("Name"))) && (snapshot.hasChild("About")))
                                        {
                                            String retrieveUserName=snapshot.child("Name").getValue().toString();
                                            String retrieveUserAbout=snapshot.child("About").getValue().toString();
                                            holder.user_profile_name.setText(retrieveUserName);
                                            holder.txt.setText("wants to connect with you.");
                                        }
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {

                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Accept",
                                                                "Decline"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestUserName+ " has sent you a chat request");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i)
                                                    {
                                                        if (i == 0)
                                                        {
                                                            acceptChatRequest(userIDs);
                                                        }
                                                        if (i == 1)
                                                        {
                                                            cancelChatRequest(userIDs);
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else if (type.equals("sent"))
                            {
                                Button request_sent_btn = holder.itemView.findViewById(R.id.request_sent);
//                                request_sent_btn.setText("Request Sent");

                                holder.itemView.findViewById(R.id.decline_request).setVisibility(View.GONE);
                                holder.itemView.findViewById(R.id.accept_request).setVisibility(View.GONE);
                                holder.itemView.findViewById(R.id.request_sent).setVisibility(View.VISIBLE);
                                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.hasChild("imageUrl"))
                                        {
                                            final String requestProfileImage = dataSnapshot.child("imageUrl").getValue().toString();

                                            Picasso.get().load(requestProfileImage).into(holder.user_profile_image);
                                        }

                                        final String requestUserName = dataSnapshot.child("Name").getValue().toString();
                                        final String requestUserStatus = dataSnapshot.child("About").getValue().toString();

                                        holder.user_profile_name.setText(requestUserName);
                                        holder.txt.setText("you have already sent a request");


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Cancel Chat Request"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Already Sent Request to "+requestUserName );

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i)
                                                    {
                                                        if (i == 0)
                                                        {
                                                            cancelChatRequest(userIDs);
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }




            @NonNull
            @Override
            public RequestsFragment.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_request_layout,viewGroup,false);
                RequestsFragment.RequestsViewHolder viewHolder=new RequestsFragment.RequestsViewHolder(view);
                return viewHolder;
            }
        };
        requestList.setAdapter(adapter);
        adapter.startListening();
        norequests.setVisibility(View.GONE);
    }



    private void cancelChatRequest(String receiveUserId)
    {
        FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(currentUserId).child(receiveUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(receiveUserId).child(currentUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {

                                                FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(currentUserId).child(receiveUserId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(getContext(), "User added to your friendList", Toast.LENGTH_SHORT).show();

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

    private void acceptChatRequest(String receiveUserId)
    {
        friendsRef.child(currentUserId).child(receiveUserId).child("Friends").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            friendsRef.child(receiveUserId).child(currentUserId).child("Friends").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(currentUserId).child(receiveUserId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(receiveUserId).child(currentUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {

                                                                                        FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(currentUserId).child(receiveUserId).removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                    {
                                                                                                        if(task.isSuccessful())
                                                                                                        {
                                                                                                            Toast.makeText(getContext(), "User added to your friendList", Toast.LENGTH_SHORT).show();

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
                                        }
                                    });
                        }
                    }
                });
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView user_profile_image;
        TextView user_profile_name,txt;
        Button accept_request,decline_request;
        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile_name=itemView.findViewById(R.id.user_profile_name);
            txt=itemView.findViewById(R.id.txt);
            user_profile_image=itemView.findViewById(R.id.user_profile_image);
            accept_request=itemView.findViewById(R.id.accept_request);
            decline_request=itemView.findViewById(R.id.decline_request);
        }
    }


    private void receivedRequests()
    {

    }


    private void sentRequests()
    {

    }
}