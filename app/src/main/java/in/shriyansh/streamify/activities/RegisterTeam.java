package in.shriyansh.streamify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.shriyansh.streamify.R;
import in.shriyansh.streamify.ui.LabelledSpinner;
import in.shriyansh.streamify.utils.Constants;
import in.shriyansh.streamify.utils.PreferenceUtils;

import static in.shriyansh.streamify.network.Urls.ADD_MEMBERS;
import static in.shriyansh.streamify.network.Urls.CREATE_TEAM;

public class RegisterTeam extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btn_register_team;
    private LinearLayout mem2_container;
    private LinearLayout mem3_container;
    private LinearLayout mem4_container;
    private LinearLayout mem5_container;

    private EditText team_name;
    private EditText leader_roll;
    private EditText team_member_roll;

    private RequestQueue volleyQueue;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_team);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        volleyQueue = Volley.newRequestQueue(this);

        final int mem_num = PreferenceUtils.getIntegerPreference(RegisterTeam.this, PreferenceUtils.PREF_MEM_NUM);
        final int event_id = PreferenceUtils.getIntegerPreference(RegisterTeam.this, PreferenceUtils.PREF_USER_EVENT);

        createFormFields(mem_num);

        btn_register_team = findViewById(R.id.register_confirm);
        btn_register_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendTeamDetails(mem_num, event_id);

                Intent intent = new Intent(RegisterTeam.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void createFormFields(int mem_num) {

        mem2_container = findViewById(R.id.mem2_container);
        mem3_container = findViewById(R.id.mem3_container);
        mem4_container = findViewById(R.id.mem4_container);
        mem5_container = findViewById(R.id.mem5_container);

        if (mem_num<=1) {
            mem2_container.setVisibility(View.GONE);
            mem3_container.setVisibility(View.GONE);
            mem4_container.setVisibility(View.GONE);
            mem5_container.setVisibility(View.GONE);
        }

        else if (mem_num<=2) {
            mem3_container.setVisibility(View.GONE);
            mem4_container.setVisibility(View.GONE);
            mem5_container.setVisibility(View.GONE);
        }

        else if (mem_num<=3) {
            mem4_container.setVisibility(View.GONE);
            mem5_container.setVisibility(View.GONE);
        }

        else if (mem_num<=4) {
            mem5_container.setVisibility(View.GONE);
        }

    }

    private void sendTeamDetails(int mem_num, int event_id) {

        sendLeaderDetails(event_id);
        addTeamMembers(mem_num, PreferenceUtils.getIntegerPreference(RegisterTeam.this, PreferenceUtils.PREF_TEAM_ID));

    }

    private void sendLeaderDetails(int event_id) {

        leader_roll = findViewById(R.id.leader_roll);
        team_name = findViewById(R.id.team_name);

        String rollNo = leader_roll.getText().toString();
        String teamname = team_name.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("rollNo", rollNo);
        params.put("team_name", teamname);
        params.put("event_id", Integer.toString(event_id));
        Log.e(TAG, params.toString());

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                CREATE_TEAM, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    Log.e(TAG, status);
                    int team_id = response.getInt("team_id");

                    if (status.equals("OK")) {
                        PreferenceUtils.setIntegerPreference(RegisterTeam.this, PreferenceUtils.PREF_TEAM_ID, team_id);
                        Toast.makeText(RegisterTeam.this, "YAY!!!!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(RegisterTeam.this, "OOPS something went wrong here!!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.toString());
                Toast.makeText(RegisterTeam.this, "OOPS something went wrong here too!!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.HTTP_HEADER_CONTENT_TYPE_KEY,
                        Constants.HTTP_HEADER_CONTENT_TYPE_JSON);
                return headers;
            }
        };

    }

    private void addTeamMembers(int mem_num, int team_id) {

        for (int i=1; i<mem_num; i++) {

            if (i==1) {
                team_member_roll = findViewById(R.id.mem2roll);
            }

            else if (i==2) {
                team_member_roll = findViewById(R.id.mem3roll);
            }

            else if (i==3) {
                team_member_roll = findViewById(R.id.mem4roll);
            }

            else if (i==4) {
                team_member_roll = findViewById(R.id.mem5roll);
            }

            String rollNo = team_member_roll.getText().toString();

            Map<String, String> params = new HashMap<>();
            params.put("rollNo", rollNo);
            params.put("team_id", Integer.toString(team_id));
            Log.e(TAG, params.toString());

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                    ADD_MEMBERS, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        Log.e(TAG, status);

                    } catch (JSONException e) {
                        e.printStackTrace();

                        Toast.makeText(RegisterTeam.this, "OOPS something went wrong here again!!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.toString());
                    Toast.makeText(RegisterTeam.this, "OOPS something went wrong!!", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put(Constants.HTTP_HEADER_CONTENT_TYPE_KEY,
                            Constants.HTTP_HEADER_CONTENT_TYPE_JSON);
                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.HTTP_INITIAL_TIME_OUT,
                    Constants.HTTP_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            volleyQueue.add(stringRequest);
        }

    }
}
