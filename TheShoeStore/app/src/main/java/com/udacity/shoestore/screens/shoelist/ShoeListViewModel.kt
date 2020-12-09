package com.udacity.shoestore.screens.shoelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe

class ShoeListViewModel : ViewModel() {

    private val _shoeList = MutableLiveData<MutableList<Shoe>>()
    val shoeList: LiveData<MutableList<Shoe>>
        get() = _shoeList


    init {
        _shoeList.value =
            mutableListOf(
                Shoe(
                    "Air Max",
                    11.5,
                    "Apple",
                    "Incredible high-fidelity audio"
                ),
                Shoe(
                    "Air Pro",
                    10.5,
                    "Apple",
                    "Breakthrough Listening Experience"
                )
            )
    }

    fun onSave(shoe: Shoe) {
        _shoeList.value?.add(shoe)
    }
}