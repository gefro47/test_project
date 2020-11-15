package com.example.calendar12

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_pererab.*

class PererabActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pererab)

        var kek : Boolean = false

        saveAndReturn.setOnClickListener {
            val textIntent = Intent(this, Calendar_activity::class.java)
            textIntent.putExtra("Hours", hours.text.toString())
            textIntent.putExtra("Vihodnoy",kek)
            setResult(Activity.RESULT_OK, textIntent)

            finish()
        }

        switchVih.setOnCheckedChangeListener { _, onSwitch ->
            if(onSwitch){
                kek = true
                Toast.makeText(this, "+", Toast.LENGTH_SHORT).show()
            }
            else{
                kek = false
                Toast.makeText(this, "-", Toast.LENGTH_SHORT).show()
            }
        }

        casualBack.setOnClickListener {
            finish()
        }
    }


}