package ca.tundrafam.androidradio;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PlaylistListAdapter extends ArrayAdapter<Playlist.Entry> {
    private final LayoutInflater inflater;

    public PlaylistListAdapter(Context context) {
        super(context, android.R.layout.simple_expandable_list_item_1);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Playlist.Entry> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null)
            view = inflater.inflate(R.layout.playlist_item, parent, false);
        else
            view = convertView;

        Playlist.Entry entry = getItem(position);
        ((TextView)view.findViewById(android.R.id.text1)).setText(entry.getLabel());

        ImageView icon = ((ImageView) view.findViewById(R.id.item_icon));
        if (entry.type.equals("section")) {
            icon.setImageDrawable(ResourcesCompat.getDrawable(view.getResources(),
                    R.drawable.ic_subject_black_24dp, null));
        } else if (entry.type.equals("audio")) {
            icon.setImageDrawable(ResourcesCompat.getDrawable(view.getResources(),
                    R.drawable.ic_volume_up_black_24dp, null));
        } else {
            icon.setImageDrawable(ResourcesCompat.getDrawable(view.getResources(),
                    R.drawable.ic_link_black_24dp, null));
        }

        return view;
    }
}
