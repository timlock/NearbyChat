import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.view.MainActivity


class ChatUserAdapter (private val onItemClicked: (Profile?) -> Unit) : RecyclerView.Adapter<ChatUserAdapter.ViewHolder>()
{
    lateinit var context: Context

    var savedProfiles: List<Profile> = mutableListOf()
        set(profiles) {
            field = profiles.toMutableList()
            notifyDataSetChanged()
        }

    inner class ViewHolder(itemView: View, onItemClicked: (Profile?) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.chats_user_name)
        val userLastInteraction: TextView = itemView.findViewById(R.id.chats_user_last_interaction)
        val symbol: ImageView = itemView.findViewById(R.id.chats_user_symbol)
        val signalStrength: ImageView = itemView.findViewById(R.id.chats_user_signal_strength)
        val layout: View = itemView.findViewById(R.id.chats_user_layout)
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
        val profile: Profile = savedProfiles[position]
        viewHolder.userName.text = profile.name

        val dateFormat = SimpleDateFormat(context?.getString(R.string.date_pattern) + " " + context?.getString(R.string.time_pattern))

        viewHolder.userLastInteraction.text = dateFormat.format(profile.lastInteraction)

        viewHolder.symbol.setColorFilter(
            ResourcesCompat.getColor(context.resources,
                MainActivity.getUserColorRes(profile.color), null
            ))
        viewHolder.signalStrength.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                MainActivity.getSignalStrengthIcon(profile.signalStrength0to4())
            )
        )
        val typedValue = TypedValue()
        if(profile.unread) {
            context.theme.resolveAttribute(
                com.google.android.material.R.attr.colorPrimary,
                typedValue,
                true
            )
        } else {
            context.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnPrimary,
                typedValue,
                true
            )
        }
        viewHolder.layout.setBackgroundColor(
            typedValue.data
        )
        viewHolder.profile = profile
    }

    override fun getItemCount(): Int {
        return savedProfiles.size
    }
}