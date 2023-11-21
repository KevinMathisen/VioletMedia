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

/**
 * Recyclerviewadapter which shows videos
 *
 * @param videos List of videos to diplay
 * @param context Context of parent activity
 */
class VideoAdapter(
    var videos: List<VideoData>, val context: Context
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>(){

    /**
     * Viewholder for videos
     */
    inner class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Updates videos based on given list
     */
    fun setFilteredList(videos: MutableList<VideoData>){
        this.videos = videos
        notifyDataSetChanged()
    }

    /**
     * Creates a viewholder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoBinding.inflate(layoutInflater, parent, false)
        return VideoViewHolder(binding)
    }

    /**
     * Binding data and button onclicklisteners to the viewholder
     */
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]

        // Apply video name and description
        holder.binding.apply {
            tvTitle.text = video.name
            tvDesc.text = video.description
        }

        // Play video on button click
        holder.binding.imgbutPlay.setOnClickListener {

            // Inform user of videoplayer launch
            Toast.makeText(holder.itemView.context,"Videoplayer is launching", Toast.LENGTH_SHORT).show()

            // Start video player with videoname and subtitles if they are defined
            val intent = Intent(holder.itemView.context, VideoPlayer::class.java)
            intent.putExtra("source", video.source)
            if (videos[position].subtitle != null) {
                intent.putExtra("subtitle", video.subtitle)
                intent.putExtra("subtitleType", video.subtitleType)
            }

            startActivity(holder.itemView.context, intent, null)
        }

        // Edit video in button click
        holder.binding.imgbutEdit2.setOnClickListener {
            val intent = Intent(context, EditVideo::class.java)
            intent.putExtra("source", video.name)
            context.startActivity(intent)
        }
    }

    /**
     * Get amount of videos
     */
    override fun getItemCount(): Int {
        return  videos.size
    }
}