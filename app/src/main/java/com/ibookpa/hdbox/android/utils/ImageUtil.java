package com.ibookpa.hdbox.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.ibookpa.hdbox.android.R;

/**
 * Created by tc on 6/29/16. 图片处理类
 */
public class ImageUtil {
    /**
     * 将utl的图片加载到指定 ImageView 中
     */
    public static void requestImg(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .placeholder(R.color.grey_300)
                .override(256,128)
                .bitmapTransform(new ImageTransformation(context))
                .error(R.color.red_300)
                .into(view);
    }


    public static void requestImgFromBytes(Context context, byte[] bytes, ImageView view) {
        Glide.with(context)
                .load(bytes)
                .placeholder(R.color.grey_300)
                .error(R.color.red_300)
                .into(view);
    }

    public static class ImageTransformation implements Transformation<Bitmap> {
//        private Context mContext;

        private static Paint mMaskingPaint = new Paint();
        private BitmapPool mBitmapPool;

        public ImageTransformation(Context context) {
            this(context, Glide.get(context).getBitmapPool());
        }

        public ImageTransformation(Context context, BitmapPool pool) {
            mBitmapPool = pool;
//            mContext = context.getApplicationContext();
        }

        @Override
        public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {

            Bitmap source = resource.get();

            int width = source.getWidth();
            int height = source.getHeight();

            Bitmap result = mBitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(source, 0, 0, mMaskingPaint);
            canvas.drawColor(0x44000000);

            return BitmapResource.obtain(result, mBitmapPool);
        }

        @Override
        public String getId() {
            return "MaskTransformation(maskId)";
        }
    }


}
