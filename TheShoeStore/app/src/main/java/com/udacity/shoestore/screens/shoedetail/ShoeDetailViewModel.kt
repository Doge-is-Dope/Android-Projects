package com.udacity.shoestore.screens.shoedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe

class ShoeDetailViewModel : ViewModel() {

    val shoeName = MutableLiveData<String>()

    val shoeCompany = MutableLiveData<String>()

    val shoeSize = MutableLiveData<String>()

    val shoeDesc = MutableLiveData<String>()


    init {
        // avoid setting nulls during initialization
        resetFields()
    }

    override fun onCleared() {
        super.onCleared()
        resetFields()
    }

    fun getShoeObject(): Shoe {
        return Shoe(
            name = shoeName.value.toString(),
            size = if (shoeSize.value.toString().isEmpty()) 0.0 else shoeSize.value.toString().toDouble(),
            company = shoeCompany.value.toString(),
            description = shoeDesc.value.toString()
        )
    }

    private fun resetFields() {
        shoeName.value = ""
        shoeCompany.value = ""
        shoeSize.value = ""
        shoeDesc.value = ""
    }
}