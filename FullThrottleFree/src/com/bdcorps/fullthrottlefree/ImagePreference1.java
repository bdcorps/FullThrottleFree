package com.bdcorps.fullthrottlefree;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

public class ImagePreference1 extends Preference {
	private Drawable mIcon;
	Bitmap a;

	public ImagePreference1(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);

		this.setLayoutResource(R.layout.imagepref1);

	}

	public ImagePreference1(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	protected void onBindView(final View view) {
		super.onBindView(view);
		/*
		 * final ImageView imageView = (ImageView)view.findViewById(R.id.icon);
		 * if ((imageView != null) && (this.mIcon != null)) {
		 * imageView.setImageResource(R.drawable.dra2); }
		 */
	}

	public void setIcon(final Drawable icon) {
		if (((icon == null) && (this.mIcon != null))
				|| ((icon != null) && (!icon.equals(this.mIcon)))) {
			this.mIcon = icon;
			this.notifyChanged();
		}
	}

	public Drawable getIcon() {
		return this.mIcon;
	}
}