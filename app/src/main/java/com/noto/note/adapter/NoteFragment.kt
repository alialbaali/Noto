package com.noto.note.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.noto.R
import com.noto.databinding.FragmentNoteBinding

/**
 * A simple [Fragment] subclass.
 */
class NoteFragment : Fragment() {

    // Binding
    private lateinit var binding: FragmentNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)


        return binding.root
    }

}
