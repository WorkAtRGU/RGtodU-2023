package uk.ac.rgu.rgtodu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import uk.ac.rgu.rgtodu.data.Task;
import uk.ac.rgu.rgtodu.data.TaskPriority;
import uk.ac.rgu.rgtodu.data.TaskRepository;
import uk.ac.rgu.rgtodu.data.TaskStatus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskRecyclerViewFragment extends Fragment {

    private static final String TAG = "TaskRecyclerViewFrag";

    // the list of tasks being displayed
    List<Task> mTasks;
    // the RecyclerView adapter being used to display them
    RecyclerView.Adapter rvAdapter;

    public TaskRecyclerViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TaskListRecyclerViewActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskRecyclerViewFragment newInstance() {
        TaskRecyclerViewFragment fragment = new TaskRecyclerViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
			// there shouldn't be anything to update here
        }
		
		// create an empty list of data to be displayed
        this.mTasks = new ArrayList<Task>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_recycler_view_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get some sample tasks
        TaskRepository repo = TaskRepository.getRepository(getContext());
        List<Task> tasks = repo.getSyntheticTasks(1000);

        // get the RecycylerView on the UI
        RecyclerView rv = view.findViewById(R.id.rv_taskRecyclerView);

        // create a new Adapter for the RecyclerView
        RecyclerView.Adapter adapter = new TaskRecyclerViewAdapter(getContext(), tasks);
        // set the recycler view's adapter
        rv.setAdapter(adapter);
        // setup the layout manager on the recycler view
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // now get the Tasks from the remote endpoint
        // donwloadAllTasks();
        // commented out, as now we're using the local database
        mTasks.clear();
        List<Task> takss = TaskRepository.getRepository(getContext()).getAllTasks();
        mTasks.addAll(takss);
        rvAdapter.notifyDataSetChanged();
    }

    /**
     * Gets all of the Tasks in the remote Firebase Realtime Database
     * @return n a {@link List} of {@link Task} entities from the remote database
     */
    private void donwloadAllTasks(){
        // make my volley request
        String url = "https://cm3110-2023-default-rtdb.europe-west1.firebasedatabase.app/dcorsar.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // empty the list of tasks that are currently being displayed
                        mTasks.clear();
                        try {
                            JsonFirebaseTasksToTaskConverter converter = new JsonFirebaseTasksToTaskConverter();

                            // convert the respond to the root Json object
                            JSONObject jsonObject = new JSONObject(response);
                            mTasks.addAll(converter.convertJsonTasks(jsonObject));

                            // update the UI
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.download_error_json, Toast.LENGTH_LONG);

                        } finally {

                            Log.d(TAG, "downloaded " + mTasks.size() + " tasks");
                            Log.d(TAG, mTasks.toString());

                            // update the RecyclerView adapter
                            rvAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("tasks", error.getLocalizedMessage());
                    }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);
    }
}