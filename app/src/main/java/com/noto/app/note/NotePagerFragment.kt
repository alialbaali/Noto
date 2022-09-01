package com.noto.app.note

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.noto.app.databinding.NotePagerFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NotePagerFragment : Fragment() {

    private val viewModel by viewModel<NotePagerViewModel> { parametersOf(args.folderId, args.noteId, args.selectedNoteIds) }

    private val args by navArgs<NotePagerFragmentArgs>()

    private val windowInsetsController by lazy {
        activity?.window?.decorView?.let { view ->
            ViewCompat.getWindowInsetsController(view)
        }
    }

    private val notificationManager by lazy {
        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var adapter: FragmentStateAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NotePagerFragmentBinding.inflate(inflater, container, false).withBinding {
        setupFadeTransition()
        setupState()
        setupListeners()
    }

    private fun NotePagerFragmentBinding.setupState() {
        abl.bringToFront()

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    tb.title = folder.getTitle(context)
                    tb.setTitleTextColor(color)
                    tb.navigationIcon?.mutate()?.setTint(color)
                    fab.backgroundTintList = color.toColorStateList()
                    fab.setRippleColor(color.withDefaultAlpha().toColorStateList())
                    fabNext.iconTint = color.toColorStateList()
                    fabPrevious.iconTint = color.toColorStateList()
                    fabNext.setTextColor(color)
                    fabPrevious.setTextColor(color)
                }
            }
            .launchIn(lifecycleScope)

        combine(viewModel.noteIds, viewModel.selectedId) { noteIds, selectedId ->
            if (vp.adapter == null && noteIds.isNotEmpty()) {
                adapter = object : FragmentStateAdapter(this@NotePagerFragment) {
                    override fun getItemCount(): Int = noteIds.count()
                    override fun createFragment(position: Int): Fragment = NoteReadingModeFragment().apply {
                        arguments = bundleOf(
                            Constants.FolderId to args.folderId,
                            Constants.NoteId to noteIds[position],
                        )
                    }
                }.also(vp::setAdapter)
            }
            val selectedIndex = noteIds.indexOf(selectedId)
            val isPreviousEnabled = noteIds.getOrNull(selectedIndex - 1) != null
            val isNextEnabled = noteIds.getOrNull(selectedIndex + 1) != null
            vp.currentItem = selectedIndex
            if (isPreviousEnabled) fabPrevious.enable() else fabPrevious.disable()
            if (isNextEnabled) fabNext.enable() else fabNext.disable()
        }.launchIn(lifecycleScope)

        viewModel.isDoNotDisturb
            .onEach { isDoNotDisturb ->
                if (isDoNotDisturb && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.isScreenOn
            .onEach { isScreenOn ->
                if (isScreenOn) {
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.isFullScreen
            .onEach { isFullScreen ->
                if (isFullScreen) {
                    windowInsetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
                } else {
                    windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
                }
            }
            .launchIn(lifecycleScope)

        viewModel.isDimScreen
            .onEach { isDimScreen ->
                val layoutParams = activity?.window?.attributes
                activity?.window?.attributes = if (isDimScreen) {
                    layoutParams?.apply { screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF }
                } else {
                    layoutParams?.apply { screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE }
                }
            }
            .launchIn(lifecycleScope)

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Int>(Constants.ScrollPosition)
            ?.observe(viewLifecycleOwner) { scrollPosition -> abl.isLifted = scrollPosition != 0 }
    }

    private fun NotePagerFragmentBinding.setupListeners() {
        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }

        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        tb.setOnClickListener {
            navController?.currentBackStackEntry?.savedStateHandle?.set(Constants.ClickListener, 0)
        }

        vp.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.selectIdByIndex(position)
                }
            }
        )

        fab.setOnClickListener {
            val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle
            val scrollPosition = savedStateHandle?.get<Int>(Constants.ScrollPosition) ?: 0
            val isTitleVisible = savedStateHandle?.get<Boolean>(Constants.IsTitleVisible) ?: true
            val isBodyVisible = savedStateHandle?.get<Boolean>(Constants.IsBodyVisible) ?: true
            navController?.navigateSafely(
                NotePagerFragmentDirections.actionNotePagerFragmentToNoteFragment(
                    folderId = args.folderId,
                    noteId = viewModel.selectedId.value,
                    scrollPosition = scrollPosition,
                    isTitleVisible = isTitleVisible,
                    isBodyVisible = isBodyVisible,
                )
            )
        }

        fabNext.setOnClickListener {
            viewModel.selectNextId()
        }

        fabPrevious.setOnClickListener {
            viewModel.selectPreviousId()
        }

        fabNext.setOnLongClickListener {
            adapter?.createFragment(viewModel.noteIds.value.lastIndex)
            viewModel.selectLastId()
            true
        }

        fabPrevious.setOnLongClickListener {
            adapter?.createFragment(0)
            viewModel.selectFirstId()
            true
        }
    }

    override fun onDetach() {
        super.onDetach()
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity?.window?.attributes = activity?.window?.attributes?.apply { screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted)
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
    }
}