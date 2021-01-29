package com.example.teoguideas.Controllers.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.teoguideas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.editText_contraseña
import kotlinx.android.synthetic.main.activity_login.editText_correo

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()//ocultar top bar
        setContentView(R.layout.activity_login)
        hideLoadingScreen()
        textView_segnin.setOnClickListener{
            finish()
        }
        btn_login.setOnClickListener{
            showLoadingScreen()
            val correo=editText_correo.text.toString()
            val contrasena=editText_contraseña.text.toString()
            if(correo.isEmpty() || contrasena.isEmpty()){
                Toast.makeText(this,"Ingrese todos los campos", Toast.LENGTH_SHORT).show()
                hideLoadingScreen()
                return@setOnClickListener

            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(correo,contrasena).addOnSuccessListener{
                    fetchUser(correo,contrasena)

            }.addOnFailureListener{
                hideLoadingScreen()
                Log.d("FIREBASE USER", "ENTROO AQUIII ETNONCES TAMOS JODIDOS")
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun fetchUser(correo:String,contrasena:String){
        var db= FirebaseFirestore.getInstance()
        val docRef = db.collection("usuarios").whereEqualTo("correo",correo)
        docRef.get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    if(result.documents.size.toString().toInt()!=0){
                        var id=result.documents.get(0).id
                        Log.d("FIREBASE USER", id)
                        val intent= Intent(this, RootActivity::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("url",id)
                        startActivity(intent)
                    }
                    else{
                        hideLoadingScreen()
                        Toast.makeText(this,"Usuario no registrado ",Toast.LENGTH_SHORT).show()
                    }


                } else {
                    progressBarContainerLogin.visibility= View.GONE
                    Log.d("FIREBASE USER", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                progressBarContainerLogin.visibility= View.GONE
                Log.d("FIREBASE USER", "get failed with ", exception)
            }
    }
    fun showLoadingScreen(){
        container.visibility=View.GONE
        progressBarContainerLogin.visibility=View.VISIBLE
    }
    fun hideLoadingScreen(){
        progressBarContainerLogin.visibility= View.GONE
        container.visibility=View.VISIBLE
    }
}
