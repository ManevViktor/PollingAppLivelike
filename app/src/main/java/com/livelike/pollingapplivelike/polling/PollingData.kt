package com.livelike.pollingapplivelike.polling

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class PollingData<R>(val thread : CoroutineScope,
                     val flow :MutableStateFlow<R>,
                     val action : () -> R,
                     private val interval : Long =11000) {
    
    
    
    fun startPooling(){
        Timer().scheduleAtFixedRate(task, 0, interval)
    }
    
    
    val task = object : TimerTask() {
        override fun run() {
            thread.launch(Dispatchers.IO) {
                val result = action.invoke()
                flow.emit(result)
            }
        }
        
        override fun cancel() : Boolean {
            return super.cancel()
        }
        
        
    }
    
    fun stopPooling(){
        task.cancel()
    }
    
}