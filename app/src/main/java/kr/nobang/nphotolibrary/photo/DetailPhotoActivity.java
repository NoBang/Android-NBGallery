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
public class DetailPhotoActivity extends FragmentActivity {


    /**
     * 이미지 불러오기 완료.
     */
    public final static int COMPLETE_PHOTO = 1001;

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
        DetailPhotoBaseFragment fragment = new DetailPhotoBaseFragment();
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



