package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.CategoryDao
import com.softklass.linkbarn.data.model.Category
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

open class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
) {
    open fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    open suspend fun getCategoryById(id: String): Category? = categoryDao.getCategoryById(id)

    open suspend fun getCategoryByName(name: String): Category? = categoryDao.getCategoryByName(name)

    open suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)

    open suspend fun getOrCreateCategory(name: String): Category {
        val existingCategory = getCategoryByName(name)
        if (existingCategory != null) {
            return existingCategory
        }

        val newCategory = Category(name = name)
        insertCategory(newCategory)
        return newCategory
    }

    open suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    open suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    open suspend fun deleteCategoryById(id: String) = categoryDao.deleteCategoryById(id)
}
