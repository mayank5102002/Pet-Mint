package com.leafcode.petmint.newpet

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentImageSelectionBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.storage.StorageReference
import android.provider.OpenableColumns
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class ImageSelectionFragment : Fragment() {

    //Viewmodel of the activity of this fragment
    val activityViewModel : AddPetViewModel by activityViewModels()

    //List for the uri of selected images
    private lateinit var imageUriList : MutableList<Uri?>

    private val STORAGE_PERMISSION_CODE = 501

    //Codes to check which image is being selected
    private val GalleryPick1 = 200
    private val GalleryPick2 = 201
    private val GalleryPick3 = 202

    //Objects of post button,progress bar and Imageviews
    private lateinit var imageUpload1 : ImageView
    private lateinit var imageUpload2 : ImageView
    private lateinit var imageUpload3 : ImageView
    private lateinit var postButton : Button
    private lateinit var deleteButton1 : ImageView
    private lateinit var deleteButton2 : ImageView
    private lateinit var deleteButton3 : ImageView
    private lateinit var progressBar: ProgressBar
    //Object for storage reference
    private lateinit var storageRef : StorageReference

    //Reference for Database and Firebase Authentication
    private lateinit var db: FirebaseDatabase
    private val auth : FirebaseAuth = Firebase.auth

    //Variable to count the images selected to upload
    private var countImages = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentImageSelectionBinding = FragmentImageSelectionBinding.inflate(inflater,container,false)

        //Updating the title of action bar
        activityViewModel.updateActionBarTitle("Select Images")

        //Initialising the list of uri
        imageUriList = mutableListOf(null,null,null)

        //Initialising the storage reference and database reference
        storageRef = FirebaseStorage.getInstance().reference
        db = Firebase.database

        //Initialising the views
        imageUpload1 = binding.imageUpload1
        imageUpload2 = binding.imageUpload2
        imageUpload3 = binding.imageUpload3
        postButton = binding.postButton
        deleteButton1 = binding.deleteImage1
        deleteButton2 = binding.deleteImage2
        deleteButton3 = binding.deleteImage3
        progressBar = binding.progressBar
        progressBar.visibility = View.INVISIBLE

        //Setting the views
        //deleting the uri from the list when delete button clicked
        imageUpload1.setOnClickListener{
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE)) {
                galleryIntent(0)
            }
        }

        deleteButton1.setOnClickListener{
            imageUpload1.setImageResource(R.drawable.add_button_image)
            imageUriList[0] = null
        }

        imageUpload2.setOnClickListener{
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE)) {
                galleryIntent(1)
            }
        }

        deleteButton2.setOnClickListener{
            imageUpload2.setImageResource(R.drawable.add_button_image)
            imageUriList[1] = null
        }

        imageUpload3.setOnClickListener{
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE)) {
                galleryIntent(2)
            }
        }

        deleteButton3.setOnClickListener{
            imageUpload3.setImageResource(R.drawable.add_button_image)
            imageUriList[2] = null
        }

        //Click listener for post button
        postButton.setOnClickListener {

            if (!checkInternetConnectivity()){
                Toast.makeText(this.requireActivity(),"No Internet connection",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Checking and returning if no image is selected
            if (imageUriList.all { it == null }){
                Toast.makeText(requireActivity(),"Please select atleast one image",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Counting the number of images selected to upload
            for (i in imageUriList){
                if (i != null) {
                        countImages++
                }
            }

            //Initialising the image variable in viewmodel to prevent it from being null
            activityViewModel.initialiseImageUploadConfirm()

            //Disabling the views to be clicked
            disablingViews()

            //Uploading the images and if it is failed enabling the views again
           if (!uploadImagesAndUpdateUrl()){
               Toast.makeText(requireActivity(),"Some Error Occured in image posting",Toast.LENGTH_SHORT).show()
               enablingViews()
           }
        }

        //Livde data to check the number of images uploaded
        activityViewModel.imagesUploadConfirm.observe(viewLifecycleOwner, Observer {
            if (it == countImages){
                activityViewModel.readyUploadAd()
            }
        })

        //Live data to check if the ad is ready to be uploaded and then uploading the add
        activityViewModel.uploadAd.observe(viewLifecycleOwner, Observer {
            if (it == true){
                activityViewModel.uploadAd()
                activityViewModel.adUpload()
            }
        })

        //Live data to check if the ad is uploaded and then closing the activity
        activityViewModel.adUploaded.observe(viewLifecycleOwner, Observer {
            Toast.makeText(this.requireActivity(),"New Advertisement Added",Toast.LENGTH_SHORT).show()
            this.activity?.finish()
        })

        activityViewModel.fault.observe(viewLifecycleOwner,{
            if (it==true){
                Toast.makeText(this.requireActivity(),"Some error occured, please try again",Toast.LENGTH_SHORT).show()
                this.activity?.finish()
                progressBar.visibility = View.INVISIBLE
            }
        })

        return binding.root
    }

    //Function to upload the images
    private fun uploadImagesAndUpdateUrl() : Boolean{

        try {

            //Showing the progress bar
            progressBar.visibility = View.VISIBLE

            var j = 0

            //Starting loop to upload the images
            for (i in imageUriList) {

                activityViewModel.uploadImages(j, i.toString())

                j++
            }
        }
        catch (e : Exception){
            Log.w(
                "ImageSelectionFragment",
                "Image upload task was unsuccessful.",e)
        }

        return true
    }

    //Function to disable all the views
    private fun disablingViews(){

        imageUpload1.isClickable = false
        imageUpload2.isClickable = false
        imageUpload3.isClickable = false
        deleteButton1.isClickable = false
        deleteButton2.isClickable = false
        deleteButton3.isClickable = false
        postButton.isClickable = false

    }

    //Function to enable all the views
    private fun enablingViews(){

        imageUpload1.isClickable = true
        imageUpload2.isClickable = true
        imageUpload3.isClickable = true
        deleteButton1.isClickable = true
        deleteButton2.isClickable = true
        deleteButton3.isClickable = true
        postButton.isClickable = true

    }

    //Gallery intent function to call the gallery intent to select the images
    private fun galleryIntent(i : Int) {
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        when(i){
            //Calling the intent for different image selection
            0-> startActivityForResult(intent,GalleryPick1)
            1-> startActivityForResult(intent,GalleryPick2)
            else-> startActivityForResult(intent,GalleryPick3)
        }
    }

    //Function to get the uri of the selected image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
        Loop to check if their is data and it is alright
        Then checking for which image it was called and
        then putting the data in teh appropriate position
        and setting the appropriate imageView to the image
         */
        if (resultCode == RESULT_OK && data != null) {
                if (requestCode == GalleryPick1) {
                    val fileSize = getSize(this.requireContext(),data.data!!)!!.toLong()
                    if (fileSize < 10000000) {
                        imageUriList[0] = data.data!!
                        imageUpload1.setImageURI(imageUriList[0])
                        imageUpload1.scaleType = ImageView.ScaleType.FIT_CENTER
                    } else {
                        Toast.makeText(this.requireActivity(),"Image size can't be more than 10 MB",Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                if (requestCode == GalleryPick2) {
                        val fileSize = getSize(this.requireContext(),data.data!!)!!.toLong()
                        if (fileSize < 10000000) {
                            imageUriList[1] = data.data!!
                            imageUpload2.setImageURI(imageUriList[1])
                            imageUpload2.scaleType = ImageView.ScaleType.FIT_CENTER
                        } else {
                            Toast.makeText(this.requireActivity(),"Image size can't be more than 10 MB",Toast.LENGTH_SHORT).show()
                            return
                        }
                }
                if (requestCode == GalleryPick3) {
                        val fileSize = getSize(this.requireContext(),data.data!!)!!.toLong()
                        if (fileSize < 10000000) {
                            imageUriList[2] = data.data!!
                            imageUpload3.setImageURI(imageUriList[2])
                            imageUpload3.scaleType = ImageView.ScaleType.FIT_CENTER
                        } else {
                            Toast.makeText(this.requireActivity(),"Image size can't be more than 10 MB",Toast.LENGTH_SHORT).show()
                            return
                        }
                }
            }
        }

    fun getSize(context: Context, uri: Uri?): String? {
        var fileSize: String? = null
        val cursor: Cursor? = context.getContentResolver()
            .query(uri!!, null, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex)
                }
            }
        } finally {
            cursor?.close()
        }
        return fileSize
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) : Boolean{
        if (ContextCompat.checkSelfPermission(this.requireActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(permission), requestCode)
        } else {
            return true
        }
        return false
    }

    private fun checkInternetConnectivity() : Boolean{

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo?.isConnected != null){
            return networkInfo.isConnected
        } else {
            return false
        }

    }

}