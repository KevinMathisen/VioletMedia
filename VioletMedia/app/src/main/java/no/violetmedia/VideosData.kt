package no.violetmedia

import android.app.Application
import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson


data class VideoData(var name: String, var description: String, var source: String, var local: Boolean)

class VideoDataManager {
    companion object {
        private const val VIDEO_PREFERENCES = "videos_preferences"
        private const val VIDEO_JSON_KEY = "videos_json"

        // Save a list of videos to SharedPreferences
        fun saveVideos(context: Context, videosData: MutableList<VideoData>) {
            val editor =
                context.getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE).edit()
            val gson = Gson()

            val videosList = videosData.toList()
            val json = gson.toJson(videosData)

            editor.putString(VIDEO_JSON_KEY, json)
            editor.apply()
        }

        // Retrieve the list of videos from SharedPreferences
        fun getVideos(context: Context): MutableList<VideoData> {
            val sharedPreferences =
                context.getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE)
            val gson = Gson()

            val json = sharedPreferences.getString(VIDEO_JSON_KEY, null)

            val type = object : TypeToken<List<VideoData>>() {}.type
            val videoList: List<VideoData> = gson.fromJson(json, type) ?: emptyList()
            return videoList.toMutableList()
        }

    }
}