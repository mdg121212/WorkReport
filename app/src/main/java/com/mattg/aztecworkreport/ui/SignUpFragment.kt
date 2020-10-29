package com.mattg.aztecworkreport.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mattg.aztecworkreport.R
import com.mattg.aztecworkreport.helpers.BaseFragment
import com.mattg.aztecworkreport.models.User
import com.mattg.aztecworkreport.db.UserDatabase
import com.mattg.aztecworkreport.db.UserRepository
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.launch
import java.util.*
/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SignUpFragment : BaseFragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        btn_signup_done.setOnClickListener {
            coroutineScope.launch {
                val dao = UserDatabase.getInstance(requireContext()).userDao()
                val repository = UserRepository(dao)

                if(et_signup_username.text.toString().isBlank()){
                    mainScope.launch {  Toast.makeText(requireContext(), "Must enter a user name", Toast.LENGTH_SHORT).show() }
                }
                if(et_super_number.text.toString().isBlank()){
                    mainScope.launch {  Toast.makeText(requireContext(), "Must enter a 10 digit phone number", Toast.LENGTH_SHORT).show() }
                }

                if (et_signup_pin.text.toString().isBlank()) {
                   mainScope.launch {  Toast.makeText(requireContext(), "Must enter 4 digit pin", Toast.LENGTH_SHORT).show()}
                } else {
                    val nameToAdd = et_signup_username.text.toString().toLowerCase(Locale.ROOT)
                    val pinToAdd = et_signup_pin.text.toString().trim().toInt()
                    val length = pinToAdd.toString().length

                        val numberToAdd = et_super_number.text.toString()
                        val numberLength = numberToAdd.length



                    if (length == 4 && !nameToAdd.isBlank() && numberLength == 10) {
                        //add correct format to the number that was input, then generate the user with that number set for sms texts
                        val number = "+1$numberToAdd"
                        val userToAdd = User(nameToAdd, pinToAdd, number)
                        //add user to database
                        repository.addUser(userToAdd)
                        mainScope.launch {
                            toastSuccess()
                        }
                        findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                    } else {

                        mainScope.launch {
                            toastError()
                        }
                    }
                }
            }
        }
    }

    private fun toastSuccess() {
        Toast.makeText(requireContext(), "You successfully signed up!", Toast.LENGTH_SHORT).show()
    }


    private fun toastError(){
        Toast.makeText(requireContext(), "Must enter a user name, a 4 digit pin, and a 10 digit phone number", Toast.LENGTH_SHORT).show()
    }
}