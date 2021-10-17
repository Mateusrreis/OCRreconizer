package com.example.ocrreconizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.ocrreconizer.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {

    val args: SecondFragmentArgs by navArgs()
    private var binding: FragmentSecondBinding? = null
    private var adapter: ArrayAdapter<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvStore?.text = args.itemList[0]
        setupAdapter(args.itemList.copyOfRange(1, args.itemList.size))
    }

    private fun setupAdapter(itens: Array<String>){
        adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, itens)
        binding?.itemList?.adapter = adapter
    }
}