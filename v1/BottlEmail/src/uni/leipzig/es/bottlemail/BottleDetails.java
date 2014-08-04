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
 * Created by Clemens on 21.05.13.
 */
public class BottleDetails extends Activity {

    private Context mContext;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottle_details);

        mContext = getApplicationContext();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        // set actionbar titles
        actionBar.setTitle(getString(R.string.action_title_dummy_bottle));
        //actionBar.setSubtitle("subtitle");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottle_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.composemessage_tv_message:
        	case R.id.action_compose: 
        		Toast.makeText(this, "Open MessageActivity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ComposeMessage.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}