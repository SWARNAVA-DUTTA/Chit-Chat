package dutta.swarnava.chitchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import dutta.swarnava.chitchat.ChatActivity;
import dutta.swarnava.chitchat.JavaClasses.Contacts;
import dutta.swarnava.chitchat.R;
import dutta.swarnava.chitchat.User_Profile;

import static android.content.ContentValues.TAG;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private List<Contacts> contactVOList;
    private Context mContext;
    public ContactAdapter(List<Contacts> contactVOList, Context mContext){
        this.contactVOList = contactVOList;
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_display_layout, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contacts contactVO = contactVOList.get(position);
        holder.ContactName.setText(contactVO.getName());
        holder.ContactAbout.setText(contactVO.getAbout());
        Picasso.get().load(contactVO.getImageUrl()).into(holder.ContactImage);
        holder.L1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    Intent i=new Intent(mContext, User_Profile.class);
//    i.putExtra("messageReceiverName",contactVOList.get(position).getName());
//    i.putExtra("messageReceiverImage",contactVOList.get(position).getImageUrl());
    i.putExtra("messageReceiverId",contactVOList.get(position).getUid());
    mContext.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        ImageView ContactImage;
        TextView ContactName;
        TextView ContactAbout;
        LinearLayout L1;
//        CheckBox mAdd;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ContactImage = (ImageView) itemView.findViewById(R.id.user_profile_image);
            ContactName = (TextView) itemView.findViewById(R.id.user_profile_name);
            ContactAbout = (TextView) itemView.findViewById(R.id.user_about);
            L1=itemView.findViewById(R.id.L1);
        }

    }

}
