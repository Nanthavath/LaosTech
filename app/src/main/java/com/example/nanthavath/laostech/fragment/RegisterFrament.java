package com.example.nanthavath.laostech.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.nanthavath.laostech.MainActivity;
import com.example.nanthavath.laostech.R;
import com.example.nanthavath.laostech.utility.MyAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class RegisterFrament extends Fragment {

//    Explicit

    private Uri uri;
    private ImageView imageView;
    private boolean aBoolean = true;
    private String nameString;
    private String emailString ;
    private String passwordString,uidString,pathURLString,
    myPostString;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Create toolbar
        createToolbar();

//        Photo Controller
        photoController();


    }   //Main Class

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_register, menu); //get item menu

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemUpload) {
            uploadProcess();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadProcess() {

        EditText nameEditText = getView().findViewById(R.id.edtName);
        EditText emailEditText = getView().findViewById(R.id.edtEmail);
        EditText passwordEditText = getView().findViewById(R.id.edtPassword);

//        get Value From EditText
        nameString = nameEditText.getText().toString().trim();
         emailString = emailEditText.getText().toString().trim();
         passwordString = passwordEditText.getText().toString().trim();

//        Check choose Photo
        if (aBoolean) {
//            None Choose Photo
            MyAlert myAlert = new MyAlert(getActivity());
            myAlert.normalDialog("None Choose Photo", "Please choose photo");

        } else if (nameString.isEmpty() || emailString.isEmpty() || passwordString.isEmpty()) {
//            Have Space
            MyAlert myAlert = new MyAlert(getActivity());
            myAlert.normalDialog("Have Space", "Please Fill all bank");

        } else {
//            No Space
            createAuthentication();
            uploadPhotoToFirebase();

        }

    }

    private void createAuthentication() {


        final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailString,passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            uidString = firebaseAuth.getCurrentUser().getUid();
                            Log.wtf("8AugV1", "uidString ==> " + uidString);  //Log.wtf for HUAWEI  Log.d for Other

                        } else {
                            MyAlert myAlert = new MyAlert(getActivity());
                            myAlert.normalDialog("Cannot Register",
                                    "Because==>" + task.getException().getMessage());

                        }

                    }
                });




    }

    private void uploadPhotoToFirebase() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference=firebaseStorage.getReference();
        StorageReference storageReference1 = storageReference.child("Avata/" + nameString);
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(),"Uplaod Success",Toast.LENGTH_SHORT).show();
                findPathUrlPhoto();
                createPost();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Uplaod Unsuccess",Toast.LENGTH_SHORT).show();

            }
        });



    }   //uploadPhoto

    private void createPost() {
        ArrayList<String> stringsArrayList = new ArrayList<String>();
        stringsArrayList.add("Hell");
        myPostString=stringsArrayList.toString();
        Log.wtf("9AugV2","myPost ==> " +
                myPostString);

    }

    private void findPathUrlPhoto() {
        try {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            final String[] urisStrings = new String[1];
            storageReference.child("Avata").child(nameString).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urisStrings[0] = uri.toString();
                            pathURLString = urisStrings[0];
                            Log.wtf("9AugV1", "patchURL ===>" +
                                    pathURLString);



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.wtf("9AugV1", "e Error==>" +
                            e.toString());

                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }




    }//

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            uri = data.getData();
            aBoolean = false;
            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                imageView.setImageBitmap(bitmap1);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getActivity(), "Please choose Photo", Toast.LENGTH_SHORT).show();
        }

    }

    private void photoController() {
        imageView = getView().findViewById(R.id.imvPhoto);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Open app picture
                startActivityForResult(Intent.createChooser(intent, "Please Choose App"), 1);   // get picture


            }
        });
    }

    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toolbarRegister);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Register");
        //((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Please Choose Photo and Fill All Blank");
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

}
