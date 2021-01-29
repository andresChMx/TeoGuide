package com.example.teoguideas.Controllers.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teoguideas.Adapter.AdapterInicioSearch
import com.example.teoguideas.Adapter.AdapterRecomendados
import com.example.teoguideas.Common.Common
import com.example.teoguideas.Controllers.Activities.FichaTecnicaActivity
import com.example.teoguideas.Model.CentroHistorico
import com.example.teoguideas.Model.InicioSearchResult

import com.example.teoguideas.R
import com.example.teoguideas.Retrofit.IComicAPI

import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.fragment_inicio.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [InicioFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [InicioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InicioFragment : Fragment() {

    lateinit var recomendadosAdapter:AdapterRecomendados
    var recomendadosList: List<CentroHistorico> = ArrayList()
    lateinit var inicioSearchAdapter:AdapterInicioSearch
    var searchResultList:List<InicioSearchResult> = ArrayList()
    private val TAG="FIREBASEEEE"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //val imageView = findViewById<ImageView>(R.layout.activity_root)
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    private fun getPathFromURI(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        //var sorting = ContactsContract.Contacts.DISPLAY_NAME + " DESC"
        var cursor = getActivity()?.getContentResolver()
            ?.query(contentUri!!, proj, null, null, null)
        //val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recomendadosAdapter= AdapterRecomendados(recomendadosList)
        recyclerView.apply{

            //iComicAPI = Common.api

            adapter=recomendadosAdapter
            layoutManager=LinearLayoutManager(this.context)
        }
        fetchRecomendados()
    /*
        floatingActionButton.setOnClickListener {
            startActivityForResult(pickImageChooserIntent, IMAGE_RESULT);
        }
*/
    }
    fun fetchRecomendados(){
        var db= FirebaseFirestore.getInstance()
        val docRef = db.collection("centrosHistoricos").whereGreaterThan("visitas",0)
        docRef.get()
            .addOnSuccessListener { result ->
                if (result != null) {

                        var tmpData: MutableList<CentroHistorico> = mutableListOf<CentroHistorico>()
                        for (document in result) {
                            tmpData.add(CentroHistorico(document.id.toInt(),document.data?.get("nombre").toString(),document.data?.get("ubicacion").toString(),document.data?.get("historia").toString(),document.data?.get("lat")?.toString()?.toFloat(),document.data?.get("long")?.toString()?.toFloat(),document.data?.get("imagen").toString(),document.data?.get("url").toString()))
                            Log.d(TAG,tmpData.size.toString())

                        }

                        recomendadosAdapter.arrCentrosHistoricos=tmpData
                        recomendadosAdapter.notifyDataSetChanged()


                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }




    override fun onResume() {
        super.onResume()
    }

}

