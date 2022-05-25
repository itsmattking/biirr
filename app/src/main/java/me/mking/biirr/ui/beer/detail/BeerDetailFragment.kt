package me.mking.biirr.me.mking.biirr.ui.beer.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.mking.biirr.databinding.FragmentBeerDetailBinding
import me.mking.biirr.me.mking.biirr.ui.beer.list.ImageHolder
import me.mking.biirr.ui.State

@AndroidEntryPoint
class BeerDetailFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<BeerDetailViewModel>()

    private var _binding: FragmentBeerDetailBinding? = null
    private val binding: FragmentBeerDetailBinding get() = _binding!!

    private val args by navArgs<BeerDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeerDetailBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect {
                        onState(it)
                    }
                }
            }
        }
        viewModel.load(args.beerId)
    }

    private fun onState(state: State<BeerDetailViewData>) {
        when (state) {
            is State.Loading -> binding.progressBar.isVisible = true
            is State.Ready -> {
                with(binding) {
                    progressBar.isVisible = false
                    titleTextView.text = state.data.title
                    when (state.data.image) {
                        is ImageHolder.Drawable -> imageView.setImageResource(state.data.image.resId)
                        is ImageHolder.Url -> Glide.with(requireContext())
                            .load(state.data.image.url)
                            .transition(DrawableTransitionOptions.withCrossFade(300))
                            .into(imageView)
                    }
                    descriptionTextView.text = state.data.description
                    taglineTextView.text = state.data.tagLine
                    abuAbvTextView.text = state.data.abvIbu
                }
            }
        }
    }

}
