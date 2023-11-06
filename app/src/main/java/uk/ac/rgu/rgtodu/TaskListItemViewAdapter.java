package uk.ac.rgu.rgtodu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import uk.ac.rgu.rgtodu.data.Task;

/**
 * Adapter class for displaying {@link Task}s in an {@link ListView}.
 */
public class TaskListItemViewAdapter extends ArrayAdapter<Task>  {

    // the context that the adapter is operating in
    private Context context;
    // the tasks being displayed
    private List<Task> tasks;

    /**
     * Creates a new {@link TaskListItemViewAdapter, }
     * @param context In which the adapter will be operating
     * @param resource
     * @param objects The Tasks that will be displayed by the adapter
     */
    public TaskListItemViewAdapter(@NonNull Context context, int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);
        this.context = context;
        this.tasks = objects;
    }

    /**
     * Creates a new {@link TaskListItemViewAdapter, }
     * @param context In which the adapter will be operating
     * @param resource
     */
    public TaskListItemViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    /**
     * Set the data being displayed in this adapter to be tasks
     * @param tasks The {@link List} of {@link Task}s to be displayed
     */
    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
        // this.tasks is only used here, need to call the ArrayAdapter methods
        // to remove the data its storing and add all of the tasks to it
        super.clear();
        super.addAll(tasks);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // here we setup convertView to display the Task a location position with the tasks list.

        // get the View that will be used to display the Task
        View itemView = convertView;
        if (itemView == null){
            itemView = LayoutInflater.from(this.context).inflate(R.layout.task_list_view_item, parent, false);
        }

        // get the Task that is being displayed
        Task task = this.tasks.get(position);

        // Now update itemView to display the task name
        TextView tv_taskName = itemView.findViewById(R.id.tv_taskListItemName);
        tv_taskName.setText(task.getName());

        // now update itemView to display the hours to completion
        TextView tv_taskPoms = itemView.findViewById(R.id.tv_taskListItemPomodoros);
        String msg = context.getString(R.string.tv_taskListPomodoros, task.getPomodorosRemaining());
        tv_taskPoms.setText(msg);

        // add an click listener to the button
        Button btn = itemView.findViewById(R.id.btn_taskListItemView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = tasks.get(position);
                Log.d("TASK_CLICKED", task.getName());
            }
        });

        // alternatively - add a click listener to the entire row
        /* itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = tasks.get(position);
                Log.d("TASK_CLICKED", task.getName());
            }
        }); */

        return itemView;
    }
}