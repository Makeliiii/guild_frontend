package project.guild;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button BtnAddView;
    private Button BtnEditJob;

    String idUser;
    List<Job> jobList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = this.getSharedPreferences(
                "Guild", Context.MODE_PRIVATE);
        idUser = prefs.getString("Guild.idUser", "");

        RequestQueue queue = Volley.newRequestQueue(this);
        String domain = getResources().getString(R.string.domain);
        String url = domain + "/api/availableJobs/"+idUser;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jobs = response.getJSONArray("jobs");

                    for (int i = 0; i < jobs.length(); i++) {
                        JSONObject job = jobs.getJSONObject(i);

                        String title = job.getString("title");
                        String description = job.getString("description");
                        String phone = job.getString("phone");
                        String id = job.getString("_id");

                        JSONObject salaryObj = job.getJSONObject("salary");
                        String salary = salaryObj.getString("$numberDecimal");

                        JSONArray locationArr = job.getJSONArray("location");
                        String location = locationArr.getString(0);

                        jobList.add(new Job(title, description, location, phone, salary + "€", id));
                    }
                    Log.i("mylog","request completed");
                    JobListAdapter adapter = new JobListAdapter(MainActivity.this, R.layout.jobs, jobList);
                    listView.setAdapter(adapter);
                    Log.i("mylog","set adapter");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });

        queue.add(jsonObjectRequest);

        BtnEditJob = findViewById(R.id.BtnEditJob);
        BtnEditJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToactivity_edit_job_view();
            }
        });

        BtnAddView = findViewById(R.id.BtnAddJob);

        jobList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        BtnAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToactivity_add_job_view();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent jobIntent = new Intent(getApplicationContext(), DetailedJobView.class);
                jobIntent.putExtra("JOB", (Serializable) jobList.get(position));
                startActivity(jobIntent);
            }
        });
    }

    private void moveToactivity_add_job_view(){
        Intent intent = new Intent(MainActivity.this, AddJobView.class);
        intent.putExtra("idUser", idUser);
        startActivity(intent);
        finish();
    }

    private void moveToactivity_edit_job_view(){
        Intent intent = new Intent(MainActivity.this, EditJobView.class);
        intent.putExtra("idUser", idUser);
        startActivity(intent);
        finish();
    }
}
