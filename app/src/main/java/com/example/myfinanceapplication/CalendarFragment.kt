import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.myfinanceapplication.AddGoalFragment
import com.example.myfinanceapplication.R

class CalendarFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = AddGoalFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        calendarView.setBackgroundColor(resources.getColor(android.R.color.white))

        val calendarHeader: FrameLayout = view.findViewById(R.id.calendarHeader)
        calendarHeader.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))

        val cancelButton: Button = view.findViewById(R.id.cancelButton)
        val doneButton: Button = view.findViewById(R.id.doneButton)

        cancelButton.setOnClickListener {
            // Close the calendar
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        doneButton.setOnClickListener {
            // Save and pass the selected date back to the calling fragment
            // Implement your logic here
        }

        return view
    }
}
