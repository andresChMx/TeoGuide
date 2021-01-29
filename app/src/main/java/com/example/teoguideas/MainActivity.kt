package com.example.teoguideas

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlincomicreader.Adapter.MyComicAdapter
import com.example.teoguideas.Common.Common
import com.example.teoguideas.Retrofit.IComicAPI
import com.example.teoguideas.Service.PicassoImageLoadingService
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ss.com.bannerslider.Slider
import java.io.*
import java.lang.StringBuilder


import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.concurrent.TimeUnit



class MainActivity : AppCompatActivity() {

    internal var compositeDisposable = CompositeDisposable()
    internal lateinit var iComicAPI: IComicAPI
    internal var mBitmap: Bitmap? = null
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissions = ArrayList<String>()
    private val permissionsRejected = ArrayList<String>()


    private fun getPathFromURI(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }
    val pickImageChooserIntent: Intent
        get() {

            val outputFileUri = captureImageOutputUri

            val allIntents = ArrayList<Intent>()
            val packageManager = packageManager

            val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            val listCam = packageManager.queryIntentActivities(captureIntent, 0)
            for (res in listCam) {
                val intent = Intent(captureIntent)
                intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                intent.setPackage(res.activityInfo.packageName)
                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
                }
                allIntents.add(intent)
            }

            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
            for (res in listGallery) {
                val intent = Intent(galleryIntent)
                intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                intent.setPackage(res.activityInfo.packageName)
                allIntents.add(intent)
            }

            var mainIntent = allIntents[allIntents.size - 1]
            for (intent in allIntents) {
                if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                    mainIntent = intent
                    break
                }
            }
            allIntents.remove(mainIntent)

            val chooserIntent = Intent.createChooser(mainIntent, "Select source")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())

            println(chooserIntent)
            return chooserIntent
        }

    private fun askPermissions() {
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsToRequest = findUnAskedPermissions(permissions)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest!!.size > 0)
                requestPermissions(permissionsToRequest!!.toTypedArray<String>(), ALL_PERMISSIONS_RESULT)
        }
    }
    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }
    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }
    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }
    private val captureImageOutputUri: Uri?
        get() {
            var outputFileUri: Uri? = null
            println("URI :" + outputFileUri)

            //val getImage = getExternalFilesDir("")
            //val getImage = Environment.getExternalStorageDirectory().toString()
            val getImage = getApplicationContext().getFilesDir().getPath()
            println("getImage :" + getImage)
            if (getImage != null) {
                println("image :" + getImage)
                outputFileUri = Uri.fromFile(File(getImage))
            }
            return outputFileUri
        }

    private fun getImageFromFilePath(data: Intent?): String? {
        val isCamera = data == null || data.data == null

        return if (isCamera)
            captureImageOutputUri!!.path
        else {
            println(data)
            getPathFromURI(data!!.data)
        }

    }

    fun getImageFilePath(data: Intent?): String? {
        return getImageFromFilePath(data)
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {

            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                DialogInterface.OnClickListener { dialog, which -> requestPermissions(permissionsRejected.toTypedArray<String>(), ALL_PERMISSIONS_RESULT) })
                            return
                        }
                    }

                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askPermissions()
        //Init API
        iComicAPI = Common.api


        Slider.init(PicassoImageLoadingService(this))

        recycler_comic.setHasFixedSize(true)
        recycler_comic.layoutManager = GridLayoutManager(this,2)

        swipe_refresh.setColorSchemeResources(R.color.colorPrimary,android.R.color.holo_orange_dark,android.R.color.background_dark)
        swipe_refresh.setOnRefreshListener {
            if (Common.isConnectedToInternet(baseContext)){

                fetchComic()
            }
            else{
                Toast.makeText(baseContext,"Please check u connection",Toast.LENGTH_SHORT).show()

            }
        }
        swipe_refresh.post(Runnable {
            if (Common.isConnectedToInternet(baseContext)){

                fetchComic()
            }
            else{
                Toast.makeText(baseContext,"Please check u connection",Toast.LENGTH_SHORT).show()

            }
        })

        btn_camara.setOnClickListener(View.OnClickListener {
            val intent:Intent = Intent(this,CamaraActivity::class.java)
            startActivity(intent)
        })

        /*
        btnProbando.setOnClickListener(View.OnClickListener {
            val intent:Intent = Intent(this,perfilRecursoActivity::class.java)
            startActivity(intent)
        })
        */

        btnProbando.setOnClickListener {
            println("Funcionando")
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }

            //val chooserIntent:Intent = Intent.createChooser(intent,"Ingrese la imagen")


            //startActivityForResult(Intent.createChooser(intent, "Ingrese la Imagen"), 2)
            startActivityForResult(pickImageChooserIntent, 200);
            println("Funcionando22")


            //val filePath = getImageFilePath(intent)
            //if (filePath != null) {
            //    println("Entro")
            //    mBitmap = BitmapFactory.decodeFile(filePath)

            //}
        }

        btnSubir.setOnClickListener {
            if (mBitmap != null)
                println("ENTRE GA")
                multipartImageUpload()
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


                println(data)
                val filePath = getImageFilePath(data)
                if (filePath != null) {
                    mBitmap = BitmapFactory.decodeFile(filePath)

                }


    }

    private fun multipartImageUpload() {
        try {
            val filesDir = applicationContext.filesDir
            val file = File(filesDir, "image" +".png")


            val bos = ByteArrayOutputStream()
            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()


            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()


            val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
            val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
            val name = RequestBody.create(MediaType.parse("text/plain"), "upload")

            //val req = apiService.postImage(body, name)
            val req = iComicAPI.postImage(body, name)
            req.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                    if (response.code() == 200) {
                        //textView.text = "Uploaded Successfully!"
                        //textView.setTextColor(Color.BLUE)
                        println("OKKKKKKKKKKKKKKKKKKKKKKKKKK")
                    }
                    Toast.makeText(applicationContext, response.code().toString() + " ", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //textView.text = "Uploaded Failed!"
                    //textView.setTextColor(Color.RED)
                    println("MALLLLLLLLLLLL")
                    Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                    t.printStackTrace()
                }
            })


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }



    private fun fetchComic() {
        val dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please wait...")
            .build()
        if (!swipe_refresh.isRefreshing)
            dialog.show()
        compositeDisposable.add(iComicAPI.CHistoList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({comicList ->
                txt_comic.text = StringBuilder("Populares ")
                    .append(comicList.size)
                    .append("")
                recycler_comic.adapter = MyComicAdapter(baseContext,comicList)
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

    companion object {
        private val ALL_PERMISSIONS_RESULT = 107
        private val IMAGE_RESULT = 200
    }
}
