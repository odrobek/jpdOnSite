package edu.msoe.drobeka.jpdonsite.joblist

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import edu.msoe.drobeka.jpdonsite.R
import edu.msoe.drobeka.jpdonsite.databinding.FragmentJobListBinding
import edu.msoe.drobeka.jpdonsite.googledrive.GoogleDrive
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

const val TAG = "JobListFragment"

class JobListFragment : Fragment() {

    private var _binding: FragmentJobListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val jobListViewModel: JobListViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuth: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleAuth = GoogleSignIn.getClient(requireContext(), gso)
        Log.d("JobListFragment", auth.currentUser!!.email!!)


        googleSignIn()
        GoogleDrive.initialize(requireContext())
    }

    private fun googleSignIn() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account == null) {
            val signInIntent = googleAuth.signInIntent
            startActivityForResult(signInIntent, 35)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 35) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            }
            catch (e:ApiException) {
                Toast.makeText(requireContext(), "$e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Log.d(TAG, auth.currentUser.toString())
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobListBinding.inflate(inflater, container, false)

        binding.jobRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set adapter for recycler view - how will I be getting data in?
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        launch {
                            try {
                                // get folder id for 'jobs' so that other files are not found
                                // move this to a different class
                                // simulate drive behavior with a different class
                                val drive = GoogleDrive.get().drive
                                val filesInRoot = drive.Files().list()
                                    .setFields("files(id, name, parents)")
                                    .setQ("'root' in parents and trashed=false")
                                    .execute().files

                                var jobsFolderId = ""
                                for (file in filesInRoot) {
                                    if (file.name == "jobs") {
                                        jobsFolderId = file.id
                                    }
                                }

                                // get all files within the jobs folder
                                // TODO filter so only folders show up for this part
                                var result = drive.Files().list()
                                    .setFields("files(id, name)")
                                    .setQ("'$jobsFolderId' in parents and trashed=false")
                                    .execute()

                                var folderNames = mutableListOf<Job>()
                                for (file in result.files) {
                                    Log.d(TAG, file.toString())
                                    folderNames.add(
                                        Job(
                                            file.id,
                                            file.name, ""
                                        )
                                    )
                                }
                                withContext(Dispatchers.Main) {
//                                    binding.progressBarCyclic.visibility = View.GONE
                                    binding.jobRecyclerView.adapter =
                                        JobListAdapter(folderNames.sortedBy { job -> job.title }
                                            .reversed()) { folderId ->
                                                findNavController().navigate(
                                                    JobListFragmentDirections.loadJobDetail(folderId)
                                                )
                                        }
                                }

                            }
                            catch (userAuthEx: UserRecoverableAuthIOException) {
                                startActivity(
                                    userAuthEx.intent
                                )
                            }
                            catch (e: Exception) {
                                Log.e(TAG, e.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_job_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.clear_db -> {
                jobListViewModel.clearDB()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}