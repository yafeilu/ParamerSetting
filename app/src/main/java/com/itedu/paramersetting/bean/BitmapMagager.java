package com.itedu.paramersetting.bean;

import android.graphics.Bitmap;

/**
 * Created by luyafei on 2017/11/15.
 */

public class BitmapMagager {
    private static BitmapMagager bitmapMagager;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;
    private BitmapMagager(){

    }
    public static BitmapMagager getInstance(){
        if (bitmapMagager==null){
            bitmapMagager=new BitmapMagager();
        }
        return bitmapMagager;
    }

}
