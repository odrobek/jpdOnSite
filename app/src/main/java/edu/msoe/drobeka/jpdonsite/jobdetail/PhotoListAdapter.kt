/**
 * Olek Drobek
 * CSC 4911
 * Final Project - JPD OnSite
 * PhotoListAdapter.kt
 */
package edu.msoe.drobeka.jpdonsite.jobdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import edu.msoe.drobeka.jpdonsite.databinding.ListJobPhotosBinding
import edu.msoe.drobeka.jpdonsite.getScaledBitmap
import edu.msoe.drobeka.jpdonsite.googledrive.GoogleDrive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

class PhotoHolder(
    private val binding: ListJobPhotosBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photoLocation: String) {
        binding.root.setOnClickListener {
            Toast.makeText(binding.root.context, photoLocation, Toast.LENGTH_SHORT).show()
        }

        binding.deletePhoto.isEnabled = false

        binding.textView2.text = photoLocation
        if (photoLocation != "") {
            val photoFile = File(binding.root.context.applicationContext.filesDir,
                "$photoLocation.jpg"
            )

            if (photoFile.exists()) {
                binding.jobPhoto.doOnLayout { measuredView ->
                    var scaledBitMap = getScaledBitmap(
                        photoFile.absolutePath,
                        measuredView.width,
                        measuredView.height
                    )
                    var counter = 0
                    while (scaledBitMap == null && counter < 10) {
                        scaledBitMap = getScaledBitmap(
                                photoFile.absolutePath,
                        measuredView.width,
                        measuredView.height
                        )
                        counter++
                    }
                    if(scaledBitMap != null) {
                        binding.jobPhoto.setImageBitmap(scaledBitMap)
                        binding.jobPhoto.tag = photoLocation
                        binding.jobPhoto.contentDescription = ""
                    }
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