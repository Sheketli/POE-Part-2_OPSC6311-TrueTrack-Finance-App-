package com.example.truetrackfinance.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.truetrackfinance.R
import com.example.truetrackfinance.databinding.FragmentIncomeAllocationBinding
import com.example.truetrackfinance.ui.viewmodel.CategoryViewModel
import com.example.truetrackfinance.ui.viewmodel.AuthViewModel
import com.example.truetrackfinance.util.CurrencyUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "IncomeAllocation"

/**
 * IncomeAllocationFragment implements the Zero-Based Budgeting framework.
 */
@AndroidEntryPoint
class IncomeAllocationFragment : Fragment() {

    private var _binding: FragmentIncomeAllocationBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CategoryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    
    private lateinit var adapter: AllocationAdapter
    private var totalIncome: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIncomeAllocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing detailed budgeting view")
        
        val userId = authViewModel.getActiveUserId()
        viewModel.initialise(userId)
        
        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = AllocationAdapter { _, _ ->
            calculateRemaining()
        }
        binding.rvAllocations.adapter = adapter
        binding.rvAllocations.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }

        viewModel.currentBudget.observe(viewLifecycleOwner) { budget ->
            budget?.let {
                totalIncome = it.totalIncome
                
                // Pre-fill fields if not already focused
                if (!binding.etIncome.hasFocus()) {
                    binding.etIncome.setText(String.format(Locale.US, "%.2f", it.totalIncome))
                }
                if (!binding.etMinSpent.hasFocus()) {
                    binding.etMinSpent.setText(String.format(Locale.US, "%.2f", it.minSpentGoal))
                }
                if (!binding.etMaxSpent.hasFocus()) {
                    binding.etMaxSpent.setText(String.format(Locale.US, "%.2f", it.maxSpentGoal))
                }
                
                calculateRemaining()
            }
        }

        viewModel.categoryLimits.observe(viewLifecycleOwner) { limits ->
            val limitMap = limits.associate { it.categoryId to it.limitAmount }
            adapter.setInitialLimits(limitMap)
            calculateRemaining()
        }
    }

    private fun setupListeners() {
        binding.etIncome.doOnTextChanged { text, _, _, _ ->
            totalIncome = CurrencyUtil.parseAmount(text.toString()) ?: 0.0
            calculateRemaining()
        }

        binding.btnSaveAllocation.setOnClickListener {
            val remaining = calculateRemainingValue()
            val minSpent = CurrencyUtil.parseAmount(binding.etMinSpent.text.toString()) ?: 0.0
            val maxSpent = CurrencyUtil.parseAmount(binding.etMaxSpent.text.toString()) ?: 0.0

            if (Math.abs(remaining) > 0.01) {
                Toast.makeText(requireContext(), "Please allocate all funds to achieve a Zero-Based Budget", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (minSpent > maxSpent && maxSpent > 0) {
                Toast.makeText(requireContext(), "Minimum goal cannot be greater than maximum goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.i(TAG, "Action: Saving budget plan with Min/Max goals")
            viewModel.setMonthlyGoals(totalIncome, minSpent, maxSpent)
            viewModel.saveCategoryLimits(adapter.getLimits())
            
            Toast.makeText(requireContext(), "Budget plan saved successfully!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun calculateRemaining() {
        val remaining = calculateRemainingValue()
        binding.tvRemainingToAllocate.text = getString(R.string.remaining_to_allocate_format, CurrencyUtil.format(remaining))
        
        if (Math.abs(remaining) < 0.01) {
            binding.tvRemainingToAllocate.setTextColor(ContextCompat.getColor(requireContext(), R.color.success))
            binding.tvRemainingToAllocate.text = "Perfectly Allocated! (Zero-Based)"
        } else if (remaining < 0) {
            binding.tvRemainingToAllocate.setTextColor(android.graphics.Color.RED)
        } else {
            binding.tvRemainingToAllocate.setTextColor(android.graphics.Color.GRAY)
        }
    }

    private fun calculateRemainingValue(): Double {
        val allocated = adapter.getLimits().values.sum()
        return totalIncome - allocated
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
