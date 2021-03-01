package team.weathy.ui.license

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import team.weathy.R

class LicenseViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    val title = itemView.findViewById<TextView>(R.id.tv_license)

    fun bind(licenseData : LicenseData){
        title.text = licenseData.title
    }
}