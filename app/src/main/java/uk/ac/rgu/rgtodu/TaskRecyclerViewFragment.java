package uk.ac.rgu.rgtodu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.ac.rgu.rgtodu.data.JsonFirebaseTasksToTaskConverter;
import uk.ac.rgu.rgtodu.data.Task;
import uk.ac.rgu.rgtodu.data.TaskRepository;
import uk.ac.rgu.rgtodu.data.TasksViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskRecyclerViewFragment extends Fragment {

    private static final String TAG = "TaskRecyclerViewFrag";

    // ViewModel for the tasks
    private TasksViewModel mTasksViewModel;

    // the list of tasks being displayed
    LiveData<List<Task>> mTasks;
    // the RecyclerView adapter being used to display them
    RecyclerView.Adapter rvAdapter;



    public TaskRecyclerViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *

     * @return A new instance of fragment TaskListRecyclerViewActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskRecyclerViewFragment newInstance() {
        TaskRecyclerViewFragment fragment = new TaskRecyclerViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        // get a ViewModelProvider for this fragment
        ViewModelProvider provider = new ViewModelProvider(this);
        // now get the ViewModel for Tasks
        this.mTasksViewModel = provider.get(TasksViewModel.class);

        // now get all the tasks
        this.mTasks = this.mTasksViewModel.getAllTasks();
        // now observe any changes
        this.mTasks.observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if (rvAdapter != null){
                    rvAdapter.notifyDataSetChanged();
                }
            }
        });


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

        // setup the RecyclerView

        // get the RecycylerView on the UI
        RecyclerView rv = view.findViewById(R.id.rv_taskRecyclerView);

        // create a new Adapter for the RecyclerView with the empty list
        rvAdapter = new TaskRecyclerViewAdapter(getContext(), this.mTasks);
        // set the recycler view's rv_adapter
        rv.setAdapter(rvAdapter);
        // setup the layout manager on the recycler view
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // now get the Tasks from the remote endpoint
        // donwloadAllTasks();
        // above commented out, as now we're using the local database

        // below commented out as using LiveData and ViewModel
//        mTasks.clear();
//        List<Task> tasks = TaskRepository.getRepository(getContext()).getAllTasks();
//        mTasks.addAll(takss);
//        rvAdapter.notifyDataSetChanged();
    }

    /**
     * Gets all of the Tasks in the remote Firebase Realtime Database
     * @return n a {@link List} of {@link Task} entities from the remote database
     */
    private void donwloadAllTasks(){
        // make my volley request
        String url = "https://cm3110-2022-default-rtdb.firebaseio.com/dcorsar.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // empty the list of tasks that are currently being displayed
                        List<Task> tasks = new ArrayList<>();
                        try {
                            JsonFirebaseTasksToTaskConverter converter = new JsonFirebaseTasksToTaskConverter();

                            // convert the respond to the root Json object
                            JSONObject jsonObject = new JSONObject(response);
                            tasks.addAll(converter.convertJsonTasks(jsonObject));

                            // update the UI
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.download_error_json, Toast.LENGTH_LONG);

                        } finally {

                            Log.d(TAG, "downloaded " + tasks.size() + " tasks");
                            Log.d(TAG, mTasks.toString());

                            // update the RecyclerView adapter
                            rvAdapter.notifyDataSetChanged();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error with downloading ");
                // display message to the user
                Toast.makeText(getContext(), R.string.download_error, Toast.LENGTH_LONG);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);

    }
}