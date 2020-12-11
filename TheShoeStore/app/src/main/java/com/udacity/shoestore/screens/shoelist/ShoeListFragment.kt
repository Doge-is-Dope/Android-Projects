package com.udacity.shoestore.screens.shoelist

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentShoeListBinding
import com.udacity.shoestore.models.Shoe


class ShoeListFragment : Fragment() {

    private lateinit var viewModel: ShoeListViewModel
    private lateinit var binding: FragmentShoeListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_list, container, false)

        binding.fabAdd.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                ShoeListFragmentDirections.actionShoeListFragmentToShoeDetailFragment()
            )
        )

        viewModel = ViewModelProvider(requireActivity()).get(ShoeListViewModel::class.java)

        viewModel.shoeList.observe(viewLifecycleOwner, Observer { newList ->
            addView(newList)
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun addView(shoes: MutableList<Shoe>) {
        binding.linear.removeAllViews()
        for (shoe in shoes) {
            val textView = TextView(context)
            textView.text = getString(R.string.shoe_format_string, shoe.name, shoe.size, shoe.company, shoe.description)
            binding.linear.addView(textView)
        }
    }

}