package no.violetmedia

import android.app.Application
import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.Serializable

/**
 * The VideoData dataclass represents a stored video
 *
 * @param name Name if the video, has to be unique
 * @param description Description of video, can be null
 * @param source Source of the video, can either be an url or a reference to a local file
 * @param subtitle Url of a subtitle file, can be null
 */
data class VideoData(var name: String, var description: String?, var source: String, var subtitle: String?)

/**
 * A companionobject which manages the persistent storage and retrival of videos in the application
 */
class VideoDataManager {
    companion object {
        // For defining and finding the sharedPreferences file
        private const val VIDEO_PREFERENCES = "videos_preferences"
        private const val VIDEO_JSON_KEY = "videos_json"

        /**
         * Save a list of videos to SharedPreferences
         *
         * @param context Context for accessing the shared preference
         * @param videosData The videos to save
         */
        fun saveVideos(context: Context, videosData: MutableList<VideoData>) {

            // Initiate the editor
            val editor =
                context.getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE).edit()
            val gson = Gson()

            // Convert videos to json
            val json = gson.toJson(videosData)

            // Save the json to the SharedPreferences
            editor.putString(VIDEO_JSON_KEY, json)
            editor.apply()
        }

        /**
         * Retrieve the list of videos from SharedPreferences
         *
         * @param context context for accessing the SharedPreferences
         */
        fun getVideos(context: Context): MutableList<VideoData> {
            // Get SharedPreferences with videos
            val sharedPreferences =
                context.getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE)
            val gson = Gson()

            // Get the videos saved in json format
            val json = sharedPreferences.getString(VIDEO_JSON_KEY, null)

            // Convert the json to a list of videoData, and return it as a mutable list
            val type = object : TypeToken<List<VideoData>>() {}.type
            val videoList: List<VideoData> = gson.fromJson(json, type) ?: emptyList()
            return videoList.toMutableList()
        }

        /**
         * Returns if a video exists
         *
         * @param context context to the get SharedPreferences from
         * @param name Name of video to check if exists
         */
        fun doesVideoExist(context: Context, name: String): Boolean {
            val videos = getVideos(context)
            return videos.any{it.name == name}
        }
    }
}