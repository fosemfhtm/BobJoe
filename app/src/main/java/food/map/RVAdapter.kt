package food.map

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(val context: Context?, val food_photoList: ArrayList<Photo>) :
    RecyclerView.Adapter<RVAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return food_photoList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(food_photoList[position], context!!)
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val dogPhoto = itemView?.findViewById<ImageView>(R.id.dogPhotoImg)
        val dogName = itemView?.findViewById<TextView>(R.id.dogNameTv)

        fun bind (dog: Photo, context: Context) {
            if (dog.food_photo != "") {
                val resourceId = context.resources.getIdentifier("@drawable/${dog.food_photo}", "drawable", context.packageName)
                dogPhoto?.setImageResource(resourceId)
            } else {
                dogPhoto?.setImageResource(R.mipmap.ic_launcher)
            }
            dogName?.text = dog.name
        }
    }
}