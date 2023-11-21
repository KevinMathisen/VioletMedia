# PROG2007_Project

## Overview
This is a PROG2007 NTNU project where we have developed an android application. 
Group members:
* Martin Solevåg Glærum
* Carl Petter Mørch-Reiersen
* Kevin Nikolai Mathisen

The application developed, named VioletMedia, is a video player which can stream and play remote and local videofiles. The application is open source, and stores all data locally on the phone to ensure privacy. 

## Third-party Libraries 
We used a library called [*Exoplayer*](https://developer.android.com/guide/topics/media/exoplayer) which is a media player which supported playback of videofiles which can be stored locally or streamed. 
[Exoplayer Github](https://github.com/google/ExoPlayer)

## How to use
The GitRepository has two components, one which is the android application and one which is a node server which can serve video files for the applicaiton to stream. 

The application can stream videofiles from anywhere, so it does not need the node server to work. The node server is only intended as a quick and easy way to stream your own videofiles from a server/pc to your phone over the internet. 


### How to use the android application
To use the android application you have to create a new video. When creating a video you can either choose a local file or a video file from an url:
* To choose a local video file simply click the "*\.\.\.*" button, which will let you select any local videofile. 
* To choose a video file from the internet, simply paste the videoUrl into the URL field. The has to be one of the supported file formats, which can be found [here](https://developer.android.com/guide/topics/media/exoplayer/supported-formats). Of the most common formats, such as .mp4, .mkv, and .mov, only .mp4 is supported. 

You can also choose an optional subtitle file for your video, which has to be in the `.vtt`, `.xml`, `.ttml`, `.ass`, or `.srt` file format.  

### How to run the node server 
To run the node server run the following commands the video-server directory, in your terminal of choice (with node installed):
* `npm install`, to install all dependencies 
* `node index.js`, to run the server

It will then print the port the server is running on. 
You can then copy any videofiles you want to stream into the `./videos` folder. The videos can then be accessed by the following url: ```http://ip-of-the-server:port-numer/name_of_videofile```

You can then use this url in VioletMedia. 

