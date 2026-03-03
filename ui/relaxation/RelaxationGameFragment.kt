package com.wellnessflow.habbittracker.ui.relaxation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wellnessflow.habbittracker.R
import com.wellnessflow.habbittracker.databinding.FragmentRelaxationGameBinding
import kotlin.random.Random

class RelaxationGameFragment : Fragment() {

    private var _binding: FragmentRelaxationGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameGrid: GridLayout
    private var tiles = mutableListOf<GameTile>()
    private var correctPattern = mutableListOf<Int>()
    private var currentPattern = mutableListOf<Int>()
    private var gameStartTime = 0L
    private var gameTimer: CountDownTimer? = null
    private var isGameCompleted = false

    data class GameTile(
        val view: ImageView,
        val correctPosition: Int,
        var currentPosition: Int,
        val colorRes: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelaxationGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            setupGame()
            setupButtons()
            startNewGame()
        } catch (e: Exception) {
            // Handle any initialization errors gracefully
            e.printStackTrace()
        }
    }

    private fun setupGame() {
        try {
            gameGrid = binding.gameGrid
            
            // Create color resources for tiles (15 tiles, 1 empty space)
            val tileColors = listOf(
                R.drawable.tile_color_1,
                R.drawable.tile_color_2,
                R.drawable.tile_color_3,
                R.drawable.tile_color_4,
                R.drawable.tile_color_5,
                R.drawable.tile_color_6,
                R.drawable.tile_color_7,
                R.drawable.tile_color_8,
                R.drawable.tile_color_9,
                R.drawable.tile_color_10,
                R.drawable.tile_color_11,
                R.drawable.tile_color_12,
                R.drawable.tile_color_13,
                R.drawable.tile_color_14,
                R.drawable.tile_color_15
            )

            // Create 15 tiles (position 15 is empty)
            for (i in 0 until 15) {
                val tileView = createTileView(tileColors[i])
                val tile = GameTile(tileView, i, i, tileColors[i])
                tiles.add(tile)
                gameGrid.addView(tileView)
            }
            
            // Create empty space indicator
            createEmptySpace()
            
        } catch (e: Exception) {
            e.printStackTrace()
            // Show error message to user
            binding.tvProgress.text = "Game initialization failed"
        }
    }
    
    private fun createEmptySpace() {
        val emptyView = View(requireContext())
        val size = try {
            resources.getDimensionPixelSize(R.dimen.tile_size)
        } catch (e: Exception) {
            72 * resources.displayMetrics.density.toInt()
        }
        
        val layoutParams = GridLayout.LayoutParams().apply {
            width = size
            height = size
            setMargins(4, 4, 4, 4)
        }
        
        emptyView.layoutParams = layoutParams
        emptyView.background = ContextCompat.getDrawable(requireContext(), R.drawable.empty_space_background)
        gameGrid.addView(emptyView)
    }

    private fun createTileView(colorRes: Int): ImageView {
        val tileView = ImageView(requireContext())
        val size = try {
            resources.getDimensionPixelSize(R.dimen.tile_size)
        } catch (e: Exception) {
            // Fallback size if dimension not found
            72 * resources.displayMetrics.density.toInt()
        }
        
        val layoutParams = GridLayout.LayoutParams().apply {
            width = size
            height = size
            setMargins(4, 4, 4, 4)
        }
        
        tileView.layoutParams = layoutParams
        tileView.setImageResource(colorRes)
        tileView.scaleType = ImageView.ScaleType.CENTER_CROP
        tileView.background = ContextCompat.getDrawable(requireContext(), R.drawable.tile_background)
        tileView.setOnClickListener { onTileClick(tileView) }
        
        return tileView
    }

    private fun setupButtons() {
        binding.btnShuffle.setOnClickListener { shuffleTiles() }
        binding.btnHint.setOnClickListener { showHint() }
        binding.btnPlayAgain.setOnClickListener { startNewGame() }
    }

    private fun startNewGame() {
        isGameCompleted = false
        gameStartTime = System.currentTimeMillis()
        binding.cardCompletion.visibility = View.GONE
        
        // Set correct pattern (light to dark gradient)
        // Tiles are already ordered from lightest to darkest in the drawable list
        correctPattern = (0 until 15).toMutableList() // 15 tiles, position 15 is empty
        currentPattern = correctPattern.toMutableList()
        
        // Shuffle tiles
        shuffleTiles()
        
        // Start timer
        startTimer()
        
        // Update progress
        updateProgress()
    }

    private fun shuffleTiles() {
        if (isGameCompleted) return
        
        // Create a shuffled pattern that's solvable (15 tiles + 1 empty)
        val shuffledPattern = (0 until 15).toMutableList()
        shuffledPattern.shuffle()
        
        // Update tile positions
        for (i in tiles.indices) {
            tiles[i].currentPosition = shuffledPattern[i]
            updateTilePosition(tiles[i])
        }
        
        // Update current pattern
        currentPattern = shuffledPattern
        
        // Animate shuffle
        animateShuffle()
        
        updateProgress()
    }

    private fun updateTilePosition(tile: GameTile) {
        val row = tile.currentPosition / 4
        val col = tile.currentPosition % 4
        
        val layoutParams = tile.view.layoutParams as GridLayout.LayoutParams
        layoutParams.rowSpec = GridLayout.spec(row)
        layoutParams.columnSpec = GridLayout.spec(col)
        tile.view.layoutParams = layoutParams
    }

    private fun onTileClick(tileView: ImageView) {
        if (isGameCompleted) return
        
        val tile = tiles.find { it.view == tileView } ?: return
        val currentPos = tile.currentPosition
        
        // Find adjacent empty position (sliding puzzle style)
        val emptyPos = findEmptyPosition()
        if (isAdjacent(currentPos, emptyPos)) {
            // Move tile to empty position
            tile.currentPosition = emptyPos
            updateTilePosition(tile)
            
            // Animate movement
            animateTileMovement(tile.view)
            
            updateProgress()
            checkWinCondition()
        } else {
            // Show hint animation for invalid move
            animateInvalidMove(tile.view)
        }
    }
    
    private fun findEmptyPosition(): Int {
        val usedPositions = tiles.map { it.currentPosition }.toSet()
        return (0 until 16).first { it !in usedPositions }
    }
    
    private fun isAdjacent(pos1: Int, pos2: Int): Boolean {
        val row1 = pos1 / 4
        val col1 = pos1 % 4
        val row2 = pos2 / 4
        val col2 = pos2 % 4
        
        return (kotlin.math.abs(row1 - row2) == 1 && col1 == col2) ||
               (kotlin.math.abs(col1 - col2) == 1 && row1 == row2)
    }

    private fun checkWinCondition() {
        val isCorrect = tiles.all { it.currentPosition == it.correctPosition }
        if (isCorrect && !isGameCompleted) {
            completeGame()
        }
    }

    private fun completeGame() {
        isGameCompleted = true
        gameTimer?.cancel()
        
        val completionTime = System.currentTimeMillis() - gameStartTime
        val minutes = (completionTime / 60000).toInt()
        val seconds = ((completionTime % 60000) / 1000).toInt()
        
        binding.tvCompletionTime.text = "Completed in ${String.format("%02d:%02d", minutes, seconds)}"
        
        // Show completion card with animation
        binding.cardCompletion.visibility = View.VISIBLE
        binding.cardCompletion.alpha = 0f
        binding.cardCompletion.scaleX = 0.8f
        binding.cardCompletion.scaleY = 0.8f
        
        val animator = ObjectAnimator.ofFloat(binding.cardCompletion, "alpha", 0f, 1f)
        val scaleXAnimator = ObjectAnimator.ofFloat(binding.cardCompletion, "scaleX", 0.8f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(binding.cardCompletion, "scaleY", 0.8f, 1f)
        
        animator.duration = 300
        scaleXAnimator.duration = 300
        scaleYAnimator.duration = 300
        
        animator.interpolator = AccelerateDecelerateInterpolator()
        scaleXAnimator.interpolator = BounceInterpolator()
        scaleYAnimator.interpolator = BounceInterpolator()
        
        animator.start()
        scaleXAnimator.start()
        scaleYAnimator.start()
    }

    private fun showHint() {
        if (isGameCompleted) return
        
        // Find first misplaced tile and highlight it
        val misplacedTile = tiles.find { it.currentPosition != it.correctPosition }
        misplacedTile?.let { tile ->
            animateHint(tile.view)
        }
    }

    private fun startTimer() {
        gameTimer?.cancel()
        gameTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsed = System.currentTimeMillis() - gameStartTime
                val minutes = (elapsed / 60000).toInt()
                val seconds = ((elapsed % 60000) / 1000).toInt()
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }
            
            override fun onFinish() {}
        }
        gameTimer?.start()
    }

    private fun updateProgress() {
        val correctTiles = tiles.count { it.currentPosition == it.correctPosition }
        binding.tvProgress.text = "$correctTiles/15 tiles"
    }

    private fun animateShuffle() {
        tiles.forEach { tile ->
            tile.view.alpha = 0.7f
            tile.view.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }
    }

    private fun animateSwap(view1: ImageView?, view2: ImageView?) {
        view1?.let { v1 ->
            v1.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(100)
                .withEndAction {
                    v1.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
        
        view2?.let { v2 ->
            v2.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(100)
                .withEndAction {
                    v2.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
    }

    private fun animateHint(view: ImageView) {
        val animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.5f, 1f)
        animator.duration = 500
        animator.repeatCount = 2
        animator.start()
    }
    
    private fun animateTileMovement(view: ImageView) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(150)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
    
    private fun animateInvalidMove(view: ImageView) {
        view.animate()
            .translationX(-10f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .translationX(10f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .translationX(0f)
                            .setDuration(100)
                            .start()
                    }
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameTimer?.cancel()
        _binding = null
    }
}
