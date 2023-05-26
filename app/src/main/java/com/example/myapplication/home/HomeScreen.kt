package com.example.myapplication.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.account.*
import com.example.myapplication.databinding.ActivityHomeScreenBinding
import com.example.myapplication.entry.ResetPasswordActivity
import com.example.myapplication.viewmodel.ProductViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class HomeScreen : AppCompatActivity() {
    lateinit var binding:ActivityHomeScreenBinding
    private val fragmentManager= supportFragmentManager
    private lateinit var viewModel: ProductViewModel
    lateinit var snackBar: Snackbar
    lateinit var home:MenuItem
    private lateinit var cart:MenuItem
    lateinit var account:MenuItem
    private lateinit var networkChangeReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        snackBar=Snackbar.make(binding.snackbarView,"No internet available",Snackbar.LENGTH_INDEFINITE).setAnchorView(binding.bottomNav)
        networkChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (!isNetworkAvailable(this@HomeScreen)) {
                    snackBar.show()
                    return
                }
                else{
                    snackBar.dismiss()
                }
            }
        }
        registerReceiver(
            networkChangeReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(!checkPermission()){
                askPermission()
            }
        }
        val bottomNavView=findViewById<BottomNavigationView>(R.id.bottomNav).menu
        home=bottomNavView.findItem(R.id.homeButton)
        cart=bottomNavView.findItem(R.id.cartButton)
        account=bottomNavView.findItem(R.id.accountButton)
        val isCart=intent.getBooleanExtra("gotoCart",false)
        replaceFragment(MainFragment())

        if(isCart){
            cart.isChecked=true
            account.isEnabled=true
            home.isEnabled=true
            cart.isEnabled=false
            replaceFragment(CartFragment())
        }
        viewModel= ViewModelProvider(this)[ProductViewModel::class.java]

        binding.bottomNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.homeButton-> {
                    replaceFragment(MainFragment())
                    home.isEnabled=false
                    cart.isEnabled=true
                    account.isEnabled=true
                }
                R.id.cartButton->{
                    replaceFragment(CartFragment())
                    account.isEnabled=true
                    home.isEnabled=true
                    cart.isEnabled=false
                }
                R.id.accountButton->{
                    replaceFragment(ProfileFragment())
                    account.isEnabled=false
                    home.isEnabled=true
                    cart.isEnabled=true
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment:Fragment){

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()
    }

    fun gotoResetPassword(){
        intent= Intent(this, ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    fun gotoMyOrders(){
        intent= Intent(this, MyOrdersActivity::class.java)
        startActivity(intent)
    }
    fun gotoWishlist(){
        intent= Intent(this, WishlistActivity::class.java)
        startActivity(intent)
    }
    fun gotoProductView(
        PID: String,
        pName: String,
        pDesc: String,
        pImgUrl: String,
        price: String,
        wishListItemId: String?,
        cartItemId:String?,
        home:Boolean
    ){
        intent= Intent(this,ProductViewActivity::class.java).apply {
            putExtra("pName",pName)
            putExtra("PID",PID)
            putExtra("pDesc",pDesc)
            putExtra("price",price)
            putExtra("pImgUrl",pImgUrl)
            putExtra("Wid",wishListItemId)
            putExtra("cId",cartItemId)
            putExtra("home",home)
        }
        startActivity(intent)
    }
    fun showSnackBar(msg:String){
        Snackbar.make(binding.snackbarView,msg,Snackbar.LENGTH_SHORT).setAnchorView(binding.bottomNav).show()
        viewModel.cartResponseData()
    }

    fun gotoLogin() {
        val i= Intent(this@HomeScreen,MainActivity::class.java).apply {
            flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(i)
    }

    fun replaceFragmentWithFocus() {
        replaceFragment(MainFragment())
        home.isEnabled=false
        cart.isEnabled=true
        account.isEnabled=true
        home.isChecked=true
    }
    fun replaceFragmentWithFocusOnCart() {
        replaceFragment(CartFragment())
        home.isEnabled=true
        cart.isEnabled=false
        account.isEnabled=true
        cart.isChecked=true
    }
    fun showAlert(title: String,msg:String,callback: (Boolean) -> Unit){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton("Yes"){ _: DialogInterface?, _: Int ->
            callback(true)
        }
        builder.setNegativeButton("No"){ _: DialogInterface?, _: Int ->
            callback(false)
        }
        builder.setNeutralButton("Cancel"){ _: DialogInterface?, _: Int ->
            callback(false)
        }
        val dialog = builder.create()
        dialog.show()
    }
    fun showProgressBar(){
        binding.progressBar.visibility= View.VISIBLE
        binding.overlay.visibility= View.VISIBLE
    }
    fun hideProgressBar(){
        binding.progressBar.visibility= View.GONE
        binding.overlay.visibility= View.GONE
    }

    fun showNotifications(){
        createNotificationChannel()
        val intent = Intent(this, MyOrdersActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this@HomeScreen, "12")
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle("Yay! Order Placed")
            .setContentText("Your order has been placed")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@HomeScreen,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this@HomeScreen, arrayOf( Manifest.permission.POST_NOTIFICATIONS), 123 )
                }
                return
            }
            notify(12, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Name"
            val descriptionText = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("12", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun isNetworkAvailable(context: Context) =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }
    fun checkPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(
                this@HomeScreen,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askPermission(){
        ActivityCompat.requestPermissions(this@HomeScreen, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 123 )
    }

}
