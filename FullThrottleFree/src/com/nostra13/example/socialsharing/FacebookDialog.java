/*
 * Copyright 2013 Keisuke Kobayashi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nostra13.example.socialsharing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import com.bdcorps.fullthrottlefree.R;
import com.bdcorps.fullthrottlefree.Settings;

/**
 * RateThisApp<br>
 * A library to show the app rate dialog
 * @author Keisuke Kobayashi <k.kobayashi.122@gmail.com>
 *
 */
public class FacebookDialog {
	
	private static final String TAG = FacebookDialog.class.getSimpleName();
	
	public static boolean lock=true;
	
	public static void showRateDialog(final Context context) {
		Intent myIntent = new Intent(context, HomeActivity.class);
		context.startActivity(myIntent);
	}
	
	/**
	 * Clear data in shared preferences.<br>
	 * This API is called when the rate dialog is approved or canceled.
	 * @param context
	 */
	
}
