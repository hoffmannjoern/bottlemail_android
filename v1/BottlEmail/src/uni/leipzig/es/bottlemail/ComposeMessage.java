package uni.leipzig.es.bottlemail;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Clemens on 24.05.13.
 */
public class ComposeMessage extends Activity {

    private Context mContext;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);

        mContext = getApplicationContext();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        // set actionbar titles
        actionBar.setTitle(getString(R.string.action_title_dummy_bottle));
        actionBar.setSubtitle(getString(R.string.action_subtitle_compose));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.action_send:
        	//case R.id.compose_message_et_message: //action_send?
        	case R.id.action_send:	
                Toast.makeText(this, "Sende Nachricht...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, BottleDetails.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
