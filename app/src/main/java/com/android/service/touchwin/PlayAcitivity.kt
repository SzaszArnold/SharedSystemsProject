package com.android.service.touchwin

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_play_acitivity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PlayAcitivity : AppCompatActivity() {
    private var checkedNumber=""
    private var number=0
    private var count = 0
    private var countSend=0
    private lateinit var mediaPlayer: MediaPlayer
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_acitivity)
        checkedNumber=intent.getStringExtra(extramessage)
        mediaPlayer=MediaPlayer.create(this,R.raw.sound)
        btn_Touch.setBackgroundColor(0)
        btn_StartGame.setOnClickListener {
            // egy kedvezőtlen esetben is 1 percig futó while ciklús, mely azt szolgálja, hogy egy időbe induljanak a telefonok.
            val finishInfinite= LocalDateTime.now().format(DateTimeFormatter.ofPattern("mm")).toInt()+1.toString().toInt()
            while(true){
                val currentDateTime= LocalDateTime.now()
                val time = currentDateTime.format(DateTimeFormatter.ofPattern("mm")).toString().toInt()
                Log.d("idoo","$time, infinite: $finishInfinite")
                if(time==finishInfinite){
                    play()
                    break}
            }
        }
        btn_getScore.setOnClickListener { getScore() }
    }
    //alaphelyzetbe álltija az érintő gombot
    private fun defaultButtonTouch() {
        btn_Touch.text = ""
        btn_Touch.setBackgroundColor(0)
        btn_Touch.isClickable = false
    }
    //a függvény lekéri az APItól a következő számot, ami alapján villog/sem
    private fun getNumber() {
        interfaceAPI.endpoints.getNumber(checkedNumber).enqueue(object : Callback<response> {
            override fun onResponse(call: Call<response>, response: Response<response>) {
                Log.d("szam", checkedNumber)
                if (response.isSuccessful) {
                    number= response.body()!!.number!!
                    Log.d("GetNumber","$number")

                } else {
                    Toast.makeText(this@PlayAcitivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<response>, t: Throwable) {
                Toast.makeText(this@PlayAcitivity, "Fail", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //a függvény felküldi az APInak a kijelölt számot, s mellé a lokálisan szémolt pontot, válaszként a fő készülék vissza kapja a végső pontot,
    // a többi eszköz 0át kap.
    private fun getScore() {
        interfaceAPI.endpoints.getScore(checkedNumber, countSend.toString()).enqueue(object : Callback<response> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<response>, response: Response<response>) {
                if (response.isSuccessful) {
                    if(response.body()!!.number!! !=0){
                        finalScore.text = "Final score: ${response.body()!!.number!!}"
                    }
                } else {
                    Toast.makeText(this@PlayAcitivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<response>, t: Throwable) {
                Toast.makeText(this@PlayAcitivity, "Fail", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // a játék maga, különböző tulajdonság nullázások. elindul egy időzitő visszaszámlálás, másodpercenkénti API kérés, a választ elosztja 3mal, ha ez a szám
    // megegyezik a kijelölt számmal, akkor kivilágásodik az érintő felület. az idő lejárta után lementődik az adat, illetve minden alap helyzetbe áll.
    private fun play(){
        scoreText.text = ""
        finalScore.text = ""
        countSend=0
        btn_StartGame.isClickable = false
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("ResourceAsColor", "SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val rest=number %3
                getNumber()
                timerdisplay.text = "Seconds remaining: " + millisUntilFinished / 1000
                if (rest == checkedNumber.toInt()) {
                    btn_Touch.setBackgroundColor(R.color.nyunyu)
                    btn_Touch.setOnClickListener {
                        count += 1
                        defaultButtonTouch()
                    }
                } else {
                    defaultButtonTouch()
                }
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                timerdisplay.text = "Done!"
                scoreText.text = "Score: $count"
                countSend+=count
                count = 0
                btn_StartGame.isClickable = true
                defaultButtonTouch()
                mediaPlayer.start()
            }
        }.start()

    }
}


