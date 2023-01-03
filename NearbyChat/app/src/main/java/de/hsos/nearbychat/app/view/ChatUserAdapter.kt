import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Profile

class ChatUserAdapter (private val availableProfiles: List<Profile>) : RecyclerView.Adapter<ChatUserAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.chat_user_name)
        val userMessage: TextView = itemView.findViewById(R.id.chat_user_message)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.chat_user, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ChatUserAdapter.ViewHolder, position: Int) {
        val profile: Profile = availableProfiles[position]
        viewHolder.userName.text = profile.name
        //viewHolder.userMessage.text = TODO: set to last message
    }

    override fun getItemCount(): Int {
        return availableProfiles.size
    }
}