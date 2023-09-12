package com.example.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookhub.R
import com.example.bookhub.activity.DescriptionActivity
import com.example.bookhub.database.BookEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, val bookList: List<BookEntity>) : RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_favorite_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
            return bookList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {

        val book = bookList[position]

        holder.tvBookName.text = book.bookName
        holder.tvBookAuthor.text = book.bookAuthor
        holder.tvBookPrice.text = book.bookPrice
        holder.tvBookRating.text = book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.tvBookImage)

        holder.llContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", book.book_Id.toString())
            context.startActivity(intent)
        }
    }

    class FavouriteViewHolder(view:View): RecyclerView.ViewHolder(view){
        val tvBookName: TextView = view.findViewById(R.id.tvFavBookTitle)
        val tvBookAuthor: TextView = view.findViewById(R.id.tvFavBookAuthor)
        val tvBookPrice: TextView = view.findViewById(R.id.tvFavBookPrice)
        val tvBookRating: TextView = view.findViewById(R.id.tvFavBookRating)
        val tvBookImage: ImageView = view.findViewById(R.id.imgFavBookImage)
        val llContent: LinearLayout = view.findViewById(R.id.llFavContent)
    }
}