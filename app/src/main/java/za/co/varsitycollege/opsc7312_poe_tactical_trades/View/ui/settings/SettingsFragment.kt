package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.settings

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
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
import java.util.Locale

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
        if (activity is MainActivity) {
            (activity as MainActivity).setHeaderTitle("Settings")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfilePicture()
        loadNotificationSettings()
        setupSpinners()
        setupRadioGroup()
        setupButtons()
    }

    private fun setupSpinners() {
        setupSpinner(binding.DropDownTheme, R.array.theme_options)
        setupSpinner(binding.DropDownGraphTheme, R.array.graph_theme_options)
        setupSpinner(binding.DropDownLanguage, R.array.language_options)
        setupSpinner(binding.DropDownStartValue, R.array.starting_value_options)
    }

    private fun setupSpinner(spinner: Spinner, arrayResourceId: Int) {
        ArrayAdapter.createFromResource(
            requireContext(),
            arrayResourceId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun setupRadioGroup() {
        binding.NotificationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val isEnabled = checkedId == R.id.radioButton2
            updateNotificationSettings(isEnabled)
            setRadioButtonTint(binding.radioButton2, isEnabled)
            setRadioButtonTint(binding.radioButton3, !isEnabled)
        }
    }

    private fun setRadioButtonTint(radioButton: RadioButton, isSelected: Boolean) {
        val drawable = radioButton.background?.mutate()
        if (drawable != null) {
            DrawableCompat.wrap(drawable).apply {
                DrawableCompat.setTint(this, if (isSelected) Color.TRANSPARENT else ContextCompat.getColor(requireContext(), R.color.gray))
            }
            radioButton.background = drawable
        }
    }

    private fun changeLanguage(language: String) {
        val locale = Locale(language.lowercase(Locale.ROOT))
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        saveLanguagePreference(language)
        recreateActivity()
    }

    private fun updateNotificationSettings(isEnabled: Boolean) {
        auth.currentUser?.let { user ->
            FirebaseHelper.databaseReference.child(user.uid).child("notificationsEnabled").setValue(isEnabled)
                .addOnSuccessListener {
                    showToast("Notification settings updated successfully.")
                }.addOnFailureListener {
                    showToast("Failed to update notification settings: ${it.message}")
                }
        }
    }

    private fun saveLanguagePreference(language: String) {
        requireContext().getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE).edit()
            .putString("selected_language", language)
            .apply()
    }

    private fun loadNotificationSettings() {
        auth.currentUser?.let { user ->
            FirebaseHelper.databaseReference.child(user.uid).child("notificationsEnabled").get()
                .addOnSuccessListener { snapshot ->
                    if (isAdded) { // Check if the fragment is still added
                        val notificationsEnabled = snapshot.getValue(Boolean::class.java) ?: false
                        binding.radioButton2.isChecked = notificationsEnabled
                        setRadioButtonTint(binding.radioButton2, notificationsEnabled)
                        setRadioButtonTint(binding.radioButton3, !notificationsEnabled)
                    }
                }.addOnFailureListener {
                    if (isAdded) { // Check if the fragment is still added
                        showToast("Failed to load notification settings: ${it.message}")
                    }
                }
        }
    }


    private fun setupButtons() {
        binding.btnSaveAndExit.setOnClickListener { saveUserData() }
        binding.BtnDiscardChanges.setOnClickListener { discardChanges() }
        binding.BtnDeleteAccount.setOnClickListener { deleteAccount() }
        binding.BtnSignOut.setOnClickListener { signOut() }
        binding.updateProfile.setOnClickListener { openImagePicker() }
    }

    private fun saveUserData() {
        val username = binding.editTxtUsername.text.toString()
        val name = binding.editTxtName.text.toString()
        val password = binding.editTxtPassword.text.toString()
        val selectedTheme = binding.DropDownTheme.selectedItem.toString()
        val selectedGraphTheme = binding.DropDownGraphTheme.selectedItem.toString()
        val selectedLanguage = binding.DropDownLanguage.selectedItem.toString()
        val startValue = binding.DropDownStartValue.selectedItem.toString()

        // Update the user language preference first
        if (selectedLanguage != getCurrentLanguage()) {
            changeLanguage(selectedLanguage)
        }

        updateUserData(username, name, password, startValue, selectedTheme, selectedGraphTheme, selectedLanguage)
    }
    private fun getCurrentLanguage(): String {
        return requireContext().getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
            .getString("selected_language", Locale.getDefault().language) ?: Locale.getDefault().language
    }


    private fun discardChanges() {
        loadProfilePicture()
        loadNotificationSettings()
        clearInputs()
        showToast("Changes Discarded")
    }

    private fun updateUserData(username: String, name: String, password: String, startValue: String, theme: String, graphTheme: String, language: String) {
        auth.currentUser?.let { user ->
            val userId = user.uid
            if (password.isNotEmpty() && password.matches(Regex("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+\$).{8,}"))) {
                user.updatePassword(password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        storeUserData(userId, username, name, user.email ?: "", startValue, theme, graphTheme, language)
                        discardChanges()
                    } else {
                        showToast("Failed to update password: ${it.exception?.message}")
                    }
                }
            } else {
                storeUserData(userId, username, name, user.email ?: "", startValue, theme, graphTheme, language)
                discardChanges()
            }
        }
    }

    private fun storeUserData(userId: String, username: String, name: String, email: String, startValue: String, theme: String, graphTheme: String, language: String) {
        FirebaseHelper.updateUserData(
            context = requireContext(),
            userId = userId,
            username = username,
            name = name,
            email = email,
            startValue = startValue,
            theme = theme,
            graphTheme = graphTheme,
            language = language
        )
    }

    private fun deleteAccount() {
        auth.currentUser?.let { user ->
            FirebaseHelper.databaseReference.child(user.uid).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Account deleted")
                        FirebaseHelper.signOut()
                    } else {
                        showToast("Failed to delete account: ${task.exception?.message}")
                    }
                }
        }
    }

    private fun signOut() {
        FirebaseHelper.signOut()
        showToast("Signed out")
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            profileImageUri = data.data
            uploadProfilePicture()
        }
    }

    private fun uploadProfilePicture() {
        auth.currentUser?.let { user ->
            profileImageUri?.let { uri ->
                FirebaseHelper.uploadProfilePicture(user.uid, uri) { url, message ->
                    if (url != null) {
                        storeUserProfilePicture(user.uid, url)
                    } else {
                        showToast("Failed to upload profile picture: $message")
                    }
                }
            }
        }
    }

    private fun storeUserProfilePicture(userId: String, profilePictureUrl: String) {
        FirebaseHelper.databaseReference.child(userId).child("profilePictureUrl").setValue(profilePictureUrl)
            .addOnSuccessListener {
                showToast("Profile picture updated")
                loadProfilePicture()
            }.addOnFailureListener {
                showToast("Failed to update profile picture: ${it.message}")
            }
    }

    private fun loadProfilePicture() {
        auth.currentUser?.let { user ->
            FirebaseHelper.getProfilePictureUrl(user.uid) { url, message ->
                if (isAdded) {
                    if (url != null) {
                        Glide.with(this)
                            .load(url)
                            .apply(RequestOptions().transform(RoundedCorners(16)))
                            .into(binding.myImageView)
                    } else {
                        showToast("Failed to load profile picture: $message")
                    }
                }
            }
        }
    }

    private fun clearInputs() {
        binding.editTxtUsername.text.clear()
        binding.editTxtName.text.clear()
        binding.editTxtPassword.text.clear()
        binding.DropDownTheme.setSelection(0)
        binding.DropDownGraphTheme.setSelection(0)
        binding.DropDownLanguage.setSelection(0)
        binding.DropDownStartValue.setSelection(0)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun recreateActivity() {
        activity?.recreate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
