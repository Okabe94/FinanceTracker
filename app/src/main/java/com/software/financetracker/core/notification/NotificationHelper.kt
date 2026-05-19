package com.software.financetracker.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.software.financetracker.R

object NotificationHelper {
    private const val CHANNEL_ID = "budget_alerts"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alertas de presupuesto",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones sobre límites de categoría"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun notifyAt80Percent(context: Context, categoryId: Long, categoryName: String, spentPercent: Int) {
        post(
            context = context,
            notificationId = categoryId.toInt(),
            title = "Límite cercano: $categoryName",
            text = "Has gastado el $spentPercent% de tu límite mensual."
        )
    }

    fun notifyAt100Percent(context: Context, categoryId: Long, categoryName: String) {
        post(
            context = context,
            notificationId = categoryId.toInt() + 10_000,
            title = "Límite superado: $categoryName",
            text = "Has alcanzado o superado tu límite mensual."
        )
    }

    private fun post(context: Context, notificationId: Int, title: String, text: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                notificationId,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(notificationId, notification)
    }
}
