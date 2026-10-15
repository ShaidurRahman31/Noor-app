package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaDao {
    @Query("SELECT * FROM categories ORDER BY orderIndex ASC")
    fun getAllCategories(): Flow<List<DuaCategory>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): DuaCategory?

    @Query("SELECT * FROM duas WHERE verificationStatus != 'PENDING_REVIEW' ORDER BY titleBn ASC")
    fun getAllVerifiedDuas(): Flow<List<DuaItem>>

    @Query("SELECT * FROM duas ORDER BY titleBn ASC")
    fun getAllDuasForAdmin(): Flow<List<DuaItem>>

    @Query("SELECT * FROM duas WHERE categoryId = :categoryId AND verificationStatus != 'PENDING_REVIEW' ORDER BY titleBn ASC")
    fun getDuasByCategory(categoryId: String): Flow<List<DuaItem>>

    @Query("SELECT * FROM duas WHERE id = :id")
    fun getDuaById(id: String): Flow<DuaItem?>

    @Query("SELECT * FROM duas WHERE id = :id")
    suspend fun getDuaByIdSync(id: String): DuaItem?

    @Query("SELECT * FROM duas WHERE isBookmarked = 1 ORDER BY lastViewedTimestamp DESC, titleBn ASC")
    fun getBookmarkedDuas(): Flow<List<DuaItem>>

    @Query("SELECT * FROM duas WHERE lastViewedTimestamp > 0 ORDER BY lastViewedTimestamp DESC LIMIT 10")
    fun getRecentDuas(): Flow<List<DuaItem>>

    @Query("SELECT * FROM duas WHERE isQuranic = 1 AND verificationStatus != 'PENDING_REVIEW'")
    fun getQuranicDuas(): Flow<List<DuaItem>>

    @Query("SELECT * FROM duas WHERE verificationStatus != 'PENDING_REVIEW' ORDER BY RANDOM() LIMIT 1")
    fun getRandomDailyDua(): Flow<DuaItem?>

    @Query("""
        SELECT * FROM duas 
        WHERE (verificationStatus != 'PENDING_REVIEW') AND (
            titleBn LIKE '%' || :query || '%' OR 
            titleEn LIKE '%' || :query || '%' OR 
            arabicText LIKE '%' || :query || '%' OR 
            transliterationBn LIKE '%' || :query || '%' OR 
            meaningBn LIKE '%' || :query || '%' OR 
            reference LIKE '%' || :query || '%' OR
            referenceEdition LIKE '%' || :query || '%'
        )
        ORDER BY titleBn ASC
    """)
    fun searchDuas(query: String): Flow<List<DuaItem>>

    @Query("UPDATE duas SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)

    @Query("UPDATE duas SET lastViewedTimestamp = :timestamp, viewCount = viewCount + 1 WHERE id = :id")
    suspend fun markDuaViewed(id: String, timestamp: Long)

    @Query("UPDATE duas SET userNotes = :notes WHERE id = :id")
    suspend fun updateUserNotes(id: String, notes: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: DuaCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<DuaCategory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDua(dua: DuaItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuas(duas: List<DuaItem>)

    @Update
    suspend fun updateDua(dua: DuaItem)

    @Delete
    suspend fun deleteDua(dua: DuaItem)

    @Query("SELECT COUNT(*) FROM duas")
    suspend fun getDuaCount(): Int

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}
