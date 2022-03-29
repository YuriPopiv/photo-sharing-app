package com.popivyurii.photosharing.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Created by Yurii Popiv on 28.03.2022.
 */
class GalleryViewModel() : ViewModel() {

    private val _localPhotos = MutableLiveData<List<PhotoModel>>()
    val localPhotos: LiveData<List<PhotoModel>>
        get() = _localPhotos


    fun loadPhotos(photos: List<PhotoModel>){

        _localPhotos.value = photos
    }


}
@Suppress("UNCHECKED_CAST")
class GalleryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return GalleryViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}