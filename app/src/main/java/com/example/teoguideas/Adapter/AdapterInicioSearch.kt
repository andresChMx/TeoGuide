package com.example.teoguideas.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.teoguideas.Controllers.Activities.FichaTecnicaActivity
import com.example.teoguideas.Model.InicioSearchResult
import com.example.teoguideas.R
import kotlinx.android.synthetic.main.item_inicio_search.view.*
import kotlinx.android.synthetic.main.item_recomendacion.view.*
import kotlinx.android.synthetic.main.item_recomendacion.view.textView_nombre

class AdapterInicioSearch(var arrResults: List<InicioSearchResult>) :
    RecyclerView.Adapter<AdapterInicioSearch.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterInicioSearch.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_inicio_search, parent, false))
    }

    override fun getItemCount(): Int {
        return arrResults.size
    }

    override fun onBindViewHolder(holder: AdapterInicioSearch.ViewHolder, position: Int) {
        holder.bindTo(arrResults[position])
        holder.itemView.setOnClickListener{
            val intent= Intent(holder.itemView.context, FichaTecnicaActivity::class.java)
            intent.putExtra("url",arrResults.get(position).url)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView = itemView.textView_nombre
        val ubicacionTextView = itemView.textView_ubicacion

        fun bindTo(itemSearchResult: InicioSearchResult) {
            nombreTextView.text=itemSearchResult.nNombre
            ubicacionTextView.text=itemSearchResult.ubicacion
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