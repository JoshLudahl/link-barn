package com.softklass.linkbarn.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.model.Status
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {
    @Query("SELECT * FROM links")
    fun getAllLinks(): Flow<List<Link>>

    @Query("SELECT * FROM links WHERE id = :id")
    fun getLinkById(id: String): Flow<Link?>

    @Query("SELECT * FROM links WHERE status = :status")
    fun getLinksByStatus(status: Status): Flow<List<Link>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: Link)

    @Update
    suspend fun updateLink(link: Link)

    @Delete
    suspend fun deleteLink(link: Link)

    @Query("DELETE FROM links WHERE id = :id")
    suspend fun deleteLinkById(id: String)
}