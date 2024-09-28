package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper.firebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }
    private val PICK_IMAGE_REQUEST = 1
    private var profileImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navController = findNavController()

        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle("Settings")
            }
        }

        loadProfilePicture()
        loadNotificationSettings()
        setupSpinners()
        setupRadioGroup()
        setupButtons()

        return root
    }

    private fun setupSpinners() {
        // Theme Spinner
        val themeSpinner: Spinner = binding.DropDownTheme
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.theme_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            themeSpinner.adapter = adapter
        }


        // Graph Theme Spinner
        val graphThemeSpinner: Spinner = binding.DropDownGraphTheme
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.graph_theme_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            graphThemeSpinner.adapter = adapter
        }

        // Language Spinner
        val languageSpinner: Spinner = binding.DropDownLanguage
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            languageSpinner.adapter = adapter
        }

        // Start Value Spinner
        val startValueSpinner: Spinner = binding.DropDownStartValue
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.starting_value_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            startValueSpinner.adapter = adapter
        }


    }


    private fun recreateActivity() {
        activity?.recreate()
    }

    private fun setupRadioGroup() {
        val notificationGroup: RadioGroup = binding.NotificationRadioGroup
        notificationGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton2 -> {
                    updateNotificationSettings(true)
                    setRadioButtonTint(binding.radioButton2, true)
                    setRadioButtonTint(binding.radioButton3, false)
                }
                R.id.radioButton3 -> {
                    updateNotificationSettings(false)
                    setRadioButtonTint(binding.radioButton2, false)
                    setRadioButtonTint(binding.radioButton3, true)
                }
            }
        }
    }

    private fun setRadioButtonTint(radioButton: RadioButton, isSelected: Boolean) {
        val drawable = radioButton.background?.mutate()

        if (drawable != null) {
            DrawableCompat.wrap(drawable).apply {
                if (isSelected) {
                    DrawableCompat.setTintList(this, null)
                } else {
                    DrawableCompat.setTint(this, ContextCompat.getColor(requireContext(), R.color.gray))
                }
            }
            radioButton.background = drawable
        }
    }



    private fun updateNotificationSettings(isOn: Boolean) {
        val user = auth.currentUser
        user?.let {
            FirebaseHelper.databaseReference.child(user.uid).child("notificationsEnabled").setValue(isOn)
        }
    }

    private fun loadNotificationSettings() {
        val user = auth.currentUser
        user?.let {
            FirebaseHelper.databaseReference.child(user.uid).child("notificationsEnabled").get()
                .addOnSuccessListener { snapshot ->
                    val notificationsEnabled = snapshot.getValue(Boolean::class.java) ?: false
                    if (notificationsEnabled) {
                        binding.radioButton2.isChecked = true
                        setRadioButtonTint(binding.radioButton2, true)
                        setRadioButtonTint(binding.radioButton3, false)
                    } else {
                        binding.radioButton3.isChecked = true
                        setRadioButtonTint(binding.radioButton2, false)
                        setRadioButtonTint(binding.radioButton3, true)
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load notification settings: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
    private fun setupButtons() {
        binding.btnSaveAndExit.setOnClickListener {
            val username = binding.editTxtUsername.text.toString()
            val name = binding.editTxtName.text.toString()
            val password = binding.editTxtPassword.text.toString()
            val selectedTheme = binding.DropDownTheme.selectedItem.toString()
            val selectedGraphTheme = binding.DropDownGraphTheme.selectedItem.toString()
            val selectedLanguage = binding.DropDownLanguage.selectedItem.toString()
            val startValue = binding.DropDownStartValue.selectedItem.toString()

            updateUserData(username, name, password, startValue, selectedTheme, selectedGraphTheme, selectedLanguage)
            applyTheme()
        }

        binding.BtnDiscardChanges.setOnClickListener {
            discardChanges()
            Toast.makeText(requireContext(), "Changes Discarded", Toast.LENGTH_SHORT).show()
        }

        binding.BtnDeleteAccount.setOnClickListener {
            deleteAccount()
        }

        binding.BtnSignOut.setOnClickListener {
            FirebaseHelper.signOut()
            Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
        }

        binding.updateProfile.setOnClickListener {
            openImagePicker()
            loadProfilePicture()
        }
    }

    private fun discardChanges() {
        loadProfilePicture()
        loadNotificationSettings()
        clearInputs()
    }

    private fun updateUserData(username: String, name: String, password: String, startValue:String, theme: String, graphTheme: String, language: String) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid

            val passwordPattern =
                "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+\$).{8,}".toRegex()


            if ((password.isNotEmpty()) && (passwordPattern.matches(password))) {
                user.updatePassword(password)
                storeUserData(userId, username, name, it.email ?: "", startValue, theme, graphTheme, language)

                discardChanges()
            }
            else if ((password.isNotEmpty()) && (!passwordPattern.matches(password)))
            {
                Toast.makeText(requireContext(), "Password must meet complexity requirements, Password will not be updated.", Toast.LENGTH_LONG)
                    .show()
            }
            else if (password.isEmpty())
            {
                storeUserData(userId, username, name, it.email ?: "",startValue, theme, graphTheme, language)
                discardChanges()
            }
        }
    }

    private fun applyTheme() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            FirebaseHelper.getTheme(currentUser.uid) { theme, error ->
                if (error != null) {
                    Log.e("AuthStateListener", "Error retrieving theme: $error")
                } else if (theme != null) {
                    when (theme) {
                        "Dark Theme" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        "Light Theme" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    recreateActivity()
                }

            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            recreateActivity()
        }
    }
    private fun storeUserData(userId: String, username: String, name: String, email: String, startValue: String, theme: String, graphTheme: String, language: String) {
        FirebaseHelper.updateUserData(
            context = requireContext(),
            userId = userId,
            username = username,
            name = name,
            email = email,
            startValue =  startValue,
            theme = theme,
            graphTheme = graphTheme,
            language = language
        )
    }

    private fun clearInputs() {
        binding.editTxtUsername.text.clear()
        binding.editTxtName.text.clear()
        binding.editTxtPassword.text.clear()
        binding.DropDownTheme.setSelection(0)
        binding.DropDownGraphTheme.setSelection(0)
        binding.DropDownLanguage.setSelection(0)
        binding.NotificationRadioGroup.clearCheck()
    }

    private fun deleteAccount() {
        val user = auth.currentUser
            if (user != null) {
                FirebaseHelper.databaseReference.child(user.uid).removeValue()
                Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                FirebaseHelper.signOut()
            } else {
                Toast.makeText(requireContext(), "Failed to delete account", Toast.LENGTH_SHORT).show()
            }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            profileImageUri = data.data
            uploadProfilePicture()
        }
    }

    private fun uploadProfilePicture() {
        val user = auth.currentUser
        user?.let {
            profileImageUri?.let { uri ->
                FirebaseHelper.uploadProfilePicture(it.uid, uri) { url, message ->
                    if (url != null) {
                        storeUserProfilePicture(it.uid, url)
                    } else {
                        Toast.makeText(requireContext(), "Failed to upload profile picture.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun storeUserProfilePicture(userId: String, profilePictureUrl: String) {
        FirebaseHelper.databaseReference.child(userId).child("profilePictureUrl").setValue(profilePictureUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()
                loadProfilePicture()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update profile picture: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadProfilePicture() {
        val user = auth.currentUser
        user?.let {
            FirebaseHelper.getProfilePictureUrl(it.uid) { url, message ->
                if (url != null) {
                    Glide.with(this)
                        .load(url)
                        .apply(RequestOptions().transform(RoundedCorners(16)))
                        .into(binding.myImageView)
                } else {

                    Toast.makeText(requireContext(), "Failed to load profile picture: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}