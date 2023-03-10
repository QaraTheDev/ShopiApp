package com.example.shopiapp.presentation.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shopiapp.databinding.FragmentHomeBinding
import com.example.shopiapp.R
import com.example.shopiapp.model.AccountDto
import com.example.shopiapp.presentation.ui.adapters.*
import com.example.shopiapp.presentation.viewmodels.AccountSourceViewModel
import com.example.shopiapp.presentation.viewmodels.HomeViewModel
import com.example.shopiapp.util.GoodDetailsUtil
import com.example.shopiapp.util.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() :
    BindFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel by viewModels<HomeViewModel>()
    private val accountViewModel by activityViewModels<AccountSourceViewModel>()

    private lateinit var categoriesAdapter: HomeCategoriesAdapter
    private lateinit var latestAdapter: HomeLatestCollectionAdapter
    private lateinit var flashSaleAdapter: HomeFlashSaleCollectionAdapter
    private lateinit var brandAdapter: HomeBrandsCollectionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleArguments()
        viewModel.getCategories()
        viewModel.getData()
        initAdapters()
        setClickListeners()
        setFlowCollectors()
        bind()
    }

    private fun handleArguments() {
        arguments?.let { args ->
            accountViewModel.setAccount(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    args.getParcelable(
                        resources.getString(R.string.account_key),
                        AccountDto::class.java
                    )
                } else {
                    args.getParcelable(resources.getString(R.string.account_key))
                } ?: return
            )
        }
    }

    private fun initAdapters() {
        categoriesAdapter =
            HomeCategoriesAdapter { category -> viewModel.onCategoryClick(category) }
        latestAdapter = HomeLatestCollectionAdapter(
            { good -> viewModel.addToCart(good) },
            { good -> viewModel.onGoodClick(good) }
        )
        flashSaleAdapter = HomeFlashSaleCollectionAdapter(
            { good -> viewModel.addToCart(good) },
            { good -> viewModel.addToFavourites(good) },
            { good -> viewModel.onGoodClick(good) }
        )
        brandAdapter = HomeBrandsCollectionAdapter()
    }

    private fun setClickListeners() {
        with(binding) {
            mainMenu.setOnClickListener { }
            locationButton.setOnClickListener { }
            searchButton.setOnClickListener { }
            viewAllLatestButton.setOnClickListener { }
            viewAllFlashSaleButton.setOnClickListener { }
            viewAllBrandsButton.setOnClickListener { }
        }
    }

    private fun setFlowCollectors() {
        viewModel.stateFlow.onEach { handleState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.errorFlow.onEach { handleError(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.categoriesFlow
            .onEach { categoriesAdapter.submitList(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.brandsFlow
            .onEach { brandAdapter.submitList(it) }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.latestFlow.combine(viewModel.flashSaleFlow) { latest, sale ->
            if (latest.isNotEmpty() && sale.isNotEmpty()) {
                latestAdapter.submitList(latest)
                flashSaleAdapter.submitList(sale)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.goodDetailsFlow.onEach { details ->
            val args = Bundle().apply {
                putParcelable(
                    resources.getString(R.string.good_details_key),
                    GoodDetailsUtil().convertGoodDetailsToDto(details)
                )
            }
            findNavController().navigate(R.id.action_homeFragment_to_goodDetailsFragment, args)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun bind() {
        with(binding) {
            categories.adapter = categoriesAdapter
            latestCollection.adapter = latestAdapter
            flashSaleCollection.adapter = flashSaleAdapter
            brandsCollection.adapter = brandAdapter
            with(searchInputText) {
                setAdapter(
                    HomeSearchAutoCompleteAdapter(requireContext()) { input ->
                        viewModel.getSearchKeyWords(
                            input
                        )
                    }
                )
                setDelay(1000)
                setLoadIndicator(searchLoadingIndicator)
            }

            Glide.with(requireContext())
                .load(accountViewModel.account.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.home_photo_place_holder)
                .into(photo)
        }
    }

    private fun handleState(state: State) {
        binding.progressBar.isVisible = state == State.LOADING
    }

    private fun handleError(exception: Exception) {
//        Todo handle
    }
}