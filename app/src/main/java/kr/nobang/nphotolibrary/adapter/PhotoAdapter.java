package kr.nobang.nphotolibrary.adapter;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import data.PhotoItem;
import kr.nobang.nphotolibrary.R;
import kr.nobang.nphotolibrary.photo.CameraPreview;

/**
 * 이미지셀 어댑터
 *
 * @author byeongnamno
 */
public class PhotoAdapter extends BaseAdapter {

    /**
     * 셀 타입
     */
    int type_cam = 0, type_pic = 1;

    /**
     * 사진 클릭 리스너
     */
    public interface PhotoClickListener {

        void onClick(int position, View v);

    }

    /**
     * 레이아웃 인플레이터
     */
    private LayoutInflater mInflater;

    /**
     * 엘범 데이터
     */
    private ArrayList<PhotoItem> arrayList;

    /**
     * 사진 갯수
     */
    private int count = 0;

    /**
     * 컨텍스트
     */
    private Context context;

    /**
     * 카메라 프리뷰
     */
    private CameraPreview preview;

    /**
     * 선택한 이미지 인덱스 기억
     */
    private ArrayList<Integer> selectArray;

    /**
     * 카메라
     */
    private static Camera camera;

    /**
     * 이미지 최대 갯수
     */
    private int maxCount = 0;

    /**
     * 이미 글쓰기 뷰에 있는 이미지 갯수
     */
    private int imageCount = 0;

    /**
     * 카메라 클릭 리스너
     */
    private View.OnClickListener cameraListener;

    /**
     * 사진 클릭 리스너
     */
    private PhotoClickListener photoListener;

    /**
     * 카메라 아이콘
     */
    private int cameraIcon = R.drawable.photo_camera;


    public PhotoAdapter(Context context, int imageCount, int maxCount) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        arrayList = new ArrayList<>();
        selectArray = new ArrayList<>();

        this.imageCount = imageCount;
        this.maxCount = maxCount;

    }

    public void sync() {
        count = arrayList.size();
        notifyDataSetChanged();
    }

    public int getCount() {
        return count;
    }

    public PhotoItem getItem(int position) {
        return arrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        final PhotoItem item = getItem(position);

        if (item.isCam) {
            return type_cam;
        } else {
            return type_pic;
        }

    }

    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder;
        final ViewHolder camHolder;
        int type = getItemViewType(position);
        final PhotoItem item = getItem(position);

        if (type == type_cam) {
            if (convertView == null) {
                camHolder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.cell_photo_gallery, parent, false);
                camHolder.container = (FrameLayout) convertView
                        .findViewById(R.id.photoContainer);
                camHolder.imageCheck = (ImageView) convertView
                        .findViewById(R.id.imageCheck);
                convertView.setTag(camHolder);

                preview = new CameraPreview(context,
                        getCameraInstance());
                camHolder.imageCheck
                        .setImageResource(cameraIcon);
                camHolder.imageCheck.setVisibility(View.VISIBLE);
                camHolder.container.addView(preview);

                if (cameraListener != null)
                    camHolder.container
                            .setOnClickListener(cameraListener);
            } else {
                camHolder = (ViewHolder) convertView.getTag();
            }

            return convertView;

        } else {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.cell_photo_gallery, parent, false);
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.imageThumb);
                holder.imageCheck = (ImageView) convertView
                        .findViewById(R.id.imageCheck);
                holder.container = (FrameLayout) convertView
                        .findViewById(R.id.photoContainer);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String imageStr = (String) holder.imageview.getTag();

            if (imageStr == null) {
                imageStr = "";
            }

            if (imageStr.equals(item.thumbPath) == false) {
                ImageLoader.getInstance().displayImage(
                        "file://" + item.thumbPath, holder.imageview);
                holder.imageview.setTag(item.thumbPath);
            }


            if (selectArray.contains(Integer.valueOf(position))) {
                holder.imageCheck.setVisibility(View.VISIBLE);
            } else {
                holder.imageCheck.setVisibility(View.GONE);
            }

            if (photoListener != null) {
                holder.imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photoListener != null) {
                            photoListener.onClick(position, v);
                        }
                    }
                });
            }

            return convertView;
        }

    }

    public void clear() {
        arrayList.clear();
    }

    public ArrayList<PhotoItem> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<PhotoItem> arrayList) {
        this.arrayList = arrayList;
    }


    /**
     * 뷰홀더
     *
     * @author byeongnamno
     */
    public class ViewHolder {
        ImageView imageview;
        ImageView imageCheck;
        FrameLayout container;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
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

    public void setPhotoListener(PhotoClickListener photoListener) {
        this.photoListener = photoListener;
    }

    public void setCameraListener(View.OnClickListener cameraListener) {
        this.cameraListener = cameraListener;
    }

    public ArrayList<Integer> getSelectArray() {
        return selectArray;
    }

    public void setCameraIcon(int cameraIcon) {
        this.cameraIcon = cameraIcon;
    }
}