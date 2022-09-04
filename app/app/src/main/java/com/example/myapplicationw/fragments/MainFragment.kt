package com.example.myapplicationw.fragments

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.myapplicationw.Service.ServiceBuilder
import com.example.myapplicationw.Service.WeatherGet
import com.example.myapplicationw.adapter.VpAdapter
import com.example.myapplicationw.data.Data
import com.example.myapplicationw.data.Forecastday
import com.example.myapplicationw.data.MainViewModel
import com.example.myapplicationw.data.WeatherModel
import com.example.myapplicationw.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainFragment : Fragment() {
    private val fList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf(
        "Hours",
        "Days"
    )
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionListener()
        checkPermission()
        loadDataFromService()
//        init()
        updateCurrentCard()


    }

    private fun init() = with(binding) {
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, pos ->
            tab.text = tList[pos]
        }.attach()
    }

    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            val maxMinTemp = "${it.maxTemp}Cº/${it.minTemp}Cº"
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp
            tvCondition.text = it.condition
            tvCondition.text = it.condition
            tvMaxMin.text = maxMinTemp
            Picasso.get().load("https:" + it.imageUrl).into(imWeather)
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun loadDataFromService() {
        val weatherService = ServiceBuilder.buildService(WeatherGet::class.java)

        val requests = weatherService.getLocation()

        requests.enqueue(object : Callback<Data> {

            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful) {
                    val items = response.body()

                    val a = parseDay(items)
                    parseWeatherData(items, a[0])

//                    Log.d("MyLog", currentDataBase.imageUrl)

                } else {
                    Log.d("Error", this.toString())
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.d("ErrorOnServer", "500")
            }
        })
    }

    private fun parseDay(result: Data?): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray: ArrayList<Forecastday> =
            result!!.forecast.forecastday as ArrayList<Forecastday>
        for (i in daysArray.indices) {
            val day = daysArray[i]
            val item = WeatherModel(
                result.location.name,
                day.date,
                day.day.condition.text,
                "",
                day.day.maxtemp_c.toString(),
                day.day.mintemp_c.toString(),
                day.day.condition.icon, day.hour.toString()
            )
            list.add(item)
        }
        return list
    }

    private fun parseWeatherData(result: Data?, weatherItem: WeatherModel) {
        val item = WeatherModel(
            result!!.location.name,
            result.current.last_updated,
            result.current.condition.text,
            result.current.temp_c.toString(),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            result.current.condition.icon,
            weatherItem.hours
        )
        model.liveDataCurrent.value = item
        Log.d("DayList", item.minTemp)
        Log.d("DayList", item.maxTemp)
        Log.d("DayList", item.hours)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MainFragment()

    }
}