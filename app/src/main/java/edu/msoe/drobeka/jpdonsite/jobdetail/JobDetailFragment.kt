package edu.msoe.drobeka.jpdonsite.jobdetail

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
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
import edu.msoe.drobeka.jpdonsite.databinding.FragmentJobDetailBinding
import edu.msoe.drobeka.jpdonsite.googledrive.GoogleDrive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date


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
            jobDetailViewModel.updateJob { oldJob ->
                var newPhotos = oldJob.photos
                newPhotos.add(lastPhotoName!!)
                oldJob.copy(photos = newPhotos)
            }
            jobDetailViewModel.afterPhotoChange()
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
                withContext(Dispatchers.IO) {
                    folder = GoogleDrive.get().drive.Files().get(args.folderId).execute()
                }
                updateUi(folder)
            }
        }
    }

    private fun updateUi(folder: com.google.api.services.drive.model.File?) {
        binding.apply {
            jobTitle.text = folder!!.name

            // photoRecyclerView.adapter = PhotoListAdapter(job.photos)
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