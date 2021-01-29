package com.example.teoguideas.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.teoguideas.Model.CentroHistorico
import com.example.teoguideas.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomMarkerInfoWindowView: GoogleMap.InfoWindowAdapter {
    val markerItemView:View
    constructor(context:Context){
        markerItemView=LayoutInflater.from(context).inflate(R.layout.maker_info_window,null)
    }
    override fun getInfoWindow(marker: Marker?): View {
        var centroTmp:CentroHistorico=marker?.tag as CentroHistorico
        markerItemView.findViewById<TextView>(R.id.textView_nombre).text=centroTmp.nNombre
        markerItemView.findViewById<TextView>(R.id.textView_ubicacion).text=centroTmp.tUbicacion
        return markerItemView
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInfoContents(p0: Marker?): View? {
        return null
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}