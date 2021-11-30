package id.coba.kotlinpintar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CustomizedListView extends Activity {
    // All static variables
    static final String URI = "https://api.androidhive.info/music/music.xml";

    // XML node keys
    static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_ARTIST = "artist";
    static final String KEY_DURATION = "duration";
    static final String STATUS_SYNC = "0";
    static final String KEY_THUMB_URL = "thumb_url";

    ListView list;
    LazyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kotor);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            URL url = new URL("https://api.androidhive.info/music/music.xml");
            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(conn.getInputStream());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException ioex) {
            ioex.printStackTrace();
        }catch (ParserConfigurationException pce){
            pce.printStackTrace();
        }catch (SAXException sax){
            sax.printStackTrace();
        }



        XMLParser parser = new XMLParser();
//        String xml = parser.getXmlFromUrl(URL);
//        Document doc = parser.getDomElement(xml);

        NodeList nl = doc.getElementsByTagName(KEY_SONG);

        for (int i = 0; i < nl.getLength(); i++) {

            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);

            map.put(KEY_ID, parser.getValue(e, KEY_ID));
            map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
            map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
            map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));


            songsList.add(map);
        }

        list=(ListView)findViewById(R.id.list);

        adapter=new LazyAdapter(this, songsList);
        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {

            }
        });
    }
}