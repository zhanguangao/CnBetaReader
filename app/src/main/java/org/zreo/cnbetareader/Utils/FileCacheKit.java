package org.zreo.cnbetareader.Utils;

import android.content.Context;
import java.io.File;


/**
 * Created by Administrator on 2015/7/30.
 */

public class FileCacheKit {
    private static final int MESSAGE_FINISH = 0x01;
    private static FileCacheKit fileCacheKit;
    private File cacheDir;

    FileCacheKit(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    FileCacheKit(Context context) {
        this.cacheDir = context.getCacheDir();
    }

    public static FileCacheKit getInstance(File cacheDir) {
        if (fileCacheKit == null) {
            fileCacheKit = new FileCacheKit(cacheDir);
        }
        return fileCacheKit;
    }

    public static FileCacheKit getInstance() {
        if (fileCacheKit == null)
            throw new NullPointerException(
                    "must getInstance(File cacheDir) before getInstance()");
        return fileCacheKit;
    }

    public static FileCacheKit getInstance(Context context) {
        if (fileCacheKit == null) {
            fileCacheKit = new FileCacheKit(context);
        }
        return fileCacheKit;
    }

    public void cleanCache() {
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    public long getCacheSize() {
        try {
            return FileKit.getFolderSize(cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public File getCacheDir() {
        return cacheDir;
    }


}
