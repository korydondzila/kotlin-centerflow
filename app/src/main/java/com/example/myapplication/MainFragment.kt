package com.example.myapplication


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.binding.FragmentDataBindingComponent
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.dependencyInjection.Injectable
import com.example.myapplication.utilities.autoCleared
import com.example.myapplication.utilities.toDp
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import javax.inject.Inject

class MainFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    val viewModel: MainViewModel by viewModels {
        viewModelFactory
    }

    var binding by autoCleared<FragmentMainBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var adapter by autoCleared<CenterFlowAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = viewModel

        val manager = FlexboxLayoutManager(context, FlexDirection.ROW)
        manager.justifyContent = JustifyContent.CENTER
        manager.alignItems = AlignItems.CENTER

        adapter = CenterFlowAdapter(
            dataBindingComponent,
            appExecutors,
            recyclerView = binding.numberBallCollection,
            cellSize = context?.resources?.getDimensionPixelSize(R.dimen.number_ball_size)?.toDp() ?: 32,
            interItemSpacing = 4,
            lineSpacing = 6
        )

        binding.numberBallCollection.layoutManager = manager
        binding.numberBallCollection.adapter = adapter

        viewModel.numbers.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.setPadding(it.size)
        })
    }
}
