package com.techmata.transcomfy.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techmata.transcomfy.app.utils.ConnectionDetector;
import com.techmata.transcomfy.app.utils.MyApplication;
import com.techmata.transcomfy.app.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sparks on 28/03/2016.
 */
public class SignUpFragment extends Fragment {

    private EditText fname ,lname , phoneno, email, password, password_confirm;
    private ProgressBar progressBar;
    private TextView status,txtsignin;
    private LinearLayout linearLayout;
    Boolean isInternetPresent;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_sign_up, container, false);


        fname = (EditText)layout.findViewById(R.id.fname);
        lname = (EditText)layout.findViewById(R.id.lname);
        phoneno = (EditText)layout.findViewById(R.id.phone_no);
        email = (EditText)layout.findViewById(R.id.email);
        password = (EditText)layout.findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());
        password_confirm = (EditText)layout.findViewById(R.id.passwordConfirm);
        password_confirm.setTransformationMethod(new PasswordTransformationMethod());
        progressBar = (ProgressBar)layout.findViewById(R.id.progressBar);
        status = (TextView)layout.findViewById(R.id.status);
        linearLayout = (LinearLayout)layout.findViewById(R.id.signupLayout);
        progressBar.setVisibility(View.GONE);
        status.setVisibility(View.GONE);

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();

        Button register = (Button)layout.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetPresent){
                    if (email.getText().toString().isEmpty()) {
                        email.setError(getString(R.string.error_empty_fields));
                    } else if (fname.getText().toString().isEmpty()) {
                        fname.setError(getString(R.string.error_empty_fields));
                    } else if (lname.getText().toString().isEmpty()) {
                        lname.setError(getString(R.string.error_empty_fields));
                    } else if (phoneno.getText().toString().isEmpty()) {
                        phoneno.setError(getString(R.string.error_empty_fields));
                    } else if (password.getText().toString().isEmpty() || password.getText().toString().length() < 5) {
                        password.setError(getString(R.string.error_empty_fields));
                    } else if (password_confirm.getText().toString().isEmpty()) {
                        password_confirm.setError(getString(R.string.error_empty_fields));
                    } else if (!password_confirm.getText().toString().equalsIgnoreCase(password_confirm.getText().toString())) {
                        password_confirm.setError(getString(R.string.error_pass_no_match));
                    } else if (!MyApplication.isValidEmail(email.getText().toString())) {
                        email.setError("Invalid email");
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        //Register
                        SignUp(fname.getText().toString(),lname.getText().toString(),email.getText().toString(),phoneno.getText().toString(),password.getText().toString(), MyApplication.API_KEY);
                    }
                } else {
                    Toast.makeText(getActivity(),"No internet",Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtsignin = (TextView)layout.findViewById(R.id.txt_sign_in);
        txtsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra("signin", "signin");
                startActivity(intent);
            }
        });
        return layout;
    }
    private void SignUp(String fName,String lName,String userEmail,String PhoneNo,String userPass,String API_KEY){

        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("fname", fName);
            jsonParam.put("lname", lName);
            jsonParam.put("email", userEmail);
            jsonParam.put("password", userPass);
            jsonParam.put("phone_no", PhoneNo);
            jsonParam.put("api_key", API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("myparam",jsonParam.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                MyApplication.URL+"/user/signup",
                jsonParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){

                            try {
                                Log.i("myresp",response.toString(2));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (response.has("code") && response.getInt("code") == 200){
                                   Intent intent = new Intent(getActivity(), AuthActivity.class);
                                    intent.putExtra("signin","signin");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getActivity().finish();
                                    startActivity(intent);
                                } else {
                                    String err = "SignUp Error";
                                    if (response.has("error")){
                                        try {
                                            err = response.getString("error");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    progressBar.setVisibility(View.GONE);
                                    status.setVisibility(View.VISIBLE);
                                    status.setText(err);
                                    linearLayout.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            progressBar.setVisibility(View.GONE);
                            status.setVisibility(View.VISIBLE);
                            status.setText("Error, please try again");
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("myerr",error.toString());
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                status.setText("Registration Error");
                linearLayout.setVisibility(View.VISIBLE);

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return params;
            }
        };
        MyApplication myApp = new MyApplication(getContext());
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
        MyApplication.getInstance().addToRequestQueue(jsonRequest);
    }
}
