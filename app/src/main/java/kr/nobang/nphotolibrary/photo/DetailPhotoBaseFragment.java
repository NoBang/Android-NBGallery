package kr.nobang.nphotolibrary.photo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.json.JSONArray;

import java.util.ArrayList;

import data.PhotoItem;
import kr.nobang.nphotolibrary.R;
import kr.nobang.nphotolibrary.adapter.PhotoAdapter;


/**
 * 사진 불러오기 뷰
 *
 * @author byeongnamno
 */
public class DetailPhotoBaseFragment extends BaseFragment implements
        OnRefreshListener {

    /**
     * 그리드 뷰
     */
    private GridView gridView;

    /**
     * 이미지 어댑터
     */
    private PhotoAdapter adapter;

    /**
     * 리플래시 뷰
     */
    private CustomSwipeRefreshLayout refreshView;

    /**
     * 이미지 로드중인지
     */
    private boolean isLoading = false;

    /**
     * 사용하기 버튼 이미지 선택하고 다음으로 이동하
     */
    private TextView textUse;

    /**
     * 이미 글쓰기 뷰에 있는 이미지 갯수
     */
    private int imageCount = 0;

    /**
     * 이미지 최대 갯수
     */
    private int maxCount = 0;

    /**
     * 데이터 배열
     */
    private ArrayList<PhotoItem> arrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_gridview, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageCount = LoadPhotoActivity.SELECT_IMAGE_COUNT;
        maxCount = LoadPhotoActivity.IMAGE_MAX;

        initLayout();

        settingNext();
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
        adapter = new PhotoAdapter(getAttachActivity(), imageCount, maxCount);
        adapter.setPhotoListener(new PhotoAdapter.PhotoClickListener() {
            @Override
            public void onClick(int position, View v) {
                if (adapter.getSelectArray().contains(Integer.valueOf(position))) {
                    adapter.getSelectArray().remove(Integer.valueOf(position));
                } else {

                    if (adapter.getSelectArray().size() >= Math.max(
                            (maxCount - imageCount),
                            1)) {
                        adapter.getSelectArray().remove(0);
                    }

                    adapter.getSelectArray().add(Integer.valueOf(position));
                }

                showNextView();
                adapter.notifyDataSetChanged();
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
     * 다음버튼 설정
     */
    private void settingNext() {
        textUse = (TextView) getView().findViewById(R.id.textUse);
        textUse.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (adapter.getSelectArray().size() != 0) {

                    JSONArray json = new JSONArray();

                    for (int i = 0; i < adapter.getSelectArray().size(); i++) {
                        PhotoItem item = adapter.getItem(adapter.getSelectArray().get(i));
                        json.put("file://" + item.filePath);
                    }

                    Intent intent = new Intent();
                    intent.putExtra("file", json.toString());
                    getAttachActivity().setResult(Activity.RESULT_OK, intent);
                    getAttachActivity().finish();
                }
            }
        });
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

                refreshView.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.setRefreshing(true);
                        isLoading = true;
                    }
                });

            }

            @Override
            protected Void doInBackground(Void... params) {

                adapter.getArrayList().clear();
                adapter.getArrayList().addAll(arrayList);
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

    /**
     * 다음버튼 나오게
     */
    private void showNextView() {
        if (adapter.getSelectArray().size() == 0) {
            ViewExpandAnimation.collapse(textUse);
        } else {
            if (ViewExpandAnimation.isShow(textUse) == false) {
                ViewExpandAnimation.expand(textUse);
            }
        }
    }

    public void setArrayList(ArrayList<PhotoItem> arrayList) {
        this.arrayList = arrayList;
    }
}
