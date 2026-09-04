package com.example.libmpvplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libmpvplayer.databinding.ItemVideoBinding

class VideoAdapter(
    private val onClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VH>() {

    private val items = ArrayList<VideoItem>()

    fun submit(list: List<VideoItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoItem) {
            binding.titleText.text = item.title
            binding.metaText.text = buildString {
                append(VideoScanner.formatDuration(item.durationMs))
                append(" · ")
                append(VideoScanner.formatSize(item.sizeBytes))
            }
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
