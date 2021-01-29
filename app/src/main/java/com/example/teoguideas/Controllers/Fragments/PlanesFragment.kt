package com.example.teoguideas.Controllers.Fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teoguideas.Adapter.AdapterPlanes
import com.example.teoguideas.Controllers.Activities.NuevoPlanActivity
import com.example.teoguideas.Controllers.Activities.RootActivity
import com.example.teoguideas.Model.Plan

import com.example.teoguideas.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_planes.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlanesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlanesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlanesFragment : Fragment() {
    private var userId:String?=null
    lateinit var planesAdapter:AdapterPlanes
    var plansList:List<Plan> = ArrayList()
    private val REQUEST_CODE_NUEVOPLAN=2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userId=arguments?.getString(USER_ID)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_planes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        planesAdapter= AdapterPlanes(plansList,userId?:"")
        recyclerView_planes.apply{
            adapter=planesAdapter
            layoutManager= LinearLayoutManager(this.context)
        }

        floatingActionButton.setOnClickListener{
            var intent=Intent(activity,NuevoPlanActivity::class.java)
            intent.putExtra("userId",userId);
            startActivityForResult(intent,REQUEST_CODE_NUEVOPLAN)
        }
        fetchPlanes()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(context,"Registro Guardado Exitosamente", Toast.LENGTH_LONG).show()
                //var result=data?.getStringExtra("result") capturar datos devueltos
                //fetch planes por usuario actual
                fetchPlanes()
            }

    }
    override fun onResume() {
        super.onResume()
    }
    fun fetchPlanes(){
        var db= FirebaseFirestore.getInstance()
        val docRef = db.collection("planes").whereEqualTo("userId",userId)
        docRef.get()
            .addOnSuccessListener { result ->
                if (result != null) {

                    var tmpData: MutableList<Plan> = mutableListOf<Plan>()
                    for (document in result) {
                        tmpData.add(Plan(document.id.toString(),document.data?.get("nombre").toString(),document.data?.get("descripcion").toString(),document.data?.get("fecha").toString()))


                    }

                    planesAdapter.arrPlanes=tmpData
                    planesAdapter.notifyDataSetChanged()


                } else {
                    //Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "get failed with ", exception)
            }
    }
    companion object{
        private val USER_ID="USER_ID"
        fun newInstance(userId:String):PlanesFragment{
            val fragment=PlanesFragment()
            val args=Bundle()
            args.putString(USER_ID,userId)
            fragment.arguments=args
            return  fragment
        }
    }
}





























