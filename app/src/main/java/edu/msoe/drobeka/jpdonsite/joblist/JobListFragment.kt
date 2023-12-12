package edu.msoe.drobeka.jpdonsite.joblist

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
        Log.d("JobListFragment", googleAuth.toString())


        googleSignIN()

    }

    private fun googleSignIN() {
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
                    val user = auth.currentUser
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun getDriveService(context: Context): Drive {
        GoogleSignIn.getLastSignedInAccount(context).let { googleAccount ->
            val credential = GoogleAccountCredential.usingOAuth2(
                requireContext(), listOf(DriveScopes.DRIVE)
            )
            credential.selectedAccount = googleAccount!!.account!!
            return Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName(getString(R.string.app_name))
                .build()
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
                                val drive = getDriveService(requireContext())
                                var result = drive.Files().list()
                                    .setFields("nextPageToken, files(id, name, parents)")
                                    .setQ("'1dfyAFcYuySmFgqgpzZirjOjepXk9rbc8' in parents and trashed=false")
                                    .execute()
                                for(file in result.files) {
                                    Log.d(TAG, file.toString())
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


                jobListViewModel.jobs.collect { jobs ->
                    binding.jobRecyclerView.adapter =
                        JobListAdapter(jobs.sortedBy { job -> job.title }.reversed()) { jobId ->
                            findNavController().navigate(
                                JobListFragmentDirections.loadJobDetail(jobId)
                            )
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
            R.id.new_job -> {
                showNewJob()
                true
            }

            R.id.clear_db -> {
                jobListViewModel.clearDB()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewJob() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newJob = Job(
                id = UUID.randomUUID(),
                title = (30000..31000).random().toString(),
                description = "A random job description",
                photos = arrayListOf()
            )
            withContext(Dispatchers.IO) {
                jobListViewModel.addJob(newJob)
            }
        }
    }
}