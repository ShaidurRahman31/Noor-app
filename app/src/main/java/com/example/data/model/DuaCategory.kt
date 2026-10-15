package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class DuaCategory(
    @PrimaryKey
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val descriptionBn: String,
    val iconKey: String,
    val orderIndex: Int
)
