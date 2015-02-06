package kr.nobang.nphotolibrary.photo;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * 모든 fragment는 이클래스 상속 한다. getActivity null을 막기 위한 클래스
 * 
 * @author offon
 *
 */
public class BaseFragment extends Fragment {

	private Activity activity;

	@Override
	public void onAttach(Activity activity) {

		this.activity = activity;
		super.onAttach(activity);
	}

	public Activity getAttachActivity() {

		if (getActivity() == null) {
			return activity;
		} else {
			return getActivity();
		}

	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
