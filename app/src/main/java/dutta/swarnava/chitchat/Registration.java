package dutta.swarnava.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {
    EditText edt_ph;
    Button btn_send;
    TextView msg,ctry_code,carriercharges;
    String phone="";
    Toolbar toolbar;
    AlertDialog.Builder builder;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ctry_code=findViewById(R.id.ctry_code);
        carriercharges=(TextView)findViewById(R.id.carriercharges);
        edt_ph=findViewById(R.id.edt_ph);
//        edt_ph.requestFocus();
        msg=findViewById(R.id.msg);
        msg.setText("Chit Chat will send an SMS message to verify your phone number");
        btn_send=findViewById(R.id.btn_send);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter your phone Number");
        builder = new AlertDialog.Builder(this);
    }

    //This Button has to be clicked to initiate the process
    @Override
    protected void onStart() {

        super.onStart();
        if(currentUser!=null)
        {
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
    }
    public void start_process(View view) {
        if(edt_ph.getText().toString().isEmpty()){
            edt_ph.setError("");
            provide_phone_number_dialog();
        }
        else{
            confirm_phone_number();
        }
    }
public void provide_phone_number_dialog()
{
    builder.setMessage("Please enter your phone number.")
            .setCancelable(false)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    edt_ph.setFocusable(true);
                }
            });
    AlertDialog alert = builder.create();
    alert.setTitle("");
    alert.show();
}
    public void confirm_phone_number()
    {
        builder.setMessage("Is this OK, or would you like to change the number?")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        phone=ctry_code.getText().toString()+edt_ph.getText().toString();
                        Intent intent = new Intent(getApplicationContext(), OtpVerification.class);
                        intent.putExtra("message_key", phone);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        edt_ph.requestFocus();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("We will be verifying the phone number: "+ctry_code.getText().toString()+edt_ph.getText().toString());
        alert.show();
    }
}
