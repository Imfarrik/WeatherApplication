package com.example.myapplicationw

import com.example.myapplicationw.Service.ServiceBuilder
import com.example.myapplicationw.Service.WeatherGet
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplicationw.data.WeatherModel
import com.example.myapplicationw.databinding.ActivityMainBinding
import com.example.myapplicationw.fragments.MainFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindings = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, MainFragment.newInstance())
            .commit()

    }

}