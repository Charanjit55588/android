package charanjit.lufthansa;

import android.app.Application;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Glide
        Glide.setup(new GlideBuilder(this)
                .setMemoryCache(new LruResourceCache(100 * 1024 * 1024))
                .setDiskCache(new InternalCacheDiskCacheFactory(this, 384 * 1024 * 1024))
                .setDecodeFormat(DecodeFormat.PREFER_RGB_565));
    }
}
