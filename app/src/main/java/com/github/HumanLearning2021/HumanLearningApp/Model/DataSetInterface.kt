package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable


/**
 * An interface representing the part of the model interacting with data sets
 */
interface DataSetInterface {
<<<<<<< HEAD
    fun getPicture(categoryString: String): CategorizedPicture
=======
    suspend fun getPicture(categoryString: String): CategorizedPicture
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
}