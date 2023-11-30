package edu.msoe.drobeka.jpdonsite.jobdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import edu.msoe.drobeka.jpdonsite.databinding.ListJobPhotosBinding
import edu.msoe.drobeka.jpdonsite.getScaledBitmap
import java.io.File

class PhotoHolder(
    private val binding: ListJobPhotosBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photoLocation: String) {
        if (photoLocation != "") {
            val photoFile = File(binding.root.context.applicationContext.filesDir, photoLocation)

            if (photoFile.exists()) {
                binding.jobPhoto.doOnLayout { measuredView ->
                    val scaledBitMap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.jobPhoto.setImageBitmap(scaledBitMap)
                    binding.jobPhoto.tag = photoLocation
                    binding.jobPhoto.contentDescription =
                        ""
                }
            } else {
                binding.jobPhoto.setImageBitmap(null)
                binding.jobPhoto.tag = null
                binding.jobPhoto.contentDescription =
                    ""
            }
        }
    }
}


class PhotoListAdapter(
    private val photos: List<String>
) : RecyclerView.Adapter<PhotoHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : PhotoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListJobPhotosBinding.inflate(inflater, parent, false)
        return PhotoHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo)
    }

    override fun getItemCount() = photos.size
}