package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.loseweight.R
import com.loseweight.objects.Spinner
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils
import java.util.*



class SpinnerAdapter(private val mContext: Context) : BaseAdapter(), Filterable {
    private val infalter: LayoutInflater
    private val data = ArrayList<Spinner>()
    private val dataSource = ArrayList<Spinner>()

    internal var isEnable = true

    internal var isFilterable = false

    init {
        infalter = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Spinner {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addAll(files: ArrayList<Spinner>) {

        try {

            this.data.clear()
            this.data.addAll(files)

            if (isFilterable) {
                this.dataSource.clear()
                this.dataSource.addAll(files)
            }

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun add(files: Spinner) {

        try {
            this.data.add(files)

            if (isFilterable) {
                this.dataSource.add(files)
            }

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()

            convertView = infalter.inflate(R.layout.spinner_item, null)
            holder.tvMenuTitle = convertView as TextView?

            convertView!!.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
        }

        try {
            holder.tvMenuTitle!!.setText(data[position].title)
        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        return convertView
    }

    inner class ViewHolder {
        internal var tvMenuTitle: TextView? = null
    }

    fun setParentCategEnabled(isEnable: Boolean) {
        this.isEnable = isEnable
    }

    override fun isEnabled(position: Int): Boolean {
        return super.isEnabled(position)
    }

    fun setFilterable(isFilterable: Boolean) {
        this.isFilterable = isFilterable
    }

    override fun getFilter(): Filter? {

        return if (isFilterable) {
            PTypeFilter()
        } else null

    }

    private inner class PTypeFilter : Filter() {

        override fun publishResults(prefix: CharSequence, results: Filter.FilterResults) {
            // NOTE: this function is *always* called from the UI thread.

            data.clear()
            data.addAll(results.values as ArrayList<Spinner>)
            if (data != null && !data.isEmpty()) {
                notifyDataSetChanged()
            } else {
                //                data.clear()
                //                data.addAll(dataSource)
                notifyDataSetChanged()
            }
        }

        override fun performFiltering(prefix: CharSequence?): Filter.FilterResults {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.

            val results = Filter.FilterResults()
            val new_res = ArrayList<Spinner>()
            if (prefix != null && prefix.toString().length > 0) {
                for (index in dataSource.indices) {

                    try {
                        val si = dataSource[index]

                        if (si.title.toLowerCase().contains(
                                        prefix.toString().toLowerCase())) {
                            new_res.add(si)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                results.values = new_res
                results.count = new_res.size

            } else {
                Debug.e("", "Called synchronized view")

                results.values = dataSource
                results.count = dataSource.size

            }

            return results
        }
    }
}