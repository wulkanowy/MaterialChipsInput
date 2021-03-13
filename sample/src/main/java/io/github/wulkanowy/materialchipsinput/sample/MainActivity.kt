package io.github.wulkanowy.materialchipsinput.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.wulkanowy.materialchipsinput.ChipItem
import io.github.wulkanowy.materialchipsinput.sample.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val randomNumbers = mutableListOf<ChipItem>()

        repeat(30) {
            randomNumbers.add(
                SampleChipItem(Random.nextInt().toString(), Random.nextInt().toString())
            )
        }

        with(binding.mainChipsInput) {
            filterableChipItems = randomNumbers
            onTextChangeListener = {
                binding.scrollContainer.scrollTo(0, binding.scrollContainer.bottom)
            }
        }
    }
}

data class SampleChipItem(override val title: String, override val summary: String) : ChipItem

