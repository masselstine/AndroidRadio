package ca.tundrafam.androidradio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class PlaylistFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<Playlist.Entry>>  {

    private PlaylistListAdapter adapter;
    private TextView now_playing;
    private ImageButton back_btn;
    private ImageButton stop_btn;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PlaylistListAdapter(getActivity());
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        RadioClient.getInstance().init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        now_playing = (TextView)view.findViewById(R.id.nowPlaying);
        back_btn = (ImageButton)view.findViewById(R.id.backButton);
        stop_btn = (ImageButton)view.findViewById(R.id.stopButton);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loader<?> loader = getLoaderManager().getLoader(0);
                PlaylistLoader pl_loader = (PlaylistLoader)loader;
                pl_loader.gotoPrevURLStr();
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioClient.getInstance().stop();
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Playlist.Entry entry = adapter.getItem(position);

        if (entry.type.equals("section")) {
            return;
        }

        Loader<?> loader = getLoaderManager().getLoader(0);
        PlaylistLoader pl_loader = (PlaylistLoader)loader;

        if (entry.type.equals("audio")) {
            now_playing.setText(entry.text);
            RadioClient.getInstance().play(entry.url);
        } else {
            pl_loader.setURLStr(entry.url);
        }

        super.onListItemClick(l, v, position, id);
    }

    @Override
    public Loader<List<Playlist.Entry>> onCreateLoader(int id, Bundle args) {
        return new PlaylistLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Playlist.Entry>> loader, List<Playlist.Entry> data) {
        adapter.setData(data);
        getListView().setSelection(0);
    }

    @Override
    public void onLoaderReset(Loader<List<Playlist.Entry>> loader) {
        adapter.setData(null);
    }

}
