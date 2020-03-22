package com.noto.note.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.R
import com.noto.databinding.DialogNotebookBinding
import com.noto.databinding.FragmentNotebookBinding
import com.noto.network.Repos
import com.noto.note.adapter.NavigateToNote
import com.noto.note.adapter.NotebookRVAdapter
import com.noto.note.model.Notebook
import com.noto.note.model.NotebookColor
import com.noto.note.viewModel.NotebookViewModel
import com.noto.note.viewModel.NotebookViewModelFactory
import timber.log.Timber

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

    private val viewModel by viewModels<NotebookViewModel> {
        NotebookViewModelFactory(Repos.notebookRepository, Repos.noteRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotebookBinding.inflate(inflater, container, false)

        // Arguments
        arguments?.let { args ->
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

        // RV
        binding.rv.let { rv ->

            viewModel.getNotes(notebookId)

            adapter = NotebookRVAdapter(this)
            rv.adapter = adapter

            rv.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            viewModel.notes.observe(viewLifecycleOwner, Observer {
                it?.let {
                    if (it.isEmpty()) {
                        rv.visibility = View.GONE
                        binding.emptyNotebook.visibility = View.VISIBLE
                    } else {
                        rv.visibility = View.VISIBLE
                        binding.emptyNotebook.visibility = View.GONE
                        adapter.submitList(it)
                    }
                }
            })
        }

        // Collapsing Toolbar
        binding.ctb.let { ctb ->

            ctb.title = notebookTitle

            ctb.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_bold))

            ctb.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_medium))
        }

        binding.fab.setOnClickListener {
            this.findNavController().navigate(
                NotebookFragmentDirections.actionNotebookFragmentToNoteFragment(
                    0L,
                    notebookId,
                    notebookTitle,
                    notebookColor
                )
            )
        }

        binding.fab.imageTintList = ColorStateList.valueOf(Color.WHITE)

        binding.tb.let { tb ->

            tb.setNavigationOnClickListener {
                this.findNavController().navigateUp()
            }

            tb.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.style -> {
                        NotebookDialog(context!!).apply {
                            this.notebook = Notebook(notebookId, notebookTitle, notebookColor)
                            dialogBinding.createBtn.text = resources.getString(R.string.update)
                            dialogBinding.tv.text = resources.getString(R.string.update_notebook)
                            dialogBinding.createBtn.setOnClickListener {
                                when {
                                    this.notebook.notebookTitle.isBlank() -> {

                                        this.dialogBinding.til.error =
                                            "Notebook with the same title already exists!"
                                        this.dialogBinding.til.counterTextColor =
                                            ColorStateList.valueOf(Color.RED)

                                    }
//                                    viewModel.getNotebooks().any { it.notebookTitle == this.notebook.notebookTitle } -> {
//
//                                        this.dialogBinding.til.error =
//                                            "Notebook with the same title already exists!"
//                                        this.dialogBinding.til.counterTextColor =
//                                            ColorStateList.valueOf(Color.RED)
//
//                                    }
                                    else -> {
                                        viewModel.updateNotebook(this.notebook)
                                        this.dismiss()
                                    }
                                }
                            }
                            create()
                            show()
                        }
                        true
                    }

                    R.id.delete_notebook -> {
                        viewModel.deleteNotebook(notebookId)
                        this.findNavController().navigateUp()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
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

            it.ctb.contentScrim =
                resources.getDrawable(R.drawable.notebook_item_background_gray_drawable, null)

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.gray_primary_dark, null))
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

            it.ctb.contentScrim =
                resources.getDrawable(R.drawable.notebook_item_background_blue_drawable, null)

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.blue_primary_dark, null))
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

            it.ctb.contentScrim =
                resources.getDrawable(R.drawable.notebook_item_background_pink_drawable, null)

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.pink_primary_dark, null))
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

            it.ctb.contentScrim =
                resources.getDrawable(R.drawable.notebook_item_background_cyan_drawable, null)

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.cyan_primary_dark, null))
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

