package com.example.cse3311project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText lastNameInput, emailInput, idInput;
    Button submitButton, cancelButton;
    Spinner spinner;
    FirebaseAuth fAuth;
    private FirebaseDatabase databaseStart;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        idInput = findViewById(R.id.idInput);


        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);

        spinner = findViewById(R.id.registrationTypeInput);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (
                        this,
                        R.array.registrationTypeArray,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource
                (
                        android.R.layout.simple_spinner_dropdown_item
                );
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        fAuth = FirebaseAuth.getInstance();

        // If a user is already signed in, they should not be on the registration page
        // They should instead be redirected to the homepage
        if(fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                databaseStart = FirebaseDatabase.getInstance();
                database = databaseStart.getReference( "users");
                String password = idInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String accountType = spinner.getSelectedItem().toString();
                String[] emailSplit = email.split("@");

                if (TextUtils.isEmpty(password))
                {
                    idInput.setError("A password is required");
                }

                if (password.length() < 8)
                {
                    idInput.setError("Password must be 8 characters or greater");
                }

                if (TextUtils.isEmpty(email))
                {
                    emailInput.setError("Email is Required.");
                }
                else if (!email.contains("@"))
                {
                    emailInput.setError("Please enter a valid email address");
                }
                else if (!(emailSplit[1].equals("mavs.uta.edu") || emailSplit[1].equals("uta.edu")))
                {
                    emailInput.setError("Must be a UTA email");
                }
                else
                {
                    // Registration of the user in the database. If the user is successfully registered, show a toast and redirect to login page
                    // if a user is not successfully registered, show the error that occurred
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                //database = databaseStart.getReference( "userList");
                                database.child(accountType).push().setValue(currentFirebaseUser.getUid());
                                Toast.makeText(Registration.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            } else {
                                Toast.makeText(Registration.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        Toast.makeText(parent.getContext(),"Selection was made",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
