package com.example.fitkit.ui.fragments

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitkit.*
import com.example.fitkit.ui.adapter.NewsAdapter
import com.example.fitkit.database.NewsDatabase
import com.example.fitkit.model.News
import com.example.fitkit.model.NewsResponse
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_sports_news.*
import okhttp3.*
import java.io.IOException

class SportsNewsFragment : Fragment() {
    companion object {
        private var sportsNewsFragment: SportsNewsFragment? = null
        //For getting the same unique instance of the fragment on every call;
        //i.e. Singleton pattern
        fun getInstance(): Fragment? {

            if (sportsNewsFragment == null) {
                sportsNewsFragment = SportsNewsFragment()
            }
            return sportsNewsFragment
        }
    }
    private val gson = Gson()
    val articlesList = ArrayList<News>()
    private val url =
        "https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=73ae6891ccd2401f92adb0b8360fc88e"
    val newsAdapter = NewsAdapter(articlesList)
    private val newsDatabase by lazy {
        Room.databaseBuilder(requireContext(), NewsDatabase::class.java, "news.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sports_news, container, false)
    }

    private fun updateAll() {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                client.newCall(request).enqueue(this)
                activity?.runOnUiThread {
                    swiperefresh.isRefreshing = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()
                val newResult = responseBody?.string()
                val newsList = gson.fromJson(newResult, NewsResponse::class.java)
                newsDatabase.getNewsDao().deleteAllNews()
                articlesList.clear()
                activity?.runOnUiThread {
                    newsAdapter.notifyDataSetChanged()
                }
                newsDatabase.getNewsDao().insertMultipleNews(newsList.articles)
                articlesList.addAll(newsList.articles)
                articlesList.reverse()
                activity?.runOnUiThread {
                    newsAdapter.notifyDataSetChanged()
                    swiperefresh.isRefreshing = false
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articlesList.addAll(newsDatabase.getNewsDao().getNews())
        rvNews.layoutManager = LinearLayoutManager(requireContext())
        rvNews.adapter = newsAdapter
        if (articlesList.size < 1) {
            updateAll()
        }
        swiperefresh.setOnRefreshListener {
            updateAll()
        }
    }
}
