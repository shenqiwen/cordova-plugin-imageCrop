# cordova-plugin-imageCrop
#### Step 1.  (���uCrop�����������)
��������Ϻ� ��·��\platforms\android\build.gradle����� 
maven { url "https://jitpack.io" }
#### Step 2.  (��Ӵ���)
�ҵ���Ŀ������MainActivity ��Activity��Cordova��ܵ����
·��Ϊ \��Ŀ����\MainActivity
�滻 :
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

��Ӵ���:

/**
     * uCrop ��Դ�� ���ý��� ��� �ص�
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //���гɹ�
            if (requestCode == UCrop.REQUEST_CROP) {
                Uri croppedFileUri = UCrop.getOutput(data);
          //      Log.i("SQW", "cropURI:"+croppedFileUri);
            //    Log.i("SQW", "croppath:"+croppedFileUri.getPath());
                //��ȡϵͳĬ�ϵ�����Ŀ¼
                String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                // ��ʱ��� ���Զ������� ƴ�� Ϊ ���к��ͼƬ�ļ�����
                String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());
                File saveFile = new File(downloadsDirectoryPath, filename);
                //�������ص�ͼƬ
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
                    Toast.makeText(this, "���к��ͼƬ�����ڣ�" + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    Log.i("SQW", "���к��ͼƬ�����ڣ�"+saveFile.getAbsolutePath());

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

        //����ʧ��
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "����ͼƬʧ��", Toast.LENGTH_SHORT).show();
            Log.i("SQW", "����ͼƬʧ��");
       
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
