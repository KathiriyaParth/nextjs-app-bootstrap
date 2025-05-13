package com.example.phonedialer.ui.recents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonedialer.databinding.FragmentRecentsBinding

class RecentsFragment : Fragment() {
    private var _binding: FragmentRecentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecentsViewModel by viewModels()
    private lateinit var adapter: RecentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RecentsAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.recentCalls.observe(viewLifecycleOwner) { calls ->
            adapter.submitList(calls)
            binding.emptyView.visibility = if (calls.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loadRecentCalls()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
