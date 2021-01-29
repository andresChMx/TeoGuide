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
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()//ocultar top bar
        hideLoadingScreen()

        btn_signin.setOnClickListener{
            //showLoadingScreen()
            val nombreUsuario=editText_nombre.text.toString()
            val email=editText_correo.text.toString()
            val contrasena=editText_contraseña.text.toString()

            if(email.isEmpty() || contrasena.isEmpty() || nombreUsuario.isEmpty() ){
                hideLoadingScreen()
                Toast.makeText(this,"Ingrese todos los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            checkExistingUser(nombreUsuario,email,contrasena)

        }
        textView_login.setOnClickListener{
            val intent= Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }
    }
    fun checkExistingUser(nombreUsuraio:String,correo:String,contrasena:String){
        var db= FirebaseFirestore.getInstance()
        val docRef = db.collection("usuarios").whereEqualTo("correo",correo)
        docRef.get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    if(result.documents.size.toString().toInt()!=0){
                        hideLoadingScreen()
                        Toast.makeText(this,"El correo ingresado ya esta en uso",Toast.LENGTH_SHORT).show()
                        false
                    }
                    else{
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo,contrasena)
                            .addOnSuccessListener{
                                insertNewUser(nombreUsuraio,correo,contrasena)
                            }
                            .addOnFailureListener{
                                hideLoadingScreen()
                                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                            }
                    }


                } else {
                    Log.d("FIREBASE USER", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FIREBASE USER", "get failed with ", exception)
            }
    }
    fun insertNewUser(nombre:String,correo:String,contrasena:String){
        val data = hashMapOf(
            "nombre" to nombre,
            "correo" to correo,
            "contrasena" to contrasena
        )
        var db= FirebaseFirestore.getInstance()
        val docRef = db.collection("usuarios").add(data)
        docRef
            .addOnSuccessListener {documentReference ->
                Log.d("INSERTED USER", "DocumentSnapshot written with ID: ${documentReference.id}")
                val intent= Intent(this, RootActivity::class.java)
                intent.putExtra("url",documentReference.id)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                //intent.putExtra("url","http://granmuseo.calidda.com.pe/" + arrCentrosHistoricos.get(position).url)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                hideLoadingScreen()
                Log.d("INSERTED USER", "ERROR INSERTANDO AL USUARIO A LA DB")
            }
    }
    fun showLoadingScreen(){
        container.visibility=View.GONE
        progressBarSignIn.visibility=View.VISIBLE
    }
    fun hideLoadingScreen(){
        progressBarSignIn.visibility= View.GONE
        container.visibility=View.VISIBLE
    }
}
