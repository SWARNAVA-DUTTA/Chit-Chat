package dutta.swarnava.chitchat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import dutta.swarnava.chitchat.Adapters.MessageAdapter;
import dutta.swarnava.chitchat.JavaClasses.Messages;

public class GroupChatActivity extends AppCompatActivity {
    Toolbar group_chat_toolbar;
    RecyclerView private_chat_list;
    TextView custom_profile_name,userLastSeen;
    CircleImageView custom_profile_image;
    EditText input_message;
    ImageButton send_btn,send_files_btn;
    ProgressDialog loading;
    private String saveCurrentTime, saveCurrentDate;

    Uri fileUri;
    StorageTask uploadTask;
    String messageSenderId,messageReceiverId,messageReceiverName,messageReceiverImage,myUrl="",checker="",currentUserID="";
    DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        group_chat_toolbar=(Toolbar)findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(group_chat_toolbar);
        loading=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        messageSenderId=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();
        messageReceiverName = getIntent().getExtras().get("messageReceiverName").toString();
        messageReceiverImage = getIntent().getExtras().get("messageReceiverImage").toString();
        messageReceiverId=getIntent().getExtras().get("messageReceiverId").toString();

        Initialize();
        custom_profile_name.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).into(custom_profile_image);

        input_message=findViewById(R.id.input_message);
        send_btn=findViewById(R.id.send_btn);
        send_files_btn=findViewById(R.id.send_files_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, currentGroupName, Toast.LENGTH_SHORT).show();
        getSupportActionBar().setTitle(currentGroupName);

    }

    private void Initialize()
    {
        group_chat_toolbar =  findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(group_chat_toolbar);
        group_chat_toolbar.setContentInsetStartWithNavigation(0);
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

        messageAdapter=new MessageAdapter(messagesList,GroupChatActivity.this);
        private_chat_list=findViewById(R.id.private_chat_list);
        linearLayoutManager=new LinearLayoutManager(this);
        private_chat_list.setLayoutManager(linearLayoutManager);
        private_chat_list.setAdapter(messageAdapter);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}