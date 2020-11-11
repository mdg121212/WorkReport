package com.mattg.aztecworkreport.ui


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mattg.aztecworkreport.R
import com.mattg.aztecworkreport.db.UserDatabase
import com.mattg.aztecworkreport.db.UserRepository
import com.mattg.aztecworkreport.helpers.BaseFragment
import kotlinx.android.synthetic.main.dialog_show_record.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.number_change_dialog.view.*
import kotlinx.android.synthetic.main.pieces_dialog.view.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.getInstance


class MainFragment : BaseFragment() {

    private var startTime = ""
    private var endTime = ""
    private var houseNumber: Int = 0
    private var pieces = 0
    private var startTimeHour = ""
    private var startTimeMinute = ""
    private var userNameToSave = ""
    private var statString = ""
    private var isStarted: Boolean = false

    //for args
    private val args: MainFragmentArgs by navArgs()


    private lateinit var mViewModel: MainViewModel

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

        setHasOptionsMenu(true)

        val userDao = UserDatabase.getInstance(requireContext()).userDao()
        val userRepository = UserRepository(userDao)

        mViewModel = MainViewModel(userRepository, args.name)

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnu_change_number -> {
                val view = layoutInflater.inflate(R.layout.number_change_dialog, null)
                val editText = view.et_new_number
                AlertDialog.Builder(requireContext()).setTitle("Enter new number for supervisor")
                    .setView(view)
                    .setPositiveButton("Save") { _, _ ->
                        if (editText.text.toString().length == 10) {
                            val newNumber = editText.text.toString()
                            val newNumberFormat = "+1$newNumber"

                            coroutineScope.launch {
                                mViewModel.setSuperNumber(newNumberFormat, args.name)
                            }
                            Toast.makeText(
                                requireContext(),
                                "Number successfully changed!\nNew number: $newNumberFormat",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Number not changed, must enter a 10 digit phone number.  ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
            R.id.mnu_view_record -> {
                showRecordDialog()
            }
            R.id.mnu_sign_out -> {
                showSignOutDialog()
            }
        }
        return true
    }

    private fun showSignOutDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Sign Out?")
            .setPositiveButton("Yes") { _, _ ->
                val action = MainFragmentDirections.actionMainFragmentToSignInFragment(true)
                findNavController().navigate(action)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    @ExperimentalStdlibApi
    @SuppressLint("UnlocalizedSms")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val finishButton = btn_main_finish
        val startButton = btn_main_start
        //for navArgs, get value like so with variable
        userNameToSave = args.name

        mViewModel.retrievedUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val nameString = mViewModel.getFormattedName()
            userNameToSave = nameString
            tv_name_text.text = nameString
            tv_house_number.text = it.housesDone.toString()
            //retrieving the start time
            startTime = it.currentJobStartTime.toString()
        })

        mViewModel.retrievedPieces.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            tv_pieces_number.text = it.toString()
            pieces = it
        })

        mViewModel.retrievedHouses.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            tv_house_number.text = it.toString()
            houseNumber = it
        })

        mViewModel.retrievedStats.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            statString = it
        })

        mViewModel.retrievedIsStarted.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            isStarted = it
            if (it == true) {
                tv_isStarted.text = getString(R.string.house_started_string)
                startButton.isClickable = false
                finishButton.isClickable = true

            } else if (it == false) {
                tv_isStarted.text = ""
                startTime = ""
                startButton.isClickable = true
                finishButton.isClickable = false
            }
        })


        btn_main_start.setOnClickListener {
            //update textview
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
            val startingTime: Date = getInstance().time
            val formattedTime = simpleDateFormat.format(startingTime)
            Log.d("TEST", "START TIME PRE FORMAT = $startTime")
            startTime = formattedTime.toString()
            Log.d("TEST", "START TIME POST FORMAT = $startTime")
            val splitStart = startTime.split(":")

            //getting hour and minute as a string
            startTimeHour = splitStart[0]
            startTimeMinute = splitStart[1]

            coroutineScope.launch {
                //update the user in the viewModel
                mViewModel.isStarted(true, args.name)
                mViewModel.userStartTime(startTime, args.name)
                mViewModel.userHouseNumber(houseNumber, args.name)
                mViewModel.userPiecesNumber(pieces, args.name)
            }

            btn_main_start.isClickable = false
            btn_main_finish.isClickable = true
        }
        //finish click listener
        btn_main_finish.setOnClickListener {
            houseNumber++
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
            val endingTime: Date = getInstance().time

            val formattedTimeTwo = simpleDateFormat.format(endingTime)
            Log.d("TEST", formattedTimeTwo)
            endTime = formattedTimeTwo.toString()

            val houseString =
                "House $houseNumber:\n Started at: $startTime\n Finished at: $endTime "
            showPiecesNumberDialog()
            //updating the job via viewModel
            coroutineScope.launch {
                mViewModel.isStarted(false, args.name)
                mViewModel.userFinishTime(endTime, args.name)
                mViewModel.userHouseNumber(houseNumber, args.name)
                mViewModel.userPiecesNumber(pieces, args.name)
                val startSplit = mViewModel.splitTimes(startTime)
                val endSplit = mViewModel.splitTimes(endTime)
                val startHour = startSplit[0]
                val startMinute = startSplit[1]
                val endHour = endSplit[0]
                val endMinute = endSplit[1]
                val endHoursInt = endHour.toInt() - startHour.toInt()
                val endMinutesInt = endMinute.toInt() - startMinute.toInt()
                //track hours and minutes totaling up before a reset
                mViewModel.hoursTotal += endHoursInt
                mViewModel.minutesTotal += endMinutesInt
                //add to result string
                mViewModel.setResultStats(houseString, args.name)

            }

            btn_main_start.isClickable = true
            btn_main_finish.isClickable = false

        }



        btn_finish_all.setOnClickListener {

            AlertDialog.Builder(requireContext()).setTitle("Done for the day?")
                .setPositiveButton("Done") { _, _ ->
                    //generate text/file
                    mViewModel.piecesTotal = pieces

                    var ratio = mViewModel.getPiecesPerHour()
                    if (ratio > 75) {
                        ratio = 0
                    }
                    composeMessageAndSend(ratio)

                    coroutineScope.launch { mViewModel.clearStats(args.name) }

                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }.show()
        }

    }

    private fun composeMessageAndSend(ratio: Int) {
        val number = mViewModel.retrievedUser.value!!.superNumber
        val houses = mViewModel.retrievedHouses.value.toString()
        val statsString = mViewModel.retrievedStats.value!!
        val date = Date()
        val sdf = SimpleDateFormat("EEE, MMM d, yy", Locale.getDefault())
        val dateToSave = sdf.format(date)
        val stringToSend =
            "Daily numbers for $userNameToSave: $dateToSave \nFinished $houses house(s) and hung $pieces pieces.\nStart Times: $statsString \n" +
                    " Pieces per hour: $ratio"

        //save to file on device
        val fileToSave = File(requireContext().filesDir, "storedStats.txt")
        fileToSave.appendText("\n\n$stringToSend")
        //text intent

        composeMessage(stringToSend, number!!)
    }

    private fun composeMessage(message: String, number: String) {
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", message)

        }
        startActivity(intent)
    }

    private fun showRecordDialog() {
        //get the file
        val fileToSave = File(requireContext().filesDir, "storedStats.txt")

        if (fileToSave.exists()) {
            //generate text from the file
            val text = fileToSave.readText()

            val recordDialog = AlertDialog.Builder(requireContext())
            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.dialog_show_record, null)
            //set the text in the view to the contents of the file
            view.tv_record_contents.text = text
            recordDialog.setView(view)

            recordDialog.setPositiveButton("Done") { dialog, _ ->
                dialog.dismiss()
            }
                .setNegativeButton("Delete") { _, _ ->
                    AlertDialog.Builder(requireContext()).setTitle("Delete record?")
                        .setPositiveButton("Yes") { _, _ ->
                            fileToSave.delete()
                            Toast.makeText(requireContext(), "Record deleted", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }.show()

                }
                .setNeutralButton("Email Result") { _, _ ->
                    AlertDialog.Builder(requireContext()).setTitle("Send results as email?")
                        .setPositiveButton("Yes") { _, _ ->
                            val emailIntent = generateEmailIntent(text)

                            try {
                                startActivity(emailIntent)
                            } catch (e: Error) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error sending: ${e.cause}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }.show()
        } else {
            Toast.makeText(requireContext(), "No record found...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateEmailIntent(text: String): Intent {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setData(Uri.parse("mailto:")).type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "")
        emailIntent.putExtra(Intent.EXTRA_CC, "")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Work record for: $userNameToSave")
        emailIntent.putExtra(Intent.EXTRA_TEXT, text)
        return emailIntent
    }

    override fun onDestroy() {
        //saving relevant variables if this is destroyed
        coroutineScope.launch {
            mViewModel.userStartTime(startTime, args.name)
            mViewModel.saveAllStats(args.name)
            mViewModel.isStarted(isStarted, args.name)
        }

        super.onDestroy()
    }

    private fun showPiecesNumberDialog(): Int {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)

        val view = layoutInflater.inflate(R.layout.pieces_dialog, null)
        val numberEditText = view.et_number_of_pieces as EditText

        builder.setView(view)

        //set up the ok button

        builder.setPositiveButton("Done") { _, _ ->

            if (!numberEditText.text.isNullOrBlank()) {
                val piecesNumber = numberEditText.text.toString().toInt()
                pieces += piecesNumber
                Log.d("fromDialog", "pieces = $pieces")
                coroutineScope.launch {
                    mViewModel.userPiecesNumber(pieces, args.name)
                }

            } else {
                Toast.makeText(context, "Please enter number of pieces", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
        return pieces

    }

}