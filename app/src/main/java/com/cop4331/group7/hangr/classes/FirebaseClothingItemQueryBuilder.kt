package com.cop4331.group7.hangr.classes

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class FirebaseClothingItemQueryBuilder(private val userDbRef: CollectionReference) {
    private var categories = mutableListOf<String>()

    fun addCategories(categories: List<String>) {
        this.categories.addAll(categories)
    }
    fun removeCategories(categories: List<String>) {
        categories.forEach { this.categories.remove(it) }
    }

    fun build() : Query {
        var query = userDbRef.orderBy("dateCreated")
        categories.forEach { query = query.whereEqualTo("category", it) }

        return query
    }


}
