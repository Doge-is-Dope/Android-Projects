package com.udacity.shoestore.screens.shoedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentShoeDetailBinding
import com.udacity.shoestore.databinding.FragmentShoeListBinding
import com.udacity.shoestore.models.Shoe
import com.udacity.shoestore.screens.shoelist.ShoeListFragmentDirections
import com.udacity.shoestore.screens.shoelist.ShoeListViewModel


class ShoeDetailFragment : Fragment() {

    private lateinit var viewModel: ShoeListViewModel
    private lateinit var binding: FragmentShoeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_detail, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(ShoeListViewModel::class.java)

        binding.btnSave.setOnClickListener {
            val name = binding.editName.text.toString()
            val size =
                if (binding.editSize.text.toString().isEmpty())
                    "0"
                else
                    binding.editSize.text.toString()
            val company = binding.editCompany.text.toString()
            val description = binding.editDescription.text.toString()
            val newShoe = Shoe(name, size.toDouble(), company, description)
            viewModel.onSave(newShoe)

            val action =
                ShoeDetailFragmentDirections
                    .actionShoeDetailFragmentToShoeListFragment()
            findNavController(this).navigate(action)
        }

        binding.btnCancel.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                ShoeDetailFragmentDirections.actionShoeDetailFragmentToShoeListFragment()
            )
        )

        return binding.root
    }
}