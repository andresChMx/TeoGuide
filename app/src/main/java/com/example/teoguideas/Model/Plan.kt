package com.example.teoguideas.Model

class Plan {
    var id:String?=null
    var nombre:String?=null
    var descripcion:String?=null
    var fecha:String?=null
    constructor(){}


    constructor(
        id:String,
        nombre:String,
        descripcion:String,
        fecha:String
    ) {
        this.id=id
        this.nombre=nombre
        this.descripcion=descripcion
        this.fecha=fecha

    }
}