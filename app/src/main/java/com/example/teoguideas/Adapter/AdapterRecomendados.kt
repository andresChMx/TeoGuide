package com.example.teoguideas.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.teoguideas.Controllers.Activities.FichaTecnicaActivity
import com.example.teoguideas.Model.CentroHistorico
import com.example.teoguideas.R
import kotlinx.android.synthetic.main.item_recomendacion.view.*

class AdapterRecomendados(var arrCentrosHistoricos: List<CentroHistorico>) :
    RecyclerView.Adapter<AdapterRecomendados.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRecomendados.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recomendacion, parent, false))
    }

    override fun getItemCount(): Int {
        return arrCentrosHistoricos.size
    }

    override fun onBindViewHolder(holder: AdapterRecomendados.ViewHolder, position: Int) {
        holder.bindTo(arrCentrosHistoricos[position])
        holder.itemView.setOnClickListener{
            val intent= Intent(holder.itemView.context, FichaTecnicaActivity::class.java)
            intent.putExtra("url",arrCentrosHistoricos.get(position).url)
            startActivity(holder.itemView.context,intent,null)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pictureImageView = itemView.pictureImageView
        val titleTextView = itemView.textView_nombre
        val descTextView = itemView.textView_descripcion

        fun bindTo(centroH: CentroHistorico) {
            val url:String=centroH.imgportada?:""
            pictureImageView.apply {
                setDefaultImageResId(R.mipmap.ic_launcher)
                setErrorImageResId(R.mipmap.ic_launcher)
                setImageUrl(url)
            }
            titleTextView.text = centroH.nNombre
            descTextView.text=centroH.dHistoria
        }

    }
}