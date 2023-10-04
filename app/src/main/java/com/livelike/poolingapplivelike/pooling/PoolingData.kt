package com.livelike.poolingapplivelike.pooling

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class PoolingData<R>(val thread : CoroutineScope,
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