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

	AlphaMapTexture a ;
	
	public Plane pHUD_1;
	public Plane pBack_1;
	public Material mHUD_1;
	public Material mBack_1;
	public Bitmap bHUD_1;
	public Bitmap bBack_1;
	Plane pHUD_2;
	public Material mHUD_2;
	public Bitmap bHUD_2;

	Plane pHUD_3;
	public Material mHUD_3;
	public Bitmap bHUD_3;

	Plane pLaser;
	public Material mLaser;
	public Texture bLaser;

	public boolean mInit;
	public boolean mInit1;

	public MediaPlayer mMediaPlayer1;
	public VideoTexture mVideoTexture1;
	public Material mVideo1;
	public Plane pVideo1;

	public final float FILTERING_FACTOR = .3f;

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
		mInit1 = true;
		//setFrameRate(60);
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

		mMediaPlayer1 = new MediaPlayer();

		mVideoTexture1 = new VideoTexture("VideoLiveWallpaper1", mMediaPlayer1);
		mVideo1 = new Material();
		mVideo1.setColorInfluence(0);
		try {
			mVideo1.addTexture(mVideoTexture1);
		} catch (TextureException e) {
			e.printStackTrace();
		}

		pVideo1 = new Plane(1f, 1f, 1, 1);
		pVideo1.setRotY(180);
		initVideo1();
		initPlane(pVideo1, mMediaPlayer1.getVideoWidth(),
				mMediaPlayer1.getVideoHeight());

		pVideo1.setMaterial(mVideo1);
		pVideo1.setPosition(0f, 0f, 1f);
		
		a = new AlphaMapTexture("pitAlpha", R.raw.pit_1_vid_alpha);
		
		try {
			mVideo1.addTexture(a);
		} catch (TextureException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		addChild(pVideo1);

		pHUD_1 = new Plane(0.25f, 0.25f, 1, 1);
		pHUD_1.setRotY(180);

		mHUD_1 = new Material();

		mHUD_1.setColorInfluence(0);
		bHUD_1 = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.ring_middle);
		try {
			mHUD_1.addTexture(new Texture("bHUD_1", bHUD_1));

		} catch (TextureException e) {
			e.printStackTrace();
		}

		pHUD_1.setMaterial(mHUD_1);
		pHUD_1.setPosition(0f, 0f, 1f);
		pHUD_1.setTransparent(true);

		initPlane(pHUD_1, bHUD_1.getWidth(), bHUD_1.getHeight());
		addChild(pHUD_1);

		pHUD_2 = new Plane(1, 1, 1, 1);
		pHUD_2.setRotY(180);

		mHUD_2 = new Material();

		mHUD_2.setColorInfluence(0);
		bHUD_2 = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.laser_01);
		try {
			mHUD_2.addTexture(new Texture("bHUD_2", bHUD_2));

		} catch (TextureException e) {
			e.printStackTrace();
		}

		pHUD_2.setMaterial(mHUD_2);
		pHUD_2.setPosition(0f, -0.5f, 0.5f);
		pHUD_2.setTransparent(true);
		pHUD_2.setVisible(false);

		initPlane(pHUD_2, bHUD_2.getWidth(), bHUD_2.getHeight());
		addChild(pHUD_2);

		pHUD_3 = new Plane(1,1, 1, 1);
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

		/*pBack_1 = new Plane(1f, 1f, 1, 1);
		pBack_1.setRotY(180);

		mBack_1 = new Material();

		mBack_1.setColorInfluence(0);
		bBack_1 = BitmapFactory
				.decodeResource(mContext.getResources(), R.raw.a);
		try {
			mBack_1.addTexture(new Texture("bBack_1", bBack_1));

		} catch (TextureException e) {
			e.printStackTrace();
		}
		pBack_1.setMaterial(mBack_1);
		pBack_1.setPosition(0f, 0f, 1f);
		pBack_1.setTransparent(true);

		initPlane(pBack_1, bBack_1.getWidth(), bBack_1.getHeight());
		addChild(pBack_1);*/

		pLaser = new Plane(1f, 1f, 1, 1);
		pLaser.setRotY(180);

		mLaser = new Material();

		mLaser.setColorInfluence(0);

		if (bLaser == null) {
			bLaser = new Texture("bLaser", R.drawable.laser_01);
		}
		mTextureManager.addTexture(bLaser);

		pLaser.setMaterial(mLaser);
		pLaser.setPosition(0f, 0f, 0.1f);
		pLaser.setTransparent(true);

		initPlane(pLaser, bLaser.getWidth(), bLaser.getHeight());
		addChild(pLaser);
	}

	public void initVideo() {
		if (mMediaPlayer != null) {
			initMedia();
		}
	}

	public void initVideo1() {
		if (mMediaPlayer1 != null) {
			initMedia1();
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

	private void initMedia1() {
		if (mInit1 == false) {
			if (mMediaPlayer1.isPlaying()) {
				mMediaPlayer1.pause();
			}
			if (!mMediaPlayer1.isPlaying()) {
				mMediaPlayer1.stop();
				mMediaPlayer1.reset();
			}
		}
		try {

			String fileName = "android.resource://"
					+ getContext().getPackageName() + "/" + R.raw.pit_1;

			if (Globals.id_1 == 1) {
				fileName = "android.resource://"
						+ getContext().getPackageName() + "/" + R.raw.pit_1;
			} else if (Globals.id_1 == 2) {
				fileName = "android.resource://"
						+ getContext().getPackageName() + "/" + R.raw.pit_2;
			}

			mMediaPlayer1.setDataSource(getContext(), Uri.parse(fileName));

			mMediaPlayer1.setLooping(true);
			mMediaPlayer1.prepare();
			mMediaPlayer1.seekTo(0);
			mMediaPlayer1.start();
			if (mInit1) {
			} else {
				// mTextureManager.replaceTexture(mVideoTexture1);
			}
			mInit1 = false;

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

		if (mMediaPlayer1.isPlaying()) {
			mVideoTexture1.update();
		}

		pHUD_2.setVisible(false);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);

		initPlane(pVideo, mMediaPlayer.getVideoWidth(),
				mMediaPlayer.getVideoHeight());
		initPlane(pVideo1, mMediaPlayer1.getVideoWidth(),
				mMediaPlayer1.getVideoHeight());

		initVideo();
		initVideo1();

		int fileName = R.raw.pit_2_vid_alpha;
		if (Globals.id_1 == 1) {
			fileName = R.raw.pit_1_vid_alpha;
		} else if (Globals.id_1 == 2) {
			fileName = R.raw.pit_2_vid_alpha;
		}
		try {
			mVideo1.removeTexture(a);
			mVideo1.addTexture(a);
		} catch (TextureException e) {
			e.printStackTrace();
		}

		// mTextureManager.reset();
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

		if (!visible) {
			if (mMediaPlayer1 != null) {
				if (mMediaPlayer1.isPlaying()) {
					mMediaPlayer1.pause();
				}
			}
		} else if (mMediaPlayer1 != null && mInit1 == false) {
			mMediaPlayer1.start();
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

		if (mMediaPlayer1 != null) {
			mMediaPlayer1.release();
			mMediaPlayer1 = null;
		}
		mVideo1.removeTexture(mVideoTexture1);
		mTextureManager.taskRemove(mVideoTexture1);
		mMaterialManager.taskRemove(mVideo1);

		mMaterialManager.taskRemove(mHUD_1);
		mMaterialManager.taskRemove(mHUD_2);
		mMaterialManager.taskRemove( mHUD_3);
		mMaterialManager.taskRemove(mLaser);
		
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

			// getCurrentCamera().getPosition().x = mAccVals.x * .2f;
			// getCurrentCamera().getPosition().y = mAccVals.y * .2f;

			if (mAccVals.y < 5 && mAccVals.y > -5) {
				pVideo.getPosition().x = -mAccVals.y * .2f * 0.1;
				pVideo.getRotation().y = -mAccVals.y * .2f * 20 + 180;

				pVideo1.getPosition().x = -mAccVals.y * .2f * 0.1;
				pVideo1.getRotation().y = -mAccVals.y * .2f * 20 + 180;

				pHUD_1.getPosition().x = -mAccVals.y * .2f * 0.075;
				pHUD_1.getRotation().y = +mAccVals.y * .2f * 8 + 180;

				// pVideo.getPosition().x = -mAccVals.y * .2f*0.3;

				// getCurrentCamera().getLookAt().y = -mAccVals.y * .2f;
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
		//	pVideo.setX((1 - mWidthPlane) * (xOffset - 0.5));
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
				pHUD_2.setVisible(true);
				pVideo1.setY(pVideo1.getY() - 0.002);
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
				pVideo1.setY(0);
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