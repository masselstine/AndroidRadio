package ca.tundrafam.androidradio;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PlaylistLoader extends AsyncTaskLoader<List<Playlist.Entry>> {
    private final Playlist playlist;
    private String current_url_str;

    public PlaylistLoader(Context context) {
        super(context);

        playlist = new Playlist();
        current_url_str = playlist.getRoot();
    }

    public void setURLStr(String url_str) {
        playlist.pushURL(current_url_str);
        current_url_str = url_str;

        // Cause a refresh of the list
        onContentChanged();
    }

    public void gotoPrevURLStr() {
        current_url_str = playlist.popURL();
        onContentChanged();
    }

    public List<Playlist.Entry> loadInBackground() {
        InputStream stream = null;

        try {
            stream = playlist.download(current_url_str);
            playlist.parse(stream);
        } catch (Exception e) {
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return playlist.getEntries();
    }

    @Override
    public void deliverResult(List<Playlist.Entry> playlist) {
        super.deliverResult(playlist);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
