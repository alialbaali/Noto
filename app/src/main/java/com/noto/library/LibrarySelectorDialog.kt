package com.noto.library

//import android.content.Context
//import android.os.Bundle
//import android.view.View
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.noto.R
//import com.noto.databinding.DialogSelectorLibraryBinding
//import com.noto.domain.model.Library
//import com.noto.noto.NotoViewModel
//
//class LibrarySelectorDialog(
//    context: Context,
//    private val libraryListViewModel: LibraryListViewModel,
//    private val notoViewModel: NotoViewModel,
//    private val notoId: Long
//) :
//    BottomSheetDialog(context, R.style.BottomSheetDialog), LibraryItemClickListener {
//
//    private val binding = DialogSelectorLibraryBinding.inflate(layoutInflater)
//
//    private val rvAdapter = LibraryListRVAdapter(libraryListViewModel, this)
//
//    private val rvLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//    init {
//        create()
//        show()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//
//        binding.rv.adapter = rvAdapter
//        binding.rv.layoutManager = rvLayoutManager
//
//        val libraries = libraryListViewModel.libraries.value
//
//        if (libraries.isNullOrEmpty()) {
//            binding.rv.visibility = View.GONE
//            binding.tvLibrary.visibility = View.GONE
//            binding.tvPlaceHolder.visibility = View.VISIBLE
//            binding.ivPlaceHolder.visibility = View.VISIBLE
//        } else {
//            rvAdapter.submitList(libraries)
//        }
//
//    }
//
//    override fun onClick(library: Library) {
//        notoViewModel.updateNoto(library.libraryId, notoId)
//        cancel()
//    }
//}