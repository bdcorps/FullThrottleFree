package com.bdcorps.fullthrottlefree;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import rajawali.Camera2D;
import rajawali.materials.Material;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.ATexture;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.VideoTexture;
import rajawali.math.vector.Vector3;
import rajawali.primitives.Plane;
import rajawali.primitives.ScreenQuad;
import rajawali.renderer.RajawaliRenderer;
import rajawali.wallpaper.Wallpaper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;

@SuppressLint("NewApi")
public class Renderer extends RajawaliRenderer implements
		SharedPreferences.OnSharedPreferenceChangeListener, SensorEventListener {

	public static final String SHARED_PREFS_NAME = "rajawalisharedprefs";
	private SharedPreferences sp;
	public MediaPlayer mMediaPlayer;
	public VideoTexture mVideoTexture;
	public Material mVideo;
	public Plane pVideo;

public Plane pCross_1;
	public Bitmap bCross_1;
	public Material mCross_1;
	public Texture tCross_1;

	public Plane pPit_1;
	public Material mPit_1;
	public Bitmap bPit_1;
	public Texture tPit_1;

	public Plane pPit_2;
	public Material mPit_2;
	public Bitmap bPit_2;
	public Texture tPit_2;

	Plane pLaser;
	public Material mLaser;
	public Texture tLaser;
	public Bitmap bLaser;

	Plane pHUD_3;
	public Material mHUD_3;
	public Bitmap bHUD_3;

	public boolean mInit;

	public final float FILTERING_FACTOR = .11f;

	public SensorManager mSensorManager;
	public Sensor mAccelerometer;
	public Vector3 mAccVals;

	public float mGravity[];

	public String tag = "StripedLog";
	private float mWidthPlane;

	public enum ModeRenderer {
		CLASSIC, LETTER_BOXED, STRETCHED
	}

	public Renderer(Context context) {
		super(context);

		sp = context.getSharedPreferences(Wallpaper.SHARED_PREFS_NAME,
				Context.MODE_PRIVATE);
	}

	@SuppressLint("NewApi")
	@Override
	protected void initScene() {
		mGravity = new float[3];
		mSensorManager = (SensorManager) this.getContext().getSystemService(
				Context.SENSOR_SERVICE);
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		mAccVals = new Vector3();
		mInit = true;
		// setFrameRate(60);
		Camera2D cam = new Camera2D();
		this.replaceAndSwitchCamera(getCurrentCamera(), cam);
		getCurrentScene().setBackgroundColor(Color.WHITE);
		getCurrentCamera().setLookAt(0, 0, 0);
		mMediaPlayer = new MediaPlayer();

		mVideoTexture = new VideoTexture("VideoLiveWallpaper", mMediaPlayer);
		mVideo = new Material();
		mVideo.setColorInfluence(0);
		try {
			mVideo.addTexture(mVideoTexture);
		} catch (TextureException e) {
			e.printStackTrace();
		}

		pVideo = new Plane(1f, 1f, 1, 1);
		pVideo.setRotY(180);
		initVideo();
		initPlane(pVideo, mMediaPlayer.getVideoWidth(),
				mMediaPlayer.getVideoHeight());

		pVideo.setMaterial(mVideo);
		pVideo.setPosition(0f, 0f, 0f);
		addChild(pVideo);

		pCross_1 = new Plane(0.25f, 0.25f, 1, 1);
		pCross_1.setRotY(180);

		mCross_1 = new Material();

		mCross_1.setColorInfluence(0);
		bCross_1 = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.hair_01);
		tCross_1 = new Texture("bCross_1", bCross_1);
		try {
			mCross_1.addTexture(tCross_1);

		} catch (TextureException e) {
			e.printStackTrace();
		}

		pCross_1.setMaterial(mCross_1);
		pCross_1.setPosition(0f, 0f, 1f);
		pCross_1.setTransparent(true);

		initPlane(pCross_1, bCross_1.getWidth(), bCross_1.getHeight());
		addChild(pCross_1);

		pLaser = new Plane(1, 1, 1, 1);
		pLaser.setRotY(180);

		mLaser = new Material();

		mLaser.setColorInfluence(0);
		bLaser = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.laser_01);
		tLaser=new Texture("bLaser", bLaser);
		try {
			mLaser.addTexture(tLaser);

		} catch (TextureException e) {
			e.printStackTrace();
		}

		pLaser.setMaterial(mLaser);
		pLaser.setPosition(0f, -0.5f, 0.5f);
		pLaser.setTransparent(true);
		pLaser.setVisible(false);

		initPlane(pLaser, bLaser.getWidth(), bLaser.getHeight());
		addChild(pLaser);

		pHUD_3 = new Plane(1, 1, 1, 1);
		pHUD_3.setRotY(180);

		mHUD_3 = new Material();

		mHUD_3.setColorInfluence(0);
		bHUD_3 = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.silhouette);
		try {
			mHUD_3.addTexture(new Texture("bHUD_3", bHUD_3));

		} catch (TextureException e) {
			e.printStackTrace();
		}

		pHUD_3.setMaterial(mHUD_3);
		pHUD_3.setPosition(0f, 0f, 3f);
		pHUD_3.setScale(0.5f, 0.5f, 0.5f);
		pHUD_3.setTransparent(true);

		initPlane(pHUD_3, bHUD_3.getWidth(), bHUD_3.getHeight());
		addChild(pHUD_3);

		pPit_1 = new Plane(1, 1, 1, 1);
		pPit_1.setRotY(180);
		pPit_1.setTransparent(true);

		mPit_1 = new Material();

		mPit_1.setColorInfluence(0);
		bPit_1 = BitmapFactory.decodeResource(mContext.getResources(),
				R.raw.pit_1_base);

		tPit_1 = new Texture("bPit_1", bPit_1);
		try {
			mPit_1.addTexture(tPit_1);

		} catch (TextureException e) {
			e.printStackTrace();
		}
		pPit_1.setMaterial(mPit_1);
		pPit_1.setPosition(0f, 0f, 1f);
		pPit_1.setTransparent(true);

		initPlane(pPit_1, bPit_1.getWidth(), bPit_1.getHeight());
		addChild(pPit_1);

		pPit_2 = new Plane(1f, 1f, 1, 1);
		pPit_2.setRotY(180);

		pPit_2.setTransparent(true);

		mPit_2 = new Material();

		mPit_2.setColorInfluence(0);
		bPit_2 = BitmapFactory.decodeResource(mContext.getResources(),
				R.raw.pit_1_overlay);
		tPit_2 = new Texture("bPit_2", bPit_2);
		try {
			mPit_2.addTexture(tPit_2);

		} catch (TextureException e) {
			e.printStackTrace();
		}
		pPit_2.setMaterial(mPit_2);
		pPit_2.setPosition(0f, 0f, 1f);
		pPit_2.setTransparent(true);

		initPlane(pPit_2, bPit_2.getWidth(), bPit_2.getHeight());
		addChild(pPit_2);
	}

	public void initVideo() {
		if (mMediaPlayer != null) {
			initMedia();
		}
	}

	private void initMedia() {
		Uri uri = Uri.parse("");
		if (preferences != null) {
			uri = Uri.parse(preferences.getString("uri", ""));
		}

		if (mInit == false) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			}
			if (!mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				mMediaPlayer.reset();
			}
		}
		try {
			{
				String fileName = "android.resource://"
						+ getContext().getPackageName() + "/" + R.raw.city_01;

				if (Globals.id_2 == 1) {
					fileName = "android.resource://"
							+ getContext().getPackageName() + "/"
							+ R.raw.city_01;
				} else if (Globals.id_2 == 2) {
					fileName = "android.resource://"
							+ getContext().getPackageName() + "/"
							+ R.raw.city_02;
				} else if (Globals.id_2 == 3) {
					fileName = "android.resource://"
							+ getContext().getPackageName() + "/"
							+ R.raw.city_03;
				} else if (Globals.id_2 == 4) {
					fileName = "android.resource://"
							+ getContext().getPackageName() + "/"
							+ R.raw.city_04;
				}

				mMediaPlayer.setDataSource(getContext(), Uri.parse(fileName));
			}

			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
			mMediaPlayer.seekTo(0);
			mMediaPlayer.start();
			if (mInit) {
			} else {
				// mTextureManager.replaceTexture(mVideoTexture);
			}
			mInit = false;

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initPlane(Plane p, int w, int h) {
		String renderer = "classic";

		if (renderer.equalsIgnoreCase("letter_boxed")) {
			rendererMode(ModeRenderer.LETTER_BOXED, p, w, h);
		} else if (renderer.equalsIgnoreCase("stretched")) {
			rendererMode(ModeRenderer.STRETCHED, p, w, h);
		} else {
			rendererMode(ModeRenderer.CLASSIC, p, w, h);
		}

	}

	public void rendererMode(ModeRenderer modeRenderer, Plane p, int w, int h) {
		float ratioDisplay = (float) mViewportHeight / (float) mViewportWidth;
		float ratioVideo = (float) h / w;

		if (ratioDisplay == ratioVideo) {
			p.setScaleX(1f);
			p.setScaleY(1f);
			mWidthPlane = 1f;
		} else if (ratioDisplay >= 1) {
			// PORTRAIT
			switch (modeRenderer) {
			case STRETCHED:
				rendererModeStretched(p, w, h);
				break;
			case LETTER_BOXED:
				rendererModeLetterBox(p, w, h);
				break;
			default:
				rendererModeClassic(p, w, h);
				break;
			}
		} else {
			// LANDSCAPE
			switch (modeRenderer) {
			case STRETCHED:
				rendererModeStretched(p, w, h);
				break;
			case LETTER_BOXED:
				rendererModeStretched(p, w, h);
				break;
			default:
				rendererModeStretched(p, w, h);
				break;
			}
		}
	}

	public void rendererModeClassic(Plane p, int w, int h) {
		float ratioDisplay = (float) mViewportHeight / (float) mViewportWidth;
		float ratioSize = 1f / h;
		mWidthPlane = w * ratioSize * ratioDisplay;
		p.setScaleX(mWidthPlane);
		p.setScaleY(1);
	}

	public void rendererModeLetterBox(Plane p, int w, int h) {
		float ratioDisplay = (float) mViewportWidth / (float) mViewportHeight;
		float ratioSize = 1f / w;
		p.setScaleY(h * ratioSize * ratioDisplay);
		p.setScaleX(1f);
		mWidthPlane = 1f;
	}

	public void rendererModeStretched(Plane p, int w, int h) {
		float ratioDisplay = (float) mViewportHeight / (float) mViewportWidth;
		float ratioSize = 1f / h;
		p.setScaleX(w * ratioSize * ratioDisplay);
		p.setScaleY(1f);
		mWidthPlane = 1f;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		if (mMediaPlayer.isPlaying()) {
			mVideoTexture.update();
		}

		pLaser.setVisible(false);

		c++;
		{
			if (c > 30) {
				if (pPit_2.isVisible()) {
					pPit_2.setVisible(false);
				} else {
					pPit_2.setVisible(true);
				}
				c = 0;
			}
		}
	}

	int c = 0;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

	}

	int i = 1;

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);

		initPlane(pVideo, mMediaPlayer.getVideoWidth(),
				mMediaPlayer.getVideoHeight());

		initVideo();

		if (i != Globals.id_1) {
			// mTextureManager.reset();
			if (Globals.id_1 == 1) {
				//Pit Layer 1
				mPit_1.removeTexture(tPit_1);
				bPit_1 = BitmapFactory.decodeResource(mContext.getResources(),
						R.raw.pit_1_base);
				tPit_1 = new Texture("bPit_1", bPit_1);
				try {
					mPit_1.addTexture(tPit_1);
				} catch (TextureException e) {
					e.printStackTrace();
				}

				//Pit Layer 2
				mPit_2.removeTexture(tPit_2);
				bPit_2 = BitmapFactory.decodeResource(mContext.getResources(),
						R.raw.pit_1_overlay);
				tPit_2 = new Texture("bPit_2", bPit_2);
				try {
					mPit_2.addTexture(tPit_2);
				} catch (TextureException e) {
					e.printStackTrace();
				}
				
				//CrossHair
				mCross_1.removeTexture(tCross_1);
				bCross_1 = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.hair_01);
				tCross_1 = new Texture("bCross_1", bCross_1);
				try {
					mCross_1.addTexture(tCross_1);
				} catch (TextureException e) {
					e.printStackTrace();
				}
				
				//Laser
				mLaser.removeTexture(tLaser);
				tLaser = new Texture("bLaser", R.drawable.laser_01);
				try {
					mLaser.addTexture(tLaser);
				} catch (TextureException e) {
					e.printStackTrace();
				}
			} else if (Globals.id_1 == 2) {
				mPit_1.removeTexture(tPit_1);
				bPit_1 = BitmapFactory.decodeResource(mContext.getResources(),
						R.raw.pit_3_base);
				tPit_1 = new Texture("bPit_1", bPit_1);
				try {
					mPit_1.addTexture(tPit_1);
				} catch (TextureException e) {
					e.printStackTrace();
				}

				mPit_2.removeTexture(tPit_2);
				bPit_2 = BitmapFactory.decodeResource(mContext.getResources(),
						R.raw.pit_3_overlay);
				tPit_2 = new Texture("bPit_2", bPit_2);
				try {
					mPit_2.addTexture(tPit_2);
				} catch (TextureException e) {
					e.printStackTrace();
				}
				
				//CrossHair
				mCross_1.removeTexture(tCross_1);
				bCross_1 = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.hair_02);
				tCross_1 = new Texture("bCross_1", bCross_1);
				try {
					mCross_1.addTexture(tCross_1);
				} catch (TextureException e) {
					e.printStackTrace();
				}
				
				//Laser
				mLaser.removeTexture(tLaser);
				tLaser = new Texture("bLaser", R.drawable.laser_02);
				try {
					mLaser.addTexture(tLaser);
				} catch (TextureException e) {
					e.printStackTrace();
				}
			}
		}
		i = Globals.id_1;

	}

	@Override
	public void onVisibilityChanged(boolean visible) {
		super.onVisibilityChanged(visible);
		if (!visible) {
			if (mMediaPlayer != null) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}
			}
		} else if (mMediaPlayer != null && mInit == false) {
			mMediaPlayer.start();
		}

		/*
				if (visible && mTextureManager != null) {
					mTextureManager.reset();
				}*/
	}

	@Override
	public void onSurfaceDestroyed() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mVideo.removeTexture(mVideoTexture);
		mTextureManager.taskRemove(mVideoTexture);
		mMaterialManager.taskRemove(mVideo);

		super.onSurfaceDestroyed();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;

		try {

			mAccVals.x = (float) (-event.values[1] * FILTERING_FACTOR + mAccVals.x
					* (1.0 - FILTERING_FACTOR));
			mAccVals.y = (float) (event.values[0] * FILTERING_FACTOR + mAccVals.y
					* (1.0 - FILTERING_FACTOR));

			// getCurrentCamera().getPosition().x = mAccVals.x *
			// FILTERING_FACTOR;
			// getCurrentCamera().getPosition().y = mAccVals.y *
			// FILTERING_FACTOR;

			if (mAccVals.y < 5 && mAccVals.y > -5) {
				pVideo.getPosition().x = -mAccVals.y * FILTERING_FACTOR * 0.1;
				pVideo.getRotation().y = -mAccVals.y * FILTERING_FACTOR * 20
						+ 180;

				pCross_1.getPosition().x = -mAccVals.y * FILTERING_FACTOR * 0.075;
				pCross_1.getRotation().y = +mAccVals.y * FILTERING_FACTOR * 8
						+ 180;

				pLaser.getPosition().x = -mAccVals.y * FILTERING_FACTOR * 0.075;
				pLaser.getRotation().y = +mAccVals.y * FILTERING_FACTOR * 8
						+ 180;

				pPit_1.getPosition().x = -mAccVals.y * FILTERING_FACTOR * 0.1;
				pPit_1.getRotation().y = -mAccVals.y * FILTERING_FACTOR * 15
						+ 180;

				pPit_2.getPosition().x = -mAccVals.y * FILTERING_FACTOR * 0.1;
				pPit_2.getRotation().y = -mAccVals.y * FILTERING_FACTOR * 15
						+ 180;
				// pVideo.getPosition().x = -mAccVals.y * FILTERING_FACTOR*0.3;

				// getCurrentCamera().getLookAt().y = -mAccVals.y *
				// FILTERING_FACTOR;
			}
		} catch (NullPointerException ex) {
			Log.d(tag, ex.toString());
		} catch (RuntimeException ex) {
			Log.d(tag, ex.toString());
		}
	}

	@Override
	public void onOffsetsChanged(float xOffset, float yOffset,
			float xOffsetStep, float yOffsetStep, int xPixelOffset,
			int yPixelOffset) {

		if (pVideo != null) {
			// pVideo.setX((1 - mWidthPlane) * (xOffset - 0.5));
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchEvent(MotionEvent me) {
		try {
			// The MotionEvent "me" has all of the data you need to make
			// animation
			// based on touch
			if (me.getAction() == MotionEvent.ACTION_DOWN) { // Called once at
																// the
																// start of the
																// touch
				System.out.println(me.getAction() + "  --  " + me.getX() + ", "
						+ me.getY());
				pLaser.setVisible(true);
			}
			if (me.getAction() == MotionEvent.ACTION_MOVE) { // Called
																// repeatedly as
																// touch point
																// moves
				System.out.println(me.getAction() + "  --  " + me.getX() + ", "
						+ me.getY());
			}
			if (me.getAction() == MotionEvent.ACTION_UP) { // Called once at the
															// end
															// of the touch
				System.out.println(me.getAction() + "  --  " + me.getX() + ", "
						+ me.getY());
			}
			try {
				Thread.sleep(15); // Small delay to keep touch events from
									// overflowing and decreasing performance
			} catch (Exception e) {
			}
		} catch (Exception e) {
			Log.d(tag, "Error");
		}
	}

}