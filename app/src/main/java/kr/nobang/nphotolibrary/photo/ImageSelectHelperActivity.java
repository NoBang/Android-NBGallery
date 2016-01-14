package kr.nobang.nphotolibrary.photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 사진 가져오는 클래스
 *
 * @author nobang
 */
public class ImageSelectHelperActivity extends FragmentActivity {
	/**
	 * Buttons for selector dialog
	 */
	private View mBtnGallery = null, mBtnCamera = null, mBtnCancel = null;

	public final int REQ_CODE_PICK_GALLERY = 901;
	public final int REQ_CODE_PICK_CAMERA = 902;
	public final int REQ_CODE_PICK_CROP = 903;
	private ImageView imageView;
	
	private File filePath;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("mCropRequested", mCropRequested);
		savedInstanceState.putInt("mCropAspectWidth", mCropAspectWidth);
		savedInstanceState.putInt("mCropAspectHeight", mCropAspectHeight);
		try {
			BitmapDrawable drawable = (BitmapDrawable) getImageView().getDrawable();
			if (drawable != null) {
				Bitmap bitmap = drawable.getBitmap();
				savedInstanceState.putParcelable("bitmap", bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mCropRequested = savedInstanceState.getBoolean("mCropRequested");
		mCropAspectWidth = savedInstanceState.getInt("mCropAspectWidth");
		mCropAspectHeight = savedInstanceState.getInt("mCropAspectHeight");
		Bitmap bm = (Bitmap) savedInstanceState.getParcelable("bitmap");
		if (bm != null) {
			getImageView().setImageBitmap(bm);
		}
	}

	/**
	 * Call this to start!
	 */
	public void startSelectImage() {
		if (!checkWriteExternalPermission()) {
			showAlert("we need android.permission.WRITE_EXTERNAL_STORAGE");
			return;
		}
		if (!checkSDisAvailable()) {
			showAlert("Check External Storage.");
			return;
		}
		if (getImageView() == null) {
			showAlert("Your layout should have ImageView name as ivImageSelected.");
			return;
		}
		if (mBtnGallery == null) {
			setDefaultButtons();
		}
		setButtonsClick();
		showSelectDialog();
	}

	public File getSelectedImageFile() {
		return getTempImageFile();
	}

	private boolean mCropRequested = false;

	/**
	 * crop ��� ��������� 寃쎌�� ��ㅼ�����. ��ㅼ�����吏� �����쇰㈃ crop ���吏� ������.
	 *
	 * @param width
	 *            crop size width.
	 * @param height
	 *            crop size height.
	 */
	private int mCropAspectWidth = 1, mCropAspectHeight = 1;

	public void setCropOption(int aspectX, int aspectY) {
		mCropRequested = true;
		mCropAspectWidth = aspectX;
		mCropAspectHeight = aspectY;
	}

	/**
	 * ��ъ�⑺�� ��대�몄����� 理���� ��ш린 ��ㅼ��. 媛�濡�, ��몃�� 吏������� ��ш린蹂대�� ������ ��ъ�댁��濡� ��대�몄�� ��ш린瑜� 議곗�����. default size : 500
	 *
	 * @param sizePixel
	 *            湲곕낯 500
	 */
	private int mImageSizeBoundary = 1000;

	public void setImageSizeBoundary(int sizePixel) {
		mImageSizeBoundary = sizePixel;
	}

	private boolean checkWriteExternalPermission() {
		String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
		int res = checkCallingOrSelfPermission(permission);
		return (res == PackageManager.PERMISSION_GRANTED);
	}

	private boolean checkSDisAvailable() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * Set your own button views for selector dialog.
	 */
	public void setCustomButtons(View btnGallery, View btnCamera, View btnCancel) {
		if (btnGallery == null || btnCamera == null || btnCancel == null) {
			showAlert("All buttons should not null.");
		} else {
			mBtnGallery = btnGallery;
			mBtnCamera = btnCamera;
			mBtnCancel = btnCancel;
		}
	}

	/**
	 * Set default buttons for selector dialog, unless you set.
	 */
	private void setDefaultButtons() {
		Button btn1 = new Button(this);
		Button btn2 = new Button(this);
		Button btn3 = new Button(this);
		btn1.setText("From Gallery");
		btn2.setText("From Camera");
		btn3.setText("Cancel");
		mBtnGallery = btn1;
		mBtnCamera = btn2;
		mBtnCancel = btn3;
	}

	private File getTempImageFile() {

		File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/temp/");
		if (!path.exists()) {
			path.mkdirs();
		}

		// final String filename = String.valueOf(System.currentTimeMillis());

		try {
			if (filePath != null) {
				return filePath;
			}
			filePath = File.createTempFile("tempfile" + fileName, ".jpg", path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	public void setGalleryOpen() {

		if (Build.VERSION.SDK_INT > 18) {
			Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, REQ_CODE_PICK_GALLERY);
		} else {
			Intent i = new Intent(Intent.ACTION_PICK);
			i.setType(MediaStore.Images.Media.CONTENT_TYPE);
			i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, REQ_CODE_PICK_GALLERY);
		}
	}

	public void setCaptureOpen() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = getTempImageFile();
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				takePictureIntent.putExtra("return-data", true);
				startActivityForResult(takePictureIntent, REQ_CODE_PICK_CAMERA);
			}
		}

	}

	private void setButtonsClick() {
		mBtnGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectDialog.dismiss();
				Intent i = new Intent(Intent.ACTION_PICK);
				i.setType(MediaStore.Images.Media.CONTENT_TYPE);
				i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, REQ_CODE_PICK_GALLERY);
			}
		});
		mBtnCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectDialog.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempImageFile()));
				intent.putExtra("return-data", true);
				startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
			}
		});
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectDialog != null && mSelectDialog.isShowing()) {
					mSelectDialog.dismiss();
				}
			}
		});
	}

	private Dialog mSelectDialog;

	private void showSelectDialog() {
		if (mSelectDialog == null) {
			mSelectDialog = new Dialog(this);
			mSelectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			LinearLayout linear = new LinearLayout(this);
			linear.setOrientation(LinearLayout.VERTICAL);
			linear.addView(mBtnGallery);
			linear.addView(mBtnCamera);
			linear.addView(mBtnCancel);
			int dialogWidth = getResources().getDisplayMetrics().widthPixels / 2;
			if (dialogWidth / 2 > 700) {
				dialogWidth = 700;
			}
			mSelectDialog.setContentView(linear, new LayoutParams(dialogWidth, LayoutParams.WRAP_CONTENT));
		}
		mSelectDialog.show();
	}

	private void showAlert(String msg) {
		new AlertDialog.Builder(this).setTitle("Error").setMessage(msg).setPositiveButton(android.R.string.ok, null).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
			// 媛ㅻ�щ━��� 寃쎌�� 怨㏓��濡� data ��� uri媛� �����댁��.
			Uri uri = data.getData();
			copyUriToFile(uri, getTempImageFile());
			if (mCropRequested) {
				cropImage();
			} else {
				doFinalProcess();
			}
		} else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
			// 移대����쇱�� 寃쎌�� file 濡� 寃곌낵臾쇱�� ���������.
			// 移대����� ������ 蹂댁��.
			correctCameraOrientation(getTempImageFile());
			if (mCropRequested) {
				cropImage();
			} else {
				doFinalProcess();
			}
		} else if (requestCode == REQ_CODE_PICK_CROP && resultCode == Activity.RESULT_OK) {
			// crop ��� 寃곌낵��� file濡� ���������.
			doFinalProcess();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}

		if (resultCode == Activity.RESULT_CANCELED) {
			onCancelEnd();
		}
	}

	// 마지막
	private void doFinalProcess() {
		// sample size 瑜� �����⑺����� bitmap load.
		Bitmap bitmap = loadImageWithSampleSize(getTempImageFile());

		// image boundary size ��� 留����濡� ��대�몄�� 異����.
//		bitmap = resizeImageWithinBoundary(bitmap);

		// 寃곌낵 file ��� ��살�닿�� ��� ������ 硫������� ���怨�.
		saveBitmapToFile(bitmap);

		// show image on ImageView
		Bitmap bm = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath());

		if (getImageView() != null) {
			getImageView().setImageBitmap(bm);
		}

		onSelectedEnd(getSelectedImageFile(), bitmap);
	}

	public void onSelectedEnd(File selectedImageFile, Bitmap bitmap) {
	}

	public void onCancelEnd() {

	}

	private void saveBitmapToFile(Bitmap bitmap) {
		File target = getTempImageFile();
		try {
			FileOutputStream fos = new FileOutputStream(target, false);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��대�몄�� ��ъ�댁�� ������ ���, 移대����� rotation ���蹂닿�� �����쇰㈃ ������ 蹂댁�����.
	 */
	private void correctCameraOrientation(File imgFile) {
		Bitmap bitmap = loadImageWithSampleSize(imgFile);
		Log.i("gmgm", "bitmap" + bitmap);
		try {
			ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			int exifRotateDegree = exifOrientationToDegrees(exifOrientation);
			bitmap = rotateImage(bitmap, exifRotateDegree);
			saveBitmapToFile(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Bitmap rotateImage(Bitmap bitmap, int degrees) {
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
			try {
				Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != converted) {
					bitmap.recycle();
					bitmap = converted;
				}
			} catch (OutOfMemoryError ex) {
			}
		}
		return bitmap;
	}

	/**
	 * EXIF���蹂대�� ������媛����濡� 蹂���������� 硫�������
	 *
	 * @param exifOrientation
	 *            EXIF ������媛�
	 * @return ��ㅼ�� 媛����
	 */
	private int exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	/**
	 * ��������� ��ш린��� ��대�몄��濡� options ��ㅼ��.
	 */
	private Bitmap loadImageWithSampleSize(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		int width = options.outWidth;
		int height = options.outHeight;
		int longSide = Math.max(width, height);
		int sampleSize = 1;
		if (longSide > mImageSizeBoundary) {
			sampleSize = longSide / mImageSizeBoundary;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		options.inPurgeable = true;
		options.inDither = false;

		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		return bitmap;
	}

	/**
	 * mImageSizeBoundary ��ш린濡� ��대�몄�� ��ш린 議곗��. mImageSizeBoundary 蹂대�� ������ 寃쎌�� resize���吏� ������.
	 */
	private Bitmap resizeImageWithinBoundary(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width > height) {
			if (width > mImageSizeBoundary) {
				bitmap = resizeBitmapWithWidth(bitmap, mImageSizeBoundary);
			}
		} else {
			if (height > mImageSizeBoundary) {
				bitmap = resizeBitmapWithHeight(bitmap, mImageSizeBoundary);
			}
		}
		return bitmap;
	}

	private Bitmap resizeBitmapWithHeight(Bitmap source, int wantedHeight) {
		if (source == null)
			return null;

		int width = source.getWidth();
		int height = source.getHeight();

		float resizeFactor = wantedHeight * 1f / height;

		int targetWidth, targetHeight;
		targetWidth = (int) (width * resizeFactor);
		targetHeight = (int) (height * resizeFactor);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

		return resizedBitmap;
	}

	private Bitmap resizeBitmapWithWidth(Bitmap source, int wantedWidth) {
		if (source == null)
			return null;

		int width = source.getWidth();
		int height = source.getHeight();

		float resizeFactor = wantedWidth * 1f / width;

		int targetWidth, targetHeight;
		targetWidth = (int) (width * resizeFactor);
		targetHeight = (int) (height * resizeFactor);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

		return resizedBitmap;
	}

	private void copyUriToFile(Uri srcUri, File target) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// ��ㅽ�몃┝ ������
			inputStream = (FileInputStream) getContentResolver().openInputStream(srcUri);
			outputStream = new FileOutputStream(target);

			// 梨���� ������
			fcin = inputStream.getChannel();
			fcout = outputStream.getChannel();

			// 梨������� ��듯�� ��ㅽ�몃┝ ������
			long size = fcin.size();
			fcin.transferTo(0, size, fcout);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fcout.close();
			} catch (IOException ioe) {
			}
			try {
				fcin.close();
			} catch (IOException ioe) {
			}
			try {
				outputStream.close();
			} catch (IOException ioe) {
			}
			try {
				inputStream.close();
			} catch (IOException ioe) {
			}
		}
	}

	private void cropImage() {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> cropToolLists = getPackageManager().queryIntentActivities(intent, 0);
		int size = cropToolLists.size();
		if (size == 0) {
			// crop 을 처리할 앱이 없음. 곧바로 처리.
			doFinalProcess();
		} else {
			intent.setData(Uri.fromFile(getTempImageFile()));
			intent.putExtra("aspectX", mCropAspectWidth);
			intent.putExtra("aspectY", mCropAspectHeight);
			intent.putExtra("output", Uri.fromFile(getTempImageFile()));
			Intent i = new Intent(intent);
			ResolveInfo res = cropToolLists.get(0);
			i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(i, REQ_CODE_PICK_CROP);
		}
	}
	

	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = getTempImageFile();
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}

	private String fileName = "";

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

}
