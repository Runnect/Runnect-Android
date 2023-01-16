package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.databinding.ActivityStorageBinding
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.storage.adapter.StorageAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import timber.log.Timber

class StorageActivity : com.runnect.runnect.binding.BindingActivity<ActivityStorageBinding>(R.layout.activity_storage) {

    val viewModel: StorageViewModel by viewModels()
    private val storageAdapter = StorageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.model = viewModel
//        binding.lifecycleOwner = this

        val recyclerviewStorage = binding.recyclerviewCourseList
        recyclerviewStorage.adapter = storageAdapter

        gridLayoutItemDeco()
        getCourse()
        toDrawCourseBtn()
        issueHandling()


    }

    private fun gridLayoutItemDeco(){
        binding.recyclerviewCourseList.apply {
            addItemDecoration(GridSpacingItemDecoration(
                this@StorageActivity, 2,6, 18
            ))
        }
    }

    private fun issueHandling() {
        viewModel.errorMessage.observe(this) {
            Timber.tag(ContentValues.TAG).d("fail")
            binding.recyclerviewCourseList.isVisible = false
            binding.ivStorage.isVisible = true
            binding.tvIntroToDraw.isVisible = true
            binding.btnStorageDraw.isVisible = false
        }
        viewModel.getResult.observe(this) {
            Timber.tag(ContentValues.TAG).d(it.message)
            binding.ivStorage.isVisible = false
            binding.tvIntroToDraw.isVisible = false
            binding.btnStorageDraw.isVisible = false
            binding.recyclerviewCourseList.isVisible = true

            storageAdapter.submitList(it.data.courses)
            Timber.tag(ContentValues.TAG).d("it.data.courses : ${it.data.courses}")

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