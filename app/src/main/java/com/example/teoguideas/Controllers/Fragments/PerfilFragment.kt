package com.example.teoguideas.Controllers.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.teoguideas.Controllers.Activities.LoginActivity
import com.example.teoguideas.Controllers.Activities.SignInActivity

import com.example.teoguideas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_perfil.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PerfilFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {
    private var userId:String?=null

    private val SELECT_PHOTO_REQUEST_CODE=1
    var selectPhotoUri:Uri?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userId=arguments?.getString(PerfilFragment.USER_ID)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchUsuario()
        btn_select_photo.setOnClickListener{
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,SELECT_PHOTO_REQUEST_CODE)
        }

        btn_cerrar_sesion.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(context, SignInActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && data!=null){
            selectPhotoUri=data.data
            btn_select_photo.text=""
            val bitmap=MediaStore.Images.Media.getBitmap(context?.contentResolver,selectPhotoUri)
            val bitmapDrawable=BitmapDrawable(bitmap)
            btn_select_photo.setBackgroundDrawable(bitmapDrawable)

            //uploadImageToFirebaseStorage()
        }
    }
    private fun uploadImageToFirebaseStorage(){
        if(selectPhotoUri==null){return}
        val filename=UUID.randomUUID().toString()
        var ref=FirebaseStorage.getInstance().getReference("/images/users/$filename")
        ref.putFile(selectPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                //saveUserToDatabase()
            }
        }
    }
    override fun onResume() {
        super.onResume()
    }
    fun fetchUsuario(){
        var db= FirebaseFirestore.getInstance()
        val docRef = db.collection("usuarios").document(userId?:"xxxx")
        docRef.get()
            .addOnSuccessListener { result ->
                if(context!=null){
                    textView_nombre.text=result.get("nombre").toString()
                    textView_correo.text=result.get("correo").toString()
                }

            }
            .addOnFailureListener { exception ->

            }
    }
    companion object{
        private val USER_ID="USER_ID"
        fun newInstance(userId:String):PerfilFragment{
            val fragment=PerfilFragment()
            val args=Bundle()
            args.putString(USER_ID,userId)
            fragment.arguments=args
            return  fragment
        }
    }
}
