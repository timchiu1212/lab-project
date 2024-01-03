package com.example.test2;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MediaService extends Service {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private static final String CHANNEL_ID = "MyMusicPlayer";
    private MusicBinder mBinder;
    int requestCode = 1;
    int notificationId = 1;
    @SuppressLint("NotificationId0")
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // 创建通知
        Notification notification = createNotification();

        /**將歌曲載入到MediaPlayer中*/
        mediaPlayer = MediaPlayer.create(this,R.raw.onelastkiss);
        /**載入mediaPlayer給Binder音樂工具包*/
        mBinder = new MusicBinder(mediaPlayer);
        /**設置音樂播放屬性為Loop*/
        mediaPlayer.setLooping(true);
        /**將音樂歸0*/
        mediaPlayer.seekTo(0);
        /**有取用到本Service的話，則在通知欄顯示通知
         * notificationIntent的部分是使如果使用者點擊通知的話，便會跳回本APP中*/
        Intent notificationIntent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, MediaService.class);

        createNotificationChannel();




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(notificationId, notification);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("注意")
                .setContentText("音樂撥放中")
                .setSmallIcon(R.drawable.baseline_audiotrack_24)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat notificationManagerCompat
                = NotificationManagerCompat.from(MediaService.this);


            return notification;
    }



    @Nullable
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**此處為取得Notification的使用權限*/
        NotificationChannel notificationChannel = null;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, "My Service"
                    , NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            assert notificationManager != null;
            NotificationChannel channel = null;
        }

        return START_STICKY;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
}