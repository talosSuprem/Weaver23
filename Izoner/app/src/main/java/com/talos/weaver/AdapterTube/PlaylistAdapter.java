package com.talos.weaver.AdapterTube;

import static android.os.Build.VERSION_CODES.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.talos.weaver.ModelsTube.PlaylistModel;
import com.talos.weaver.R;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    ArrayList<PlaylistModel> list;
    OnitemClickListener listener;

    public PlaylistAdapter(Context context, ArrayList<PlaylistModel> list, OnitemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(com.talos.weaver.R.layout.playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        holder.blind(list.get(position), listener);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_playlist_name, txt_video_count;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_playlist_name = itemView.findViewById(com.talos.weaver.R.id.txt_playlist_name);
            txt_video_count = itemView.findViewById(com.talos.weaver.R.id.txt_video_count);

        }

        public void blind(final PlaylistModel model, final OnitemClickListener listener){
            txt_playlist_name.setText(model.getPlaylist_name());
            txt_video_count.setText("Videos" + model.getVideos());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(model);
                }
            });
        }
    }

    public interface OnitemClickListener{
        void onItemClick(PlaylistModel model);
    }
}
