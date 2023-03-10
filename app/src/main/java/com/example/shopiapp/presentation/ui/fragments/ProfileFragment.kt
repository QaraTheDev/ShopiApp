package com.example.shopiapp.presentation.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.test.domain.exceptions.SavePhotoException
import com.example.shopiapp.R
import com.example.shopiapp.databinding.FragmentProfileBinding
import com.example.shopiapp.presentation.viewmodels.AccountSourceViewModel
import com.example.shopiapp.presentation.viewmodels.ProfileViewModel
import com.example.shopiapp.util.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment @Inject constructor() :
    BindFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    companion object {
        //        список необходимых разрешений
        private val PERMISSIONS = buildList {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    }

    private val viewModel by viewModels<ProfileViewModel>()
    private val accountViewModel by activityViewModels<AccountSourceViewModel>()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestGalleryLauncher: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        initPermissionResult()
        initGalleryResult()
        setFlowCollectors()
        setClickListeners()
    }

    private fun bind() {

        with(binding) {
            with(toolbar) {
                setupWithNavController(findNavController())
                title = null
            }
            Glide.with(requireContext())
                .load(accountViewModel.account.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.profile_photo_place_holder)
                .placeholder(R.drawable.profile_photo_place_holder)
                .into(photo)

            nameUserTextView.text =
                String.format(
                    "%s %s",
                    accountViewModel.account.firstName,
                    accountViewModel.account.lastName
                )

            val balance =
                if (accountViewModel.account.balance == 0) 1593
                else accountViewModel.account.balance
            balanceTextView.text = String.format("$ %d", balance)
        }
    }

    private fun initPermissionResult() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsToGrantedMap ->
            if (permissionsToGrantedMap.values.all { it }) {
                requestGallery()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initGalleryResult() {
        requestGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
//                TODO crop and save photo in future
                    requireContext().contentResolver.openInputStream(uri).use { inputStream ->
                        inputStream?.let {
                            viewModel.savePhoto(
                                accountViewModel.account.photoUrl,
                                inputStream.readBytes()
                            )
                        }
                    }
                }
            }
    }

    private fun requestPermissions() = requestPermissionLauncher.launch(PERMISSIONS)

    private fun requestGallery() = requestGalleryLauncher.launch("image/*")

    private fun setFlowCollectors() {
        viewModel.stateFlow.onEach { handleState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.errorFlow.onEach { handleError(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.logOutFlow.onEach { handleLogOut(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.updatePhotoStateFlow.onEach { updatePhoto(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setClickListeners() {
        with(binding) {
            changePhotoImageButton.setOnClickListener { changePhoto() }
            uploadItemButton.setOnClickListener { uploadItem() }
            tradeStoreButton.setOnClickListener { openTradeStore() }
            paymentMethodButton.setOnClickListener { openPayments() }
            tradeHistoryButton.setOnClickListener { openTradeHistory() }
            restorePurchaseButton.setOnClickListener { restorePurchase() }
            helpButton.setOnClickListener { help() }
            logOutButton.setOnClickListener { logOut() }
        }
    }

    private fun handleState(state: State) {
        when (state) {
            State.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            State.COMPLETE -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun handleError(error: Exception) {
        when (error) {
            is SavePhotoException ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLogOut(isLogOut: Boolean) {
        if (isLogOut) findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
    }

    private fun updatePhoto(isPhotoChanged: Boolean) {
        if (isPhotoChanged) {
            Glide.with(requireContext())
                .load(accountViewModel.account.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.profile_photo_place_holder)
                .into(binding.photo)
        }
    }

    private fun changePhoto() {
        if (
            PERMISSIONS.all { permission ->
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            requestGallery()
        } else {
            requestPermissions()
        }
    }

    private fun uploadItem() {
//        TODO
    }

    private fun openTradeStore() {
//        TODO
    }

    private fun openPayments() {
//        TODO
    }

    private fun openTradeHistory() {
//        TODO
    }

    private fun restorePurchase() {
//        TODO
    }

    private fun help() {
//        TODO
    }

    private fun logOut() {
        viewModel.logOut()
    }
}