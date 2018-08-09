package com.example.nanthavath.laostech.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanthavath.laostech.R;
import com.example.nanthavath.laostech.ServiceActivity;
import com.example.nanthavath.laostech.utility.MyAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainFragment extends Fragment {
    private String emailString,passwordString;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Check Status
        checkStatus();

//        Register Controller
        registerController();

//        Login Controller
        loginController();

    }   // Method Main

    private void loginController() {

        Button button = getView().findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emaileditText = getView().findViewById(R.id.edtEmail);
                EditText passwordeditText = getView().findViewById(R.id.edtPassword);
                emailString = emaileditText.getText().toString().trim();
                passwordString = passwordeditText.getText().toString().trim();

                if (emailString.isEmpty() || passwordString.isEmpty()) {

                    MyAlert myAlert = new MyAlert(getActivity());
                    myAlert.normalDialog("Have Space","Please fill all bank");

                } else {
                    checkAuthen();

                }

            }
        });
    }

    private void checkAuthen() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),"Wellcome",Toast.LENGTH_SHORT).show();
//                            getActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.contentMainFragment,new ServiceFragment()).commit();
                            moveToService();
                        } else {
                            MyAlert myAlert = new MyAlert(getActivity());
                            myAlert.normalDialog("Authen false","Because " +
                                    task.getException().getMessage());
                        }
                    }
                });
    }

    private void checkStatus() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.contentMainFragment, new ServiceFragment()).commit();
            moveToService();
        }
    }

    private void moveToService() {
        startActivity(new Intent(getActivity(), ServiceActivity.class));
        getActivity().finish();

    }

    private void registerController() {
        TextView textView = getView().findViewById(R.id.txtRegister);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Replace Fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentMainFragment, new RegisterFrament())
                        .addToBackStack(null).commit();

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;


    }
}
