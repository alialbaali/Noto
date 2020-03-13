package com.noto.note.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.noto.R
import com.noto.databinding.ActivityMainBinding
import com.noto.databinding.FragmentNotebookBinding
import com.noto.note.adapter.NavigateToNote
import com.noto.note.adapter.NotebookRVAdapter
import com.noto.note.model.Note
import com.noto.note.model.NotebookColor

/**
 * A simple [Fragment] subclass.
 */
class NotebookFragment : Fragment(), NavigateToNote {

    // Binding
    private lateinit var binding: FragmentNotebookBinding

    private lateinit var adapter: NotebookRVAdapter

    private var notebookId = 0L

    private var notebookTitle = ""

    private var notebookColor = NotebookColor.GRAY

    private lateinit var exFab: ExtendedFloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotebookBinding.inflate(inflater, container, false)

        exFab = activity?.findViewById(R.id.exFab)!!

        adapter = NotebookRVAdapter(this)


        // Arguments
        requireArguments().let { args ->
            notebookId = args.getLong("notebook_id")
            notebookTitle = args.getString("notebook_title") ?: ""
            notebookColor = args.get("notebook_color") as NotebookColor
        }


        // Binding
        binding.let {

            it.lifecycleOwner = this

            when (notebookColor) {
                NotebookColor.GRAY -> setGray()
                NotebookColor.BLUE -> setBlue()
                NotebookColor.PINK -> setPink()
                NotebookColor.CYAN -> setCyan()
            }

        }

        // Coordinator Layout
        binding.cool.let { cool ->

        }

        // RV
        binding.rv.let { rv ->

            rv.adapter = adapter
            rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            val list = mutableListOf<Note>()
            for (i in 1..100) {
                list.add(
                    Note(
                        noteId = i.toLong(),
                        notebookId = i.toLong(),
                        noteTitle = "Note $i",
                        noteBody = "Note test  $i"
                    )
                )
            }
            adapter.submitList(list)
        }

        // Collapsing Toolbar
        binding.ctb.let { ctb ->
            ctb.title = notebookTitle
        }

        return binding.root
    }


    private fun setGray() {

        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_gray_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )
            exFab.backgroundTintList =
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.notebook_fab_background_gray_color,
                        null
                    )
                )
            exFab.foregroundTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    private fun setBlue() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_blue_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )
            exFab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.notebook_fab_background_blue_color, null))
            exFab.foregroundTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    private fun setPink() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_pink_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )
            exFab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.notebook_fab_background_pink_color, null))
            exFab.foregroundTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    private fun setCyan() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_cyan_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )
            exFab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.notebook_fab_background_cyan_color, null))
            exFab.foregroundTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    override fun navigate(id: Long) {
        this.findNavController()
            .navigate(
                NotebookFragmentDirections.actionNotebookFragmentToNoteFragment(
                    id,
                    notebookId,
                    notebookTitle,
                    notebookColor
                )
            )
    }

}

