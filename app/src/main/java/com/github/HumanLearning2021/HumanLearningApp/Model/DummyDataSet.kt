package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable

val utensil = Category("Utensil")
val fork = Category("Fork")
val knife = Category("Knife")
val spoon = Category("Spoon")
val creator = Admin("HLStaff")


/**
 * a class representing a dummy data set
 */
class DummyDataSet : DataSet("Dummy", creator, setOf(fork, knife, spoon), 1) {
    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)


<<<<<<< HEAD
    override fun getPicture(category: Category): CategorizedPicture =
=======
    override suspend fun getPicture(category: Category): CategorizedPicture =
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
            when (category) {
                fork -> forkPic
                knife -> knifePic
                else -> spoonPic
            }




}