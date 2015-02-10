package kr.nobang.nphotolibrary.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.nobang.nphotolibrary.R;
import kr.nobang.nphotolibrary.photo.LoadPhotoActivity;


public class MainActivity extends ActionBarActivity {

    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoadPhotoActivity.state = LoadPhotoActivity.STATE.STATE_PHOTO;
                Intent intent = new Intent(MainActivity.this, LoadPhotoActivity.class);
                startActivityForResult(intent, LoadPhotoActivity.LOAD_PHOTO);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPhotoActivity.state = LoadPhotoActivity.STATE.STATE_ALBUM;
                Intent intent = new Intent(MainActivity.this, LoadPhotoActivity.class);
                startActivityForResult(intent, LoadPhotoActivity.LOAD_PHOTO);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("gmgm", "" + requestCode + resultCode);

        if (requestCode == LoadPhotoActivity.LOAD_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {

                try {
                    jsonArray = new JSONArray(data.getStringExtra("file"));

                    Log.i("tag", jsonArray.toString());
                    showToast("photo : " + jsonArray.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (resultCode == Activity.RESULT_CANCELED) {
                showToast("cancel");
            }
        }

    }

    /**
     * 토스트 보여주기
     *
     * @param msg 메세지
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
