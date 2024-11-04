package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper.firebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.MainActivity
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.Report.ReportFragment
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
        val root: View = binding.root

        val navController = findNavController()

        if (navController.currentDestination?.id != R.id.navigation_home) {
            if (activity is MainActivity) {
                (activity as MainActivity).setHeaderTitle(getString(R.string.settings_header))
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

    private fun changeLanguage(language: String) {
        val locale = Locale(language.lowercase(Locale.ROOT))
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        saveLanguagePreference(language)
    }

    private fun updateNotificationSettings(isOn: Boolean) {
        val user = auth.currentUser
        user?.let {
            FirebaseHelper.databaseReference.child(user.uid).child("notificationsEnabled").setValue(isOn)
        }
    }

    private fun loadNotificationSettings() {
        val user = auth.currentUser
        try{
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
        } catch (e: Exception)
            {

        }

    }


    private fun saveLanguagePreference(language: String) {
        requireContext().getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE).edit()
            .putString("selected_language", language)
            .apply()
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

            val defaultSelection = getString(R.string.default_selection)

            //already database stuff

            var validTheme: String?  = if (selectedTheme != defaultSelection) selectedTheme else ""
            var validGraphTheme: String? = if (selectedGraphTheme != defaultSelection) selectedGraphTheme else ""
            var validLanguage: String?  = if (selectedLanguage != defaultSelection) selectedLanguage else ""
            val validStartValue: String?  = if (startValue != defaultSelection) startValue else ""

            if (validTheme != null || validGraphTheme != null || validLanguage != null || validStartValue != null) {

                if (validTheme != null)
                {
                    if (validTheme == getString(R.string.theme_2))
                    {
                        validTheme = "Dark Theme"
                    }else if (validTheme == getString(R.string.theme_1))
                    {
                        validTheme = "Light Theme"
                    }
                }

                if (validGraphTheme != null)
                {

                    if (validGraphTheme == getString(R.string.graph_theme_1))
                    {
                        validGraphTheme = "CyberSpace"
                    }else if (validGraphTheme == getString(R.string.graph_theme_2))
                    {
                        validGraphTheme = "Unicorn"
                    }else if (validGraphTheme == getString(R.string.graph_theme_3))
                    {
                        validGraphTheme = "Deep Ocean(colorblind red/green)"
                    }else if (validGraphTheme == getString(R.string.graph_theme_4))
                    {
                        validGraphTheme = "Pandora Green(colorblind blue/yellow)"
                    }
                }

                if (validLanguage != null)
                {
                    if (validLanguage == getString(R.string.language_1))
                    {
                        validLanguage = "English"
                    }else
                    {
                        validLanguage = "Afrikaans"
                    }
                    if (validLanguage != getCurrentLanguage()) {
                       changeLanguage(validLanguage)
                    }
                }

                updateUserData(username, name, password, validStartValue.toString(), validTheme.toString(),
                    validGraphTheme.toString(), validLanguage.toString())

                applyTheme()

            } else {
                Toast.makeText(context, "Please select valid options.", Toast.LENGTH_SHORT).show()
            }


            //updateUserData(username, name, password, startValue, selectedTheme, selectedGraphTheme, selectedLanguage)
            //applyTheme()
            //if (selectedLanguage != getCurrentLanguage()) {
            //    changeLanguage(selectedLanguage)
            //}
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

        //binding.btnDisplayReport.setOnClickListener()
       // {
         //   findNavController().navigate(R.id.navigateToReportFrament)
        //}

        binding.imgBtnDisplayReport.setOnClickListener()
        {
            findNavController().navigate(R.id.navigateToReportFrament)
        }

    }

    private fun openReportFragment() {

    }

    private fun getCurrentLanguage(): String {
        return requireContext().getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
            .getString("selected_language", Locale.getDefault().language) ?: Locale.getDefault().language
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
            language = language,
        )
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
        auth.currentUser?.let { user ->
            FirebaseHelper.getProfilePictureUrl(user.uid) { url, message ->
                if (url != null) {
                    Glide.with(binding.ivProfile.context).load(url).apply(
                        RequestOptions()
                            .placeholder(R.drawable.profile_image)
                            .error(R.drawable.profile_image)
                    ).into(binding.ivProfile)
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
        binding.NotificationRadioGroup.clearCheck()
    }



    private fun recreateActivity() {
        activity?.recreate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
