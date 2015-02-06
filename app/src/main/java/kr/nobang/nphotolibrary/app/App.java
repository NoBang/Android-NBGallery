package kr.nobang.nphotolibrary.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import kr.nobang.nphotolibrary.R;

/**
 * Created by nobang on 15. 2. 6..
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FadeInBitmapDisplayer fb = new FadeInBitmapDisplayer(150);

        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)
                // resource or drawable
                .showImageForEmptyUri(R.drawable.loading)
                        // resource or drawable
                .showImageOnFail(R.drawable.loading).displayer(fb).cacheOnDisk(true).cacheInMemory(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options).build();

        ImageLoader.getInstance().init(config);
    }
}
