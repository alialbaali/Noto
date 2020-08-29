package com.noto.app.library

import android.content.Context
import android.os.Bundle
import com.noto.domain.model.SortMethod
import com.noto.domain.model.SortType
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.noto.R
import com.noto.databinding.DialogSortLibraryBinding

//class LibrarySortDialog(context: Context, private val viewModel: LibraryListViewModel) : BottomSheetDialog(context, R.style.BottomSheetDialog) {
//    //
//    private val binding = DialogSortLibraryBinding.inflate(layoutInflater)
//
//    //
//    init {
//        create()
//        show()
//    }
//
//    //
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//
//        when (viewModel.sortMethod.value) {
//            SortMethod.Alphabetically -> binding.alphabetically.isChecked = true
//            SortMethod.Custom -> binding.custom.isChecked = true
//            SortMethod.CreationDate -> binding.creationDate.isChecked = true
//        }
//
//        when (viewModel.sortType.value) {
//            SortType.ASC -> binding.sortType.isChecked = true
//            SortType.DESC -> binding.sortType.isChecked = false
//        }
//
//        binding.creationDate.setOnClickListener {
//            dismiss()
////            viewModel.updateSortMethod(SortMethod.CreationDate)
//        }
//        binding.alphabetically.setOnClickListener {
//            dismiss()
////            viewModel.updateSortMethod(SortMethod.Alphabetically)
//        }
//        binding.custom.setOnClickListener {
//            dismiss()
////            viewModel.updateSortMethod(SortMethod.Custom)
//        }
//        binding.sortType.setOnClickListener {
//            dismiss()
////            viewModel.updateSortType()
//        }
//        binding.sortType.text = if (binding.sortType.isChecked) {
//            "ASC"
//        } else {
//            "DESC"
//        }
//    }
//
//}