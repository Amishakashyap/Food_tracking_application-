package com.example.foodtracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface FoodDao {
    @Query("SELECT * FROM food WHERE id = :id")
    suspend fun getById(id: Long): Food?

    @Query("SELECT * FROM food WHERE name_normalized LIKE :q LIMIT 50")
    suspend fun searchLike(q: String): List<Food>

    @RawQuery
    suspend fun searchFts(query: SupportSQLiteQuery): List<Food>

    suspend fun searchFts(term: String): List<Food> {
        val sql = """
            SELECT f.* FROM food f
            JOIN food_fts ft ON f.id = ft.rowid
            WHERE ft MATCH ?
            LIMIT 50
        """.trimIndent()
        val q = SimpleSQLiteQuery(sql, arrayOf("${term.trim()}*"))
        return searchFts(q)
    }
}

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: Entry): Long

    @Query("SELECT * FROM entry WHERE date = :date")
    suspend fun getByDate(date: String): List<Entry>

    @Query("DELETE FROM entry WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getById(id: Long): User?
}

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    suspend fun getByUserId(userId: Long): UserProfile?
}
