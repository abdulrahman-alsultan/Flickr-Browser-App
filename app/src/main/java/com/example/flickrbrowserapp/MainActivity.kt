package com.example.flickrbrowserapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custome.*
import kotlinx.android.synthetic.main.custome.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var grid: GridView
    private lateinit var input: EditText
    private lateinit var searchBtn: Button
    private lateinit var imagesList: MutableList<ImageData>
    private lateinit var adapter: SearchGridView
    private var imagePerPage = 10
    private lateinit var bigImage: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        input = findViewById(R.id.main_et_search)
        searchBtn = findViewById(R.id.main_btn_search)
        imagesList = mutableListOf()
        grid = findViewById(R.id.main_grid_view)
        bigImage = findViewById(R.id.big_image)

        adapter = SearchGridView(imagesList, this)
        grid.adapter = adapter

        bigImage.setOnClickListener {
            closeImage()
        }

        searchBtn.setOnClickListener {
            if (input.text.isNotEmpty()) {
                imagesList.clear()
                fetchImages()
            }
            else
                Toast.makeText(this, "You have enter any word", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchImages() {
        CoroutineScope(IO).launch {
            val image = async {
                try {
                    URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=db64db294a72ccf95fb28883ef661036&tags=${input.text}&per_page=${imagePerPage}&page=1&format=json&nojsoncallback=1")
                            .readText(Charsets.UTF_8)
                } catch (e: Exception) {
                    ""
                }
            }.await()

            if (image.isNotEmpty()) {
                displayImage(image)
            }
        }
    }


    private suspend fun displayImage(image: String) {
        withContext(Main) {
            val jsonObj = JSONObject(image)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")

            for (i in 0 until photos.length()) {
                val title = photos.getJSONObject(i).getString("title")
                val farm = photos.getJSONObject(i).getString("farm")
                val server = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val imageUrl = "https://farm$farm.staticflickr.com/$server/${id}_$secret.jpg"
                imagesList.add(ImageData(title, imageUrl))
            }
            adapter.notifyDataSetChanged()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Number of image per page")

        val view = this.layoutInflater.inflate(R.layout.custome, null)
        view.image_per_page.text = imagePerPage.toString()

        view.decrease_btn.setOnClickListener {
            if (imagePerPage > 0) {
                imagePerPage--
                view.image_per_page.text = imagePerPage.toString()
            }
        }

        view.increase_btn.setOnClickListener {
            imagePerPage++
            view.image_per_page.text = imagePerPage.toString()
        }

        builder.setView(view)

        builder.setPositiveButton("Save") { _, _ ->
            view.image_per_page.text = imagePerPage.toString()
            item.title = imagePerPage.toString()
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.create().show()

        return super.onOptionsItemSelected(item)
    }



    fun openImage(url: String){
        bigImage.visibility = View.VISIBLE
        Glide.with(this).load(url).into(bigImage)
        grid.visibility = View.GONE
        input.visibility = View.GONE
        searchBtn.visibility = View.GONE
    }

    private fun closeImage(){
        bigImage.visibility = View.GONE
        grid.visibility = View.VISIBLE
        input.visibility = View.VISIBLE
        searchBtn.visibility = View.VISIBLE
    }

}