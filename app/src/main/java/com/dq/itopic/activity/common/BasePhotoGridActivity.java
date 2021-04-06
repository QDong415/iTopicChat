package com.dq.itopic.activity.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dq.itopic.R;
import com.dq.itopic.tools.GlideEngine;
import com.dq.itopic.views.UnScrollGridView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.PictureFileUtils;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 需要选择 多张图片功能 的界面，继承此类
 * 
 * 子类只需要调用showBottomPopupWin方法，即可在底部弹出 拍照/相册 框
 */
public class BasePhotoGridActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{


	public final static int PICTURE_UPDATE_ICON = R.drawable.picture_update_icon;
	private final static int REQUIRE_PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE = 0x191;
	/**
	 * 最多选择图片的个数
	 */
	private int MAX_DEFAULT_NUM = 9;
	
	private UnScrollGridView gridView;

	// 选择的图路径
	protected List<LocalMedia> selectList = new ArrayList<>();

	private PhotoReleaseGridAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initProgressDialog(false,null);
	}
	
	protected void initGridView() {
		
		gridView = (UnScrollGridView) findViewById(R.id.gridView);
		adapter = new PhotoReleaseGridAdapter(selectList,this);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(position < selectList.size()){
					//预览图片
					LocalMedia media = selectList.get(position);
					int mediaType = PictureMimeType.getMimeType(media.getMimeType());
					switch (mediaType) {
						case PictureConfig.TYPE_IMAGE:
							// 预览图片 可自定长按保存路径
							//PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
							PictureSelector.create(BasePhotoGridActivity.this).externalPicturePreview(position, selectList,0);
							break;
						case PictureConfig.TYPE_VIDEO:
							// 预览视频
							PictureSelector.create(BasePhotoGridActivity.this).externalPictureVideo(media.getPath());
							break;
						case PictureConfig.TYPE_AUDIO:
							// 预览音频
							PictureSelector.create(BasePhotoGridActivity.this).externalPictureAudio(media.getPath());
							break;
					}
				} else {
					//点击了+👌进入相册选图
					PictureSelector.create(BasePhotoGridActivity.this)
							.openGallery(openGallery())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
							.isWeChatStyle(true)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
							.loadImageEngine(GlideEngine.createGlideEngine())
							.maxSelectNum(getMaxLimit())// 最大图片选择数量
							.minSelectNum(1)// 最小选择数量
							.imageSpanCount(3)// 每行显示个数
							.selectionMode(selectionMode())// 多选 or 单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
							.previewImage(previewImage())// 是否可预览图片
							.previewVideo(previewVideo())// 是否可预览视频
							.isCamera(showCamera())// 是否显示拍照按钮
							.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
							.enableCrop(false)// 是否裁剪
							.compress(true)// 是否压缩
							.hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
							.isGif(true)// 是否显示gif图片
							.freeStyleCropEnabled(true)// 裁剪框是否可拖拽
							.circleDimmedLayer(false)// 是否圆形裁剪
							.showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
							.showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
							.openClickSound(true)// 是否开启点击声音
							.selectionMedia(selectList)// 是否传入已选图片
							.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
				}
			}
		});

		clearCache();
	}

	public boolean previewImage(){
		return true;
	}

	public boolean previewVideo(){
		return true;
	}

	public boolean showCamera(){
		return true;
	}

	public int selectionMode(){
		return PictureConfig.MULTIPLE;
	}

	public int openGallery(){
		return PictureMimeType.ofAll();
	}

	public int getMaxLimit(){
		return MAX_DEFAULT_NUM;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PictureConfig.CHOOSE_REQUEST:
					// 图片选择结果回调
					selectList.clear();
					selectList.addAll(PictureSelector.obtainMultipleResult(data));
					// 例如 LocalMedia 里面返回三种path
					// 1.media.getPath(); 为原图path
					// 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
					// 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
					// 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
					adapter.notifyDataSetChanged();
					break;
			}
		}
	}

	// 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
	private void clearCache(){
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (EasyPermissions.hasPermissions(this, perms)) {
			// 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
			PictureFileUtils.deleteAllCacheDirFile(BasePhotoGridActivity.this);
		} else {
			EasyPermissions.requestPermissions(this, "清除缓存需要SD卡读写权限",REQUIRE_PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE, perms);
			return ;
		}
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		if(requestCode == REQUIRE_PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE){
			PictureFileUtils.deleteAllCacheDirFile(BasePhotoGridActivity.this);
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
		super.onDestroy();
	}

	//全部清空除了第一张之外的图片，目前只由子类调用该方法
	public void clearSelectPicturesWithoutFirst(){
		if (selectList.size() > 1) {
			for (int i = selectList.size() - 1 ; i > 0 ; i--) {
				selectList.remove(i);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	//子类也会调用这个方法
	public void resetAdapter(){
		adapter.notifyDataSetChanged();
	}

	public class PhotoReleaseGridAdapter extends BaseAdapter {

		public static final int TYPE_CAMERA = 1;
		public static final int TYPE_PICTURE = 0;

		private LayoutInflater mLayoutInflater;
		private List<LocalMedia> thumbPictures;

		public PhotoReleaseGridAdapter(List<LocalMedia> thumbPictures,Context mContext) {
			this.thumbPictures = thumbPictures;
			this.mLayoutInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			if (thumbPictures.size() < getMaxLimit()) {
				return thumbPictures.size() + 1;
			} else {
				return thumbPictures.size();
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (isShowAddItem(position)) {
				return TYPE_CAMERA;
			} else {
				return TYPE_PICTURE;
			}
		}

		private boolean isShowAddItem(int position) {
			int size = thumbPictures.size() == 0 ? 0 : thumbPictures.size();
			return position == size;
		}

		@Override
		public String getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.griditem_picture_delete,
						parent, false);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageview);
				viewHolder.delete_btn = convertView
						.findViewById(R.id.delete_btn);
				viewHolder.tv_duration = (TextView) convertView
						.findViewById(R.id.tv_duration);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyGridViewHolder) convertView.getTag();
			}

			if (getItemViewType(position) == TYPE_CAMERA) {
				viewHolder.imageView.setImageResource(BasePhotoGridActivity.PICTURE_UPDATE_ICON);
				viewHolder.delete_btn.setVisibility(View.GONE);
			} else {
				LocalMedia media = thumbPictures.get(position);
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
				// 图片
				if (media.isCompressed()) {
					Log.i("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
					Log.i("压缩地址::", media.getCompressPath());
				}

				Log.i("原图地址::", media.getPath());

				long duration = media.getDuration();
				viewHolder.tv_duration.setVisibility(PictureConfig.TYPE_VIDEO ==  PictureMimeType.getMimeType(media.getMimeType())
						? View.VISIBLE : View.GONE);
				viewHolder.tv_duration.setText(DateUtils.formatDurationTime(duration));
				RequestOptions options = new RequestOptions()
						.centerCrop()
						.placeholder(R.drawable.picture_placeholder)
						.diskCacheStrategy(DiskCacheStrategy.ALL);
				Glide.with(BasePhotoGridActivity.this)
						.load(path)
						.apply(options)
						.into(viewHolder.imageView);

				viewHolder.delete_btn.setVisibility(View.VISIBLE);

				viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						thumbPictures.remove(position);
						notifyDataSetChanged();
					}
				});
			}

			return convertView;
		}
	}

	private static class MyGridViewHolder {
		private ImageView imageView;
		private View delete_btn;
		private TextView tv_duration;
	}


}
