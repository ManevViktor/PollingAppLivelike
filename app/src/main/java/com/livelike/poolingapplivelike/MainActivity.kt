package com.livelike.poolingapplivelike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.livelike.common.AccessTokenDelegate
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.poolingapplivelike.pooling.CustomWidgetPolling
import com.livelike.poolingapplivelike.pooling.LiveWidgetPoolingData
import com.livelike.poolingapplivelike.util.LLCoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    
    lateinit var sdk : EngagementSDK
    val time = 11000L
    var liveWidgetFlow : MutableStateFlow<List<LiveLikeWidget>> = MutableStateFlow(listOf())
    
    
    var customWidgetFlow : MutableStateFlow<LiveLikeWidget?> = MutableStateFlow(null)
    
    val customWidgetMap = mapOf(
        "d24eadf1-7eb3-46a2-aeac-26291cef3b64" to "alert",
        "d15eaa68-f6de-4a4f-a349-efb288bec052" to "text-quiz"
    )
    
    lateinit var liveWidgetPoolingData : LiveWidgetPoolingData
    lateinit var customWidgetPoolingData : CustomWidgetPolling
    lateinit var session : LiveLikeContentSession
    
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main)
        
        initSDK()
        setupPooling()
    }
    
    
    
    private fun setupPooling() {
        liveWidgetPoolingData = LiveWidgetPoolingData(session =session, flow =  liveWidgetFlow, interval = time)
        customWidgetPoolingData = CustomWidgetPolling(sdk = sdk, widgetsMap = customWidgetMap, flow = customWidgetFlow, interval = time)
        
        
        
        lifecycleScope.launch {
    
            liveWidgetFlow.collectLatest { widgetData ->
                println("Live Widget Data Pool  ${widgetData.map { it.id }}")
            }
        }
        
        lifecycleScope.launch {
            customWidgetFlow.collectLatest {
                if (it != null) {
                    //println("custom widget pooled with id ${it.id} $it")
                }
            }
        }
        
        
        liveWidgetPoolingData.startPooling()
        customWidgetPoolingData.startPooling()
    }
    
    private fun initSDK() {
        
        sdk = EngagementSDK(
            LLCoreUtil.LIVELIKE_CLIENT_ID,
            this@MainActivity.applicationContext,
            accessTokenDelegate = object : AccessTokenDelegate {
                override fun getAccessToken() : String? {
                    return LLCoreUtil.getToken(this@MainActivity)
                }
                
                override fun storeAccessToken(accessToken : String?) {
                    LLCoreUtil.saveToken(this@MainActivity, accessToken)
                }
            })
        
        
        session = sdk.createContentSession(LLCoreUtil.PROGRAM_ID)
        
    }
    
    override fun onStop() {
        super.onStop()
        
        liveWidgetPoolingData.cancelPooling()
        customWidgetPoolingData.stopPooling()
    }
    

}