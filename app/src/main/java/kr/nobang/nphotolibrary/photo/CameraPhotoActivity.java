package kr.nobang.nphotolibrary.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.io.File;

import kr.nobang.nphotolibrary.R;


/**
 * 카메라 툴을 불러오는 액티비티
 * @author offon
 *
 */
public class CameraPhotoActivity extends ImageSelectHelperActivity {

	private String uri = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_photo_take);
		setFileName("temp"+System.currentTimeMillis());
		setCaptureOpen();
	}

	/**
	 * 선택 완료.
	 */
	private void done() {

		if (uri.length() == 0) {
			return;
		}

		Intent intent = new Intent();
		intent.putExtra("file", uri);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onSelectedEnd(File selectedImageFile, Bitmap bitmap) {
		super.onSelectedEnd(selectedImageFile, bitmap);
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
