package no.violetmedia

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import no.violetmedia.databinding.ItemVideoBinding
class VideoAdapter(
    var videos: List<VideoData>, val context: Context
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>(){
    inner class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    fun setFilteredList(videos: MutableList<VideoData>){
        this.videos = videos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoBinding.inflate(layoutInflater, parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.binding.apply {
            tvTitle.text = videos[position].name
            tvDesc.text = videos[position].description
        }
        holder.binding.imgbutPlay.setOnClickListener {
            Toast.makeText(holder.itemView.context,"Video is now playing", Toast.LENGTH_SHORT).show()

            // Start video player
            val intent = Intent(holder.itemView.context, VideoPlayer::class.java)
            intent.putExtra("source", videos[position].source)
            startActivity(holder.itemView.context, intent, null)
        }
        val item = videos[position]

        holder.binding.myConstraintLayout.setOnClickListener {
            val intent = Intent(context, EditVideo::class.java)
            intent.putExtra("source", item.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return  videos.size
    }
}