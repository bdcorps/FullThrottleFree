package com.bdcorps.fullthrottlefree;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ImagePreference2 extends Preference {
	private Drawable mIcon;
	Bitmap a;
	boolean button = false;
	private int mClickCounter;

	public ImagePreference2(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);

		this.setLayoutResource(R.layout.imagepref2);

	}

	public ImagePreference2(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	int newValue = 5;

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		final ImageView thumb_1 = (ImageView) view.findViewById(R.id.thumb_1);
		final ImageView thumb_2 = (ImageView) view.findViewById(R.id.thumb_2);

		thumb_1.setImageResource(R.drawable.im_1_select);
		
		if (mClickCounter == 1){
			thumb_1.setImageResource(R.drawable.im_1_select);
			thumb_2.setImageResource(R.drawable.im_2);
		}
		else if (mClickCounter == 2){
			thumb_1.setImageResource(R.drawable.im_1);
			thumb_2.setImageResource(R.drawable.im_2_select);
		}
		
		if (thumb_1 != null) {

			thumb_1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
				
					thumb_1.setImageResource(R.drawable.im_1_select);
					thumb_2.setImageResource(R.drawable.im_2);
					newValue = 1;
					Globals.temp = newValue;
					mClickCounter = newValue;
					persistInt(mClickCounter);
		}
			});
		}

		if (thumb_2 != null) {

			thumb_2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					thumb_1.setImageResource(R.drawable.im_1);
					thumb_2.setImageResource(R.drawable.im_2_select);
					newValue = 2;
					Globals.temp = newValue;
					mClickCounter = newValue;
					persistInt(mClickCounter);
			}
			});
		}

	}

	@Override
	protected void onClick() {

	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		// This preference type's value type is Integer, so we read the default
		// value from the attributes as an Integer.
		return a.getInteger(index, 0);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue) {
			// Restore state
			mClickCounter = getPersistedInt(mClickCounter);
		} else {
			// Set state
			int value = (Integer) defaultValue;
			mClickCounter = value;
			persistInt(value);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		/*
		 * Suppose a client uses this preference type without persisting. We
		 * must save the instance state so it is able to, for example, survive
		 * orientation changes.
		 */

		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			// No need to save instance state since it's persistent
			return superState;
		}

		// Save the instance state
		final SavedState myState = new SavedState(superState);
		myState.clickCounter = mClickCounter;
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (!state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}

		// Restore the instance state
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		mClickCounter = myState.clickCounter;
		notifyChanged();
	}

	/**
	 * SavedState, a subclass of {@link BaseSavedState}, will store the state of
	 * MyPreference, a subclass of Preference.
	 * <p>
	 * It is important to always call through to super methods.
	 */
	private static class SavedState extends BaseSavedState {
		int clickCounter;

		public SavedState(Parcel source) {
			super(source);

			// Restore the click counter
			clickCounter = source.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);

			// Save the click counter
			dest.writeInt(clickCounter);
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}