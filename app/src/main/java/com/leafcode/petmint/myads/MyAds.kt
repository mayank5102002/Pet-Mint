package com.leafcode.petmint.myads

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.leafcode.petmint.databinding.FragmentMyAdsBinding
import com.leafcode.petmint.petDetails.DetailsActivity

//Fragment to show the ads of the user
class MyAds : Fragment() {

    //ViewModel of this fragment
    private val viewModel : MyAdsViewModel by viewModels()

    private lateinit var recyclerView : RecyclerView
    private lateinit var layoutManager : RecyclerView.LayoutManager

    private lateinit var progressBar: ProgressBar

    private lateinit var emptyListView : RelativeLayout
    private lateinit var errorView : RelativeLayout
    private lateinit var refreshSwipe: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentMyAdsBinding = FragmentMyAdsBinding.inflate(layoutInflater,container,false)

        //Adding the floating button for new pet button
        val addPetButton = binding.addPetFloatingButton
        recyclerView = binding.myAdsRecyclerView
        layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
        emptyListView = binding.emptyListView
        progressBar = binding.myAdsProgressBar
        refreshSwipe = binding.refreshMyAds
        errorView = binding.errorView
        errorView.visibility = View.GONE

        //Adding decoration to the recycler view
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )

        val adapter = MyAdsAdapter(MyAdsClickListener {
            val intent = Intent(this.requireActivity(), DetailsActivity::class.java)
            intent.putExtra("currentPet",it)
            startActivity(intent)
        })

        recyclerView.adapter = adapter

        viewModel.myAdsList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(it)
                if (viewModel.locations.size > viewModel.previousLocations.size){
                    adapter.notifyItemRangeInserted(viewModel.previousLocations.size,
                        viewModel.locations.size-viewModel.previousLocations.size)
                } else if (viewModel.locations.size < viewModel.previousLocations.size){
                    adapter.notifyItemRangeRemoved(viewModel.locations.size,
                    viewModel.previousLocations.size-viewModel.locations.size)
                }
                emptyListView.visibility = View.GONE
                errorView.visibility = View.GONE
                disableProgressBar()
                disableRefreshBar()
                viewModel.setPrevious(viewModel.locations)
            } else {
                emptyViewSet()
                disableProgressBar()
                disableRefreshBar()
            }
        })

        viewModel.locationReady.observe(viewLifecycleOwner, Observer {
            if (it == true){
                if (!viewModel.locations.isNullOrEmpty()){
                    if (viewModel.locations != viewModel.previousLocations) {
                        viewModel.getAds()
                        viewModel.locationExecuted()
                    } else {
                        viewModel.locationExecuted()
                        disableRefreshBar()
                        disableProgressBar()
                    }
                } else {
                    emptyViewSet()
                    disableProgressBar()
                    disableRefreshBar()
                    viewModel.locationExecuted()
                }
            }
        })

        viewModel.adsReady.observe(viewLifecycleOwner, Observer {
            if (it == true){
                if (!viewModel.ads.isNullOrEmpty()) {
                    viewModel.updateMyAdsList(viewModel.ads)
                    viewModel.adsExecuted()
                } else {
                    emptyViewSet()
                    disableProgressBar()
                    disableRefreshBar()
                    viewModel.adsExecuted()
                }
            }
        })

        viewModel.isFaulty.observe(viewLifecycleOwner, Observer {
            if (it == true){
                viewModel.faultToastGiven()
                disableProgressBar()
                disableRefreshBar()
                errorViewSet()
            }
        })

        viewModel.setEmptyView.observe(viewLifecycleOwner, Observer {
            if (it == true){
                emptyViewSet()
                disableProgressBar()
                disableRefreshBar()
                viewModel.ViewSetEmpty()
            }
        })

        //Click listener for the add pet button
        addPetButton.setOnClickListener{
            goToAddPetActivity()
        }

        refreshSwipe.setOnRefreshListener {
            if (checkInternetConnectivity()) {
                getLocations()
            } else {
                disableProgressBar()
                disableRefreshBar()
                errorViewSet()
            }
        }

        if (checkInternetConnectivity()) {
            getLocations()
            progressBar.visibility = View.VISIBLE
        } else {
            disableRefreshBar()
            disableProgressBar()
            errorViewSet()
        }

        return binding.root
    }

    private fun getLocations(){
        viewModel.getLocations()
        emptyListView.visibility = View.GONE
    }

    private fun emptyViewSet(){
        emptyListView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun errorViewSet(){
        emptyListView.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
    }

    private fun disableProgressBar(){
        if (progressBar.isVisible) {
            progressBar.visibility = View.GONE
        }
    }

    private fun disableRefreshBar(){
        if (refreshSwipe.isRefreshing){
            refreshSwipe.isRefreshing = false
        }
    }

    //Function the go to add pet activity
    private fun goToAddPetActivity() {
        this.findNavController().navigate(MyAdsDirections.actionMyAdsToAddPetActivity())
    }

    companion object{
        val TAG = "MyAdsFragment"
    }

    private fun checkInternetConnectivity() : Boolean{

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo?.isConnected != null){
            return networkInfo.isConnected
        } else {
            return false
        }

    }

}