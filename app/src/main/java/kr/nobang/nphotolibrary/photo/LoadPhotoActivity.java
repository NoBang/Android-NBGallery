package kr.nobang.nphotolibrary.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import org.json.JSONArray;

import kr.nobang.nphotolibrary.R;


/**
 * 사진 찍기 & 앨범 사진 보여주는 액티비티
 *
 * @author nobang
 */
public class LoadPhotoActivity extends FragmentActivity {


    /**
     * 이미지 불러오기 완료.
     */
    public final static int COMPLETE_PHOTO = 1001;

    /**
     * 총 선택할수있는 이미지 갯수 디폴트 3
     */
    public static int IMAGE_MAX = 3;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_empty);

        settingFragment();
    }

    /**
     * 플래그먼트 보여준다.
     */
    private void settingFragment() {
//        PhotoBaseFragment fragment = new PhotoBaseFragment();
//        fragment.setMaxCount(IMAGE_MAX);
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.container, fragment);
//        ft.commit();

        AlbumBaseFragment fragment = new AlbumBaseFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);

        if (arg1 == RESULT_OK) {
            if (arg0 == COMPLETE_PHOTO) {
                JSONArray json = new JSONArray();

                json.put(arg2.getStringExtra("file"));
                Intent intent = new Intent();
                intent.putExtra("file", json.toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }

        if (arg1 == RESULT_CANCELED) {
            if (arg0 == COMPLETE_PHOTO) {
                settingFragment();
            }
        }
    }

}



