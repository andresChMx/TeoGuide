package com.example.teoguideas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.teoguideas.Common.Common
import com.example.teoguideas.Retrofit.IComicAPI
import com.example.teoguideas.Service.PicassoImageLoadingService
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_perfil_recurso.*
import ss.com.bannerslider.Slider
import java.lang.StringBuilder

class perfilRecursoActivity : AppCompatActivity() {

    internal var compositeDisposable = CompositeDisposable()
    internal lateinit var iComicAPI: IComicAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_recurso)

        //Init API
        iComicAPI = Common.api

        Slider.init(PicassoImageLoadingService(this))

        swipe_refresh.setColorSchemeResources(R.color.colorPrimary,android.R.color.holo_orange_dark,android.R.color.background_dark)
        swipe_refresh.setOnRefreshListener {
            if (Common.isConnectedToInternet(baseContext)){
                fetchComic()
            }
            else{
                Toast.makeText(baseContext,"Please check u connection", Toast.LENGTH_SHORT).show()

            }
        }
        swipe_refresh.post(Runnable {
            if (Common.isConnectedToInternet(baseContext)){
                fetchComic()
            }
            else{
                Toast.makeText(baseContext,"Please check u connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchComic() {
        val dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Cargando...")
            .build()
        if (!swipe_refresh.isRefreshing)
            dialog.show()
        compositeDisposable.add(iComicAPI.SubirImagen
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({comicList ->
                txtHistoria.text = StringBuilder()
                    .append(comicList.size)
                    .append("")

                Picasso.get().load(comicList[0].imgportada).into(imageView)
                txtHistoria.text = comicList[0].nNombre
                println(comicList[0].dHistoria)
                txtHistoria.text = comicList[0].tUbicacion
                //recycler_comic.adapter = MyComicAdapter(baseContext,comicList)

                if (!swipe_refresh.isRefreshing)
                    dialog.dismiss()
                swipe_refresh.isRefreshing = false
            },
                {thr ->
                    Toast.makeText(baseContext,"No se cargaron los datos",Toast.LENGTH_SHORT).show()
                    if (!swipe_refresh.isRefreshing)
                        dialog.dismiss()
                    swipe_refresh.isRefreshing = false
                }))
    }
}
