package com.example.teoguideas.Controllers.Activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.example.teoguideas.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_nuevo_plan.*
import java.text.DateFormat
import java.util.*




class NuevoPlanActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
var userId:String=""
    var anio:Int=0
    var mes:Int=0
    var dia:Int=0
    var hora:Int=0
    var minuto:Int=0

    var diaFinal:Int=0
    var mesFinal:Int=0
    var anioFinal:Int=0
    var horaFinal:Int=0
    var minutoFinal:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_plan)
        setTitle("Nuevo Plan")
        var intent=intent
        userId=intent.getStringExtra("userId")


        btn_select_date.setOnClickListener{
            var calendar= Calendar.getInstance()

            anio=calendar.get(Calendar.YEAR)
            mes=calendar.get(Calendar.MONTH)
            dia=calendar.get(Calendar.DAY_OF_MONTH)

            var datePicker=DatePickerDialog(this,this,anio,mes,dia)
            datePicker.show()
        }

        btnCancelar.setOnClickListener{
            var resultIntent= Intent()

            //resultIntent.putExtra("result","algun resultado") no hay nada que retonar
            setResult(Activity.RESULT_CANCELED,resultIntent)
            finish()
        }
        btnGuardar.setOnClickListener{
            var nombre=editText_nombre.text.toString()
            var descripcion=editText_descripcion.text.toString()
            var fecha=diaFinal.toString() + "/" + mesFinal+ "/" + anioFinal + "   " + String.format(
                "%02d:%02d %s", horaFinal, minutoFinal,
                if (horaFinal < 12) "am" else "pm"
            )
            insertNewPlan(userId,nombre,descripcion,fecha)//tambien retornamos al PlanesFragment



        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        anioFinal=year
        mesFinal=month+1
        diaFinal=dayOfMonth

        var calendar=Calendar.getInstance()
        hora=calendar.get(Calendar.HOUR_OF_DAY)
        minuto=calendar.get(Calendar.MINUTE)

        var timePickerDialog=TimePickerDialog(this,this,hora,minuto,true)
        timePickerDialog.show()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        horaFinal=hourOfDay
        minutoFinal=minute

        btn_select_date.text=diaFinal.toString() + "/" + mesFinal+ "/" + anioFinal + "   " + String.format(
            "%02d:%02d %s", horaFinal, minutoFinal,
            if (horaFinal < 12) "am" else "pm"
        )
    }
    fun insertNewPlan(userId:String,nombre:String,descripcion:String,fecha:String){
        val data = hashMapOf(
            "userId" to userId,
            "nombre" to nombre,
            "descripcion" to descripcion,
            "fecha" to fecha
        )
        var db= FirebaseFirestore.getInstance()
        val docRef = db.collection("planes").add(data)
        docRef
            .addOnSuccessListener {documentReference ->
                Log.d("INSERTED USER", "DocumentSnapshot written with ID: ${documentReference.id}")
                var resultIntent= Intent()
                resultIntent.putExtra("result","algun resultado")
                setResult(Activity.RESULT_OK,resultIntent)
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"Hubo un problema al guardar el registro.Intentelo nuevamente", Toast.LENGTH_SHORT).show()
            }
    }
}
