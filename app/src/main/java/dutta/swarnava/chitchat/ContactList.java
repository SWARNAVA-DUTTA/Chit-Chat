package dutta.swarnava.chitchat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;

import dutta.swarnava.chitchat.Adapters.ContactAdapter;
import dutta.swarnava.chitchat.JavaClasses.Contacts;

public class ContactList extends AppCompatActivity {
    private RecyclerView recView;
    private boolean checkPermission=false;
    Toolbar toolbar;
    ContactAdapter contactAdapter;
    TextView select_contact,no_of_contacts;
    LinearLayout new_grp;
    ArrayList<Contacts> userList=new ArrayList<>();
    ArrayList<Contacts> contactVOList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        recView = (RecyclerView) findViewById(R.id.userList);
        recView.setLayoutManager(new LinearLayoutManager(this));
        new_grp=(LinearLayout)findViewById(R.id.new_grp);
        Initialize();

        new_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i=new Intent(getApplicationContext(),NewGroup.class);
//                startActivity(i);
            }
        });
        Dexter.withActivity(ContactList.this)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {checkPermission=true;getAllContact();}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {checkPermission=false;}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {token.continuePermissionRequest();}
                }).check();

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void getAllContact()
    {

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");
        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = "+91" + phone;

            Contacts mContact = new Contacts("","",name, phone,"");
            contactVOList.add(mContact);
            getUserDetails(mContact);
        }


    }

    private void getUserDetails(Contacts mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = mUserDB.orderByChild("Phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  about="",
                            phone = "",
                            name = "",
                            image="";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("About").getValue()!=null)
                            about = childSnapshot.child("About").getValue().toString();
                        if(childSnapshot.child("Phone").getValue()!=null)
                            phone = childSnapshot.child("Phone").getValue().toString();
                        if(childSnapshot.child("imageUrl").getValue()!=null)
                            image = childSnapshot.child("imageUrl").getValue().toString();
                        if(childSnapshot.child("Name").getValue()!=null)
                            name = childSnapshot.child("Name").getValue().toString();
                        Contacts contactVO=new Contacts(childSnapshot.getKey(),image,name,phone,about);
                            for(Contacts mContactIterator : contactVOList){
                                if(mContactIterator.getPhone().equals(contactVO.getPhone())){
                                    contactVO.setName(mContactIterator.getName());
                                }
                            }

                        userList.add(contactVO);
                        contactAdapter = new ContactAdapter(userList, getApplicationContext());
                        recView.setAdapter(contactAdapter);
                        contactAdapter.notifyDataSetChanged();
                        no_of_contacts.setText(userList.size()+" contacts");
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void Initialize()
    {
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.contact_list_action_bar);
        View actionBarView=getSupportActionBar().getCustomView();
        getSupportActionBar().setCustomView(actionBarView);
        select_contact=findViewById(R.id.select_contact);
        no_of_contacts=findViewById(R.id.no_of_contacts);
    }
}