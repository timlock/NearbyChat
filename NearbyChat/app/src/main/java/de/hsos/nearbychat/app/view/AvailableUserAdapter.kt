import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Profile

class AvailableUserAdapter (private val availableProfiles: List<Profile>) : RecyclerView.Adapter<AvailableUserAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.available_user_name)
        val userDesc: TextView = itemView.findViewById(R.id.available_user_description)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableUserAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.available_user, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: AvailableUserAdapter.ViewHolder, position: Int) {
        val profile: Profile = availableProfiles[position]
        viewHolder.userName.text = profile.name
        viewHolder.userDesc.text = profile.description
    }

    override fun getItemCount(): Int {
        return availableProfiles.size
    }
}