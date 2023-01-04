import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Profile

class ChatUserAdapter (private val availableProfiles: List<Profile>, private val onItemClicked: (Profile?) -> Unit) : RecyclerView.Adapter<ChatUserAdapter.ViewHolder>()
{
    lateinit var context: Context

    inner class ViewHolder(itemView: View, onItemClicked: (Profile?) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.chats_user_name)
        val userMessage: TextView = itemView.findViewById(R.id.chats_user_message)
        val symbol: ImageView = itemView.findViewById(R.id.chats_user_symbol)
        var profile: Profile? = null

        init {
            itemView.setOnClickListener{
                onItemClicked(profile)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserAdapter.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.chat_user, parent, false)
        return ViewHolder(view) {
            onItemClicked(it)
        }
    }

    override fun onBindViewHolder(viewHolder: ChatUserAdapter.ViewHolder, position: Int) {
        val profile: Profile = availableProfiles[position]
        viewHolder.userName.text = profile.name
        if(profile.messages.isNotEmpty()) {
            viewHolder.userMessage.text = profile.messages.last().content
        } else {
            viewHolder.userMessage.text = ""
        }
        viewHolder.symbol.setColorFilter(profile.color)
        if(profile.isAvailable) {
            viewHolder.symbol.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_baseline_person_pin_circle_24
                )
            )
        }
        viewHolder.profile = profile
    }

    override fun getItemCount(): Int {
        return availableProfiles.size
    }
}