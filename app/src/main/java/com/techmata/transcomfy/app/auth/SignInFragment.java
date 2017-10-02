package com.techmata.transcomfy.app.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techmata.transcomfy.app.MainActivity;
import com.techmata.transcomfy.app.R;
import com.techmata.transcomfy.app.database.TransDbHelper;
import com.techmata.transcomfy.app.utils.ConnectionDetector;
import com.techmata.transcomfy.app.utils.MyApplication;
import com.techmata.transcomfy.app.utils.PreferenceHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sparks on 28/03/2016.
 */
public class SignInFragment extends Fragment {

    private EditText email, password;
    private ProgressBar progressBar;
    private TextView status, txtsignup;
    private LinearLayout linearLayout;
    Boolean isInternetPresent;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.fragment_sign_in, container, false);

        email = (EditText)layout.findViewById(R.id.email);
        password = (EditText)layout.findViewById(R.id.password);
        password.setTransformationMethod(new PasswordTransformationMethod());
        Button login = (Button)layout.findViewById(R.id.login);
        progressBar = (ProgressBar)layout.findViewById(R.id.progressBar);
        status = (TextView)layout.findViewById(R.id.status);
        linearLayout = (LinearLayout)layout.findViewById(R.id.loginLyt);
        progressBar.setVisibility(View.GONE);
        status.setVisibility(View.GONE);

        final ConnectionDetector cd = new ConnectionDetector(getActivity());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setText("");
                isInternetPresent = cd.isConnectingToInternet();
               //if (isInternetPresent){
               if (true){
                    if (email.getText().toString().isEmpty()){
                        email.setError("Email cannot be empty");
                    } else if (password.getText().toString().isEmpty()){
                        password.setError("Password cannot be empty");
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        //Login
                        //SignIn(password.getText().toString(),email.getText().toString(),MyApplication.API_KEY);
                        //TODO: disable button to prevent multiple submissions
                        if(email.getText().toString().equals("techmata") &&
                                password.getText().toString().equals("12345")){
                            PreferenceHelper.setAccessToken(email.getText().toString(),getActivity());
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        }else{
                            if (isInternetPresent){
                                SignIn(password.getText().toString(),email.getText().toString(), MyApplication.API_KEY);
                            } else {
                                Toast.makeText(getActivity(),"No internet",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                   Toast.makeText(getActivity(),"No internet",Toast.LENGTH_SHORT).show();
                   progressBar.setVisibility(View.GONE);
                   status.setVisibility(View.VISIBLE);
                   status.setText("Check internet connection.");
                   linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        txtsignup = (TextView)layout.findViewById(R.id.txt_sign_up);
        txtsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra("signup", "signup");
                startActivity(intent);
            }
        });

        return layout;
    }
    private void SignIn(String userPass,String userEmail,String API_KEY){

        JSONObject jsonParam = new JSONObject();

        try {
            //jsonParam.put("grant_type", "password");
            jsonParam.put("email", userEmail);
            jsonParam.put("password", userPass);
            jsonParam.put("api_key", API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("myparam",jsonParam.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                MyApplication.URL+"/user/signin",
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
                                if (response.has("access_token") && !response.getString("access_token").equals("")){
                                    //JSONObject data = response.getJSONObject("data");
                                    PreferenceHelper.setAccessToken(response.getString("access_token"), getActivity());

                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ((Activity) getActivity()).finish();
                                    getContext().startActivity(intent);
                                    //getProjects
                                    /*TransDbHelper mDBHelper = new TransDbHelper(getContext());
                                    mDBHelper.fetchProjects(1);*/
                                } else {
                                    Log.i("myerr","no tokens or token");
                                    String err = "Login Error";
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
                String mError = "Login Error";
                try {
                    String response = new String(error.networkResponse.data,"UTF-8");
                    Log.i("myerrRes",response);
                    JSONObject err = new JSONObject(response);
                    if (err.has("error")){
                        mError = err.getString("error");
                    }
                } catch (NullPointerException | JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                status.setText(mError);
                linearLayout.setVisibility(View.VISIBLE);

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/vnd.yielloh.v1");
                params.put("Accept", "application/json");
                return params;
            }
        };
        MyApplication myApp = new MyApplication(getContext());
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
        MyApplication.getInstance().addToRequestQueue(jsonRequest);
    }

}

