package com.dq.itopic.activity.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import com.dq.itopic.manager.QiniuManager;
import com.dq.itopic.tools.GlideEngine;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 带有选择+上传单张图片功能的界面，继承此类
 * 子类只需要调用startPictureSelect方法，即可开始选择
 * 然后子类 监听（重写）3个方法：
 * onPhotoSelectSuccess 选择成功（自动上传）
 * onPhotoUploadSuccess 上传成功
 * onPhotoUploadFail 上传失败
 *
 * @author dongjin
 *
 */
public class BasePhotoActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

	private final static int REQUIRE_PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE = 0x191;
	private ImageView currentImageView;
	private boolean cropImage = true;//默认都需要剪裁为正方形

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * @param currentImageView 头像ImageView。可以为空，为空的话，不剪裁图片
	 */
	public void startPictureSelect(ImageView currentImageView) {
		this.currentImageView = currentImageView;

		//点击了+👌进入相册选图
		PictureSelector.create(BasePhotoActivity.this)
				.openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
				.isWeChatStyle(true)//开启R.style.picture_WeChat_style样式// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
				.maxSelectNum(1)// 最大图片选择数量
				.minSelectNum(1)// 最小选择数量
				.imageSpanCount(3)// 每行显示个数
				.selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
				.previewImage(true)// 是否可预览图片
				.previewVideo(false)// 是否可预览视频
				.isCamera(true)// 是否显示拍照按钮
				.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
				.loadImageEngine(GlideEngine.createGlideEngine())
				.enableCrop(cropImage)// 是否裁剪
				.compress(true)// 是否压缩
				.withAspectRatio(xAspectRatio(), yAspectRatio())// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
				.hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
				.isGif(true)// 是否显示gif图片
				.freeStyleCropEnabled(true)// 裁剪框是否可拖拽
				.circleDimmedLayer(false)// 是否圆形裁剪
				.showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
				.showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
				.openClickSound(true)// 是否开启点击声音
				.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
	}

	public int xAspectRatio(){
		return 1;
	}

	public int yAspectRatio(){
		return 1;
	}

	/** 子类设置为true，选取图片后将进入切图模式 */
	protected void setNeedCropImage(boolean cropImage) {
		this.cropImage = cropImage;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PictureConfig.CHOOSE_REQUEST:
					// 图片选择结果回调
					List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
					if (list != null && list.size() > 0) {
						LocalMedia media = list.get(0);
						String path = "";
						if (media.isCut() && !media.isCompressed()) {
							// 裁剪过
							path = media.getCutPath();
						} else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
							// 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
							path = media.getCompressPath();
						} else {
							// 原图
							path = media.getPath();
						}

						onPhotoSelectSuccess(path, currentImageView);
						upLoadPicture(path);
					} else {
						showToast("图片保存异常");
					}
					break;
			}
		}
	}

	// 如果不是切割的upLoadBitmap就很大
	public void upLoadPicture(final String newPicturePath) {
		//先访问自己的服务器获取上传token
		QiniuManager.getInstance().requireQiniuToken(getITopicApplication(),"qiniu/uploadtoken",new HashMap<String, String>(),this, new QiniuManager.RequireQiniuTokenListener() {

			@Override
			public void success(String qiniuToken) {
				// TODO Auto-generated method stub

				//2、如果有图，调用七牛sdk上传图片到七牛
				Configuration qiniuConfig = new Configuration.Builder().build();
				UploadManager uploadManager = new UploadManager(qiniuConfig);

				//定好好9张图的名字前缀
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String time = sdf.format(new Date());
				final String namekey = "user-"+time;// <指定七牛服务上的文件名，或 null>

				uploadManager.put(newPicturePath, namekey+"-" + (int)(Math.random() * 10000), qiniuToken, new UpCompletionHandler() {

					@Override
					public void complete(String key, ResponseInfo info, JSONObject res) {
						// res 包含hash、key等信息，具体字段取决于上传策略的设置。
						progress.dismiss();
						if (res != null) {
							try {
								String qiniuFileName = res.getString("filename");
								onPhotoUploadSuccess(qiniuFileName, res, currentImageView);
								return ;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						showToast("图片上传异常，请稍后再试");
						File thumbnailPhoto = new File(newPicturePath);
						if (thumbnailPhoto.exists()) {
							thumbnailPhoto.delete();
						}
						onPhotoUploadFail(currentImageView);
					}
				}, null);
			}

			@Override
			public void fail(int code, String message) {
				//七牛token获取失败
				progress.dismiss();
				showToast(message);
				File thumbnailPhoto = new File(newPicturePath);
				if (thumbnailPhoto.exists()) {
					thumbnailPhoto.delete();
				}
				onPhotoUploadFail(currentImageView);
			}
		});
	}

	/**
	 * 选取图片成功
	 *
	 * @param picturePath
	 * @param currentImageView
	 */
	public void onPhotoSelectSuccess(String picturePath,
									 ImageView currentImageView) {
	}

	/**
	 * 上传成功
	 *
	 * @param qiniuFileName 对应的七牛文件名，配合七牛前缀就形成url
	 * @param qiniuJSONObject 对应的七牛返回的jsonObject，里面包含filename，with，height
	 * @param currentImageView 开始设置的imageView
	 */
	public void onPhotoUploadSuccess(String qiniuFileName, JSONObject qiniuJSONObject,
									 ImageView currentImageView) {
	}

	/**
	 * 上传失败
	 *
	 * @param currentImageView
	 */
	public void onPhotoUploadFail(ImageView currentImageView) {
	}

	// 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限，不要在onDestory里调用！
	public void clearCache(){
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (EasyPermissions.hasPermissions(this, perms)) {
			// 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
			PictureFileUtils.deleteAllCacheDirFile(BasePhotoActivity.this);
		} else {
			EasyPermissions.requestPermissions(this, "清除缓存需要SD卡读写权限",REQUIRE_PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE, perms);
			return ;
		}
	}

	//如果已经授权了，清除缓存，如果没权限，什么都不做，可以在onDestory里调用
	private void clearCacheIfHasPermissions(){
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (EasyPermissions.hasPermissions(this, perms)) {
			// 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
			PictureFileUtils.deleteAllCacheDirFile(BasePhotoActivity.this);
		}
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		if(requestCode == REQUIRE_PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE){
			PictureFileUtils.deleteAllCacheDirFile(BasePhotoActivity.this);
		}
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
		if(requestCode == REQUIRE_PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE){
			showToast("写入SD卡权限被禁止，会导致出现重复图片");
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		clearCacheIfHasPermissions();
		super.onDestroy();
	}
}