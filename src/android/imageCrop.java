package com.rsc.imgcrop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.net.Uri;
import android.util.Log;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rsc.tradecenter.MainActivity;

/**
 * This class echoes a string called from JavaScript.
 */
public class imageCrop extends CordovaPlugin {
    CallbackContext callback ;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext ;
        if (action.equals("crop")) { //剪裁
            String source_path = args.getString(0);
			String source_type = args.getString(1);
            String crop_width = args.getString(2);
            String crop_high = args.getString(3);

            if (source_type.equals("camera")){

                String[] dataStr = source_path.split("/");
                String fileTruePath = "/sdcard";
                for (int i = 6; i < dataStr.length; i++) {
                    fileTruePath = fileTruePath + "/" + dataStr[i];
                }

                if (  checkImageSize(fileTruePath,crop_width,crop_high) ){  // 检查图片大小
                    // 开启剪裁页面
                    startCrop(fileTruePath,crop_width,crop_high);
                }else{
                    callback.success("图片太小请选择大图!");
                }
                
            }else{

                if (  checkImageSize(source_path,crop_width,crop_high) ){  // 检查图片大小
                    // 开启剪裁页面
                    startCrop(source_path,crop_width,crop_high);
                }else{
                    callback.success("图片太小请选择大图!");
                }
                
            }

            return true;
        }else if (action.equals("compress")){  // 压缩
            String source_path = args.getString(0);
            String compress_quality = args.getString(1);

            // 压缩后的图片路径
            String compress_imgpath =  compressImage(source_path,compress_quality);

            this.coolMethod(compress_imgpath, callbackContext);
            return true;
        }else if (action.equals("addCallBackListener")) {  // 回调监听
            PluginResult result = null;
            //  注册通知
            EventBus.getDefault().register((CordovaPlugin) this);// 注册

            result = new PluginResult(PluginResult.Status.OK, "注册监听成功");
            callbackContext.sendPluginResult(result);

            return true;
        }
        return false;
    }

    /**
     * 图片质量压缩
     * @param source_path  原图
     * @param compress_quality  压缩质量
     * @return 压缩后的图片路径
     */
    private String compressImage(String source_path, String compress_quality) {
        Bitmap imageBitmap = BitmapFactory.decodeFile(source_path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(compress_quality), baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

//        int options = Integer.parseInt(compress_quality);
//        while (baos.toByteArray().length / 1024 > 3*1024) {  //循环判断如果压缩后图片是否大于3m,大于继续压缩
//            baos.reset();//重置baos即清空baos
//            options -= 10;//每次都减少10
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            //	long length = baos.toByteArray().length;
//        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 时间戳
        Date date = new Date(System.currentTimeMillis()); // 日期
        String filename = format.format(date); // 图片名
        File file = new File(Environment.getExternalStorageDirectory(),filename+".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        recycleBitmap(imageBitmap);
        return file.getPath();
    }

    private void recycleBitmap(Bitmap... imageBitmap) {
        if (imageBitmap==null) {
            return;
        }
        for (Bitmap bm : imageBitmap) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }

    /**
     * 检查图片大小
     * @param source_path 原图路径
     * @param crop_width  剪裁宽
     * @param crop_high  剪裁高
     * @return  返回值 fasle :图片实际大小 < 要求尺寸   提示图片太小
     *                  true : 图片实际大小 > 要求尺寸
     */
    private boolean checkImageSize(String source_path,String crop_width, String crop_high) {
        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.inJustDecodeBounds = true;//这个参数设置为true才有效，
        Bitmap bmp = BitmapFactory.decodeFile(source_path, bitmap_options);//这里的bitmap是个空
//        if(bmp==null){
//            Log.i("SQW","test");
//        }
        int outHeight=bitmap_options.outHeight;
        int outWidth= bitmap_options.outWidth;
        Log.i("SQW","outHeight:"+outHeight);
        Log.i("SQW","outWidth:"+outWidth);
        if ( outWidth < Integer.parseInt(crop_width) || outHeight < Integer.parseInt(crop_high)){
            return false ;

        }
        return true ;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void startCrop(String source_path, String crop_width, String crop_high) {
      //  Uri sourceUri = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505734489041&di=5cadc1705fe212a8188486dfa1558264&imgtype=0&src=http%3A%2F%2Fimg1.kwcdn.kuwo.cn%2Fstar%2FKuwoPhotoArt%2F0%2F0%2F1392881265067_0.jpg");
        Uri sourceUri = Uri.fromFile(new File(source_path));
        //裁剪后保存到文件中
        Uri destinationUri = Uri.fromFile(new File(this.cordova.getActivity().getApplication().getCacheDir(), "CropImage.jpeg"));

        UCrop uCrop = UCrop.of(sourceUri, destinationUri);

        uCrop.withAspectRatio(Float.parseFloat(crop_width),Float.parseFloat(crop_high));
        uCrop.withMaxResultSize(Integer.parseInt(crop_width),Integer.parseInt(crop_high));

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setAllowedGestures(UCropActivity.SCALE,UCropActivity.NONE,UCropActivity.NONE);
        options.setCompressionQuality(100);  // 0-100
        // options.setOvalDimmedLayer(config.isOval);
        options.setCircleDimmedLayer(false);
        options.setShowCropGrid(true);
        options.setHideBottomControls(true);
        options.setShowCropFrame(true);
		// 标题栏 颜色值 
		options.setToolbarColor(Color.parseColor("#577EE5"));
        options.setStatusBarColor(Color.parseColor("#577EE5"));
        uCrop.withOptions(options);
        uCrop.start(this.cordova.getActivity());

    }

    /**
     * 接收通知 (剪切图片完成后 接收路径)
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public void onSendCropImagePathEvent(MainActivity.SendCropImagePathEvent event) {
        String path = event.getCropImagePath();
        //      Log.i("SQW", "path: "+path);
        callback.success("file://"+path);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
