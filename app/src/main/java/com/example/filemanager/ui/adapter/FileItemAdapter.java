package com.example.filemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.example.filemanager.databinding.ItemEmptyBinding;
import com.example.filemanager.databinding.ItemRecyclerviewBinding;
import com.example.filemanager.ui.activity.MainActivity;
import com.example.filemanager.ui.data.FileData;

import java.util.ArrayList;

public class FileItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FileData> fileList;
    public MainActivity.OnClickListener onClickListener;

    public void setOnClickListener(MainActivity.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FileItemAdapter(ArrayList<FileData> fileList) {
        this.fileList = fileList;
    }

    @Override
    public int getItemViewType(int position) {
        if (fileList.get(position).isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
            return new EmptyViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, null);
            return new CustomViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomViewHolder) {
            ((CustomViewHolder) holder).bind(fileList.get(position));
            String name = fileList.get(position).getFileName();
            holder.itemView.setOnClickListener(view -> {
                if (name.contains(".")) {
                    onClickListener.onClick(name);
                } else {
                    onClickListener.onClick(name, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ItemRecyclerviewBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecyclerviewBinding.bind(itemView);
        }

        public void bind(FileData data) {
            binding.iv.setImageResource(data.getFileIcon());
            binding.tv.setText(data.getFileName());
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        ItemEmptyBinding binding;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemEmptyBinding.bind(itemView);
        }
    }
}
