package com.example.data.repository

import com.example.data.local.DuaDao
import com.example.data.local.StarterDuaData
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DuaRepository(private val duaDao: DuaDao) {

    suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val categoryCount = duaDao.getCategoryCount()
        if (categoryCount == 0) {
            duaDao.insertCategories(StarterDuaData.categories)
        }
        val duaCount = duaDao.getDuaCount()
        if (duaCount == 0) {
            duaDao.insertDuas(StarterDuaData.verifiedDuas)
        }
    }

    fun getAllCategories(): Flow<List<DuaCategory>> = duaDao.getAllCategories()

    fun getAllVerifiedDuas(): Flow<List<DuaItem>> = duaDao.getAllVerifiedDuas()

    fun getAllDuasForAdmin(): Flow<List<DuaItem>> = duaDao.getAllDuasForAdmin()

    fun getDuasByCategory(categoryId: String): Flow<List<DuaItem>> = duaDao.getDuasByCategory(categoryId)

    fun getDuaById(id: String): Flow<DuaItem?> = duaDao.getDuaById(id)

    suspend fun getCategoryById(id: String): DuaCategory? = withContext(Dispatchers.IO) {
        duaDao.getCategoryById(id)
    }

    fun getBookmarkedDuas(): Flow<List<DuaItem>> = duaDao.getBookmarkedDuas()

    fun getRecentDuas(): Flow<List<DuaItem>> = duaDao.getRecentDuas()

    fun getQuranicDuas(): Flow<List<DuaItem>> = duaDao.getQuranicDuas()

    fun getRandomDailyDua(): Flow<DuaItem?> = duaDao.getRandomDailyDua()

    fun searchDuas(query: String): Flow<List<DuaItem>> = duaDao.searchDuas(query)

    suspend fun toggleBookmark(id: String, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        duaDao.updateBookmark(id, !currentStatus)
    }

    suspend fun recordDuaViewed(id: String) = withContext(Dispatchers.IO) {
        duaDao.markDuaViewed(id, System.currentTimeMillis())
    }

    suspend fun updateUserNotes(id: String, notes: String?) = withContext(Dispatchers.IO) {
        duaDao.updateUserNotes(id, notes)
    }

    suspend fun insertDua(dua: DuaItem) = withContext(Dispatchers.IO) {
        duaDao.insertDua(dua)
    }

    suspend fun updateDua(dua: DuaItem) = withContext(Dispatchers.IO) {
        duaDao.updateDua(dua)
    }

    suspend fun deleteDua(dua: DuaItem) = withContext(Dispatchers.IO) {
        duaDao.deleteDua(dua)
    }
}
