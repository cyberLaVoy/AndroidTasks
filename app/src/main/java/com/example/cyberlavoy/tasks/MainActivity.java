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
        RequestHandler.getInstance(getApplicationContext()).handlePOSTRequest(url, body, null, null);
    }
    /*
    short_description
    long_description
    priority
    desired_completion_date
    due_date
    completion_status
    */
    public void createTask(Map<String, String> creationParams) {
        String url = "https://afternoon-wave-54596.herokuapp.com/todos";
        RequestHandler.getInstance(getApplicationContext()).handlePOSTRequest(url, creationParams, null, null);
    }
    public void updateTask(String taskID, Map<String, String> updates) {
        String url = "https://afternoon-wave-54596.herokuapp.com/todos/" + taskID;
        RequestHandler.getInstance(getApplicationContext()).handlePUTRequest(url, updates, null, null);
    }
    public void deleteTask(String taskID, Map<String, String> body) {
        String url = "https://afternoon-wave-54596.herokuapp.com/todos/" + taskID;
        RequestHandler.getInstance(getApplicationContext()).handleDELETERequest(url, body, null, null);
    }
    public void Authenticate(String email, String password) {
        String url = "https://afternoon-wave-54596.herokuapp.com/sessions";
        Map<String, String> body = new HashMap<String, String>();
        body.put("email", email);
        body.put("password", password);
        RequestHandler.getInstance(getApplicationContext()).handlePOSTRequest(url, body, null, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Map<String,String> updates = new HashMap<>();
                updates.put("priority", "27");
                updateTask("1", updates);
                deleteTask("3", updates);
                getTasks();
                return 1;
            }
        });
    }

    public void getTasks() {
        String url = "https://afternoon-wave-54596.herokuapp.com/todos";
        final String[] onResponseArray = new String[1];
        RequestHandler.getInstance(getApplicationContext()).handleGETRequest(url, onResponseArray, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                updateUI(onResponseArray);
                return 1;
            }
        });
    }

    public void updateUI(String[] onResponseArray) {
        Toast.makeText(this, onResponseArray[0], Toast.LENGTH_LONG).show();
    }
}
