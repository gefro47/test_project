package com.example.calendar12


//import java.time.Month
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_calendar_activity.*
import java.text.SimpleDateFormat
import java.util.*


class Calendar_activity : AppCompatActivity() {
    var googleSingInClient : GoogleSignInClient? = null

    private var backPressedTime = 0L

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        }else {
            Toast.makeText(applicationContext, "Нажмите еще раз для выхода!", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_activity)

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSingInClient = GoogleSignIn.getClient(this, gso)






        val fbd = SimpleDateFormat("d")
        val FBD = fbd.format(Date())
        val fbm = SimpleDateFormat("M")
        val FBM = fbm.format(Date())
        val fby = SimpleDateFormat("yyyy")
        val FBY = fby.format(Date())
        //todaytext.text = "$FBD.$FBM.$FBY"
        val uid = FirebaseAuth.getInstance().uid
        //uidtext.text = uid
        textViewDate.text = "$FBD.$FBM.$FBY"

        var DayDP:String = "$FBD"
        var MonthDP:String = "$FBM"
        var YearDP:String = "$FBY"



//        FirebaseDatabase.getInstance().getReference("users/$uid/$YearDP/$MonthDP/Количество рабочих дней").addValueEventListener(object : ValueEventListener {

        fun read_data_from_DB() {
            var getdata_day = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var data_day = p0.child("Количество рабочих дней").getValue()
                    if (data_day == null) {
                        day_in_mounth.setText("0")
                        Toast.makeText(
                            applicationContext,
                            "Запишите количество\nрабочих дней!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        day_in_mounth.setText("$data_day")
                    }
                }

            }

            var getdata_hours = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var data_hours15 = p0.child("Часы С Коэф 1,5").getValue()
                    if (data_hours15 == null) {
                        pererab_hours15.setText("0.0")
                        Toast.makeText(
                            applicationContext,
                            "Часов переработки\n нету",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        pererab_hours15.setText("$data_hours15")
                    }
                }
            }

            FirebaseDatabase.getInstance().getReference("users/$uid/$YearDP/$MonthDP")
                .addValueEventListener(getdata_day)
            FirebaseDatabase.getInstance().getReference("users/$uid/$YearDP/$MonthDP")
                .addListenerForSingleValueEvent(getdata_day)

            FirebaseDatabase.getInstance().getReference("users/$uid/$YearDP/$MonthDP/$DayDP")
                .addValueEventListener(getdata_hours)
            FirebaseDatabase.getInstance().getReference("users/$uid/$YearDP/$MonthDP/$DayDP")
                .addListenerForSingleValueEvent(getdata_hours)
        }



        fun saveData(){
            if(day_in_mounth != null) {
                var Day_In_Mounth = day_in_mounth.text.toString()
                FirebaseDatabase.getInstance().getReference("users/$uid")
                .child("$YearDP").child("$MonthDP").child("Количество рабочих дней").setValue(
                        Day_In_Mounth
                    )
            }
            FirebaseDatabase.getInstance().getReference("users/$uid")
                .child("$YearDP").child("$MonthDP").child("$DayDP").child("Часы С Коэф 1,5").setValue(
                    pererab_hours15.text
                )
            FirebaseDatabase.getInstance().getReference("users/$uid")
                .child("$YearDP").child("$MonthDP").child("$DayDP").child("Часы С Коэф 2").setValue(
                    pererab_hours2.text
                )

        }

        fun deleteData(){

        }

        button.setOnClickListener {
            saveData()
        }
        button2.setOnClickListener {
            read_data_from_DB()
        }
        button3.setOnClickListener {
            deleteData()
        }


        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        button1.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDayOfMonth ->
                    textViewDate.text = "$mDayOfMonth.${mMonth + 1}.$mYear"
                    DayDP = "$mDayOfMonth"
                    MonthDP = "${mMonth + 1}"
                    YearDP = "$mYear"
                },
                year,
                month,
                day
            )

            dpd.show()}

        pererab_button.setOnClickListener {
            val intent = Intent(this@Calendar_activity, PererabActivity::class.java)
            startActivityForResult(intent, 1)
        }


        logout_button.setOnClickListener{
            logout()
        }
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()

        //Google Session out
        googleSingInClient?.signOut()

        //Facebook Session out
        //LoginManager.getInstance().logOut()

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, hours: Intent?) {
        super.onActivityResult(requestCode, resultCode, hours)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val Hours2 = hours!!.getStringExtra("Hours")

                //pererab_hours2.text = Hours2
                if (Hours2 != "") {
                    val Hours: Double? = Hours2.toDouble()
                    if (Hours != null) {
                        if (Hours <= 2.0){
                            pererab_hours15.text = Hours2
                        }
                    }
                }
                if (Hours2 != "") {
                    val Hours: Double? = Hours2.toDouble()
                    if (Hours != null) {
                        if(Hours > 2.0){
                            pererab_hours15.text = "2"
                            pererab_hours2.text = "${Hours - 2.0}"
                        }
                    }
                }else{
                    pererab_hours15.text = "0.0"
                    pererab_hours2.text = "0.0"
                }
                val Vihodnoy = hours.getBooleanExtra("Vihodnoy", false)
                pererab_vih.text = Vihodnoy.toString()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                pererab_hours15.text = "0.0"
                pererab_hours2.text = "0.0"
            }
        }
    }

}


//{
//    "rules": {
//    "users": {
//    "$user": {
//    ".read": "auth.uid === $user",
//    ".write": "auth.uid === $user"
//}
//}
//}
//}