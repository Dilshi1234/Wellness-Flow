package com.wellnessflow.habbittracker.ui.habits

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.data.Habit
import com.wellnessflow.habbittracker.databinding.FragmentHabitsBinding

class HabitsFragment : Fragment() {
    
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HabitsViewModel
    private lateinit var habitsAdapter: HabitsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[HabitsViewModel::class.java]
        
        setupRecyclerView()
        setupUI()
        observeViewModel()
        
        // Force refresh habits on view creation
        viewModel.refreshHabits()
        
        // Ensure we have some habits to display
        ensureDefaultHabits()
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            onIncrementClick = { habitId ->
                viewModel.incrementHabitProgress(habitId)
            },
            onDecrementClick = { habitId ->
                viewModel.decrementHabitProgress(habitId)
            },
            onEditClick = { habit ->
                // Show edit dialog
                showEditHabitDialog(habit)
            },
            onDeleteClick = { habitId ->
                // Show delete confirmation
                showDeleteConfirmation(habitId)
            }
        )
        
        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }
    }
    
    private fun setupUI() {
        binding.apply {
            btnAddHabit.setOnClickListener {
                showAddHabitDialog()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            // Create a new list to ensure proper update
            habitsAdapter.submitList(habits.toList())
        }
        
        viewModel.completedCount.observe(viewLifecycleOwner) { completed ->
            viewModel.totalCount.observe(viewLifecycleOwner) { total ->
                binding.tvHabitsSummary.text = "$completed of $total completed"
            }
        }
    }
    
    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_habit, null)
        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val etHabitTarget = dialogView.findViewById<EditText>(R.id.etHabitTarget)
        val etHabitUnit = dialogView.findViewById<EditText>(R.id.etHabitUnit)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etHabitName.text.toString().trim()
                val target = etHabitTarget.text.toString().trim().toIntOrNull() ?: 1
                val unit = etHabitUnit.text.toString().trim()
                
                if (name.isNotEmpty()) {
                    val habit = Habit(
                        name = name,
                        targetValue = target,
                        unit = unit,
                        emoji = "H" // Default emoji (using text instead)
                    )
                    viewModel.addHabit(habit)
                    Toast.makeText(requireContext(), "Habit added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_edit_habit, null)
        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val etHabitTarget = dialogView.findViewById<EditText>(R.id.etHabitTarget)
        val etHabitUnit = dialogView.findViewById<EditText>(R.id.etHabitUnit)
        
        // Pre-fill with current values
        etHabitName.setText(habit.name)
        etHabitTarget.setText(habit.targetValue.toString())
        etHabitUnit.setText(habit.unit)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etHabitName.text.toString().trim()
                val target = etHabitTarget.text.toString().trim().toIntOrNull() ?: habit.targetValue
                val unit = etHabitUnit.text.toString().trim()
                
                if (name.isNotEmpty()) {
                    val updatedHabit = habit.copy(
                        name = name,
                        targetValue = target,
                        unit = unit
                    )
                    viewModel.updateHabit(updatedHabit)
                    Toast.makeText(requireContext(), "Habit updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }
    
    private fun showDeleteConfirmation(habitId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete this habit? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteHabit(habitId)
                Toast.makeText(requireContext(), "Habit deleted successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshHabits()
    }
    
    private fun ensureDefaultHabits() {
        // This will be called once to ensure we have some habits
        // The DataManager already handles loading default habits if none exist
        // This is just to trigger the refresh
        viewModel.refreshHabits()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}