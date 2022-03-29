package com.popivyurii.photosharing.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.popivyurii.photosharing.R
import com.popivyurii.photosharing.theme.PhotoSharingTheme
import java.io.File
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2

/**
 * Created by Yurii Popiv on 28.03.2022.
 */
class GalleryFragment  : Fragment() {

    private lateinit var storage: FirebaseStorage
    private lateinit var pictures: java.util.ArrayList<PhotoModel>

    private val viewModel: GalleryViewModel by viewModels { GalleryViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
        )else {
            val pass = getPicturePaths()
            pictures = ArrayList()
            pass.forEach {
                getAllImagesByFolder(it.path).let { it1 -> pictures.addAll(it1) }

            }

            viewModel.loadPhotos(pictures.toList())

        }

        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.gallery_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent {
                PhotoSharingTheme{
                    MainScreen(
                        onExploreItemClicked = { },
                        onDateSelectionClicked = { }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadFilesToFirebaseStorage(pictures)
    }
    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private fun uploadFilesToFirebaseStorage(pictures: ArrayList<PhotoModel>){
        //storage = Firebase.storage("gs://photo-sharing-6b5c8.appspot.com")
        storage = Firebase.storage
        var storageRef = storage.reference
        pictures.forEach {
            val file = Uri.fromFile(File(it.path))

            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }
            val uploadTask = storageRef.child("images/${file.lastPathSegment}").putFile(file, metadata)

            uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
                val progress = (100.0 * bytesTransferred) / totalByteCount
                Log.d(TAG, "Upload is $progress% done")
            }.addOnPausedListener {
                Log.d(TAG, "Upload is paused")
            }.addOnFailureListener {
                // Handle unsuccessful uploads
                Log.d(TAG, "Upload failed: " + it.message)
                it.printStackTrace()
            }.addOnSuccessListener {
                // Handle successful uploads on complete
                Log.d(TAG, "Upload successful")
            }
        }


    }

    private fun getPicturePaths(): ArrayList<ImageFolder> {
        val picFolders: ArrayList<ImageFolder> = ArrayList()
        val picPaths = ArrayList<String>()
        val allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID
        )
        val cursor: Cursor =
            requireActivity().contentResolver.query(allImagesuri, projection, null, null, null)!!
        try {
            cursor.moveToFirst()
            do {
                val folds = ImageFolder()
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val folder =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val datapath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                //String folderpaths =  datapath.replace(name,"");
                var folderpaths = datapath.substring(0, datapath.lastIndexOf("$folder/"))
                folderpaths = "$folderpaths$folder/"
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths)
                    folds.path = folderpaths
                    folds.folderName = folder
                    folds.firstPic = datapath //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics()
                    picFolders.add(folds)
                } else {
                    for (i in picFolders.indices) {
                        if (picFolders[i].path.equals(folderpaths)) {
                            picFolders[i].firstPic = datapath
                            picFolders[i].addpics()
                        }
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        for (i in picFolders.indices) {
            Log.d("picture folders",
                picFolders[i].folderName
                    .toString() + " and path = " + picFolders[i].path + " " + picFolders[i].numberOfPics
            )
        }

        //reverse order ArrayList
        /* ArrayList<imageFolder> reverseFolders = new ArrayList<>();

        for(int i = picFolders.size()-1;i > reverseFolders.size()-1;i--){
            reverseFolders.add(picFolders.get(i));
        }*/
        return picFolders
    }

    fun getAllImagesByFolder(path: String?): java.util.ArrayList<PhotoModel> {
        var images = java.util.ArrayList<PhotoModel>()
        val allVideosuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )
        val cursor: Cursor = requireActivity().getContentResolver().query(
            allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", arrayOf(
                "%$path%"
            ), null
        )!!
        try {
            cursor.moveToFirst()
            do {
                val pic = PhotoModel(
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                )
                images.add(pic)
            } while (cursor.moveToNext())
            cursor.close()
            val reSelection = java.util.ArrayList<PhotoModel>()
            for (i in images.size - 1 downTo -1 + 1) {
                reSelection.add(images[i])
            }
            images = reSelection
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return images
    }

    companion object {
        private const val TAG = "GalleryFragment"
    }

//    private fun openFilePicker(allowMultiple: Boolean) {
//        // BEGIN_INCLUDE (use_open_document_intent)
//        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//
//        // Filter to only show results that can be "opened", such as a file (as opposed to a list
//        // of contacts or timezones)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//
//        // Filter to show only images, using the image MIME data type.
//        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
//        // To search for all documents available via installed storage providers, it would be
//        // "*/*".
//        val fileType = "image/*"
//        intent.type = fileType
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
//        startActivityForResult(
//            intent,
//            ACTIVITY_REQUEST_CODE_OPEN_FILE_PICKER
//        )
//    }

//    fun requestPermissionAction(requestCode: Int) {
//        var permissions: Array<String?>? = null
//        permissions =
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            val permissionsToRequest: MutableList<String?> = ArrayList()
//            for (permission in permissions) {
//                if (ContextCompat.checkSelfPermission(
//                        requireActivity(),
//                        permission!!
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    permissionsToRequest.add(permission)
//                }
//            }
//            if (!permissionsToRequest.isEmpty()) {
//                // We don't have permissions, request them.
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    permissionsToRequest.toTypedArray(),
//                    requestCode
//                )
//            } else {
//                // We have permission, go ahead and execute!
//                openFilePicker(true)
//            }
//        } else {
//            // We have permission, go ahead and execute!
//            openFilePicker(true)
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        // If request is cancelled, the result arrays are empty.
//        if (grantResults.size > 0
//            && grantResults[0] == PackageManager.PERMISSION_GRANTED
//        ) {
//            // permission was granted, yay!
//            openFilePicker(true)
//        } else {
//            // permission denied, boo!
//                Toast.makeText(requireContext(), "Not granted", Toast.LENGTH_SHORT).show()
//        }
//    }



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == ACTIVITY_REQUEST_CODE_OPEN_FILE_PICKER && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                if (data.clipData != null && data.clipData!!.itemCount > 0) {
//                    val clipData = data.clipData
//                    val uris = arrayOfNulls<Uri>(clipData!!.itemCount)
//                    for (i in 0 until clipData!!.itemCount) {
//                        uris[i] = clipData!!.getItemAt(i).uri
//                    }
//                    //onImageFilesSelected(uris)
//                } else {
//                    val uri = data.data
//                    //onImageSelected(uri)
//                }
//            }
//        }
//    }

//    fun getBitmapFromUri(activity: Activity, uri: Uri, sampleSize: Int? = null): Bitmap? {
//        var parcelFileDescriptor: ParcelFileDescriptor? = null
//        return try {
//            parcelFileDescriptor = activity.contentResolver.openFileDescriptor(uri, "r")
//            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
//            val options = BitmapFactory.Options().apply {
//                sampleSize?.let { sampleSize ->
//                    inSampleSize = sampleSize
//                }
//            }
//            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
//            parcelFileDescriptor.close()
//            image
//        } catch (e: Exception) {
//            null
//        } finally {
//            try {
//                parcelFileDescriptor?.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }

}

@VisibleForTesting
@Composable
fun MainScreen(onExploreItemClicked: OnGalleryItemClicked, onDateSelectionClicked: () -> Unit) {
    Surface(color = MaterialTheme.colors.primary) {
        val transitionState = remember { MutableTransitionState(SplashState.Shown) }
        val transition = updateTransition(transitionState, label = "splashTransition")
        val splashAlpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 100) }, label = "splashAlpha"
        ) {
            if (it == SplashState.Shown) 1f else 0f
        }
        val contentAlpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 300) }, label = "contentAlpha"
        ) {
            if (it == SplashState.Shown) 0f else 1f
        }
        val contentTopPadding by transition.animateDp(
            transitionSpec = { spring(stiffness = Spring.StiffnessLow) }, label = "contentTopPadding"
        ) {
            if (it == SplashState.Shown) 100.dp else 0.dp
        }

        Box {
            LandingScreen(
                modifier = Modifier.alpha(splashAlpha),
                onTimeout = { transitionState.targetState = SplashState.Completed }
            )
            MainContent(
                modifier = Modifier.alpha(contentAlpha),
                topPadding = contentTopPadding,
                onExploreItemClicked = onExploreItemClicked,
                onDateSelectionClicked = onDateSelectionClicked
            )
        }
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    onExploreItemClicked: OnGalleryItemClicked,
    onDateSelectionClicked: () -> Unit
) {
    Column(modifier = modifier) {
        Spacer(Modifier.padding(top = topPadding))
        GalleryHome(
            modifier = modifier,
            onGalleryItemClicked = onExploreItemClicked,
            onDateSelectionClicked = onDateSelectionClicked
        )
    }
}
enum class SplashState { Shown, Completed }