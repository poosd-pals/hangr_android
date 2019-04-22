package com.cop4331.group7.hangr.classes

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class FirebaseClothingItemQueryBuilder(private val userDbRef: CollectionReference) {
    var categories = mutableListOf<String>()

    fun addCategories(categories: List<String>) {
        this.categories.addAll(categories)
    }
    fun removeCategories(categories: List<String>) {
        categories.forEach { this.categories.remove(it) }
    }

    fun build() : Query {
        if (categories.count() > 0) {
            var query = userDbRef.whereEqualTo("category", categories[0])
            for (index in 1 until categories.count()) {
                query = query.whereEqualTo("category", categories[index])
            }
            return query
        } else {
            return userDbRef
        }
    }


}
