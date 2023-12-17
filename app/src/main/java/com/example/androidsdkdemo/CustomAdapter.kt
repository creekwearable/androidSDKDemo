import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.androidsdkdemo.R
import com.example.model.ScanDeviceModel
import com.example.mylibrary.CreekManager


class CustomAdapter(private val context: Context,  var data: List<ScanDeviceModel>) : BaseAdapter() {

    override fun getCount(): Int {
        return if (data.isNotEmpty()) {
            data.size
        } else {
            // 如果数据为空，返回 1 来显示提示项
            1
        }
    }

    override fun getItem(position: Int): Any {
        return if (data.isNotEmpty()) {
            data[position]
        } else {
            // 如果数据为空，返回一个特殊的空对象
            EmptyItem
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (data.isNotEmpty()) {
            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val item = getItem(position) as ScanDeviceModel
            viewHolder.nameTextView.text = item.device?.name + "\n" + (item.macAddress ?: item.device?.id)
            viewHolder.connectDeviceView.setOnClickListener {
                if (viewHolder.connectDeviceView.text == "connect"){

                    CreekManager.sInstance.connect(item.device?.id ?: "", connect = {
                            connectState: Boolean ->
                        if (connectState){
                            viewHolder.connectDeviceView.text = "disConnect"
                        }
                    })
                }else{
                    CreekManager.sInstance.disconnect(success = {
                        viewHolder.connectDeviceView.text = "connect"
                    }, failure = { _, _ ->})
                }


            }
            return view
        } else {
            val emptyView = LayoutInflater.from(context).inflate(R.layout.empty_list_item_layout, parent, false)
            val emptyTextView = emptyView.findViewById<TextView>(R.id.emptyTextView)
            emptyTextView.text = "No items found"
            return emptyView
        }
    }

    fun setDeviceModel(listScanDeviceModel: List<ScanDeviceModel>) {
        this.data =listScanDeviceModel;
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        val nameTextView: TextView = view.findViewById(R.id.deviceName)
        val connectDeviceView: Button = view.findViewById(R.id.connectDevice)

    }
}

object EmptyItem  // 一个特殊的空对象，用于表示空数据项
