package de.unile.bootlemailbt;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class BTMBTOnClickListner implements OnClickListener{

	@Override
	public void onClick(View target) {
		// detect source
		switch (target.getId()) {
		// if close btn
		case R.id.btnClose:
			// exit the app
			Log.e("APPKILLER", "APP EXIT WITH SYSTEM CALL");
			System.exit(0);
		}
		
	}

}
