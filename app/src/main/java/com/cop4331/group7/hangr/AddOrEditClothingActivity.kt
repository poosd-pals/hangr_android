package com.cop4331.group7.hangr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_or_edit_clothing.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File

// TODO: allow user to edit fields or add tags
class AddOrEditClothingActivity : AppCompatActivity() {
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: StorageReference

    var isEditingClothingItem = false
    var currentImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_clothing)


        initListeners()
        initFirebase()
        setActivityStateEditOrNew()
    }

    private fun initListeners() {
        button_save.setOnClickListener { saveClothingItem() }
        image_view_clothing_picture.setOnClickListener { showPictureDialog() }
    }

    private fun initFirebase() {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
    }

    private fun setActivityStateEditOrNew() {
        val intent = this.intent
        if (intent != null && intent.hasExtra(EXTRA_MESSAGE)) {
            val intentFromActivity = intent.extras.getString(EXTRA_MESSAGE)
            // Can not "delete" when coming from NewClothes, cancel finishes intent
    //            if (intentFromActivity.equals("HamprActivity")) {
    //                button_delete.isClickable = false
    //                button_delete.visibility = View.INVISIBLE
    //                button_cancel.setOnClickListener { finish() }
    //            }
    //            else {
    //                // TODO: populate existing values if editing existing item from closet
    //
    //                button_cancel.setOnClickListener { moveToClosetGalleryActivity() }
    //            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> EasyImage.openGallery(this@AddOrEditClothingActivity, 0)
                1 -> requestCameraPermissionsAndOpenCamera()
            }
        }
        pictureDialog.show()
    }

    private fun requestCameraPermissionsAndOpenCamera() {
        val cameraPermissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                EasyImage.openCameraForImage(this@AddOrEditClothingActivity, 0)
            }
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(this@AddOrEditClothingActivity, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(cameraPermissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(android.Manifest.permission.CAMERA)
            .check()
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                e?.printStackTrace()
                Toast.makeText(this@AddOrEditClothingActivity, "Error grabbing image! Please try again.", Toast.LENGTH_LONG).show()
            }

            override fun onImagesPicked(imagesFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                val image = imagesFiles.first()
                Picasso.get().load(image).fit().into(image_view_clothing_picture)
                currentImage = Uri.fromFile(image)
            }

            override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                // Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(this@AddOrEditClothingActivity)
                    photoFile?.delete()
                }
            }
        })
    }

    private fun uploadCurrentImageToUserStorage() {
        // TODO: put image into cloud storage, save reference in firestore
        if (currentImage == null) {
            // TODO: handle saving of clothing item without image associated with it
        }
        else {
            progress_horizontal.visibility = View.VISIBLE
            text_progress.visibility = View.VISIBLE
            text_progress.text = this.getString(R.string.uploading_image_percentage, 0)


            val imageRef = storage.child(currentUser.uid).child(currentImage!!.lastPathSegment!!)

            val uploadTask = imageRef.putFile(currentImage!!)
            uploadTask.addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
                progress_horizontal.progress = progress
                text_progress.text = this.getString(R.string.uploading_image_percentage, progress)
            }

            // grabs the image reference to put in realtimeDB after upload completed
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) { task.exception?.let { throw it } }
                return@Continuation imageRef.downloadUrl
            })
                .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handleImageUploadSuccess(task.result!!)
                } else {
                    handleFailure(task.exception!!)
                }
            }
        }
    }

     private fun handleImageUploadSuccess(uri: Uri) {
        progress_horizontal.visibility = View.INVISIBLE
        text_progress.visibility = View.INVISIBLE
        Toast.makeText(this, "Image uploaded to firebase storage!", Toast.LENGTH_SHORT).show()
        val clothingItem = FirebaseClothingItem(
            edit_clothing_name.text.toString(),
            edit_clothing_category.text.toString(),
            edit_clothing_wears.text.toString().toInt(),
            uri.toString()
        )

        db.collection(currentUser.uid).add(clothingItem).addOnCompleteListener { Toast.makeText(this, it.result.toString(), Toast.LENGTH_SHORT).show()  }
    }

    private fun handleFailure(exception: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun saveClothingItem() {
        // TODO: check for whole form being complete before uploading
        setButtonsEnabled(false)
        progress_circular.visibility = View.VISIBLE
        uploadCurrentImageToUserStorage()
    }

    private fun setButtonsEnabled(isEnabled: Boolean) {
        button_save.isEnabled = isEnabled
        button_cancel.isEnabled = isEnabled
        button_delete.isEnabled = isEnabled
    }

    private fun moveToClosetGalleryActivity() {
        intent = Intent(this, ClosetGalleryActivity::class.java)
        startActivity(intent)
        finish()
    }
}
