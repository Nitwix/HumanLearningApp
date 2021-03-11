package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.Model.Category
import com.github.HumanLearning2021.HumanLearningApp.Model.DummyCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.Model.DummyDataSetInterface
import com.github.HumanLearning2021.HumanLearningApp.Presenter.DummyPresenter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_categorized_picture_test)

        val intent = Intent(this, DummyCategorizedPictureTestActivity::class.java)
        startActivity(intent)

    }
}