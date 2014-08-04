package uni.leipzig.es.bottlemail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Created by Clemens on 23.05.13.
 */
public class MainFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    private Context mContextFragment;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContextFragment = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_fragment, container, false);

        ListView listview = (ListView) rootView.findViewById(R.id.listView1);
        String[] bottleValues = new String[] { "Flasche 1", "Flasche 2", "Flasche 3" };

        ArrayAdapter<String> bottleAdapter = new ArrayAdapter<String>(mContextFragment, android.R.layout.simple_list_item_1, bottleValues);
        listview.setAdapter(bottleAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(mContextFragment, String.valueOf("Clicked"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContextFragment, BottleDetails.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
