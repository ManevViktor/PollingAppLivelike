package com.livelike.pollingapplivelike.polling

import com.livelike.common.LiveLikeCallback
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.engagementsdk.fetchWidgetDetails
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Timer
import java.util.TimerTask


/* map requirement is WidgetID,WidgetKind */

class CustomWidgetPolling(
    private val widgetsMap : Map<String, String>,
    private val sdk : EngagementSDK,
    private val flow : MutableStateFlow<LiveLikeWidget?>,
    private val interval : Long = 11000
) {
    
    
    fun startPooling() {
        
        Timer().scheduleAtFixedRate(task, 0, interval)
    }
    
    
    val task = object : TimerTask() {
        override fun run() {
    
            println("")
            
            
            widgetsMap.forEach {
                sdk.fetchWidgetDetails(widgetId = it.key, widgetKind = it.value,
                    
                    liveLikeCallback = object : LiveLikeCallback<LiveLikeWidget> {
                        override fun invoke(result : LiveLikeWidget?, error : String?) {
                            
                            result?.let {
                                processResult(it)
                            }
                        }
                        
                    })
            }
            
            
        }
        
    }
    
    fun stopPooling() {
        task.cancel()
    }
    
    
    private fun processResult(result : LiveLikeWidget) {
        
            
            flow.tryEmit(result)
            println("Pulled ${result.kind}  ${result.id}")
        
        
        
    }
    
    
}