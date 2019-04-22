package com.example.fitkit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitkit.News
import com.example.fitkit.NewsAdapter
import com.example.fitkit.NewsResponse
import com.example.fitkit.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_sports_news.*
import okhttp3.*
import java.io.IOException


class SportsNewsFragment : Fragment() {
    private val gson = Gson()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sports_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url =
            "https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=73ae6891ccd2401f92adb0b8360fc88e"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                client.newCall(request).enqueue(this)
            }
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()
                val newResult = responseBody?.string()
                var newsList = gson.fromJson(newResult, NewsResponse::class.java)
                activity?.runOnUiThread {
                    val newsAdapter = NewsAdapter(newsList)
                    rvNews.adapter = newsAdapter
                    newsAdapter.notifyDataSetChanged()
                }
            }
        })
        rvNews.layoutManager = LinearLayoutManager(requireContext())
    }
}
