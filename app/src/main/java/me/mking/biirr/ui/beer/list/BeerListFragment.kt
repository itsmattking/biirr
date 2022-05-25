package me.mking.biirr.me.mking.biirr.ui.beer.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.mking.biirr.databinding.FragmentBeerListBinding
import me.mking.biirr.ui.State

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class BeerListFragment : Fragment() {
    private val viewModel by viewModels<BeerListViewModel>()

    private val adapter by lazy { BeerListAdapter() }

    private var _binding: FragmentBeerListBinding? = null
    private val binding: FragmentBeerListBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeerListBinding.inflate(inflater, container, false)
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
                launch {
                    viewModel.viewAction.collect {
                        onViewAction(it)
                    }
                }
            }
        }
        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int) = when {
                        adapter.getItemViewType(position) == BeerListAdapter.ViewType.MORE.ordinal -> 3
                        else -> 1
                    }
                }
            }
        binding.recyclerView.adapter = adapter
        adapter.listener = {
            viewModel.onClickEvent(it)
        }
        viewModel.load()
    }

    private fun onState(state: State<BeerListViewData>) {
        when (state) {
            is State.Ready -> {
                binding.progressBar.isVisible = false
                adapter.submitList(state.data.items)
            }
            is State.Loading -> binding.progressBar.isVisible = true
        }
    }

    private fun onViewAction(viewAction: BeerListViewAction) {
        when (viewAction) {
            is BeerListViewAction.ShowBeer -> findNavController().navigate(
                BeerListFragmentDirections.actionBeerListFragmentToBeerDetailFragment(viewAction.beerId)
            )
            is BeerListViewAction.ToError -> Toast.makeText(
                requireContext(),
                viewAction.message,
                Toast.LENGTH_LONG
            ).show()
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}
