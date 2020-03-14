package com.noto.note.ui

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.noto.R
import com.noto.database.AppDatabase
import com.noto.databinding.FragmentNotebookListBinding
import com.noto.databinding.NotebookDialogBinding
import com.noto.network.DAOs
import com.noto.network.Repos
import com.noto.note.adapter.NavigateToNotebook
import com.noto.note.adapter.NotebookListRVAdapter
import com.noto.note.model.Notebook
import com.noto.note.model.NotebookColor
import com.noto.note.viewModel.NotebookListViewModel
import com.noto.note.viewModel.NotebookListViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class NotebookListFragment : Fragment(), NavigateToNotebook {

    // Binding
    private lateinit var binding: FragmentNotebookListBinding

    // Notebook List RV Adapter
    private lateinit var adapter: NotebookListRVAdapter

    private lateinit var viewModel: NotebookListViewModel

    private lateinit var exFab: ExtendedFloatingActionButton

    private lateinit var dialogBinding: NotebookDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel =
            ViewModelProviders.of(this, NotebookListViewModelFactory(Repos.notebookRepository))
                .get(NotebookListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotebookListBinding.inflate(inflater, container, false)

        // Binding
        binding.let {

            it.lifecycleOwner = this

        }

        activity?.window?.statusBarColor = resources.getColor(R.color.bottom_nav_color, null)

        // Collapse Toolbar
        binding.ctb.let { ctb ->

            ctb.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_bold))

            ctb.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_medium))

        }

        // RV
        binding.rv.let { rv ->

            // RV Adapter
            adapter = NotebookListRVAdapter(this)
            rv.adapter = adapter

            // RV Layout Manger
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            viewModel.notebooks.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                }
            })
        }

        exFab = activity?.findViewById(R.id.exFab) as ExtendedFloatingActionButton

        exFab.setOnClickListener {

            dialogBinding = NotebookDialogBinding.inflate(layoutInflater, container, false)

            dialogBinding.viewModel = viewModel

            viewModel.notebook.value = Notebook()

            val dialog = AlertDialog.Builder(context).let {
                it.setView(dialogBinding.root)
                it.create()
                it.show()
            }.apply {
                this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            dialogBinding.rbtnBlue.setOnClickListener {

//                dialogBinding.rbtnBlue.background = resources.getDrawable(R.drawable.dialog_rbtn_blue_state, null)

                dialogBinding.et.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.blue_primary_dark, null))

                dialogBinding.root.background =
                    resources.getDrawable(R.drawable.dialog_background_blue_drawable, null)

                dialogBinding.cancelBtn.setTextColor(
                    resources.getColor(
                        R.color.blue_primary_dark,
                        null
                    )
                )
                viewModel.notebook.value?.notebookColor = NotebookColor.BLUE

                dialogBinding.createBtn.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.blue_primary_dark, null))
            }

            dialogBinding.rbtnPink.setOnClickListener {

                dialogBinding.et.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.pink_primary_dark, null))
//                dialogBinding.rbtnPink.background = resources.getDrawable(R.drawable.dialog_rbtn_pink_state, null)

                dialogBinding.root.background =
                    resources.getDrawable(R.drawable.dialog_background_pink_drawable, null)

                dialogBinding.cancelBtn.setTextColor(
                    resources.getColor(
                        R.color.pink_primary_dark,
                        null
                    )
                )

                viewModel.notebook.value?.notebookColor = NotebookColor.PINK

                dialogBinding.createBtn.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.pink_primary_dark, null))
            }

            dialogBinding.rbtnCyan.setOnClickListener {

                dialogBinding.et.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.cyan_primary_dark, null))
//                dialogBinding.rbtnCyan.background = resources.getDrawable(R.drawable.dialog_rbtn_cyan_state, null)

                dialogBinding.root.background =
                    resources.getDrawable(R.drawable.dialog_background_cyan_drawable, null)

                dialogBinding.cancelBtn.setTextColor(
                    resources.getColor(
                        R.color.cyan_primary_dark,
                        null
                    )
                )

                viewModel.notebook.value?.notebookColor = NotebookColor.CYAN

                dialogBinding.createBtn.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.cyan_primary_dark, null))
            }

            dialogBinding.rbtnGray.setOnClickListener {

                dialogBinding.et.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.gray_primary_dark, null))

//                dialogBinding.rbtnGray.background = resources.getDrawable(R.drawable.dialog_rbtn_gray_state, null)

                dialogBinding.root.background =
                    resources.getDrawable(R.drawable.dialog_background_gray_drawable, null)

                dialogBinding.cancelBtn.setTextColor(
                    resources.getColor(
                        R.color.gray_primary_dark,
                        null
                    )
                )

                viewModel.notebook.value?.notebookColor = NotebookColor.GRAY

                dialogBinding.createBtn.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.gray_primary_dark, null))
            }

            dialogBinding.createBtn.setOnClickListener {

                if (viewModel.notebook.value?.notebookTitle?.isBlank()!!) {
                    Snackbar.make(
                        binding.root,
                        "Title can't be empty!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    dialog.dismiss()
                    viewModel.insertNote()
                }
            }

            dialogBinding.cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        return binding.root
    }

    override fun navigate(notebook: Notebook) {
        this.findNavController().navigate(
            NotebookListFragmentDirections.actionNotebookListFragmentToNotebookFragment(
                notebook.notebookId,
                notebook.notebookTitle,
                notebook.notebookColor
            )
        )
    }
}
