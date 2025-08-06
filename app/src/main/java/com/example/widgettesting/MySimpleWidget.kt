package com.example.widgettesting

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.action.ActionParameters
import androidx.glance.unit.ColorProvider
import java.util.Calendar

class MySimpleWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                MySimpleWidgetContent()
            }
        }
    }

    @Composable
    private fun MySimpleWidgetContent() {
        val timeLeft = getTimeLeftInDay()
        val dayProgress = getDayProgress()
        val progressPercentage = (dayProgress * 100).toInt()

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(
                        ColorProvider(
                            Color(0xFF1E1E2F)
                        )
                    )
                    .cornerRadius(28.dp)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "🕒",
                            style = TextStyle(
                                fontSize = 20.sp
                            )
                        )

                        Spacer(modifier = GlanceModifier.width(8.dp))

                        Text(
                            text = formatTimeLeft(timeLeft),
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(Color.White)
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    Text(
                        text = "$progressPercentage% Complete • Day Progress",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(Color(0xFFBDBDBD))
                        )
                    )
                }
                Box(
                    modifier = GlanceModifier
                        .padding(start = 12.dp)
                        .background(ColorProvider(Color(0xFF3A3A50)))
                        .cornerRadius(10.dp)
                        .clickable(actionRunCallback<RefreshAction>())
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔄",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = ColorProvider(Color.White)
                        )
                    )
                }
            }
        }
    }


    private fun getTimeLeftInDay(): Long {
        val now = Calendar.getInstance()
        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return endOfDay.timeInMillis - now.timeInMillis
    }

    private fun getDayProgress(): Float {
        val now = Calendar.getInstance()
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val totalDayTime = endOfDay.timeInMillis - startOfDay.timeInMillis
        val elapsedTime = now.timeInMillis - startOfDay.timeInMillis

        return (elapsedTime.toFloat() / totalDayTime).coerceIn(0f, 1f)
    }

    private fun formatTimeLeft(millisLeft: Long): String {
        if (millisLeft <= 0) return "00:00:00"

        val hours = millisLeft / (1000 * 60 * 60)
        val minutes = (millisLeft % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (millisLeft % (1000 * 60)) / 1000

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        MySimpleWidget().update(context, glanceId)
    }
}