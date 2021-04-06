package com.dq.itopic.tools.imageloader;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dq.itopic.tools.ValueUtil;

import androidx.fragment.app.Fragment;


/**
 * Created by soulrelay on 2016/10/11 13:42.
 * Class Note:
 * use this class to load image,single instance
 */
public class GlideLoaderUtil {


    public static void loadImage(Fragment mFragment, String url, int placeholder, ImageView imageView) {
        if (mFragment == null) {
            return;
        }
        loadImage(mFragment.getActivity(),url,placeholder,imageView);
    }

    public static void loadImage(Activity mActivity, String url, int placeholder, ImageView imageView) {
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }

        RequestOptions glideoptions = new RequestOptions()
                .centerCrop()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(mActivity).load(url).apply(glideoptions).into(imageView);
    }

    public static void loadImage(Activity mActivity, String url,  ImageView imageView) {
        Glide.with(mActivity).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    public static void loadGif(Fragment mFragment, String filename, boolean isThumbnail, int placeholder, ImageView imageView){
        if (filename != null && filename.endsWith(".gif")){
            Activity mActivity = mFragment.getActivity();
            if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
                return;
            }
            Glide.with(mActivity).asGif().load(ValueUtil.getQiniuUrlByFileName(filename,isThumbnail)).into(imageView);//采用Giflib来解码GIF
        } else {
            GlideLoaderUtil.loadImage(mFragment.getActivity(),ValueUtil.getQiniuUrlByFileName(filename,isThumbnail),placeholder,imageView);
        }
    }

    public static void loadGif(Activity mActivity, String filename, boolean isThumbnail, int placeholder, ImageView imageView){
        if (mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing()) {
            return;
        }
        if (filename != null && filename.endsWith(".gif")){
            Glide.with(mActivity).asGif().load(ValueUtil.getQiniuUrlByFileName(filename,isThumbnail)).into(imageView);//采用Giflib来解码GIF
        } else {
            GlideLoaderUtil.loadImage(mActivity,ValueUtil.getQiniuUrlByFileName(filename,isThumbnail),placeholder,imageView);
        }
    }
}
