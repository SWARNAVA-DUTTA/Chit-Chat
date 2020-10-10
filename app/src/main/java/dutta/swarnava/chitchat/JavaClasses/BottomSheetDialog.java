package dutta.swarnava.chitchat.JavaClasses;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;
import dutta.swarnava.chitchat.R;

import static android.app.Activity.RESULT_OK;

public class BottomSheetDialog extends BottomSheetDialogFragment {
String checker="";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.send_files_bottom_sheet_layout,
                container, false);

        CircleImageView send_image = v.findViewById(R.id.send_image);
        CircleImageView send_document = v.findViewById(R.id.send_document);

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker="image";
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent,"Select image"));
                dismiss();
            }
        });

        send_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker="pdf";
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent.createChooser(intent,"Select file"),438);
                dismiss();
            }
        });
        return v;
    }


}
