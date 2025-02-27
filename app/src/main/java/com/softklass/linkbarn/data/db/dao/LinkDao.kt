package com.softklass.linkbarn.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.softklass.linkbarn.data.model.Link
import kotlinx.coroutines.flow.Flow
import java.net.URI

@Dao
interface LinkDao {
    @Query("SELECT * FROM links")
    fun getAllLinks(): Flow<List<Link>>

    @Query("SELECT * FROM links WHERE id = :id")
    fun getLinkById(id: String): Flow<Link?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: Link)

    @Update
    suspend fun updateLink(link: Link)

    @Delete
    suspend fun deleteLink(link: Link)

    @Query("DELETE FROM links WHERE id = :id")
    suspend fun deleteLinkById(id: String)

    @Query("SELECT * FROM links WHERE uri = :uri LIMIT 1")
    suspend fun getLinkByUri(uri: URI): Link?
}
