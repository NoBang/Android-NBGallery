package kr.nobang.nphotolibrary.photo;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;

import data.AlbumItem;
import data.PhotoItem;
import kr.nobang.nphotolibrary.R;
import kr.nobang.nphotolibrary.adapter.AlbumAdapter;
import util.DataTransfer;


/**
 * 폰 앨범 불러오기 뷰
 *
 * @author byeongnamno
 */
public class AlbumBaseFragment extends BaseFragment implements
        OnRefreshListener {

    /**
     * 그리드 뷰
     */
    private GridView gridView;

    /**
     * 이미지 어댑터
     */
    private AlbumAdapter adapter;

    /**
     * 리플래시 뷰
     */
    private CustomSwipeRefreshLayout refreshView;

    /**
     * 이미지 로드중인지
     */
    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album_gridview, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLayout();
        loadData();
    }

    /**
     * 레이아웃 초기화
     */
    private void initLayout() {
        gridView = (GridView) getView().findViewById(R.id.gridView);
        initAdapter();
        refreshView = (CustomSwipeRefreshLayout) getView().findViewById(
                R.id.swipeRefresh);
        refreshView.setOnRefreshListener(this);
        refreshView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        refreshView.setView(gridView);
    }

    /**
     * 어댑터 초기화
     */
    private void initAdapter() {
        adapter = new AlbumAdapter(getAttachActivity());
        adapter.setPhotoListener(new AlbumAdapter.PhotoClickListener() {
            @Override
            public void onClick(int position, View v) {

                AlbumItem item = adapter.getArrayList().get(position);


                DataTransfer dt = DataTransfer.getInstance();
                dt.hashMap.put("id", item.arraylist);


                Intent intent = new Intent(
                        getAttachActivity(),
                        DetailPhotoActivity.class);

                intent.putExtra("id", item.id);

                getAttachActivity().startActivityForResult(
                        intent,
                        LoadPhotoActivity.COMPLETE_PHOTO);
            }
        });

        adapter.setCameraListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getAttachActivity(),
                        CameraPhotoActivity.class);
                getAttachActivity().startActivityForResult(
                        intent,
                        LoadPhotoActivity.COMPLETE_PHOTO);
            }
        });

        gridView.setAdapter(adapter);
        gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader
                .getInstance(), true, true));
    }


    /**
     * 데이터 불러오기
     */
    private void loadData() {

        if (isLoading) {
            return;
        }
        loadTask();
    }

    /**
     * 이미지 가져오는 task
     */
    private void loadTask() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isLoading = true;
                refreshView.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.setRefreshing(true);
                    }
                });
            }

            @Override
            protected Void doInBackground(Void... params) {

                AlbumItem camera = new AlbumItem();
                camera.isCam = true;
                adapter.getArrayList().clear();
                adapter.getArrayList().add(camera);


                ArrayList<String> album_id = new ArrayList<String>();

                final String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
                final String orderBy = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC, " + MediaStore.Images.Media.DATE_ADDED + " DESC";

                Cursor imageCursor = getActivity().getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                        null, null, orderBy);


                int count = imageCursor.getCount();

                for (int i = 0; i < count; i++) {
                    imageCursor.moveToPosition(i);
                    long id = imageCursor.getLong(imageCursor
                            .getColumnIndex(MediaStore.Images.Media._ID));
                    long bucket_id = imageCursor.getLong(imageCursor
                            .getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                    String name = imageCursor.getString(imageCursor
                            .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                    int dataColumnIndex = imageCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    int dataThumbColumnIndex = imageCursor
                            .getColumnIndex(MediaStore.Images.Thumbnails.DATA);

                    AlbumItem item = null;

                    if (album_id.contains("" + bucket_id)) {
                        item = adapter.getArrayList().get(album_id.indexOf("" + bucket_id) + 1);

                        PhotoItem pic = new PhotoItem();
                        pic.id = id;
                        pic.filePath = imageCursor.getString(dataColumnIndex);
                        pic.thumbPath = imageCursor.getString(dataThumbColumnIndex);

                        item.arraylist.add(pic);

                    } else {

                        item = new AlbumItem();
                        item.id = bucket_id;
                        item.displayName = name;
                        item.thumbPath = imageCursor.getString(dataThumbColumnIndex);

                        PhotoItem pic = new PhotoItem();
                        pic.id = id;
                        pic.filePath = imageCursor.getString(dataColumnIndex);
                        pic.thumbPath = imageCursor.getString(dataThumbColumnIndex);

                        item.arraylist.add(pic);

                        if (name.equals("Camera")) {
                            item.displayName = getString(R.string.camera);
                            adapter.getArrayList().add(1, item);
                            album_id.add(0, "" + bucket_id);
                        } else {
                            adapter.getArrayList().add(item);
                            album_id.add("" + bucket_id);

                        }

                    }
                }

                imageCursor.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);


                refreshView.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.setRefreshing(false);
                        adapter.sync();
                        isLoading = false;
                    }
                });
            }

        };

        task.execute();

    }


    @Override
    public void onRefresh() {

        loadData();

    }


}
