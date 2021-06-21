package com.example.scalertaskk

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.create_interview_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateInterviewActivity : AppCompatActivity() {

    val PICKFILE_RESULT_CODE = 0
    var fileUri : Uri?=null
    var emailList = ArrayList<String>()
    var selectedDate:String?=null
    var startTime:String?=null
    var endTime:String?=null
    var database : AppDatabase?=null
    var interviewDao : InterviewDao?=null
    var interviewModel:InterviewModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_interview_fragment)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "interview_db"
        ).build()

        interviewDao = database!!.interviewDao()
        if (intent.extras!=null)
        {
            interviewModel = intent.getSerializableExtra("data") as InterviewModel
            selectedDate = interviewModel!!.date
            startTime = interviewModel!!.startTime
            endTime = interviewModel!!.endTime
            fileUri = Uri.parse(interviewModel!!.resume)
            for(i in interviewModel!!.emails!!.emails)
            {
                Log.e("Email",i)
            }
            val email = interviewModel!!.emails!!.emails[0].split(",")
            emailList.addAll(email)
            btn_delete.visibility = View.VISIBLE
            et_date.setText(selectedDate)
            et_start_time.setText(startTime)
            et_end_time.setText(endTime)
            for (i in emailList)
            {
                addNewChip(this,i,recipient_group_FL)
            }

            if(!fileUri!!.equals("null"))
            {
                tv_upload_name.setText(getFileName(fileUri!!))
            }

        }

        et_date.setOnClickListener(View.OnClickListener {
            chooseDueDate(this@CreateInterviewActivity)
        })

        et_start_time.setOnClickListener(View.OnClickListener {
            selectTime(this,et_start_time)
        })

        et_end_time.setOnClickListener(View.OnClickListener {
            selectTime(this,et_end_time)
        })

        recipient_input_ET.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (event == null || !event.isShiftPressed) {
                        val email = recipient_input_ET.text.toString()
                        if (email.isNullOrEmpty())
                        {
                            Toast.makeText(this,"Add Valid Email",Toast.LENGTH_LONG).show()
                        }
                        else
                        {
                            emailList.add(email)
                            addNewChip(this@CreateInterviewActivity , email , recipient_group_FL)
                            recipient_input_ET.setText("")
                            return@OnEditorActionListener true
                        }
                    }
                }
                false // pass on to other listeners.
            }
        )

        tv_upload.setOnClickListener(View.OnClickListener {
            selectFile()
        })

        btn_add.setOnClickListener(View.OnClickListener {
            if (validateData(this))
            {
                progressBar.visibility=View.VISIBLE
                interviewModel = InterviewModel(emails = Email(emailList),date = selectedDate,startTime = startTime,endTime = endTime,resume = fileUri.toString())
                lifecycleScope.launch {
                    addDatatoDB(interviewModel!!)
                }
                Toast.makeText(this,"Interview Saved Successfully",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,MainActivity::class.java))
            }
        })

        btn_delete.setOnClickListener(View.OnClickListener {
            progressBar.visibility=View.VISIBLE
            lifecycleScope.launch {
                withContext(Dispatchers.IO)
                {
                    interviewDao!!.delete(interviewModel!!)
                }
            }
            Toast.makeText(this,"Interview Deleted Successfully",Toast.LENGTH_LONG).show()
            startActivity(Intent(this,MainActivity::class.java))
        })


    }

    suspend fun addDatatoDB(interviewModel: InterviewModel)= withContext(Dispatchers.IO)
    {
        interviewDao!!.insertAll(interviewModel!!)
    }


    private fun chooseDueDate(activity: CreateInterviewActivity) {
        val c: Calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(activity,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                c.set(year, (monthOfYear), dayOfMonth)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                selectedDate = sdf.format(c.time)
                et_date.setText(selectedDate)

            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun selectTime(activity: CreateInterviewActivity , editText: EditText) {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val mTimePicker = TimePickerDialog(activity,
            { timePicker, selectedHour, selectedMinute ->
                if (editText.id==R.id.et_start_time)
                {
                    startTime="$selectedHour:$selectedMinute"
                }
                else
                {
                    endTime="$selectedHour:$selectedMinute"
                }
                editText.setText("$selectedHour:$selectedMinute") },
            hour,
            minute,
            true
        )

        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }


    private fun addNewChip(context: Context, person: String, chipGroup: FlexboxLayout) {
        val chip = Chip(context)
        chip.text = person
        chip.isCloseIconEnabled = true
        chip.isClickable = true
        chip.isCheckable = false
        chipGroup.addView(chip as View, chipGroup.childCount)
        chip.setOnCloseIconClickListener {
            emailList.remove(chip.text)
            chipGroup.removeView(chip as View)

        }
    }

    private fun selectFile()
    {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose Your Resume")
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK)
        {
            if(data!!.data!=null)
            {
                fileUri = data!!.data!!
                tv_upload_name.text = getFileName(fileUri!!)
            }
        }
    }

    fun getFileName(uri: Uri): String? {
        /*var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result*/
        return File(uri.path).name
    }

    private fun validateData(context: Context):Boolean
    {
        if (selectedDate.isNullOrEmpty())
        {
            Toast.makeText(this,"Enter Valid Date",Toast.LENGTH_LONG).show()
            et_date.requestFocus()
            return false

        }
        else if (startTime.isNullOrEmpty())
        {
            Toast.makeText(this,"Enter Valid Start Time",Toast.LENGTH_LONG).show()
            et_start_time.requestFocus()
            return false

        }
        else if (endTime.isNullOrEmpty())
        {
            Toast.makeText(this,"Enter Valid End Time",Toast.LENGTH_LONG).show()
            et_end_time.requestFocus()
            return false

        }
        else if (emailList.size<2)
        {
            Toast.makeText(this,"Atleast 2 participants needed",Toast.LENGTH_LONG).show()
            return false
        }
        else
        {
            var flag=1
            progressBar.visibility=View.VISIBLE
            lifecycleScope.launch {
                withContext(Dispatchers.IO)
                {

                    val emails = interviewDao!!.checkInterview(selectedDate!!,startTime!!,endTime!!)
                    Log.e("Emails","$emails $emailList")
                    if(emails!=null)
                    {
                        for (i in emailList)
                        {
                            if (emails.emails.contains(i))
                            {
                                flag=0
                                Toast.makeText(context,"$i has another meeting",Toast.LENGTH_LONG).show()
                                return@withContext
                                break
                            }
                        }
                    }
                    progressBar.visibility=View.GONE
                }
            }


            if (flag==0)
            {
                return false
            }

        }
        return true
    }

}