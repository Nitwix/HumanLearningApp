package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreDatabaseService(
    /**
     * name of a database within the Firebase App
     */
    dbName: String,
    app: FirebaseApp? = null,
) : DatabaseService {
    private val app = app ?: Firebase.app
    private val db = Firebase.firestore(this.app)
    private val categories = db.collection("/databases/$dbName/categories")
    private val pictures = db.collection("/databases/$dbName/pictures")
    private val datasets = db.collection("/databases/$dbName/datasets")
    private val users = db.collection("/databases/$dbName/users")
    private val storage = Firebase.storage(this.app)
    private val imagesDir = storage.reference.child("$dbName/images")


    private class CategorySchema() {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var name: String

        constructor(name: String) : this() {
            this.name = name
        }

        fun toPublic() = FirestoreCategory(self.path, name, name)
    }

    private class PictureSchema() {
        @DocumentId
        lateinit var self: DocumentReference
        lateinit var category: DocumentReference
        lateinit var url: String

        constructor(category: DocumentReference, url: String) : this() {
            this.category = category
            this.url = url
        }

        suspend fun toPublic(): FirestoreCategorizedPicture {
            val cat = category.get().await().toObject(CategorySchema::class.java)
            requireNotNull(cat, { "category not found" })
            return FirestoreCategorizedPicture(self.path, cat.toPublic(), url)
        }
    }

    private class UserSchema {
        @DocumentId
        lateinit var self: DocumentReference
        var displayName: String? = null
        var email: String? = null
        fun toPublic() = FirestoreUser(
            path = self.path,
            displayName = displayName,
            email = email,
            uid = self.id.takeWhile { it != '@' },
            type = User.Type.valueOf(self.id.takeLastWhile { it != '@' }),
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun getCategories(): Set<FirestoreCategory> {
        val query = categories
        val cats = query.get().await().toObjects(CategorySchema::class.java)
        return buildSet(cats.size) {
            for (cat in cats)
                add(cat.toPublic())
        }
    }

    override suspend fun getAllPictures(category: Category): Set<CategorizedPicture> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun removePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override suspend fun putDataset(name: String, categories: Set<Category>): Dataset {
        TODO("Not yet implemented")
    }

    override suspend fun getDataset(id: Any): Dataset? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDataset(id: Any) {
        TODO("Not yet implemented")
    }

    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        TODO("Not yet implemented")
    }


    override fun getDatasets(): Set<Dataset> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(firebaseUser: FirebaseUser): FirestoreUser {
        val uid = firebaseUser.uid
        val type = User.Type.FIREBASE
        val documentRef = users.document("$uid@$type")
        val data = UserSchema().apply {
            email = firebaseUser.email
            displayName = firebaseUser.displayName
        }
        documentRef.set(data).await()
        return documentRef.get().await().toObject(UserSchema::class.java)!!.toPublic()
    }

    override suspend fun getUser(type: User.Type, uid: String): FirestoreUser? {
        val documentRef = users.document("$uid@$type")
        val user = documentRef.get().await().toObject(UserSchema::class.java)
        return user?.toPublic()
    }

    override suspend fun getPicture(category: Category): FirestoreCategorizedPicture? {
        require(category is FirestoreCategory)
        val query = pictures.whereEqualTo("category", db.document(category.path)).limit(1)
        val pic = query.get().await().toObjects(PictureSchema::class.java).getOrNull(0)
        return pic?.toPublic()
    }

    override suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture? {
        TODO("Not yet implemented")
    }

    override suspend fun putPicture(picture: Uri, category: Category): FirestoreCategorizedPicture {
        require(category is FirestoreCategory)
        val ref = imagesDir.child("${UUID.randomUUID()}")
        ref.putFile(picture).await()
        val data = PictureSchema(db.document(category.path), "gs://${ref.bucket}/${ref.path}")
        val documentRef = pictures.add(data).await()
        return documentRef.get().await().toObject(PictureSchema::class.java)!!.toPublic()
    }

    override suspend fun getCategory(categoryId: Any): Category? {
        //TODO("Update to use id")
        val query = categories.whereEqualTo("name", categoryId as String).limit(1)
        val cat = query.get().await().toObjects(CategorySchema::class.java).getOrNull(0)
        return cat?.toPublic()
    }

    override suspend fun putCategory(categoryName: String): FirestoreCategory {
        val data = CategorySchema(categoryName)
        val documentRef = categories.add(data).await()
        return documentRef.get().await().toObject(CategorySchema::class.java)!!.toPublic()
    }
}
