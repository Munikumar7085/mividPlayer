package com.example.mividplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.databinding.AlbumcardBinding
import com.example.mividplayer.databinding.SongLayoutBinding
import com.example.mividplayer.models.AlbumModel
import com.example.mividplayer.models.SongLayoutModel

class AlbumAdapter(val context:Context): RecyclerView.Adapter<AlbumAdapter.holder>() {
    private lateinit var mitemClickListener: onAlbumClickListener
    interface onAlbumClickListener{
        fun onitemClick(position: Int)
    }
    fun initItemClickListener(itemClickListener: onAlbumClickListener)
    {
        mitemClickListener=itemClickListener
    }
    class holder(albumcardBinding: AlbumcardBinding,itemClickListener: onAlbumClickListener):RecyclerView.ViewHolder(albumcardBinding.root)
    {

        val albumName=albumcardBinding.albumName
        val albumImg=albumcardBinding.albumImage

        init {
            albumcardBinding.root.setOnClickListener{
                itemClickListener.onitemClick(adapterPosition)
        }
        }
    }
    private val listDiffer= object : DiffUtil.ItemCallback<AlbumModel>() {
        override fun areItemsTheSame(oldItem: AlbumModel, newItem: AlbumModel): Boolean {
            return oldItem.albumName==newItem.albumName
        }

        override fun areContentsTheSame(oldItem: AlbumModel, newItem: AlbumModel): Boolean {
            return oldItem.equals(newItem)
        }


    }
    private var asyncListDiffer=AsyncListDiffer(this,listDiffer)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {

        return holder(AlbumcardBinding.inflate(LayoutInflater.from(parent.context),parent,false),mitemClickListener)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        val currsong=asyncListDiffer.currentList.get(position)

        Glide.with(context).load(currsong.albumSongs[0].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music).centerCrop())
            .into(holder.albumImg)

        holder.albumName.text=currsong.albumName
        holder.albumName.isSelected=true

    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }
    fun setsData(albumModel: List<AlbumModel>)
    {
        asyncListDiffer.submitList(albumModel)
    }
}