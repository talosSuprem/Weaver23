package com.talos.weaver.ModelsTube;

public class PlaylistModel {

    String playlist_name;
    String uid;
    int videos;

    public PlaylistModel(String playlist_name, String uid, int videos) {
        this.playlist_name = playlist_name;
        this.uid = uid;
        this.videos = videos;
    }

    public PlaylistModel(){}

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }
}
