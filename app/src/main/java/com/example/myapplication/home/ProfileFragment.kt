package com.example.myapplication.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.example.myapplication.ApplicationClass
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileFragment : Fragment() {
    private var _binding:FragmentProfileBinding ?= null
    private val binding get() =_binding!!
    private lateinit var imagePickerActivityResult: ActivityResultLauncher<Intent>
    private val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentProfileBinding.inflate(inflater,container,false)
        val uid=ApplicationClass.sharedPreferences!!.getString("userId","email").toString()
        val name=ApplicationClass.sharedPreferences!!.getString("Name","User").toString()
        val email=ApplicationClass.sharedPreferences!!.getString("Email","User").toString()
        val mobileNumber=ApplicationClass.sharedPreferences!!.getString("MobileNo","User").toString()

        binding.myOrders.setOnClickListener{
            (activity as HomeScreen).gotoMyOrders()
        }
        binding.wishList.setOnClickListener{
            (activity as HomeScreen).gotoWishlist()
        }
        binding.changePassword.setOnClickListener {
            (activity as HomeScreen).gotoResetPassword()
        }
        imagePickerActivityResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result!=null){
                val imageUri: Uri? = result.data?.data
                if(imageUri!=null){
                    val uploadTask = storageRef.child("user/pictures/$uid").putFile(imageUri)
                    uploadTask.addOnSuccessListener {
                        storageRef.child("user/pictures/$uid").downloadUrl.addOnSuccessListener {
                            binding.profileImage.load(it)
                        }
                    }
                }
            }
            else{
                Log.e("failure","fasd")
            }
        }
        binding.logOut.setOnClickListener{

            (activity as HomeScreen).showAlert("Log Out","Do you really want to log out of device?"){
                if(it){
                    ApplicationClass.editor!!.clear().apply()
                    (activity as HomeScreen).gotoLogin()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if((activity as HomeScreen).checkPermission() && ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)){
                binding.notificationsButton.isChecked = true
                ApplicationClass.editor!!.putBoolean("Notification", true).apply()
            }
            else if (!(activity as HomeScreen).checkPermission() or !ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)) {
                binding.notificationsButton.isChecked = false
                ApplicationClass.editor!!.putBoolean("Notification", false).apply()
            }
        }
        storageRef.child("user/pictures/$uid").downloadUrl.addOnSuccessListener {
            binding.profileImage.load(it)
        }
            .addOnFailureListener{

            }
        binding.notificationsButton.isChecked =
            ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)
        binding.notificationsButton.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if((activity as HomeScreen).checkPermission() && ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)){
                    ApplicationClass.editor!!.putBoolean("Notification",true).apply()
                    binding.notificationsButton.isChecked=true
                }
                else if(!(activity as HomeScreen).checkPermission()){
                    Toast.makeText(requireContext(),"You need to give notification permission from setting",Toast.LENGTH_SHORT).show()
                    binding.notificationsButton.isChecked=false
                    ApplicationClass.editor!!.putBoolean("Notification",false).apply()
                }
            }
            if(ApplicationClass.sharedPreferences!!.getBoolean("Notification",false)){
                ApplicationClass.editor!!.putBoolean("Notification",false).apply()
                binding.notificationsButton.isChecked=false
            }
            else{
                ApplicationClass.editor!!.putBoolean("Notification",true).apply()
                binding.notificationsButton.isChecked=true
            }
        }

        binding.userName.text=getString(R.string.hi,name)
        binding.userEmail.text=email
        binding.raiseIssue.setOnClickListener {
            val i= Intent(Intent.ACTION_SENDTO).apply {
                val uriText = "mailto:" + Uri.encode("smartkart@example.com") +
                        "?subject=" + Uri.encode("issue: $uid")
                data=Uri.parse(uriText)
            }
                requireContext().startActivity(Intent.createChooser(i,"Choose"))
        }
        binding.profileImage.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type="image/*"
            imagePickerActivityResult.launch(galleryIntent)
        }
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as HomeScreen).replaceFragmentWithFocus()
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}