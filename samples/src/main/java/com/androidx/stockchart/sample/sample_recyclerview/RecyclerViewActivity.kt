package com.androidx.stockchart.sample.sample_recyclerview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidx.stockchart.sample.R
import com.androidx.stockchart.sample.databinding.ActivityRecyclerViewBinding
import kotlin.random.Random

class RecyclerViewActivity : AppCompatActivity() {
    val binding: ActivityRecyclerViewBinding by lazy {
        ActivityRecyclerViewBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.rv.apply {
            layoutManager = LinearLayoutManager(this@RecyclerViewActivity)

            val decoration = OptionChainDecoration(
                centerWidthPx = 120.dp,
                sidePaddingPx = 8.dp,
                this@RecyclerViewActivity
            ).apply {
                attachToRecyclerView(binding.rv)
            }
            addItemDecoration(decoration)

            adapter = OptionAdapter().apply {
                submitList(generateMockData())
            }
        }
    }
    private fun generateMockData(): List<OptionItem> {
        return List(100) { i ->
            OptionItem(
                left1 = "C${i + 1}",
                left2 = "${Random.nextDouble(1.0, 5.0).format(2)}",
                center = "${50 + i * 0.5}",
                right1 = "${Random.nextDouble(1.0, 5.0).format(2)}",
                right2 = "P${i + 1}",
                strikePrice = 50.0 + i * 0.5
            )
        }
    }

    private fun Double.format(decimals: Int) = "%.${decimals}f".format(this)
    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

}