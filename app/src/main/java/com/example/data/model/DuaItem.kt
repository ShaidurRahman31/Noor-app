package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "duas",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["isBookmarked"]),
        Index(value = ["verificationStatus"])
    ]
)
data class DuaItem(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val titleBn: String,
    val titleEn: String,
    val arabicText: String,
    val transliterationBn: String,
    val meaningBn: String,
    val sourceType: String, // "QURAN" or "HADITH"
    val reference: String,
    val referenceEdition: String = "",
    val authenticityGrade: String = "সহীহ (Sahih)", // e.g. "সহীহ (Sahih)", "হাসান (Hasan)", "কুরআনিক আয়াত"
    val contextOrOccasion: String = "",
    val recommendedRepeats: Int = 1,
    val audioUrl: String? = null,
    val isQuranic: Boolean = false,
    val isProphetic: Boolean = true,
    val verificationStatus: String = "VERIFIED", // "VERIFIED", "EDITORIAL_REVIEWED", "PENDING_REVIEW"
    val reviewerNotes: String = "যাচাইকৃত ইসলামিক রেফারেন্স অনুযায়ী পরীক্ষিত",
    val isBookmarked: Boolean = false,
    val lastViewedTimestamp: Long = 0L,
    val viewCount: Int = 0,
    val userNotes: String? = null
)
