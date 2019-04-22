package com.example.fitkit

import android.arch.persistence.room.*

@Dao
interface NewsDao {

    @Insert
    fun insertNews(note: News): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMultipleNews(news: List<News>): List<Long>

    @Query("DELETE FROM news")
    fun deleteAllNews()

    @Query("SELECT * FROM news")
    fun getNews(): List<News>

}