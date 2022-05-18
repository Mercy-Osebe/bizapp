package com.test.bizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.test.bizapp.network.BizService;
import com.test.bizapp.network.SignupResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class SignUpActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText editTextPhone, editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;

    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setupClickListeners();


    }

    private void setupClickListeners(){
        TextView txtSignin = findViewById(R.id.txtSignin);

        txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
        progressBar = findViewById(R.id.progressBar);
        editTextPhone = findViewById(R.id.phoneId);
        editTextName = findViewById(R.id.nameId);
        editTextEmail = findViewById(R.id.emailId);
        editTextPassword = findViewById(R.id.passwordId);
        editTextConfirmPassword = findViewById(R.id.confirm_password_id);

        buttonRegister = findViewById(R.id.btnRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = editTextPhone.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirm_password = editTextConfirmPassword.getText().toString().trim();

//                callRegisterApi(phone,name,email, password);

                //validation
                if(phone.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
                } else if(name.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                }
                else if(confirm_password.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Confirm password is required", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(confirm_password)) {
                    Toast.makeText(SignUpActivity.this, "Confirm password does not match password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "All data is provided Yaaaaay!!!", Toast.LENGTH_SHORT).show();
                    // send request

                    callRegisterApi(phone,name, password, confirm_password);
                }


            }
        });

    }
    private void callRegisterApi(String phone, String name, String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-stage.mkononi.biz")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        BizService service = retrofit.create(BizService.class);

        service.signUp(phone, name, email, password).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful())
                {
                    //{"id":"54790ab1-052c-41bf-9983-62c7985e35ba","name":"12346789","email":"test2@gmail.com","phoneVerified":false,"phone":"+test1","createdAt":"2022-05-18T17:52:12.671Z","updatedAt":"2022-05-18T17:52:12.671Z"}
//                    Toast.makeText(SignUpActivity.this, resp, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(SignUpActivity.this,SideNavActivity.class));

                } else
                {
                    try {
                        String msg = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(msg);
                        String statusCode = jsonObject.getString("statusCode");

                        if( statusCode.equals("400"))
                        {
                            JSONArray message = jsonObject.getJSONArray("message");

                            StringBuffer errorMessage = new StringBuffer();
                            for(int x = 0; x < message.length(); x++)
                            {
                                errorMessage.append(message.get(x));
                            }
                            Toast.makeText(SignUpActivity.this, errorMessage.toString(), Toast.LENGTH_SHORT).show();
                        }else if( statusCode.equals("403"))
                        {
                            String message = jsonObject.getString("message");
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

