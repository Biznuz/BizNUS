package com.example.biznus;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.ActivityNavigatorDestinationBuilderKt;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    ImageView close, image;
    TextView list;
    EditText editTextTitle, editTextPrice, editTextDescription;
    ImageButton imageButton;
    ProgressDialog progressDialog;
    Spinner conditionSpinner;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Listings");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



        close = findViewById(R.id.close);
        image = findViewById(R.id.image);
        list = findViewById(R.id.list);
        editTextTitle = findViewById(R.id.title);
        editTextPrice = findViewById(R.id.price);
        editTextDescription = findViewById(R.id.description);
        imageButton = findViewById(R.id.addImage);
        conditionSpinner = findViewById(R.id.condtionSpinner);


        String[] conditionList = getResources().getStringArray(R.array.conditions);

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals(conditionList[0])) {
                    ((TextView) view).setTextColor(Color.GRAY);
                } else {
                    ((TextView) view).setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, conditionList) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                //set the color of first item in the drop down list to gray
                if (position == 0) {
                    view.setTextColor(Color.GRAY);
                } else {
                    //here it is possible to define color for other items by
                    view.setTextColor(Color.WHITE);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        conditionSpinner.setAdapter(adapter);


        // TODO: upload to database

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null) {
                    Toast.makeText(ListActivity.this, "Image Required", Toast.LENGTH_SHORT).show();
                } else if (editTextTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ListActivity.this, "Title Required", Toast.LENGTH_SHORT).show();
                    editTextTitle.setError("Title Required");
                    editTextTitle.requestFocus();
                } else if (editTextPrice.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ListActivity.this, "Price Required", Toast.LENGTH_SHORT).show();
                    editTextPrice.setError("Price Required");
                    editTextPrice.requestFocus();
                } else if (editTextDescription.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ListActivity.this, "Description Required", Toast.LENGTH_SHORT).show();
                    editTextDescription.setError("Description Required");
                    editTextDescription.requestFocus();
                } else if (conditionSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(ListActivity.this, "Condition Required", Toast.LENGTH_SHORT).show();
                    editTextDescription.setError("Select Condition");
                    editTextDescription.requestFocus();
                } else {
                    uploadImage();
                }
            }
        });


    }

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Listing...");
        progressDialog.show();

        StorageReference fileREF = storageReference.child("images/"+System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileREF.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        myUrl = uri.toString();
                        image.setImageURI(null);

                        String listID = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("listID", listID);
                        hashMap.put("listImage", myUrl);
                        hashMap.put("title", editTextTitle.getText().toString());
                        hashMap.put("price", editTextPrice.getText().toString());
                        hashMap.put("description", editTextDescription.getText().toString());
                        hashMap.put("condition", conditionSpinner.getSelectedItem());
                        hashMap.put("lister", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(listID).setValue(hashMap);

                        Toast.makeText(ListActivity.this, "Successfully Listed", Toast.LENGTH_SHORT).show();

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        Intent intent = new Intent(ListActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }
}