package com.example.shopiapp.presentation.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.shopiapp.R
import com.example.shopiapp.databinding.FragmentGoodDetailsBinding
import com.example.shopiapp.databinding.GoodDetailsPosterMediatorDefaultBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.shopiapp.model.GoodDetailsDto
import com.example.shopiapp.presentation.ui.adapters.GoodDetailsColorsAdapter
import com.example.shopiapp.presentation.ui.adapters.GoodDetailsPostersViewPagerAdapter
import com.example.shopiapp.presentation.viewmodels.GoodDetailsViewModel
import com.example.shopiapp.util.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class GoodDetailsFragment @Inject constructor() :
    BindFragment<FragmentGoodDetailsBinding>(FragmentGoodDetailsBinding::inflate) {

    private val viewModel by viewModels<GoodDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val good = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                resources.getString(R.string.good_details_key),
                GoodDetailsDto::class.java
            )
        } else {
            arguments?.getParcelable(resources.getString(R.string.good_details_key))
        } ?: findNavController().popBackStack()
        viewModel.setGoodDetails(good as GoodDetailsDto)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerInit()
        with(binding) {
            with(toolbar) {
                setupWithNavController(findNavController())
                title = null
                setNavigationIcon(R.drawable.good_details_toolbar_navigation_icon)
            }
            goodName.text = viewModel.goodDetails?.name ?: ""
            goodPrice.text = String.format("$ %.2f", viewModel.goodDetails?.price ?: 0.0)
            goodDescription.text = viewModel.goodDetails?.description ?: ""
            goodRating.text = String.format("%.1f", viewModel.goodDetails?.rating ?: 0.0)
            reviewsCount.text =
                String.format("%d reviews", viewModel.goodDetails?.reviewsCount ?: 0)
            listColors.adapter = GoodDetailsColorsAdapter(viewModel::selectColor)
            (listColors.adapter as GoodDetailsColorsAdapter).submitList(viewModel.colors)
            total.text = String.format("# %.2f", 0.0)
            goodsQuatityMinusButton.isEnabled = false
            goodsQuatityMinusButton.setOnClickListener {
                viewModel.removeGood()
                if (viewModel.totalSum <= 0) it.isEnabled = false
            }
            goodsQuantityPlusButton.setOnClickListener {
                viewModel.addGood()
                goodsQuatityMinusButton.isEnabled = true
            }
            viewModel.stateFlow.onEach { state ->
                when (state) {
                    State.COMPLETE -> progressBar.visibility = View.GONE
                    State.LOADING -> progressBar.visibility = View.VISIBLE
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            viewModel.totalFlow.onEach { totalSum ->
                total.text = String.format("# %.2f", totalSum)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            viewModel.colorsFlow.onEach { colors ->
                (listColors.adapter as GoodDetailsColorsAdapter).submitList(colors)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun viewPagerInit() {
        val urlList = viewModel.goodDetails?.imageUrls ?: return
        val viewPager = binding.posterViewPager
        viewPager.adapter = GoodDetailsPostersViewPagerAdapter(urlList)
        val tabLayout = binding.postersTabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.view.setPadding(6)
            if (pos == 0) tabLayout.selectTab(tab)
            val tabBinding =
                GoodDetailsPosterMediatorDefaultBinding.inflate(
                    LayoutInflater.from(binding.postersTabLayout.context),
                    binding.postersTabLayout,
                    false
                )
            viewModel.goodDetails?.imageUrls?.let { images ->
                Glide.with(tabBinding.root).load(images[pos]).into(tabBinding.pictureDefault)
                Glide.with(tabBinding.root).load(images[pos]).into(tabBinding.pictureSelected)
            }
            if (pos == 0) {
                tabBinding.pictureDefaultCard.visibility = View.GONE
            } else {
                tabBinding.pictureSelectedCard.visibility = View.GONE
            }
            tab.customView = tabBinding.root
        }.attach()
        setTabSelectedListener(tabLayout)
    }

    private fun setTabSelectedListener(
        tabLayout: TabLayout
    ) {
        tabLayout.addOnTabSelectedListener(

            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.let { view ->
                        view.findViewById<CardView>(R.id.pictureDefaultCard).visibility =
                            View.GONE
                        view.findViewById<CardView>(R.id.pictureSelectedCard).visibility =
                            View.VISIBLE
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.customView?.let { view ->
                        view.findViewById<CardView>(R.id.pictureDefaultCard).visibility =
                            View.VISIBLE
                        view.findViewById<CardView>(R.id.pictureSelectedCard).visibility =
                            View.GONE
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
//                    TODO nothing
                }
            }
        )
    }
}