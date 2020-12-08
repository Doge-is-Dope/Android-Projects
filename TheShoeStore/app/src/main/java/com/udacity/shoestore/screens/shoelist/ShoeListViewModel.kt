package com.udacity.shoestore.screens.shoelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoeListViewModel : ViewModel() {

    private val _shoeList = MutableLiveData<List<String>>()
    val shoeList: LiveData<List<String>>
        get() = _shoeList
}