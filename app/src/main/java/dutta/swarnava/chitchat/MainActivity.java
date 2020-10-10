package dutta.swarnava.chitchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import dutta.swarnava.chitchat.JavaClasses.TabsAccessorAdapter;


public class MainActivity extends AppCompatActivity {
private TabsAccessorAdapter tabsAccessorAdapter;
    AlertDialog.Builder builder;
    FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    String currentUserID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.main_tabs_pager);
        TabLayout tabs = findViewById(R.id.main_tabs);
        tabs.setupWithViewPager(viewPager);
        Toolbar main_page_toolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_page_toolbar);
        getSupportActionBar().setTitle("Chit Chat");
//        currentUser = mAuth.getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        tabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessorAdapter);
        tabs.setupWithViewPager(viewPager);
        builder = new AlertDialog.Builder(this);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ContactList.class);
                startActivity(i);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        updateUserStatus("online");
    }
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//        updateUserStatus("online");
//    }
//    @Override
//    protected void onStop()
//    {
//        super.onStop();
//        updateUserStatus("offline");
//    }
//    @Override
//    protected void onDestroy()
//    {
//        super.onDestroy();
//        updateUserStatus("offline");
//    }

//    if(currentUser==null)
//    {
//        Intent i=new Intent(getApplicationContext(),Registration.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);
//        finish();
//    }
//    else
//    {
//        verifyUserExistance();
//    }
//}
//
//    private void verifyUserExistance() {
//        String currentUserId=mAuth.getCurrentUser().getUid();
//        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot)
//                    {
//                        if((snapshot.child("Name").exists()))
//                        {
//                            Toast.makeText(MainActivity.this, "Welcome "+snapshot.child("Name").getValue().toString(), Toast.LENGTH_SHORT).show();
//
//                        }
//                        else
//                        {
//                            Intent i=new Intent(getApplicationContext(),UserCredentials.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(i);
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.create_group)
        {
//            Intent i=new Intent(getApplicationContext(),NewGroup.class);
//            startActivity(i);
        }
        if(item.getItemId()==R.id.new_message)
        {
            Intent i=new Intent(getApplicationContext(),ContactList.class);
            startActivity(i);
        }
        if(item.getItemId()==R.id.settings)
        {
            Intent i=new Intent(getApplicationContext(),Settings.class);
            startActivity(i);
        }
        if(item.getItemId()==R.id.logout)
        {
            confirm_logout();

        }
        return true;
    }

    private void confirm_logout()
    {
        builder.setMessage("Would you like to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = getSharedPreferences("Reg",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("Phone_Number");
                        editor.commit();
                        mAuth.signOut();
                        Intent intent = new Intent(MainActivity.this, Registration.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Do you want to logout?");
        alert.show();
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
}