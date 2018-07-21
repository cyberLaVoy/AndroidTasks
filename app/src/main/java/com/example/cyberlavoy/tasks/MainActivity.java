package com.example.cyberlavoy.tasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CreateUser("yoda@gmail.com", "theforce", "Yoda", "Master");
        Authenticate("yoda@gmail.com", "theforce");
    }

    // POST methods
    public void CreateUser(String email, String password, String fname, String lname) {
        String url = "https://afternoon-wave-54596.herokuapp.com/users";
        Map<String, String> body = new HashMap<String, String>();
        body.put("email", email);
        body.put("password", password);
        body.put("fname", fname);
        body.put("lname", lname);
        RequestHandler.getInstance(getApplicationContext()).handlePostRequest(url, body, null);
    }
    public void Authenticate(String email, String password) {
        String url = "https://afternoon-wave-54596.herokuapp.com/sessions";
        Map<String, String> body = new HashMap<String, String>();
        body.put("email", email);
        body.put("password", password);
        RequestHandler.getInstance(getApplicationContext()).handlePostRequest(url, body, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return getTasks();
            }
        });
    }

    // GET methods
    public Integer getTasks() {
        String url = "https://afternoon-wave-54596.herokuapp.com/todos";
        RequestHandler.getInstance(getApplicationContext()).handleGetRequest(url, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return updateUI();
            }
        });
        return 1;
    }

    public Integer updateUI() {
        Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        return 1;
    }
}
