package com.mattg.aztecworkreport.ui


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mattg.aztecworkreport.R
import com.mattg.aztecworkreport.db.UserDatabase
import com.mattg.aztecworkreport.db.UserRepository
import com.mattg.aztecworkreport.helpers.BaseFragment
import com.mattg.aztecworkreport.models.User
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.coroutines.launch
import java.util.*


const val SHARED_PREF_NAME = "workreportprefs"

class SignInFragment : BaseFragment() {
    private lateinit var mViewModel: MainViewModel
    private lateinit var sharedPref: SharedPreferences
    private var savedName = ""
    private var savedPin = 0
    private val args: SignInFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().moveTaskToBack(true)
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDao = UserDatabase.getInstance(requireContext()).userDao()
        val userRepository = UserRepository(userDao)
        sharedPref =
            requireActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

        if (args.isSignOut) {
            et_signin_username.text.clear()
            et_signin_pin.text.clear()
            checkBox_remember_me.isChecked = false
            val editor = sharedPref.edit()
            editor.putString("name", "")
                .putInt("pin", 0)
                .putBoolean("isChecked", false).apply()
            editor.apply()
        } else {

            val wasChecked = sharedPref.getBoolean(
                "isChecked",
                false
            )
            // if it wasn't checked, make sure it stays false
            if (wasChecked) {
                savedName = sharedPref.getString("name", "").toString()
                savedPin = sharedPref.getInt("pin", 0)
                et_signin_username.setText(savedName)
                et_signin_pin.setText(savedPin.toString())
                checkBox_remember_me.isChecked = true
                launchMainScreen(savedName, savedPin)
            }
        }



        btn_signup_loginscreen.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        btn_login.setOnClickListener {
            //get text values
            if (et_signin_username.text.toString().isNotEmpty() && et_signin_pin.text.toString()
                    .isNotEmpty()
            ) {
                //do a null check on input then assign input to query
                val userName =
                    et_signin_username.text.toString().trim().toLowerCase(Locale.ROOT)
                val userPin = et_signin_pin.text.toString().trim().toInt()

                if (checkBox_remember_me.isChecked) {
                    val editor = sharedPref.edit()
                    editor.putString("name", userName)
                        .putInt("pin", userPin)
                        .putBoolean("isChecked", true).apply()
                    editor.apply()


                    mViewModel = MainViewModel(userRepository, userName)


                } else {
                    val editor = sharedPref.edit()
                    editor.putBoolean("isChecked", false).apply()
                }



                coroutineScope.launch {
                    //get database access
                    val dao = UserDatabase.getInstance(requireContext()).userDao()
                    val repository = UserRepository(dao)
                    val userToGet = User(userName, userPin)

                    if (dao.getAllUsers() > 0) {

                        val isThere = repository.doesUserExist(userName)

                        if (isThere) {
                            val doesExist = repository.checkUser(userToGet)

                            if (userToGet.userName == doesExist.userName
                                && userToGet.pin == doesExist.pin
                            ) {

                                launchMainScreen(userName, userPin)


                            } else {
                                mainScope.launch { toastError() }
                            }
                        } else {
                            mainScope.launch { toastError() }
                        }


                    } else {
                        mainScope.launch {
                            toastError()
                        }
                    }
                }  //end of coroutine

            } else {
                toastFillError()
            }
        }
    }


    private fun launchMainScreen(userName: String, userPin: Int) {
        mainScope.launch {
            fun goToMainScreen() {
                val action =
                    SignInFragmentDirections.actionSignInFragmentToMainFragment(
                        userName, userPin
                    )

                findNavController().navigate(action)
            }
            goToMainScreen()

        }
    }

    private fun toastError() {
        Toast.makeText(
            requireContext(),
            "Invalid sign in.\nMake sure you have the correct user name and pin.",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun toastFillError() {
        Toast.makeText(requireContext(), "You must fill in both fields", Toast.LENGTH_SHORT).show()
    }

}