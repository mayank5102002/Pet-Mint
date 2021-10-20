package com.leafcode.petmint.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.databinding.FragmentProfilePicChangeBinding

class ProfilePicChange : Fragment() {

    private val activityViewModel : SettingsActivityViewModel by activityViewModels()

    private val GalleryPick = 200

    private lateinit var profilePic : ImageView

    private lateinit var profilePicUri : Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentProfilePicChangeBinding = FragmentProfilePicChangeBinding.inflate(layoutInflater,container,false)

        activityViewModel.updateActionBarTitle("Change Profile picture")

        profilePic = binding.imageChange
        val submitButton = binding.submitButton
        val progressBar = binding.profilePicChangeProgressBar
        progressBar.visibility = View.GONE


        profilePic.setOnClickListener{
            galleryIntent()
        }

        submitButton.setOnClickListener {

            if (!checkInternetConnectivity()){
                Toast.makeText(this.requireActivity(),"No Internet connection",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!profilePicUri.equals(null)){
                activityViewModel.updateProfilePicture(profilePicUri)
                progressBar.visibility = View.VISIBLE
            } else {
                Toast.makeText(this.requireActivity(),"Please select an image",Toast.LENGTH_SHORT).show()
            }

        }

        activityViewModel.profilePicChange.observe(viewLifecycleOwner,{
            if (it == true){
                activityViewModel.profilePicchangeSuccessful()
                Toast.makeText(this.requireActivity(),"Profile picture change successful",Toast.LENGTH_SHORT).show()
                this.findNavController().popBackStack()
            }
        })

        activityViewModel.error.observe(viewLifecycleOwner,{
            if (it == true){
                activityViewModel.errorSolved()
                Toast.makeText(this.requireActivity(),"Some error occured", Toast.LENGTH_SHORT).show()
                this.findNavController().popBackStack()
            }
        })

        return binding.root
    }

    //Gallery intent function to call the gallery intent to select the images
    private fun galleryIntent() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,GalleryPick)
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
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == GalleryPick) {
                val fileSize = getSize(this.requireContext(),data.data!!)!!.toLong()
                if (fileSize < 5000000) {
                    profilePicUri = data.data!!
                    profilePic.setImageURI(profilePicUri)
                    profilePic.scaleType = ImageView.ScaleType.FIT_CENTER
                } else {
                    Toast.makeText(this.requireActivity(),"Image size can't be more than 5 MB",
                        Toast.LENGTH_SHORT).show()
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