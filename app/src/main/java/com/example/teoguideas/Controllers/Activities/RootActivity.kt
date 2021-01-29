package com.example.teoguideas.Controllers.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teoguideas.Adapter.AdapterInicioSearch
import com.example.teoguideas.Common.Common
import com.example.teoguideas.Controllers.Fragments.ExplorarFragment
import com.example.teoguideas.Controllers.Fragments.InicioFragment
import com.example.teoguideas.Controllers.Fragments.PerfilFragment
import com.example.teoguideas.Controllers.Fragments.PlanesFragment
import com.example.teoguideas.Model.InicioSearchResult
import com.example.teoguideas.R
import com.example.teoguideas.Retrofit.IComicAPI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_root.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class RootActivity : AppCompatActivity() {

    //variables Api
    internal var compositeDisposable = CompositeDisposable()
    internal lateinit var iComicAPI: IComicAPI
    internal var mBitmap: Bitmap? = null
    lateinit var datoencontrado:String

    internal lateinit var apiService: IComicAPI
    internal var picUri: Uri? = null
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()

    internal lateinit var fabCamera: Button
    internal lateinit var fabUpload: Button
    internal lateinit var textView: TextView

    //*
    lateinit var inicioSearchAdapter: AdapterInicioSearch
    var searchResultList:List<InicioSearchResult> = ArrayList()
    var userId:String?=null

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

            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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

    private val captureImageOutputUri: Uri?
        get() {
            var outputFileUri: Uri? = null
            println("URI :" + outputFileUri)

            //val getImage = getExternalFilesDir("")
            //val getImage = Environment.getExternalStorageDirectory().toString()
            val getImage = getExternalFilesDir("")
            println("getImage :" + getImage)
            if (getImage != null) {
                println("image :" + getImage)
                outputFileUri = Uri.fromFile(File(getImage.path,"profile.png"))
            }
            return outputFileUri
        }


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        navigateTo(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        askPermissions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        userId=intent.getStringExtra("url")
        Log.d("ID USER ROOT ACTIVITY", userId)
        setSupportActionBar(findViewById(R.id.topbar_main))



        val navView: BottomNavigationView = findViewById(R.id.navigationBar)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
       navigateTo(navView.menu.findItem(R.id.navigation_inicio))


        inicioSearchAdapter= AdapterInicioSearch(searchResultList)

        recyclerViewSearchResult.apply {
            adapter=inicioSearchAdapter
            adapter=inicioSearchAdapter
            layoutManager= LinearLayoutManager(this.context)
        }
        recyclerViewSearchResult.visibility=View.INVISIBLE

        searchTextMain.setOnEditorActionListener{v,actionId,event->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //performSearch();
                true;
            }
            false;
        }
        searchTextMain.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                        fetchSearchResults()
            }
        })
        searchTextMain.setOnFocusChangeListener(View.OnFocusChangeListener { view, b ->
                if (b) {
                    recyclerViewSearchResult.visibility=View.VISIBLE
                }

        })
        inicio_btn_search.setOnClickListener{

        }
        inicio_btn_cancel_search.setOnClickListener{
            clearSearchEnviroment()
        }

        floatingActionButton.setOnClickListener {

            iComicAPI = Common.api
            startActivityForResult(pickImageChooserIntent, IMAGE_RESULT);
        }


        btnBuscarFoto.setOnClickListener {
            if (mBitmap != null) {

                multipartImageUpload()
                txtHistoria.visibility = View.VISIBLE
                txtNombre.visibility = View.VISIBLE
                //if (datoencontrado != null) intent.putExtra("SD",datoencontrado)
                //startActivity(intent)
            }
            else {
                txtHistoria.visibility = View.GONE
                txtNombre.visibility = View.GONE
                Toast.makeText(applicationContext, "Bitmap is null. Try again", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Permisos Funciones
    private fun askPermissions() {
    permissions.add(Manifest.permission.CAMERA)
    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    permissionsToRequest = findUnAskedPermissions(permissions)


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


        if (permissionsToRequest!!.size > 0)
            requestPermissions(permissionsToRequest!!.toTypedArray<String>(),
                RootActivity.ALL_PERMISSIONS_RESULT
            )
    }
}

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {



            if (requestCode == RootActivity.IMAGE_RESULT) {


                val filePath = getImageFilePath(data)
                if (filePath != null) {
                    mBitmap = BitmapFactory.decodeFile(filePath)

                    //Mostrando Perfil del Recurso
                    recyclerViewSearchResult.visibility=View.VISIBLE
                    imageView3.visibility = View.VISIBLE
                    btnBuscarFoto.visibility=View.VISIBLE
                    imageView3.setImageBitmap(mBitmap)
                }
            }
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


    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {

            RootActivity.ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                DialogInterface.OnClickListener { dialog, which -> requestPermissions(permissionsRejected.toTypedArray<String>(),
                                    RootActivity.ALL_PERMISSIONS_RESULT
                                ) })
                            return
                        }
                    }

                }
            }
        }

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
                        //datoencontrado = response.body()!!.string()
                        val gar = response.body()?.charStream()
                        val varsd: JSONArray = JSONArray(response.body()?.string())
                        //MOSTRAR EN EL FRAGMENT
                        println("OKKKKKKKKKKKKKKKKKKKKKKKKKK")

                        fun String.toWords() = trim().splitToSequence(' ').filter { it.isNotEmpty() }.toList()


                        val verga = varsd.getJSONObject(0)

                        var assdf = response.body()?.string()

                        val gson = GsonBuilder().create()



                        txtHistoria.text = verga.get("dHistroria").toString()
                        txtNombre.text = verga.get("nNombre").toString()
                        var urlImage = verga.get("imgportada").toString()
                        Picasso.get().load(urlImage).into(imageView3)

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

    private fun navigateTo(item: MenuItem): Boolean {
        item.isChecked=true
        return supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, getFragmentFor(item))
            .commit() > 0
    }
    private fun getFragmentFor(item: MenuItem): Fragment {
        clearSearchEnviroment()
        if(item.itemId==R.id.navigation_inicio){
            MainSearchBar.visibility = View.VISIBLE
            floatingActionButton.show()
            toolBarTitle.text="Inicio"
            return InicioFragment()
        }else if(item.itemId==R.id.navigation_explorar){
            MainSearchBar.visibility = View.INVISIBLE
            floatingActionButton.hide()
            toolBarTitle.text="Explorar"
            return ExplorarFragment()
        }else if(item.itemId==R.id.navigation_planes){
            MainSearchBar.visibility = View.INVISIBLE
            floatingActionButton.hide()
            toolBarTitle.text="Planes"
            return PlanesFragment.newInstance(userId?:"XXXX")
        }else if(item.itemId==R.id.navigation_perfil){
            MainSearchBar.visibility = View.INVISIBLE
            floatingActionButton.hide()
            toolBarTitle.text="Perfil"
            return PerfilFragment.newInstance(userId?:"xxxx")
        }
        return InicioFragment()
    }
    fun fetchSearchResults(){
        Log.d("search text",searchTextMain.text.toString())
        var db= FirebaseFirestore.getInstance()
        //val docRef = db.collection("centrosHistoricos").whereGreaterThanOrEqualTo("nombre",searchTextMain.text.toString()).whereLessThanOrEqualTo("nombre",searchTextMain.text.toString())
        val docRef = db.collection("centrosHistoricos").whereArrayContains("hints", searchTextMain.text.toString())
        var tmpData: MutableList<InicioSearchResult> = mutableListOf<InicioSearchResult>()
        docRef.get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    if(result.documents.size>0){

                        for (document in result) {
                            tmpData.add(InicioSearchResult(document.id.toInt(),document.data?.get("nombre").toString(),document.data?.get("ubicacion").toString(),document.data?.get("url").toString()))
                            Log.d("Firebase",document.data?.get("nombre").toString())

                        }
                        inicioSearchAdapter.arrResults=tmpData
                        inicioSearchAdapter.notifyDataSetChanged()
                        //recomendadosAdapter.arrCentrosHistoricos=tmpData
                        //recomendadosAdapter.notifyDataSetChanged()
                    }else{
                        //Toast.makeText(context,"Sitio no encontrado", Toast.LENGTH_LONG).show()
                        inicioSearchAdapter.arrResults=tmpData
                        inicioSearchAdapter.notifyDataSetChanged()
                    }

                } else {
                   // Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "get failed with ", exception)
            }

    }
    fun clearSearchEnviroment(){
        recyclerViewSearchResult.visibility=View.GONE
        txtNombre.visibility=View.GONE
        txtHistoria.visibility=View.GONE
        imageView3.visibility=View.GONE
        btnBuscarFoto.visibility=View.GONE
        searchTextMain.text.clear()
        searchTextMain.clearFocus()
        searchTextMain.onEditorAction(EditorInfo.IME_ACTION_DONE)
        val inputManager:InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }

    companion object {
        private val ALL_PERMISSIONS_RESULT = 107
        private val IMAGE_RESULT = 200
    }
}
