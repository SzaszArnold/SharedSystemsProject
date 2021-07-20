package com.android.service.touchwin

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import android.annotation.SuppressLint as SuppressLint1

const val extramessage=""
class MainActivity : AppCompatActivity() {
    private lateinit var phoneNumber: RadioGroup
    private var checkedNumber: String = ""
    @android.annotation.SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        phoneNumber = findViewById(R.id.phoneNumber)

        phoneNumber.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radioButton: RadioButton = this.findViewById(checkedId)
                checkedNumber = radioButton.text.toString().toInt().toString()
            })
        buttonStart.setOnClickListener {
            getNumber()
        }
    }
    // itt valósul meg a készülék regisztrálása az APInál, felkell küldeni a kiválasztott számot, megvárja a választ, ha jött válasz, akkor tovább lép
    // intentel a PlayActivitybe
    private fun getNumber() {
        interfaceAPI.endpoints.getNumber(checkedNumber).enqueue(object : Callback<response> {
            override fun onResponse(call: Call<response>, response: Response<response>) {
                Log.d("szam", checkedNumber)
                if (response.isSuccessful) {
                    val intent = Intent(this@MainActivity, PlayAcitivity::class.java).apply {
                        putExtra(extramessage, checkedNumber)
                    }
                    startActivity(intent)

                } else {
                    Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<response>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Fail", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

