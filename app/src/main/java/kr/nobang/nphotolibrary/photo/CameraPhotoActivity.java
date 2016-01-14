package kr.nobang.nphotolibrary.photo;

import java.io.File;

import kr.nobang.nphotolibrary.R;

import org.json.JSONArray;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

/**
 * 카메라 툴을 불러오는 액티비티
 * 
 * @author nobang
 *
 */
public class CameraPhotoActivity extends ImageSelectHelperActivity {

	private String uri = "";


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_photo_take);
		Log.i("gmngn", "oncrate");
		setFileName("temp"+System.currentTimeMillis());
		setCaptureOpen();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
	@Override
	public void onSelectedEnd(File selectedImageFile, Bitmap bitmap) {
		super.onSelectedEnd(selectedImageFile, bitmap);
		Log.i("gmgm", "bb"+bitmap.getWidth());
		uri = "file://" + selectedImageFile.getAbsolutePath();
		done();
	}

	@Override
	public void onCancelEnd() {
		super.onCancelEnd();
		setResult(RESULT_CANCELED);
		finish();
	}

}
