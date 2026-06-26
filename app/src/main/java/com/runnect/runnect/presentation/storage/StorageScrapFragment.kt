package com.runnect.runnect.presentation.storage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.runnect.runnect.R
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.event.ScreenRefreshEvent
import com.runnect.runnect.presentation.event.ScreenRefreshEventBus
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.Param
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StorageScrapFragment : Fragment() {
    @Inject
    lateinit var screenRefreshEventBus: ScreenRefreshEventBus

    private val viewModel: StorageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RunnectTheme {
                    val getState by viewModel.myScrapCourseGetState.observeAsState()
                    val scrapState by viewModel.courseScrapState.observeAsState()

                    var courses by remember { mutableStateOf(emptyList<MyScrapCourse>()) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(getState) {
                        when (val current = getState) {
                            is UiStateV2.Success -> {
                                courses = current.data
                                Analytics.logEvent(
                                    EventName.VIEW_STORAGE_SCRAP,
                                    Param.COURSE_COUNT to current.data.size
                                )
                            }

                            is UiStateV2.Failure -> errorMessage = current.msg
                            else -> Unit
                        }
                    }

                    LaunchedEffect(scrapState) {
                        when (val current = scrapState) {
                            is UiStateV2.Success -> {
                                courses = courses.filterNot {
                                    it.publicCourseId.toLong() == current.data.publicCourseId
                                }
                            }

                            is UiStateV2.Failure -> errorMessage = current.msg
                            else -> Unit
                        }
                    }

                    StorageScrapScreen(
                        state = StorageScrapUiState.from(
                            getState = getState,
                            scrapState = scrapState,
                            courses = courses,
                            errorMessage = errorMessage
                        ),
                        onRefresh = { viewModel.getMyScrapCourses() },
                        onScrapItemClick = { course -> navigateToCourseDetail(course) },
                        onHeartClick = { course ->
                            viewModel.postCourseScrap(id = course.publicCourseId, scrapTF = false)
                        },
                        onGoToScrapClick = { navigateToDiscover() },
                        onErrorShown = { errorMessage = null }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Analytics.logEvent(EventName.VIEW_STORAGE_SCRAP)
        viewModel.getMyScrapCourses()
        collectScreenRefreshEvents()
    }

    private fun collectScreenRefreshEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            screenRefreshEventBus.events.collect { event ->
                if (event is ScreenRefreshEvent.RefreshStorageScrap) {
                    viewModel.getMyScrapCourses()
                }
            }
        }
    }

    private fun navigateToCourseDetail(course: MyScrapCourse) {
        val intent = Intent(activity, CourseDetailActivity::class.java).apply {
            putExtra(EXTRA_PUBLIC_COURSE_ID, course.publicCourseId)
            putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_STORAGE_SCRAP)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    private fun navigateToDiscover() {
        val intent = Intent(activity, MainActivity::class.java).apply {
            putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, "fromMyScrap")
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    companion object {
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        const val EXTRA_ROOT_SCREEN = "rootScreen"
    }
}
