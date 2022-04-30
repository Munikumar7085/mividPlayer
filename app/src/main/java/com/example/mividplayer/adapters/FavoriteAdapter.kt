package com.example.mividplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FavoriteSongsLayoutBinding
import com.example.mividplayer.models.SongLayoutModel

class FavoriteAdapter(val context: Context):
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    private lateinit var mfavoriteItemClickListener:FavoriteItemClickListener
    interface FavoriteItemClickListener{
        fun onItemClick(position:Int)

    }
    fun initFavoriteItemClickListener(favoriteItemClickListener: FavoriteItemClickListener)
    {
        mfavoriteItemClickListener=favoriteItemClickListener
    }
    class FavoriteViewHolder(favoriteSongsLayoutBinding:FavoriteSongsLayoutBinding,favoriteItemClickListener: FavoriteItemClickListener):RecyclerView.ViewHolder(favoriteSongsLayoutBinding.root){
        val image=favoriteSongsLayoutBinding.favoriteSongImage
        val songName=favoriteSongsLayoutBinding.favoriteSongName
        init {
            favoriteSongsLayoutBinding.root.setOnClickListener{
                favoriteItemClickListener.onItemClick(adapterPosition)
            }
        }
    }
     var favoriteListDiffer=object: DiffUtil.ItemCallback<SongLayoutModel>() {
        override fun areItemsTheSame(oldItem: SongLayoutModel, newItem: SongLayoutModel): Boolean {
            return oldItem.songName==newItem.songName
        }


        override fun areContentsTheSame(
            oldItem: SongLayoutModel,
            newItem: SongLayoutModel
        ): Boolean {
           return oldItem.equals(newItem)
        }

       }
    private var favoriteAsyncListDiffer= AsyncListDiffer(this@FavoriteAdapter,favoriteListDiffer)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
       return FavoriteViewHolder(FavoriteSongsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false),mfavoriteItemClickListener)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currsong=favoriteAsyncListDiffer.currentList.get(position)
        holder.songName.text=currsong.songName
        Glide.with(context).load(currsong.uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music))
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return favoriteAsyncListDiffer.currentList.size
    }
    fun setData(favoritesongsList:ArrayList<SongLayoutModel>)
    {
        favoriteAsyncListDiffer.submitList(favoritesongsList)
    }
}