package com.example.truetrackfinance

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.truetrackfinance.data.db.AppDatabase
import com.example.truetrackfinance.data.db.dao.*
import com.example.truetrackfinance.data.db.entity.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var budgetDao: BudgetDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = db.userDao()
        categoryDao = db.categoryDao()
        expenseDao = db.expenseDao()
        budgetDao = db.budgetDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeUserAndReadInList() = runTest {
        val user = User(id = 1, fullName = "Test User", username = "testuser", email = "test@example.com", passwordHash = "hash")
        userDao.insertUser(user)
        val byName = userDao.getUserByUsername("testuser")
        assertEquals(byName?.fullName, "Test User")
    }

    @Test
    fun insertCategoryAndRetrieve() = runTest {
        val cat = Category(id = 1, userId = 1, name = "Food", colorHex = "#FF0000", emoji = "🍕")
        categoryDao.insertCategory(cat)
        val allCats = categoryDao.getCategoriesForUser(1)
        assertTrue(allCats.any { it.name == "Food" })
    }

    @Test
    fun insertExpenseAndVerifyTotal() = runTest {
        val cat = Category(id = 1, userId = 1, name = "Food", colorHex = "#FF0000")
        categoryDao.insertCategory(cat)
        
        val expense = Expense(userId = 1, categoryId = 1, amount = 150.50, description = "Lunch", date = System.currentTimeMillis())
        expenseDao.insertExpense(expense)
        
        val total = expenseDao.observeTotalSpentInMonth(1, 0, System.currentTimeMillis() + 1000).first()
        assertEquals(150.50, total, 0.01)
    }

    @Test
    fun budgetLimitLogic() = runTest {
        val budget = Budget(userId = 1, monthKey = "2026-04", totalGoal = 5000.0)
        budgetDao.insertOrUpdateBudget(budget)
        
        val limit = CategoryLimit(userId = 1, categoryId = 1, monthKey = "2026-04", limitAmount = 1000.0)
        budgetDao.insertOrUpdateCategoryLimit(limit)
        
        val retrieved = budgetDao.getLimitForCategory(1, 1, "2026-04")
        assertEquals(1000.0, retrieved ?: 0.0, 0.01)
    }
}
