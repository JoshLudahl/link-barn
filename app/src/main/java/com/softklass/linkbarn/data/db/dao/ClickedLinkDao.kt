package com.softklass.linkbarn.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softklass.linkbarn.data.model.ClickedLink
import kotlinx.coroutines.flow.Flow

@Dao
interface ClickedLinkDao {
    @Query("SELECT * FROM clicked_links ORDER BY clicked_at DESC")
    fun getAllClickedLinks(): Flow<List<ClickedLink>>

    @Query("SELECT * FROM clicked_links WHERE link_id = :linkId ORDER BY clicked_at DESC")
    fun getClickedLinksByLinkId(linkId: String): Flow<List<ClickedLink>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClickedLink(clickedLink: ClickedLink)

    @Query("DELETE FROM clicked_links WHERE link_id = :linkId")
    suspend fun deleteClickedLinksByLinkId(linkId: String)

    @Query("DELETE FROM clicked_links")
    suspend fun deleteAllClickedLinks()

    @Query("SELECT COUNT(*) FROM clicked_links WHERE link_id = :linkId")
    suspend fun getClickCountForLink(linkId: String): Int
}
