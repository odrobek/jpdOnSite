/**
 * Olek Drobek
 * CSC 4911
 * Final Project - JPD OnSite
 * JobDetailFragment.kt
 */

package edu.msoe.drobeka.jpdonsite.jobdetail

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.client.http.FileContent
import edu.msoe.drobeka.jpdonsite.databinding.FragmentJobDetailBinding
import edu.msoe.drobeka.jpdonsite.googledrive.GoogleDrive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date


private const val TAG = "JobDetailFragment"

class JobDetailFragment : Fragment() {

    private var _binding: FragmentJobDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: JobDetailFragmentArgs by navArgs()

    private val jobDetailViewModel: JobDetailViewModel by viewModels {
        JobDetailViewModelFactory(args.folderId)
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto ->
        if (didTakePhoto && lastPhotoName != null) {
            // this whole block will be changed to just uploading the photo to Google Drive
            CoroutineScope(Dispatchers.IO).launch {
                val photoFile = File(
                    requireContext().applicationContext.filesDir,
                    lastPhotoName
                )
                val gFile = com.google.api.services.drive.model.File()
                gFile.name = photoFile.name
                gFile.parents = arrayOf(args.folderId).toMutableList()
                val fileContent = FileContent("image/jpeg", photoFile)
                GoogleDrive.get().drive.Files().create(gFile, fileContent)
                    .execute()

                Log.d(TAG, "File uploaded")
            }
        }
    }

    private var lastPhotoName : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentJobDetailBinding.inflate(layoutInflater, container, false)

        binding.photoRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            imageButton.setOnClickListener {
                lastPhotoName = "IMG_${Date().toString().replace(" ", "")}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir,
                    lastPhotoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "edu.msoe.drobeka.jpdonsite.fileprovider",
                    photoFile
                )

                takePhoto.launch(photoUri)
            }

            fromGallery.setOnClickListener {
                val intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
                // need to override this method to get the Uri for the photo and then upload it to Google Drive
            }


            val captureImageIntent = takePhoto.contract.createIntent(
                requireContext(),
                Uri.parse("")
            )
            imageButton.isEnabled = canResolveIntent(captureImageIntent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                var folder: com.google.api.services.drive.model.File? = null
                var filesInFolder: MutableList<String> = mutableListOf()
                withContext(Dispatchers.IO) {
                    folder = GoogleDrive.get().drive.Files().get(args.folderId).execute()
                    var inFolder = GoogleDrive.get().drive.Files().list()
                        .setFields("files(id, name, parents)")
                        .setQ("'${args.folderId}' in parents and trashed=false")
                        .execute().files
                    for(file in inFolder) {
                        filesInFolder.add(file.id)
                        getImageFile(file.id)
                    }

                    // download image and then save bitmaps


                }
                updateUi(folder, filesInFolder)
            }
        }
    }

    private fun getImageFile(fileId: String) {
        Log.e("idDownload", fileId)
        val file = File(requireContext().applicationContext.filesDir, "${fileId}.jpg")
        if (!file.exists()) {
            try {
                val photoFile = GoogleDrive.get().drive.Files().get(fileId).execute()
                saveImageInFilesDir(photoFile.id)
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
            }
        }
    }

    private fun saveImageInFilesDir(id: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(requireContext().applicationContext.filesDir, "${id}.jpg")
            try {
                val outputStream = FileOutputStream(file)
                GoogleDrive.get().drive.files().get(id)
                    .executeMediaAndDownloadTo(outputStream)
                outputStream.flush()
                outputStream.close()
                Log.d(TAG, "written")
            } catch (e: Exception) {
                Log.d(TAG, e.message!!)
            }
        }
    }



    private fun updateUi(folder: com.google.api.services.drive.model.File?,
                         files: List<String>) {
        binding.apply {
            jobTitle.text = folder!!.name

            photoRecyclerView.adapter = PhotoListAdapter(files)
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }
}