package a13ssandr0.keytester;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class KeyArrayAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> values;
    private final int width;

    public KeyArrayAdapter(Context context, ArrayList<String> values){
        super(context, R.layout.pressed_key_item, values);
        this.values = values;
        // create a new TextView used only to calculate the width of the TextView
        // that displays the latest number (usually the widest)
        // so that all the text views will have the same width
        TextView tv = new TextView(getContext());
        tv.setText(String.valueOf(values.size()));
        tv.measure(0,0);
        width = tv.getMeasuredWidth();
    }

    // class for holding the cached view
    static class ViewHolder {
        TextView position;
        TextView keycode;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // holder of the views to be reused.
        ViewHolder viewHolder;

        // if no previous views found
        if (convertView == null) {
            // create the container ViewHolder
            viewHolder = new ViewHolder();

            // inflate the views from layout for the new row
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.pressed_key_item, parent, false);

            // set the view to the ViewHolder.
            viewHolder.position = convertView.findViewById(R.id.number);
            viewHolder.keycode  = convertView.findViewById(R.id.keycode);

            // save the viewHolder to be reused later.
            convertView.setTag(viewHolder);
        } else {
            // there is already ViewHolder, reuse it.
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.position.setText(String.valueOf(position+1)); //row numbers start at 1
        viewHolder.position.setWidth(width);
        viewHolder.keycode.setText(values.get(position));
        return convertView;
    }
}
