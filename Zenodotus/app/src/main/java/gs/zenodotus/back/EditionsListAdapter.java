package gs.zenodotus.back;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gs.zenodotus.R;
import gs.zenodotus.back.database.EditionItem;

public class EditionsListAdapter extends ArrayAdapter<EditionItem> {
    private Context context;
    private int layoutResourceId;
    private List<EditionItem> items;

//    public EditionItem getItem(int position) {
//        return items.get(position);
//    }

    public EditionsListAdapter(Context context, int resource,
                               List<EditionItem> items) {
        super(context, resource);
        this.context = context;
        this.layoutResourceId = resource;
        this.items = items;
        Log.d("Adapter", "items length " + items.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layoutResourceId, parent, false);
        TextView descriptionTextView = (TextView) rowView.findViewById(R.id
                .item_label);
        TextView labelTextView = (TextView) rowView.findViewById(R.id
                .item_description);
        descriptionTextView.setText(items.get(position).description);
        labelTextView.setText(items.get(position).label);
        return rowView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public EditionItem getItem(int position) {
        return items.get(position);
    }
}
