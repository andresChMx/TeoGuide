package com.example.teoguideas.Model

class InicioSearchResult {
    var idCentro:Int?=null
    var nNombre:String?=null
    var ubicacion:String?=null
    var url:String?=null
    constructor(){}


    constructor(
        idCentro: Int?,
        nNombre: String?,
        ubicacion:String?,
        url:String?
    ) {
        this.idCentro= idCentro
        this.nNombre = nNombre
        this.ubicacion=ubicacion
        this.url=url
    }
}