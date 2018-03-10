package ca.tundrafam.androidradio;

import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Playlist {
    private List<Entry> entries;
    private List<String> visited_urls;
    private StringBuilder playlist = new StringBuilder();
    private String root_url = "http://opml.radiotime.com/";

    public Playlist() {
        visited_urls = new ArrayList<String>();
    }

    public InputStream download(String url_str) throws IOException {
        URL url = new URL(url_str);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Store the url_str on our "url stack"
        //visited_urls.add(url_str);

        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public String getRoot() {
        return root_url;
    }

    public void pushURL(String url) {
        visited_urls.add(url);
    }

    public String popURL() {
        String last = getRoot();

        if (visited_urls.size() >= 1) {
            last = visited_urls.get(visited_urls.size() - 1);
            visited_urls.remove(visited_urls.size() - 1);
        }
        return last;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void parse(InputStream stream) {
        Parser parser = new Parser();

        try {
            entries = parser.parse(stream);
        } catch (XmlPullParserException|IOException e) {
            e.printStackTrace();
        }
    }

    class Parser {
        List<Entry> entries;

        public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
            entries = new ArrayList<Entry>();
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                readPlaylist(parser);
            } catch (Exception e) {
                entries = null;
            } finally {
                in.close();
            }
            return entries;
        }

        private void readPlaylist(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, "opml");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("body")) {
                    readBody(parser);
                } else {
                    stepOver(parser);
                }
            }
        }

        private void readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
            Entry entry = null;
            parser.require(XmlPullParser.START_TAG, null, "outline");

            entry = new Entry();
            entry.text = parser.getAttributeValue(null, "text");
            entry.type = parser.getAttributeValue(null, "type");
            entry.url = parser.getAttributeValue(null, "URL");
            entries.add(entry);
            // consume end tag
            parser.next();

            return;
        }

        private void readSection(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, "outline");

            // Save entry as a section header
            Entry entry = new Entry();
            entry.text = parser.getAttributeValue(null, "text");
            entry.type = "section";
            entries.add(entry);


            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                String url = parser.getAttributeValue(null, "URL");
                if (name.equals("outline")) {
                    if (url != null) {
                        readEntry(parser);
                    } else {
                        readSection(parser);
                    }
                } else {
                    stepOver(parser);
                }
            }
        }

        private void readBody(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, "body");
            int depth = parser.getDepth();

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                String url = parser.getAttributeValue(null, "URL");
                if (name.equals("outline")) {
                    if (url != null) {
                        readEntry(parser);
                    } else {
                        readSection(parser);
                    }
                } else {
                    stepOver(parser);
                }
            }
        }

        private void stepOver(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }
    }

    class Entry {
        public String type;
        public String text;
        public String url;

        private Entry() { }

        private Entry(String type, String text, String url) {
            this.type = type;
            this.text = text;
            this.url = url;
        }

        public String getLabel() {
            return text;
        }
    }

}
