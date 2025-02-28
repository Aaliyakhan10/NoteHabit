package com.example.habittacker

import android.content.Context
import android.graphics.Color
import android.service.quicksettings.Tile
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.example.habittacker.databinding.AddHabitBinding
import com.example.habittacker.models.habit
import com.example.habittacker.viewmodel.viewModelHabits
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.marsad.stylishdialogs.StylishAlertDialog
import com.shashank.sony.fancytoastlib.FancyToast

object Utils {
    private lateinit var firebaseDatabase: DatabaseReference
    fun makeToast(context: Context, message: String) {
        FancyToast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var firebaseAuth: FirebaseAuth? = null
    fun getInstance(): FirebaseAuth {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance()
        }
        return firebaseAuth!!
    }

    fun getCurrentUser(): String {
        return FirebaseAuth.getInstance().currentUser?.uid.toString()

    }
    val colors = listOf(
        // Dark Colors
        Color.rgb(139, 0, 0),      // Dark Red (Deep Red)
        Color.rgb(255, 69, 0),     // Red-Orange (still visible, vibrant)
        Color.rgb(0, 0, 139),      // Dark Blue (Navy)
        Color.rgb(0, 128, 0),      // Dark Green
        Color.rgb(75, 0, 130),     // Indigo (Dark Purple)

        // More Darker Tones
        Color.rgb(0, 0, 0),        // Black (classic dark tone)
        Color.rgb(105, 105, 105),  // Dim Gray
        Color.rgb(47, 79, 79),     // Dark Slate Gray
        Color.rgb(160, 82, 45),    // Sienna (Dark Orange-Brown)
        Color.rgb(178, 34, 34),    // Firebrick (Dark Red)

        // Darker Shades of Muted Colors
        Color.rgb(128, 0, 128),    // Purple (Dark Purple)
        Color.rgb(34, 139, 34),    // Forest Green (still dark)
        Color.rgb(128, 128, 0),    // Olive (Dark Yellow-Green)
        Color.rgb(139, 69, 19),    // Peru (Dark Brown)
        Color.rgb(205, 92, 92),    // Indian Red (Dark Red-Pink)

        // Additional Dark Colors
        Color.rgb(69, 69, 69),     // Dark Gray
        Color.rgb(64, 64, 64),     // Charcoal Gray
        Color.rgb(153, 50, 204),   // Dark Orchid (Deep Purple)
        Color.rgb(255, 0, 0),      // Red (Bright Red for contrast)
        Color.rgb(85, 107, 47)     // Dark Olive Green
    )



    private lateinit var pDialog: StylishAlertDialog
    fun progressDialog(context: Context, message: String) {
        pDialog = StylishAlertDialog(context, StylishAlertDialog.PROGRESS)
        pDialog.progressHelper.barColor = R.color.SeafoamGreenDark
        pDialog.setTitleText(message)
            .setCancellable(false)
            //.setCancelledOnTouchOutside(false)
            .show()

    }

    fun normalDialog(context: Context, message: String) {
        pDialog = StylishAlertDialog(context, StylishAlertDialog.NORMAL)
        pDialog.progressHelper.barColor = R.color.SeafoamGreenDark
        pDialog.setTitleText(message)

            .show()


    }

    fun warningDialog(context: Context, message: String, yesButtonText: String) {
        StylishAlertDialog(context, StylishAlertDialog.WARNING)
            .setTitleText(message)

            .setConfirmText(yesButtonText)
            .setConfirmClickListener(StylishAlertDialog::dismissWithAnimation)

            .show()

    }

    fun defaultAlert(context: Context) {
        var dialog: AlertDialog? = null

                                dialog = AlertDialog.Builder(context)
                            .setTitle("Are you sure you want to logout?")
                            .setPositiveButton("Logout") { _, _ ->
                              dialog?.dismiss()

                            }
                            .setNegativeButton("Cancel") { _, _ ->
                                dialog?.dismiss()
                            }
                            .show()
    }
        fun hideProgressDialog(context: Context) {
            pDialog = StylishAlertDialog(context, StylishAlertDialog.PROGRESS)
            pDialog.hide()
        }

        fun successToast(context: Context, message: String) {
            FancyToast.makeText(context, message, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
        }


    }