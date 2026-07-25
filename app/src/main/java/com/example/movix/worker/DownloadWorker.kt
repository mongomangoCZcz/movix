package com.example.movix.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class DownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val client = OkHttpClient()

    override suspend fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()
        val fileName = inputData.getString("fileName") ?: "downloaded_file"
        val downloadPath = inputData.getString("path") ?: applicationContext.filesDir.absolutePath
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val notificationId = 1
        val notificationBuilder = NotificationCompat.Builder(applicationContext, "downloads")
            .setContentTitle("Stahování: $fileName")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(100, 0, false)

        setForeground(ForegroundInfo(notificationId, notificationBuilder.build()))

        return try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) return Result.failure()
            
            val body = response.body ?: return Result.failure()
            val contentLength = body.contentLength()
            val inputStream = body.byteStream()
            val file = File(downloadPath, fileName)
            val outputStream = FileOutputStream(file)
            
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            var totalBytesRead: Long = 0
            
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (contentLength > 0) {
                    val progress = (totalBytesRead * 100 / contentLength).toInt()
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(notificationId, notificationBuilder.build())
                }
            }
            
            outputStream.close()
            inputStream.close()
            
            notificationBuilder.setContentText("Stahování dokončeno")
                .setOngoing(false)
                .setProgress(0, 0, false)
            notificationManager.notify(notificationId, notificationBuilder.build())

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
