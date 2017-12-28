# cordova-plugin-imageCrop (只支持Android)
### 使用方法



### 使用时完成如下步骤
#### Step 1.  (添加uCrop库所需的配置)
插件添加完毕后 在路径\platforms\android\build.gradle里添加
```
maven { url "https://jitpack.io" }
```
#### Step 2.  (添加代码)
找到项目所属的MainActivity 此Activity是Cordova框架的入口
路径为 \项目包名\MainActivity
替换导包 :
```
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import org.apache.cordova.CordovaActivity;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

```
添加代码:

    /**
     * uCrop 开源库 剪裁界面 结果 回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //裁切成功
            if (requestCode == UCrop.REQUEST_CROP) {
                Uri croppedFileUri = UCrop.getOutput(data);
          //      Log.i("SQW", "cropURI:"+croppedFileUri);
            //    Log.i("SQW", "croppath:"+croppedFileUri.getPath());
                //获取系统默认的下载目录
                String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                // 将时间戳 与自定义名称 拼接 为 剪切后的图片文件名称
                String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());
                File saveFile = new File(downloadsDirectoryPath, filename);
                //保存下载的图片
                FileInputStream inStream = null;
                FileOutputStream outStream = null;
                FileChannel inChannel = null;
                FileChannel outChannel = null;
                try {
                    inStream = new FileInputStream(new File(croppedFileUri.getPath()));
                    outStream = new FileOutputStream(saveFile);
                    inChannel = inStream.getChannel();
                    outChannel = outStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    Toast.makeText(this, "裁切后的图片保存在：" + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    Log.i("SQW", "裁切后的图片保存在："+saveFile.getAbsolutePath());
                    EventBus.getDefault().post(new SendCropImagePathEvent(saveFile.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        outChannel.close();
                        outStream.close();
                        inChannel.close();
                        inStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //裁切失败
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "裁切图片失败", Toast.LENGTH_SHORT).show();
            Log.i("SQW", "裁切图片失败");
       
        }
    }
    public class  SendCropImagePathEvent{
        private String cropImagePath;

        public SendCropImagePathEvent(String cropImagePath) {
            this.cropImagePath = cropImagePath;
        }

        public String getCropImagePath() {
            return cropImagePath;
        }
    }
