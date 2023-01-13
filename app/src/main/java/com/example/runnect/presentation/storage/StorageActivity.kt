package com.example.runnect.presentation.storage

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityStorageBinding
import com.example.runnect.presentation.search.SearchActivity
import com.example.runnect.presentation.storage.adapter.StorageAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
@AndroidEntryPoint
class StorageActivity : BindingActivity<ActivityStorageBinding>(R.layout.activity_storage) {

    val viewModel: StorageViewModel by viewModels()
    private val storageAdapter = StorageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.model = viewModel
//        binding.lifecycleOwner = this

        val recyclerviewStorage = binding.recyclerviewCourseList
        recyclerviewStorage.adapter = storageAdapter

        getCourse()
        toDrawCourseBtn()
        issueHandling()


    }

    private fun issueHandling() {
        viewModel.errorMessage.observe(this) {
            Timber.tag(ContentValues.TAG).d("fail")
//            binding.recyclerviewCourseList.isVisible = false
//            binding.ivStorage.isVisible = true
//            binding.tvIntroToDraw.isVisible = true
//            binding.btnStorageDraw.isVisible = false
        }
        viewModel.getResult.observe(this) {
            Timber.tag(ContentValues.TAG).d(it.message)
//            binding.ivStorage.isVisible = false
//            binding.tvIntroToDraw.isVisible = false
//            binding.btnStorageDraw.isVisible = false
//            binding.recyclerviewCourseList.isVisible = true

        }
    }

    private fun toDrawCourseBtn() {
        binding.btnStorageDraw.setOnClickListener {
            val intent = Intent(this@StorageActivity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCourse() {
        viewModel.getCourseList()
    }
}