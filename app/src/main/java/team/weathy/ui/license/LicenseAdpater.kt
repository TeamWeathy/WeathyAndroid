package team.weathy.ui.license

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import team.weathy.R
import team.weathy.util.setOnDebounceClickListener

class LicenseAdpater(private val context: Context) : RecyclerView.Adapter<LicenseViewHolder>(){
    var data = mutableListOf<LicenseData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LicenseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_license, parent, false)
        return LicenseViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LicenseViewHolder, position: Int) {
        holder.bind(data[position])

        holder.itemView.setOnDebounceClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    interface ItemClickListener{
        fun onClick(view : View, position : Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener : ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}
