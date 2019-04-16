package com.cop4331.group7.hangr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cop4331.group7.hangr.classes.CreateCircularDrawable
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.constants.CATEGORIES
import com.cop4331.group7.hangr.constants.EXISTING_CLOTHING_ITEM_DATA
import com.cop4331.group7.hangr.constants.EXISTING_CLOTHING_ITEM_PARENT_ID
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
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import hideKeyboard
import kotlinx.android.synthetic.main.activity_add_or_edit_clothing.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File

// TODO: allow user to edit fields and update db entry
class AddOrEditClothingActivity : AppCompatActivity() {
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: StorageReference

    var isEditingClothingItem = false
    var previousImageStorageFilename: String? = null
    var currentImageUri: Uri? = null
    var currentImageStorageFilename: String? = null
    var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_clothing)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initListeners()
        initFirebase()
        initSpinner()
        setActivityStateEditOrNew()
    }

    private fun initListeners() {
        image_editing_clothing.setOnClickListener { showPictureDialog() }
        button_finish.setOnClickListener { handleSavePressed() }
        button_delete.setOnClickListener { deleteClothingItem() }

        // hide keyboard when clicking on the scrollview or nested linear layout
        add_or_edit_layout.setOnClickListener { it.hideKeyboard() }
        nested_layout.setOnClickListener { it.hideKeyboard() }

        nacho_colors.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
        nacho_colors.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
        nacho_tags.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
        nacho_tags.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
    }

    private fun initFirebase() {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
    }

    private fun initSpinner() {
        spinner_category.setItems<String>(CATEGORIES)
        spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) { category = CATEGORIES[position] }
            override fun onNothingSelected(adapterView: AdapterView<*>) { category = null }
        }
    }

    private fun setActivityStateEditOrNew() {
        if (intent.hasExtra(EXISTING_CLOTHING_ITEM_DATA)) {
            isEditingClothingItem = true
            val existingClothingItem = intent.extras?.getParcelable(EXISTING_CLOTHING_ITEM_DATA) as FirebaseClothingItem

            if (existingClothingItem.imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(existingClothingItem.imageUrl)
                    .centerCrop()
                    .placeholder(CreateCircularDrawable.make(this))
                    .into(image_editing_clothing)
            } else {
                Glide
                    .with(this)
                    .load(R.drawable.ic_add_a_photo_black_24dp)
                    .into(image_editing_clothing)
            }

            edit_clothing_name.setText(existingClothingItem.name)

            val index = CATEGORIES.indexOf(existingClothingItem.category)
            spinner_category.setSelection(if (index == -1) 0 else index + 1)

            edit_clothing_wears.setText(existingClothingItem.wears.toString())

            nacho_colors.setText(existingClothingItem.colors)
            nacho_tags.setText(existingClothingItem.tags)

            previousImageStorageFilename = existingClothingItem.imageFilename
        } else {
            isEditingClothingItem = false

            button_delete.isClickable = false
            button_delete.visibility = View.INVISIBLE
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

    private fun deleteClothingItem() {
        if (!intent.hasExtra(EXISTING_CLOTHING_ITEM_PARENT_ID))
            Toast.makeText(this, "Cannot find item to delete!", Toast.LENGTH_LONG).show()
        else if (!previousImageStorageFilename.isNullOrBlank())
            removeImageFromStorage()
        else
            removeCurrentItemFromFirebase()
    }

    private fun removeCurrentItemFromFirebase() {
        val documentId = intent.getStringExtra(EXISTING_CLOTHING_ITEM_PARENT_ID)
        db.collection(currentUser.uid).document(documentId).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully deleted item!", Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                handleFailure(it.exception)
            }
        }
    }

    private fun removeImageFromStorage() {
        val imageRef = storage.child(currentUser.uid).child(previousImageStorageFilename!!)
        imageRef.delete().addOnCompleteListener {
            if (it.isSuccessful) { removeCurrentItemFromFirebase() }
            else { handleFailure(it.exception) }
        }
    }

    private fun requestCameraPermissionsAndOpenCamera() {
        val cameraPermissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                EasyImage.openCameraForImage(this@AddOrEditClothingActivity, 0)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    this@AddOrEditClothingActivity,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(cameraPermissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(android.Manifest.permission.CAMERA)
            .check()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                e?.printStackTrace()
                Toast.makeText(
                    this@AddOrEditClothingActivity,
                    "Error grabbing image! Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onImagesPicked(imagesFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                val image = imagesFiles.first()
                Glide
                    .with(this@AddOrEditClothingActivity)
                    .load(image)
                    .centerCrop()
                    .placeholder(CreateCircularDrawable.make(this@AddOrEditClothingActivity))
                    .into(image_editing_clothing)

                currentImageUri = Uri.fromFile(image)
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
        progress_horizontal.visibility = View.VISIBLE
        text_progress.visibility = View.VISIBLE
        text_progress.text = this.getString(R.string.uploading_image_percentage, 0)

        currentImageStorageFilename = currentImageUri!!.lastPathSegment!!
        val imageRef = storage.child(currentUser.uid).child(currentImageStorageFilename!!)

        val uploadTask = imageRef.putFile(currentImageUri!!)
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
                if (task.isSuccessful) { handleImageUploadSuccess(task.result!!) }
                else { handleFailure(task.exception) }
            }
    }

    private fun handleImageUploadSuccess(uri: Uri) {
        progress_horizontal.visibility = View.INVISIBLE
        text_progress.visibility = View.INVISIBLE
        Toast.makeText(this, "Image uploaded to firebase storage!", Toast.LENGTH_SHORT).show()
        saveClothingItem(uri)
    }

    private fun handleFailure(exception: Exception?) {
        throw exception!!
    }

    private fun handleSavePressed() {
        // TODO: check for whole form being complete before uploading proceeding
        setFormUiEnabled(false)
        progress_circular.visibility = View.VISIBLE
        if (currentImageUri != null)
            uploadCurrentImageToUserStorage()
        else
            saveClothingItem()
    }

    private fun saveClothingItem(uri: Uri? = null) {
        val wearsString = edit_clothing_wears.text.toString()
        val wearsInt= if (wearsString.isBlank()) -1 else wearsString.toInt()

        val colors = mutableListOf<String>()
        nacho_colors.allChips.forEach { colors.add(it.text.toString()) }
        val tags = mutableListOf<String>()
        nacho_tags.allChips.forEach { tags.add(it.text.toString()) }

        val clothingItem = FirebaseClothingItem(
            edit_clothing_name.text.toString(),
            category ?: "",
            wearsInt,
            colors,
            tags,
            uri?.toString() ?: "",
            currentImageStorageFilename ?: ""
        )

        // TODO: add colors / tags to database entry for current user, implement autofill suggestions in the Nachos (???)
        db.collection(currentUser.uid).add(clothingItem).addOnCompleteListener { if (it.isSuccessful) finish() else handleFailure(it.exception)}    }

    private fun setFormUiEnabled(isEnabled: Boolean) {
        image_editing_clothing.isClickable = isEnabled
        edit_clothing_name.isEnabled = isEnabled
        edit_clothing_wears.isEnabled = isEnabled
        nacho_colors.isEnabled = isEnabled
        nacho_tags.isEnabled = isEnabled
        button_finish.isEnabled = isEnabled
        button_delete.isEnabled = isEnabled
    }
}
