package kr.nobang.nphotolibrary.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * 카메라 프리뷰
 *
 * @author byeongnamno
 */
public class TakeCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private static Camera camera;
	String TAG = "CameraPreview";

	public TakeCameraPreview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			mHolder = holder;
			camera = getCameraInstance();
			camera.setPreviewDisplay(mHolder);
		} catch (Exception e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		stop();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			camera.setPreviewDisplay(mHolder);
			camera.setDisplayOrientation(setRotate());
			camera.startPreview();

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	/**
	 * 화면 각도
	 *
	 * @return 카메라 회전 각도
	 */
	private int setRotate() {

		WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		int rotation = windowManager.getDefaultDisplay().getRotation();
		// int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

		int degrees = 0;

		switch (rotation) {

		case Surface.ROTATION_0:
			degrees = 0;
			break;

		case Surface.ROTATION_90:
			degrees = 90;
			break;

		case Surface.ROTATION_180:
			degrees = 180;
			break;

		case Surface.ROTATION_270:
			degrees = 270;
			break;

		}

		int result = (90 - degrees + 360) % 360;

		return result;
	}

	public void reConnect() {
		try {
			camera.reconnect();
		} catch (Exception e) {
			camera = getCameraInstance();
			e.printStackTrace();
		}
	}

	/**
	 * A safe way to get an instance of the Camera object.
	 */
	public static Camera getCameraInstance() {
		
		stop();
		
		try {
			if (camera == null) {
				camera = Camera.open(0); // attempt to get a Camera instance
			} else {
				camera.reconnect();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Camera is not available (in use or does not exist)
			try {
				camera = Camera.open(0);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return camera; // returns null if camera is unavailable
	}

	/**
	 * 정지
	 */
	public static void stop() {
		try {
			camera.stopPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			camera.release();
			camera = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void takePic(final File file) {
		camera.takePicture(new ShutterCallback() {

			@Override
			public void onShutter() {

			}
		}, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				new SaveImageTask(file).execute(data);
			}
		});
	}

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

		private File file;
		
		public SaveImageTask(File file) {
			this.file = file;
		}
		
		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			// Write to SD Card
			try {

				outStream = new FileOutputStream(file);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + file.getAbsolutePath());

				refreshGallery(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			return null;
		}

	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
//		sendBroadcast(mediaScanIntent);
	}
}