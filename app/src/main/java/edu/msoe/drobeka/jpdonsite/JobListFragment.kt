package edu.msoe.drobeka.jpdonsite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.msoe.drobeka.jpdonsite.databinding.FragmentJobListBinding
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.random
import java.util.Date
import java.util.UUID

class JobListFragment : Fragment() {

    private var _binding: FragmentJobListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val jobListViewModel: JobListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
                description = "A random job description"
            )
            withContext(Dispatchers.IO) {
                jobListViewModel.addJob(newJob)
            }
        }
    }
}