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
        val dogPhoto1 = itemView?.findViewById<ImageView>(R.id.dogPhotoImg1)
        val dogPhoto2 = itemView?.findViewById<ImageView>(R.id.dogPhotoImg2)
        val dogPhoto3 = itemView?.findViewById<ImageView>(R.id.dogPhotoImg3)


        fun bind (dog: Photo, context: Context) {
            if (dog.food_photo != "") {
                val resourceId = context.resources.getIdentifier("@drawable/${dog.food_photo}", "drawable", context.packageName)
                dogPhoto1?.setImageResource(resourceId)
                dogPhoto2?.setImageResource(resourceId)
                dogPhoto3?.setImageResource(resourceId)

            } else {
                dogPhoto1?.setImageResource(R.mipmap.ic_launcher)
                dogPhoto2?.setImageResource(R.mipmap.ic_launcher)
                dogPhoto3?.setImageResource(R.mipmap.ic_launcher)

            }
        }
    }
}