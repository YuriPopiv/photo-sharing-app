package com.popivyurii.photosharing.gallery

/**
 * Created by Yurii Popiv on 28.03.2022.
 */
class ImageFolder {
    var path: String? = null
    var folderName: String? = null
    var numberOfPics = 0
    var firstPic: String? = null

    fun addpics() {
        numberOfPics++
    }
}