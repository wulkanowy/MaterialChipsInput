package io.github.wulkanowy.materialchipsinput.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.wulkanowy.materialchipsinput.ChipItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val randomNumbers = mutableListOf<ChipItem>()

        repeat(30) {
            randomNumbers.add(ChipItem(Random.nextInt().toString(), Random.nextInt().toString()))
        }

        with(mainChipsInput) {
            filterableChipItems = randomNumbers
            onTextChangeListener = { scrollContainer.scrollTo(0, scrollContainer.bottom) }
        }
    }
}
