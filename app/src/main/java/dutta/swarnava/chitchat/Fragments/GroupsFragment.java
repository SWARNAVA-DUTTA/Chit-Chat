package dutta.swarnava.chitchat.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import dutta.swarnava.chitchat.GroupChatActivity;
import dutta.swarnava.chitchat.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View groupfragmentview;
//    myadapter adapter;
//    RecyclerView recView;
    ListView listView;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups=new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupsFragment() {
        // Required empty public constructor
    }
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupfragmentview= inflater.inflate(R.layout.fragment_groups, container, false);
        retrieveGroups();
        listView=(ListView) groupfragmentview.findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentGroupName=adapterView.getItemAtPosition(i).toString();
                Intent groupChatIntent=new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);
            }
        });
//        recView=(RecyclerView)groupfragmentview.findViewById(R.id.recView);
//        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        FirebaseRecyclerOptions<group_model> options =
//                new FirebaseRecyclerOptions.Builder<group_model>()
//                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Groups"), group_model.class)
//                        .build();
//        adapter= new myadapter(options);
//        recView.setAdapter(adapter);
//        adapter.setOnItemClickListener(new myadapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position)
//            {
//
//            }
//        });
        return groupfragmentview;
    }

    private void retrieveGroups()
    {
//        databaseReference=firebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Groups");
        databaseReference=firebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while(iterator.hasNext())
                {
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
}