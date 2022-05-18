package com.test.bizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.test.bizapp.network.BizService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText editTextPhone,editTextPassword;
    Button btnLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        setupClickListeners();
    }

    private void setupClickListeners(){
        progressBar = findViewById(R.id.progressBar);
        editTextPhone = findViewById(R.id.phoneId);
        editTextPassword = findViewById(R.id.passwordId);

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = editTextPhone.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                //validation
                if(phone.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(password)) {
                    Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                } else {
                    // send request

                    callLoginApi(phone, password);
                }


            }
        });

    }
    //{ "name": "test", "email": "test@gmail.com", "phone": "12132", "password": "123" }
    private void callLoginApi(String phone, String password){
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-stage.mkononi.biz")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        BizService service = retrofit.create(BizService.class);

        service.login(phone, password).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful())
                {
                    startActivity(new Intent(LoginActivity.this,SideNavActivity.class));
                    finish();

                } else
                {
                    try {
                        String msg = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(msg);
                        String statusCode = jsonObject.getString("statusCode");

                         if( statusCode.equals("401"))
                        {
                            String message = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                        startActivity(new Intent(LoginActivity.this,SideNavActivity.class));
                        finish();
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
                Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}