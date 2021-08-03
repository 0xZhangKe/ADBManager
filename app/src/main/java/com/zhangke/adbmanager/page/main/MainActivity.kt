package com.zhangke.adbmanager.page.main

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zhangke.adbmanager.R
import com.zhangke.adbmanager.databinding.ActivityMainBinding
import com.zhangke.adbmanager.util.AppViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(viewModelStore, AppViewModelFactory()).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.logText.movementMethod = ScrollingMovementMethod.getInstance()
        binding.lifecycleOwner = this
        binding.vm = viewModel
        viewModel.init()
    }
}