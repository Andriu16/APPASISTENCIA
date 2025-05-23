package dev.app.peru.guidecsharp.Vista.Otros

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment(val listener : (day : Int, month: Int, year : Int) ->Unit ):DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        var _diaOT = c.get(Calendar.DAY_OF_MONTH)
        var _mesOT = c.get(Calendar.MONTH)
        var _añoOT = c.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity as Context,this, _añoOT, _mesOT, _diaOT)
        //0picker.datePicker.minDate = c.timeInMillis
        return picker
    }
}