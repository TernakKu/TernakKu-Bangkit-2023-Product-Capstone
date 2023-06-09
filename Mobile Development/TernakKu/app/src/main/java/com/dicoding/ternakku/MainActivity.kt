package com.dicoding.ternakku

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.ternakku.data.retrofit.ApiConfig
import com.dicoding.ternakku.data.retrofit.response.DiseasesItem
import com.dicoding.ternakku.data.retrofit.response.ListDiseaseResponse
import com.dicoding.ternakku.databinding.ActivityMainBinding
import com.dicoding.ternakku.preference.LoginPreference
import com.dicoding.ternakku.ui.history.HistoryActivity
import com.dicoding.ternakku.ui.login.LoginActivity
import com.dicoding.ternakku.ui.scan.ScanActivity
import com.dicoding.ternakku.viewmodelfactory.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataSetting")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        getListData()

        val layoutManager = LinearLayoutManager(this)
        binding.rVList.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rVList.addItemDecoration(itemDecoration)


        binding.efbScan.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanActivity::class.java)
            startActivity(intent)
        }

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }

        })

        binding.icLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Apakah anda yakin ingin keluar ?")
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    mainViewModel.logout()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        binding.icHistory.setOnClickListener {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }
        setUpFloatingActionButton()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getLoginUser().observe(this) {
            getListData()
        }
    }

    private fun setViewModel(){
        val pref = LoginPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[MainViewModel::class.java]

        mainViewModel.getLoginUser().observe(this) { user ->
            if (user.isLogin) {

            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

        }
    }

    private fun getListData(){
        showLoading(true)
        val client = ApiConfig.getApiService().getListDiseases()
        client.enqueue(object : Callback<ListDiseaseResponse> {
            override fun onResponse(
                call: Call<ListDiseaseResponse>,
                response: Response<ListDiseaseResponse>
            ) {
                showLoading(false)
                if(response.isSuccessful){
                    val responsBody = response.body()
                    if (responsBody!= null){
                        setData(responsBody.diseases)
                    }
                } else {
                    Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ListDiseaseResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(this@MainActivity, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setData(listPenyakit: List<DiseasesItem>){
        val adapter = ListPenyakitAdapter(listPenyakit)
        binding.rVList.adapter = adapter
    }



    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setUpFloatingActionButton() {
        binding.efbScan.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScanActivity::class.java))
        }
        // Detect a scroll and respond based on the direction
        binding.rVList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0){ // Scrolling down
                    binding.efbScan.extend()
                }else{ // Scrolling up
                    binding.efbScan.shrink()
                }
            }
        })
    }

    companion object{
        const val TAG = "MainActivity"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAMED = "extra_named"
        const val EXTRA_DETAIL = "extra_detail"
        const val EXTRA_HANDLE = "extra_handle"
    }


}