package no.violetmedia

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import no.violetmedia.databinding.ItemVideoBinding

class VideoAdapter(
    var videos: List<VideoList>
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>(){
    inner class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoBinding.inflate(layoutInflater, parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.binding.apply {
            tvTitle.text = videos[position].title
        }
        val item = videos[position]
    }

    override fun getItemCount(): Int {
        return  videos.size
    }
}