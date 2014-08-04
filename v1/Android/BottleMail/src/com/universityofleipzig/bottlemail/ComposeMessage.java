package com.universityofleipzig.bottlemail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Created by Clemens on 24.05.13.
 */
public class ComposeMessage extends SherlockActivity {

    private Context mContext;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        mContext = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
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
        getSupportMenuInflater().inflate(R.menu.composemessage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeAsUp:
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
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
