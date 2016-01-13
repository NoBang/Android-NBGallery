package kr.nobang.nphotolibrary.photo;

import java.io.File;
import java.io.IOException;

import kr.nobang.nphotolibrary.R;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;

/**
 * 카메라 툴을 불러오는 액티비티
 * 
 * @author nobang
 *
 */
public class CameraPhotoActivity extends Activity {

	private String uri = "";

	private String fileName;

	/**
	 * 카메라 프리뷰
	 */
	private TakeCameraPreview preview;

	/**
	 * 카메라 컨테이너
	 */
	private FrameLayout container_camera;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_photo_take);

		container_camera = (FrameLayout) findViewById(R.id.container_camera);
		fileName = "temp" + System.currentTimeMillis();
		preview = new TakeCameraPreview(this);
		container_camera.addView(preview);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		 preview.stop();
	}

	/**
	 * 임시저장 위치
	 * 
	 * @return
	 */
	private File getTempImageFile() {

		File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/temp/");
		if (!path.exists()) {
			path.mkdirs();
		}

		// final String filename = String.valueOf(System.currentTimeMillis());

		File file = null;
		try {
			file = File.createTempFile("tempfile" + fileName, ".jpg", path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 선택 완료.
	 */
	private void done() {

		if (uri.length() == 0) {
			return;
		}

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(uri);
		Intent intent = new Intent();
		intent.putExtra("file", jsonArray.toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	// @Override
	// public void onSelectedEnd(File selectedImageFile, Bitmap bitmap) {
	// super.onSelectedEnd(selectedImageFile, bitmap);
	// uri = "file://" + selectedImageFile.getAbsolutePath();
	// done();
	// }
	//
	// @Override
	// public void onCancelEnd() {
	// super.onCancelEnd();
	// setResult(RESULT_CANCELED);
	// finish();
	// }

}
