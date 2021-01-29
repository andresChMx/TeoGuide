package com.example.teoguideas.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teoguideas.Common.Common
import com.example.teoguideas.Interface.IRecyclerOnClick
import com.example.teoguideas.Model.CentroHistorico
import com.example.teoguideas.R
import com.squareup.picasso.Picasso

class AdapterResultPerfil(internal var context: Context,
                          internal var centroList:List<CentroHistorico>):
    RecyclerView.Adapter<AdapterResultPerfil.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.activity_perfil_recurso,p0,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return centroList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        Picasso.get().load(centroList[p1].imgportada).into(p0.centro_image)
        p0.centro_name.text = centroList[p1].nNombre
        p0.setClick(object: IRecyclerOnClick {
            override fun onClick(view: View, position: Int) {
                Common.selected_centroH = centroList[position]
            }
        })
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        internal var centro_image: ImageView
        internal var centro_name: TextView

        lateinit var iRecyclerOnClick: IRecyclerOnClick

        fun setClick(iRecyclerOnClick: IRecyclerOnClick){
            this.iRecyclerOnClick = iRecyclerOnClick;
        }

        init {
            centro_image = itemView.findViewById(R.id.centro_image) as ImageView
            centro_name = itemView.findViewById(R.id.txtHistoria) as TextView
        }

        override fun onClick(p0: View?) {
            iRecyclerOnClick.onClick(p0!!,adapterPosition)
        }
    }
}

