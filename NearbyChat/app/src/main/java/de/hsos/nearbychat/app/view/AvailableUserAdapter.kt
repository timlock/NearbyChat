import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.view.MainActivity

class AvailableUserAdapter (private val onItemClicked: (Profile?) -> Unit) : RecyclerView.Adapter<AvailableUserAdapter.ViewHolder>()
{
    lateinit var context: Context
    private val availableProfiles: List<Profile> = mutableListOf()

    inner class ViewHolder(itemView: View, onItemClicked: (Profile?) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.available_user_name)
        val userDesc: TextView = itemView.findViewById(R.id.available_user_message)
        val symbol: ImageView = itemView.findViewById(R.id.available_user_symbol)
        val signalStrength: ImageView = itemView.findViewById(R.id.available_user_signal_strength)
        var profile: Profile? = null

        init {
            itemView.setOnClickListener{
                onItemClicked(profile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableUserAdapter.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.available_user, parent, false)
        return ViewHolder(view) {
            onItemClicked(it)
        }
    }

    override fun onBindViewHolder(viewHolder: AvailableUserAdapter.ViewHolder, position: Int) {
        val profile: Profile = availableProfiles[position]
        viewHolder.userName.text = profile.name
        viewHolder.userDesc.text = profile.description
        viewHolder.symbol.setColorFilter(
            ResourcesCompat.getColor(context.resources,
                MainActivity.getUserColorRes(profile.color), null
            ))
        viewHolder.signalStrength.setColorFilter(
            ResourcesCompat.getColor(context.resources,
                MainActivity.getUserColorRes(profile.color), null
            ))
        viewHolder.signalStrength.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_network_wifi_2_bar_24)) //TODO: visualize different strengths
        viewHolder.profile = profile
    }

    override fun getItemCount(): Int {
        return availableProfiles.size
    }
}