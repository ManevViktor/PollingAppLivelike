package com.livelike.pollingapplivelike.polling

import com.livelike.common.LiveLikeCallback
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.engagementsdk.WidgetStatus
import com.livelike.engagementsdk.WidgetsRequestOrdering
import com.livelike.engagementsdk.WidgetsRequestParameters
import com.livelike.engagementsdk.chat.data.remote.LiveLikePagination
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask

class LiveWidgetPollingData(
    val session : LiveLikeContentSession,
    val flow : MutableStateFlow<List<LiveLikeWidget>>,
    private val interval : Long = 11000
) {
    
    
    private var currentTime : Long = 0
    private val utcDateFormat = "yyyy-mm-dd'T'HH:mm:ss.SSS'Z'";
    private val utcFormat = SimpleDateFormat(utcDateFormat, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
    
    fun startPooling() {
        
        
        val date = utcFormat.format(Date())
        currentTime = utcFormat.parse(date)!!.time
        
        Timer().scheduleAtFixedRate(task, 0, interval)
    }
    
    
    val task = object : TimerTask() {
        override fun run() {
            
           
            session.getWidgets(
                LiveLikePagination.FIRST,
                WidgetsRequestParameters(
                    widgetStatus = WidgetStatus.PUBLISHED,
                    ordering = WidgetsRequestOrdering.RECENT
                ),
                
                liveLikeCallback = object : LiveLikeCallback<List<LiveLikeWidget>> {
                    override fun invoke(result : List<LiveLikeWidget>?, error : String?) {
                        
                        if (result != null) {
                            processResult(result)
                        }
                    }
                    
                })
            
            
        }
        
    }
    
    
    private fun processResult(result : List<LiveLikeWidget>) {
        val liveWidgetsList = result.filter { timePredicate(it.publishedAt) }
        
        println("Pulled live widgets ${liveWidgetsList.size}")
        flow.tryEmit(liveWidgetsList)
        
    }
    
    
    private fun timePredicate(timeUTC : String?) : Boolean {
        
        return try {
            (utcFormat.parse(timeUTC)?.time ?: 0) > (currentTime)
        } catch (ex : Exception) {
            false
        }
        
    }
    
    fun cancelPooling(){
        task.cancel()
    }
    
    
}