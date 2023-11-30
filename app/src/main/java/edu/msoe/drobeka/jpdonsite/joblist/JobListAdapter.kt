package edu.msoe.drobeka.jpdonsite.joblist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.msoe.drobeka.jpdonsite.R
import edu.msoe.drobeka.jpdonsite.databinding.ListItemJobBinding
import edu.msoe.drobeka.jpdonsite.jobs.Job
import java.util.UUID

class JobHolder(
    private val binding: ListItemJobBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(job: Job, onJobClicked: (jobId: UUID) -> Unit) {
        binding.textView.text = binding.root.context.getString(R.string.job_num, job.title)
        binding.root.setOnClickListener {
            onJobClicked(job.id)
        }
    }
}

class JobListAdapter(
    private val jobs: List<Job>,
    private val onJobClicked: (jobId: UUID) -> Unit
) : RecyclerView.Adapter<JobHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JobHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemJobBinding.inflate(inflater, parent, false)
        return JobHolder(binding)
    }

    override fun getItemCount() = jobs.size

    override fun onBindViewHolder(holder: JobHolder, position: Int) {
        val job = jobs[position]
        holder.bind(job, onJobClicked)
    }
}