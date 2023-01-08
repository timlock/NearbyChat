import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Message
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatAdapter (private val context: Context?) : RecyclerView.Adapter<ChatAdapter.ViewHolder>()
{
    var lastDay: String = ""

    var messages: List<Message> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageIn: ConstraintLayout? = null
        var messageOut: ConstraintLayout? = null
        var textIn: TextView? = null
        var textOut: TextView? = null
        var timeIn: TextView? = null
        var timeOut: TextView? = null
        var date: TextView? = null
        var received: ImageView? = null
        init {
            messageIn = itemView.findViewById(R.id.chat_message_in)
            messageOut = itemView.findViewById(R.id.chat_message_out)
            textIn = itemView.findViewById(R.id.chat_message_in_text)
            timeIn = itemView.findViewById(R.id.chat_message_in_time)
            date = itemView.findViewById(R.id.chat_message_date)
            textOut = itemView.findViewById(R.id.chat_message_out_text)
            timeOut = itemView.findViewById(R.id.chat_message_out_time)
            received = itemView.findViewById(R.id.chat_message_out_received)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.message, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ChatAdapter.ViewHolder, position: Int) {
        val message: Message = messages[position]
        val contentView: TextView?
        val timeView: TextView?

        if(message.isSelfAuthored) {
            contentView = viewHolder.textOut
            timeView = viewHolder.timeOut
            viewHolder.messageIn?.visibility = View.INVISIBLE
        } else {
            contentView = viewHolder.textIn
            timeView = viewHolder.timeIn
            viewHolder.messageOut?.visibility = View.INVISIBLE
        }

        val internFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateFormat = SimpleDateFormat(context?.getString(R.string.date_pattern))
        val timeFormat = SimpleDateFormat(context?.getString(R.string.time_pattern))


        //TODO: Colorize messages in profile colors
        contentView?.text = message.content
        timeView?.text = timeFormat.format(message.timeStamp)

        if(internFormat.format(message.timeStamp) != lastDay) {
            lastDay = internFormat.format(message.timeStamp)
            viewHolder.date?.text = dateFormat.format(message.timeStamp)
        } else {
            viewHolder.date?.text = ""
        }

        if(message.isReceived) {
            viewHolder.received?.visibility = View.VISIBLE
        } else {
            viewHolder.received?.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}