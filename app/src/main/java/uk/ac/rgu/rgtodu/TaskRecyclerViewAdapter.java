package uk.ac.rgu.rgtodu;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.rgu.rgtodu.data.Task;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    // member variables for the context the adapter is working in
    private Context context;
    // the data thats going to be displayed
    LiveData<List<Task>> tasks;

    // TAG for logging
    private static final String TAG = "TaskRecyclerViewAdapter";

    /**
     * Creates a new {@link TaskRecyclerViewAdapter}
     * @param context that the adapter is working in
     * @param tasks data to be displayed
     */
    public TaskRecyclerViewAdapter(Context context, LiveData<List<Task>> tasks){
        super();
        // initialise the member variables
        this.context = context;
        this.tasks = tasks;
    }

    /**
     * Set the data being displayed in this adapter to be tasks
     * @param tasks The {@link List} of {@link Task}s to be displayed
     */
    public void setTasks(LiveData<List<Task>> tasks){
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the layout file for the row
        View itemView = LayoutInflater.from(this.context)
                .inflate(R.layout.task_list_view_item, parent, false);
        // store it in a ViewHolder
        TaskViewHolder viewHolder = new TaskViewHolder(itemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // get the task at position
        Task task = this.tasks.getValue().get(position);

        // update the task name
        TextView tv_taskName = holder.itemView.findViewById(R.id.tv_taskListItemName);
        tv_taskName.setText(task.getName());

        // update the pomodoros
        TextView tv_taskPom = holder.itemView.findViewById(R.id.tv_taskListItemPomodoros);
        String msg = context.getString(R.string.tv_taskListPomodoros, task.getPomodorosRemaining());
        tv_taskPom.setText(msg);
    }

    @Override
    public int getItemCount() {
        // if tasks is null, return 0, otherwise return the size of tasks
        return (this.tasks.getValue() == null)? 0 :this.tasks.getValue().size();
    }

    /**
     * ViewHolder for the View that's going to display Tasks
     */
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View itemView;
        private TaskRecyclerViewAdapter adapter;

        /**
         *
         * @param itemView The root View that's being displayed
         * @param adapter The adapter that is holding this ViewHolder
         */
        public TaskViewHolder(@NonNull View itemView, TaskRecyclerViewAdapter adapter) {
            super(itemView);
            this.itemView = itemView;
            this.adapter = adapter;

            // add a listener to the button in the taskItemView
            itemView.findViewById(R.id.btn_taskListItemView).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // get the clicked item's position
            int position = getAdapterPosition();

            // get the task at that position
            Task task = this.adapter.tasks.getValue().get(position);

            // display a log message with the task's name
            Log.d(TAG, task.getName() );

            // create a Bundle and navigate to the ViewTaskFragment
            Bundle bundle = new Bundle();
            // it shouldn't but just in case
            if (task != null){
                bundle.putParcelable(ViewTaskFragment.ARG_TASK, task);
            }
            // launch the action
            Navigation.findNavController(v).navigate(R.id.action_task_recycler_view_to_view_task, bundle);
        }
    }
}
