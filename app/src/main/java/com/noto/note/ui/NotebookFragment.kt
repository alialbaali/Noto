package com.noto.note.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.noto.R
import com.noto.databinding.FragmentNotebookBinding
import com.noto.network.Repos
import com.noto.note.adapter.NavigateToNote
import com.noto.note.adapter.NotebookRVAdapter
import com.noto.note.model.NotebookColor
import com.noto.note.viewModel.NotebookViewModel
import com.noto.note.viewModel.NotebookViewModelFactory

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

    private lateinit var exFabNewNote: ExtendedFloatingActionButton

    private lateinit var exFabNewNotebook: ExtendedFloatingActionButton

    private lateinit var fabs: List<ExtendedFloatingActionButton>

    private val viewModel by viewModels<NotebookViewModel> {
        NotebookViewModelFactory(Repos.notebookRepository, Repos.noteRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotebookBinding.inflate(inflater, container, false)

        exFab = activity?.findViewById(R.id.exFab)!!

        exFabNewNote = activity?.findViewById(R.id.exFab_new_note)!!

        fabs = listOf(exFab, exFabNewNote, exFabNewNotebook)

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
                resources.getColor(R.color.gray_primary, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.gray_primary,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.gray_primary,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.gray_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.gray_primary,
                    null
                )
            )
            fabs.forEach { fab ->
                fab.backgroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(
                            R.color.gray_primary_dark,
                            null
                        )
                    )
            }
        }
    }

    private fun setBlue() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.blue_primary, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.blue_primary,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.blue_primary,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.blue_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.blue_primary,
                    null
                )
            )
            fabs.forEach { fab ->
                fab.backgroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(
                            R.color.blue_primary_dark,
                            null
                        )
                    )
            }
        }
    }

    private fun setPink() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.pink_primary, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.pink_primary,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.pink_primary,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.pink_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.pink_primary,
                    null
                )
            )
            fabs.forEach { fab ->
                fab.backgroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(
                            R.color.pink_primary_dark,
                            null
                        )
                    )
            }
        }
    }

    private fun setCyan() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.cyan_primary, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.cyan_primary,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.cyan_primary,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.cyan_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.cyan_primary,
                    null
                )
            )
            fabs.forEach { fab ->
                fab.backgroundTintList =
                    ColorStateList.valueOf(
                        resources.getColor(
                            R.color.cyan_primary_dark,
                            null
                        )
                    )
            }
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

