package com.example.teoguideas.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.teoguideas.Controllers.Activities.FichaTecnicaActivity
import com.example.teoguideas.Model.InicioSearchResult
import com.example.teoguideas.Model.Plan
import com.example.teoguideas.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_plan.view.*
import kotlinx.android.synthetic.main.item_recomendacion.view.*
import kotlinx.android.synthetic.main.item_recomendacion.view.textView_descripcion
import kotlinx.android.synthetic.main.item_recomendacion.view.textView_nombre

class AdapterPlanes(var arrPlanes: List<Plan>,var userId:String):
    RecyclerView.Adapter<AdapterPlanes.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPlanes.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_plan, parent, false))
        }

        override fun getItemCount(): Int {
            return arrPlanes.size
        }
        override fun onBindViewHolder(holder: AdapterPlanes.ViewHolder, position: Int) {
            holder.bindTo(arrPlanes[position])
            holder.textViewOptions.setOnClickListener{
                Log.d("OPT MENUU", "si entordoifjsdf")
                var popup=PopupMenu(holder.itemView.context,holder.textViewOptions)
                popup.inflate(R.menu.option_menu)
                popup.setOnMenuItemClickListener {
                    var db= FirebaseFirestore.getInstance()
                    val docRef = db.collection("planes").document(arrPlanes[position].id?:"0")
                    docRef.delete()
                        .addOnSuccessListener { result ->
                            fetchPlanes()
                        }
                        .addOnFailureListener { exception ->

                        }
                    true
                }
                popup.show()
            }
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
                    this.arrPlanes=tmpData
                    this.notifyDataSetChanged()


                } else {
                    //Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "get failed with ", exception)
            }
    }
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nombreTextView = itemView.textView_nombre
            val fechaTextView=itemView.textView_fecha
            val descripcionTextView= itemView.textView_descripcion
            val textViewOptions=itemView.textViewOptions

            fun bindTo(itemPlan: Plan) {
                nombreTextView.text=itemPlan.nombre
                fechaTextView.text=itemPlan.fecha
                descripcionTextView.text=itemPlan.descripcion
                /*val url:String="http://granmuseo.calidda.com.pe${centroH.imgportada}"
                pictureImageView.apply {
                    setDefaultImageResId(R.mipmap.ic_launcher)
                    setErrorImageResId(R.mipmap.ic_launcher)
                    setImageUrl(url)
                }

                titleTextView.text = centroH.nNombre
                descTextView.text=centroH.dHistoria¨*/
            }

        }
}