package com.example.bookhub.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.example.bookhub.R
import com.example.bookhub.database.BookDatabase
import com.example.bookhub.database.BookEntity
import com.example.bookhub.databinding.ActivityDescriptionBinding
import com.example.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDescriptionBinding

    lateinit var tvBookName: TextView
    lateinit var tvBookAuthor: TextView
    lateinit var tvBookPrice: TextView
    lateinit var tvBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var tvBookDesc: TextView
    lateinit var btnAddFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressBarLayout: RelativeLayout
    lateinit var toolbar: Toolbar

    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tvBookName = binding.tvBookName
        tvBookAuthor = binding.tvBookAuthor
        tvBookPrice = binding.tvBookPrice
        tvBookRating = binding.tvBookRating
        imgBookImage = binding.imgBookImage
        tvBookDesc = binding.tvBookDesc
        btnAddFav = binding.btnFav

        progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        progressBarLayout = binding.progressLayout
        progressBarLayout.visibility = View.VISIBLE

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null){
                bookId = intent.getStringExtra("book_id")
        } else {
                finish()
                Toast.makeText(this,"Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
            }

        if (bookId == "100"){
                finish()
                Toast.makeText(this,"Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
            }



        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                    it
                    val success = it.getBoolean("success")
                    try {
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressBarLayout.visibility = View.GONE

                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image")).into(imgBookImage)
                            tvBookName.text = bookJsonObject.getString("name")
                            tvBookAuthor.text = bookJsonObject.getString("author")
                            tvBookPrice.text = bookJsonObject.getString("price")
                            tvBookRating.text = bookJsonObject.getString("rating")
                            tvBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                tvBookName.text.toString(),
                                tvBookAuthor.text.toString(),
                                tvBookPrice.text.toString(),
                                tvBookRating.text.toString(),
                                tvBookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()

                            if (isFav){
                                btnAddFav.text = "Remove From Favourites"
                                val favColor = ContextCompat.getColor(applicationContext, R.color.colorFavourite)
                                btnAddFav.setBackgroundColor(favColor)
                            } else{
                                btnAddFav.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(applicationContext, com.google.android.material.R.color.design_default_color_primary)
                                btnAddFav.setBackgroundColor(noFavColor)
                            }

                            btnAddFav.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity,1).execute().get()){

                                    val async = DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()

                                    if (result){
                                        Toast.makeText(this, "book added to favourites", Toast.LENGTH_SHORT).show()

                                        btnAddFav.text = "Remove From Favourites"
                                        val favColor = ContextCompat.getColor(applicationContext, R.color.colorFavourite)
                                        btnAddFav.setBackgroundColor(favColor)
                                    }else{
                                        Toast.makeText(this, "Some error occurred ", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val async = DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()

                                    if (result){
                                        Toast.makeText(this, "book removed from favourites", Toast.LENGTH_SHORT).show()

                                        btnAddFav.text = "Add To Favourites"
                                        val noFavColor = ContextCompat.getColor(applicationContext, com.google.android.material.R.color.design_default_color_primary)
                                        btnAddFav.setBackgroundColor(noFavColor)
                                    }else{
                                        Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }


                        } else {
                            Toast.makeText(this, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "Some unexpected error occurred!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this, "Volley", Toast.LENGTH_SHORT).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "c0f41882c3b723"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){ text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){
                    text, listener->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int): AsyncTask<Void, Void,Boolean>(){


        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1 ->{
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_Id.toString())
                    db.close()
                    return book != null
                }

                2 ->{
                    db.bookDao().insert(bookEntity)
                    db.close()
                    return true
                }

                3 ->{
                    db.bookDao().delete(bookEntity)
                    db.close()
                    return true
                }

            }

            return false
        }

    }

}