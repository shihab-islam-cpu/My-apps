package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExpenseRecord
import com.example.data.model.IncomeRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // Incomes
    @Query("SELECT * FROM income_records ORDER BY createdAt DESC")
    fun getAllIncomes(): Flow<List<IncomeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomes(incomes: List<IncomeRecord>)

    @Update
    suspend fun updateIncome(income: IncomeRecord)

    @Delete
    suspend fun deleteIncome(income: IncomeRecord)

    // Expenses
    @Query("SELECT * FROM expense_records ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseRecord>)

    @Update
    suspend fun updateExpense(expense: ExpenseRecord)

    @Delete
    suspend fun deleteExpense(expense: ExpenseRecord)
}
