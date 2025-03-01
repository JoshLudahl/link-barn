package com.softklass.linkbarn.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softklass.linkbarn.data.model.Link
import java.net.URI
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_database",
        ).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun databaseIsCreated() {
        assertNotNull(db)
    }

    @Test
    fun testInsertAndRetrieveLink() = runBlocking {
        val testLink = Link(
            name = "Test Link",
            uri = URI("https://example.com"),
        )

        db.linkDao().insertLink(testLink)

        val retrievedLinks = db.linkDao().getAllLinks().first()
        assertEquals(1, retrievedLinks.size)
        assertEquals(testLink.name, retrievedLinks[0].name)
        assertEquals(testLink.uri, retrievedLinks[0].uri)
    }

    @Test
    fun testUpdateLink() = runBlocking {
        val testLink = Link(
            name = "Original Name",
            uri = URI("https://example.com"),
        )

        db.linkDao().insertLink(testLink)

        val updatedLink = testLink.copy(name = "Updated Name")
        db.linkDao().updateLink(updatedLink)

        val retrievedLink = db.linkDao().getLinkById(testLink.id).first()
        assertEquals("Updated Name", retrievedLink?.name)
    }

    @Test
    fun testDeleteLink() = runBlocking {
        val testLink = Link(
            name = "To Delete",
            uri = URI("https://example.com"),
        )

        db.linkDao().insertLink(testLink)
        db.linkDao().deleteLinkById(testLink.id)

        val retrievedLinks = db.linkDao().getAllLinks().first()
        assertTrue(retrievedLinks.isEmpty())
    }

    @Test
    fun testGetLinksByStatus() = runBlocking {
        val unreadLink = Link(
            name = "Unread Link",
            uri = URI("https://example.com"),
        )
        val readLink = Link(
            name = "Read Link",
            uri = URI("https://example.com"),
        )

        db.linkDao().insertLink(unreadLink)
        db.linkDao().insertLink(readLink)
    }
}
