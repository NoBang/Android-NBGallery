package kr.nobang.nphotolibrary.photo;


import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.AbsListView;

/**
 * 리플래시 뷰 스크롤 관련 버그 수정.
 *
 * @author offon
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private AbsListView listView;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setView(AbsListView view) {
        this.listView = view;
    }

    @Override
    public boolean canChildScrollUp() {


        return listView.getFirstVisiblePosition() > 0
                || listView.getChildAt(0) == null
                || listView.getChildAt(0).getTop() < 0;

    }
}