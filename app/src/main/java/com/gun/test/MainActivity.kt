package com.gun.test

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Locale


class MainActivity : AppCompatActivity() , TextToSpeech.OnInitListener{
    //-tts 엔진
    lateinit var textToSpeech: TextToSpeech
    // -카카오톡 읽어 오기
    private var dialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //블루투스
        val blue_button=findViewById<Button>(R.id.blue_button)

        blue_button.setOnClickListener {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            startActivity(intent)
        }



        //-tts 엔진(ON,OFF)
        val switch_tts_activ =findViewById<Switch>(R.id.switch_tts_activ)

        switch_tts_activ.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Log.d("my_tts","test start...0")
                val intent: Intent=Intent()
                intent.action=TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
                activityResult.launch(intent)


            }else{
                Log.d("my_tts","finish")
                textToSpeech.stop()
                textToSpeech.shutdown()

            }
        }


        // -카카오톡 읽어 오기(채팅 일어서 내용 레이아웃에 띄위기)

        //상대방 읽어오기
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                @SuppressLint("SetTextI18n")
                override fun onReceive(context: Context, intent: Intent) {
                    val time = intent.getStringExtra(MyNotificationService.EXTRA_TIME)
                    val name = intent.getStringExtra(MyNotificationService.EXTRA_NAME)
                    val text = intent.getStringExtra(MyNotificationService.EXTRA_TEXT)
                    var room = intent.getStringExtra(MyNotificationService.EXTRA_ROOM)
                    if (room == null) room = "개인 채팅"
                    Log.d("message_data",text.toString())


                    val data: String = text.toString()

                    var speechStatus: Int = 0

                    //안드로이드 버전에 따른 조건(롤리팝보다 같거나 높다면
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null)
                    }


                }
            }, IntentFilter(MyNotificationService.ACTION_NOTIFICATION_BROADCAST)
        )
        //나와의 채팅 읽어오기(테스트 전용 배포시 뺴기,MyAccessibilityService 클래스도 빼기)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                @SuppressLint("SetTextI18n")
                override fun onReceive(context: Context, intent: Intent) {
                    val text = intent.getStringExtra(MyAccessibilityService.EXTRA_TEXT)


                    //입력값 변수에 담기

                    val data: String = text.toString()

                    var speechStatus: Int = 0

                    //안드로이드 버전에 따른 조건(롤리팝보다 같거나 높다면
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, null)

                    } else {
                        textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null)
                    }
                    //배속
                    val seekbar=findViewById<SeekBar>(R.id.seekBar)
                    val textspeed=findViewById<TextView>(R.id.textspeed)

                    seekbar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                            if (progress==1){
                                textToSpeech.setSpeechRate(1.0f)
                                textspeed.text="1.0"
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, null)

                                } else {
                                    textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null)
                                }
                            }
                            if (progress==5){
                                textToSpeech.setSpeechRate(1.5f)
                                textspeed.text="1.5"
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, null)

                                } else {
                                    textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null)
                                }

                            }
                            if (progress==10){
                                textToSpeech.setSpeechRate(2.0f)
                                textspeed.text="2.0"
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, null)

                                } else {
                                    textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null)
                                }

                            }

                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {

                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {

                        }
                    })

                }
            }, IntentFilter(MyAccessibilityService.ACTION_NOTIFICATION_BROADCAST)
        )
    } //onCreate


    //-tts 엔진
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        Log.d("my_tts","ready...2")
        //보이스가 있다면
        if(it.resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
            //음성전환 준비
            textToSpeech = TextToSpeech(this, this)

        }else{ //없다면 다운로드
            //데이터 다운로드
            val installIntent: Intent = Intent()
            installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            startActivity(installIntent)
        }
    }



    // -카카오톡 읽어 오기(권한 설정)
    override fun onResume() {
        super.onResume()
        if (dialog != null) {
            dialog!!.dismiss()
        }
        if (!checkNotificationPermission()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("알림 접근 권한 필요")
            builder.setMessage("알림 전근 권한이 필요합니다.")
            builder.setPositiveButton(
                "설정"
            ) { dialog: DialogInterface?, which: Int ->
                startActivity(
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                )
            }
            dialog = builder.create()
            dialog!!.show()
            return
        }
        if (!isMyServiceRunning(MyNotificationService::class.java)) {
            val intent = Intent(applicationContext, MyNotificationService::class.java)
            startService(intent) // 서비스 시작
            Toast.makeText(this.applicationContext, "알림 읽기 서비스 - 시작됨", Toast.LENGTH_SHORT).show()
        }
        if (!checkAccessibilityPermission()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("접근성 권한 필요")
            builder.setMessage("접근성 권한이 필요합니다.\n\n설치된 서비스 -> 허용")
            builder.setPositiveButton(
                "설정"
            ) { dialog: DialogInterface?, which: Int ->
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                )
            }
            dialog = builder.create()
            dialog!!.show()
            return
        }
    }

    // -카카오톡 읽어오기(서비스가 실행 중인지 확인하는 함수)
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    // -카카오톡 읽어오기(알림 접근 권한이 있는지 확인하는 함수)
    fun checkNotificationPermission(): Boolean {
        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        return sets.contains(applicationContext.packageName)
    }

    // -카카오톡 읽어오기(접근성 권한이 있는지 확인하는 함수)
    fun checkAccessibilityPermission(): Boolean {
        val manager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val list =
            manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (i in list.indices) {
            val info = list[i]
            if (info.resolveInfo.serviceInfo.packageName == application.packageName) {
                return true
            }
        }
        return false
    }
    //-카카오톡 읽어오기
    companion object {
        private const val TAG = "MainActivity"
    }

    //-tts 엔진(TextToSpeech 엔진 초기화시 호출되는 함수)
    override fun onInit(status: Int) {
        Log.d("my_tts","oninit...1")
        if(status == TextToSpeech.SUCCESS){

            //언어값
            val languageStatus: Int = textToSpeech.setLanguage(Locale.KOREAN)

            //데이터 문제(데이터가 없거나 언어를 지원할 수 없다면)
            if(languageStatus == TextToSpeech.LANG_MISSING_DATA ||
                languageStatus == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "언어를 지원할 수 없습니다.",
                    Toast.LENGTH_SHORT).show()
            }
        }else{ //실패
            Toast.makeText(this, "음성전환 엔진 에러입니다.", Toast.LENGTH_SHORT).show()
        }
    }


}