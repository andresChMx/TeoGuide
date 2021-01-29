package com.example.teoguideas.Model

class CentroHistorico {

    var idCentroH:Int?=null
    var nNombre:String?=null
    var tUbicacion:String?=null
    var dHistoria:String?=null
    var NLat:Float?=null
    var NLong:Float?=null
    var imgportada:String?=null
    var url:String?=null
    
    constructor(){}


    constructor(
        idCentroH: Int?,
        nNombre: String?,
        tUbicacion: String?,
        dHistoria: String?,
        NLat: Float?,
        NLong: Float?,
        imgportada: String?,
        url:String?
    ) {
        this.idCentroH = idCentroH
        this.nNombre = nNombre
        this.tUbicacion = tUbicacion
        this.dHistoria = dHistoria
        this.NLat = NLat
        this.NLong = NLong
        this.imgportada = imgportada
        this.url=url
        
    }
}