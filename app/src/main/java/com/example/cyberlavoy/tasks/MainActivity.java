package com.example.cyberlavoy.tasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
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
        RequestHandler.getInstance(getApplicationContext()).handlePOSTRequest(url, body, null);
    }
    /*
    short_description
    long_description
    priority
    desired_completion_date
    due_date
    date_entered
    completion_status
    */
    public void createTask(String shortDescription) {
        String url = "https://afternoon-wave-54596.herokuapp.com/todos";
        Map<String, String> body = new HashMap<String, String>();
        body.put("short_description", shortDescription);
        RequestHandler.getInstance(getApplicationContext()).handlePOSTRequest(url, body, null);
    }
    public void Authenticate(String email, String password) {
        String url = "https://afternoon-wave-54596.herokuapp.com/sessions";
        Map<String, String> body = new HashMap<String, String>();
        body.put("email", email);
        body.put("password", password);
        RequestHandler.getInstance(getApplicationContext()).handlePOSTRequest(url, body, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                createTask("Something.");
                getTasks();
                return 1;
            }
        });
    }

    // GET methods
    public void getTasks() {
        String url = "https://afternoon-wave-54596.herokuapp.com/todos";
        final ArrayList onResponseArray = new ArrayList();
        RequestHandler.getInstance(getApplicationContext()).handleGETRequest(url, onResponseArray, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                updateUI(onResponseArray);
                return 1;
            }
        });
    }

    public void updateUI(ArrayList<String> onResponseArray) {
        Toast.makeText(this, onResponseArray.get(0), Toast.LENGTH_LONG).show();
    }
}
