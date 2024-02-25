package com.loseweight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.loseweight.objects.Spinner
import com.loseweight.R
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils
import org.json.JSONArray
import java.util.*


class SpinnerSelAdapter(c: Context) : BaseAdapter(), Filterable {

    // private Context mContext;
    private val infalter: LayoutInflater
    var all: ArrayList<Spinner>? = ArrayList()
        private set
    internal var dataSource = ArrayList<Spinner>()

    internal var isFilterable = false

    val selectedAll: ArrayList<Spinner>
        get() {
            val data = ArrayList<Spinner>()

            for (spinner in this.all!!) {
                if (spinner.isSelected) {
                    data.add(spinner)
                }
            }

            return data
        }

    val selectedIds: String
        get() {
            var str = ""

            for (spinner in all!!) {
                if (spinner.isSelected) {
                    str = str + spinner.ID + ","
                }
            }

            if (str.length > 0) {
                str = str.substring(0, str.length - 1)
            }

            return str
        }

    val selectedIdList: ArrayList<String>
        get() {
            val data = ArrayList<String>()

            for (spinner in this.all!!) {
                if (spinner.isSelected) {
                    data.add(spinner.ID)
                }
            }

            return data
        }

    val selectedIdArray: JSONArray
        get() {
            val data = JSONArray()

            try {
                for (spinner in this.all!!) {
                    if (spinner.isSelected) {
                        data.put(spinner.ID)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return data
        }

    val selectedTitle: String
        get() {
            var str = ""

            for (spinner in all!!) {
                if (spinner.isSelected) {
                    str = str + spinner.title + ", "
                }
            }

            str = str.trim { it <= ' ' }
            if (str.length > 0) {
                str = str.substring(0, str.length - 1)
            }

            return str
        }

    val isSelectedAll: Boolean
        get() {

            for (spinner in this.all!!) {
                if (!spinner.isSelected) {
                    return false
                }
            }

            return true
        }

    val selectedCount: Int
        get() {

            var cnt = 0

            for (spinner in this.all!!) {
                if (spinner.isSelected) {
                    cnt = cnt + 1
                }
            }

            return cnt
        }

    val isSelectedAtleastOne: Boolean
        get() {

            for (spinner in this.all!!) {
                if (spinner.isSelected) {
                    return true
                }
            }

            return false
        }

    init {
        infalter = c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // mContext = c;
    }

    override fun getCount(): Int {
        return all!!.size
    }

    override fun getItem(position: Int): Spinner {
        return all!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setFilterable(isFilterable: Boolean) {
        this.isFilterable = isFilterable
    }

    fun addAll(files: ArrayList<Spinner>) {

        try {
            this.all!!.clear()
            this.all!!.addAll(files)

            if (isFilterable) {
                this.dataSource.clear()
                this.dataSource.addAll(files)
            }

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        notifyDataSetChanged()
    }

    fun isSelected(position: Int): Boolean {
        return all!![position].isSelected
    }

    fun changeSelection(position: Int, isMultiSel: Boolean) {

        for (i in all!!.indices) {
            if (position == i) {
                all!![i].isSelected = !all!![i].isSelected
            } else if (!isMultiSel) {
                all!![i].isSelected = false
            }
        }

        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        for (i in all!!.indices) {
            if (position == i) {
                all!![i].isSelected = true
            } else {
                all!![i].isSelected = false
            }
        }

        notifyDataSetChanged()
    }

    fun selectAll(selectall: Boolean) {
        for (i in all!!.indices) {
            all!![i].isSelected = selectall
        }

        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()

            convertView = infalter.inflate(R.layout.spinner_sel_item, null)
            holder.tvMenuTitle = convertView!!
                    .findViewById<View>(R.id.tvSpinnerTitle) as TextView

            holder.chkSpinnetItem = convertView
                    .findViewById<View>(R.id.chkSpinnetItem) as CheckBox
            holder.chkSpinnetItem!!.isFocusable = false
            holder.chkSpinnetItem!!.isEnabled = false
            holder.chkSpinnetItem!!.isClickable = false
            holder.chkSpinnetItem!!.isLongClickable = false

            convertView.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
        }

        try {

            holder.chkSpinnetItem!!.isChecked = all!![position].isSelected
            holder.tvMenuTitle!!.text = all!![position].title

            //            holder.chkSpinnetItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //                @Override
            //                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            //                    changeSelection(position, true);
            //                }
            //            });

        } catch (e: Exception) {
            Utils.sendExceptionReport(e)
        }

        return convertView
    }

    inner class ViewHolder {
        internal var tvMenuTitle: TextView? = null
        internal var chkSpinnetItem: CheckBox? = null
    }

    override fun getFilter(): Filter? {

        return if (isFilterable) {
            PTypeFilter()
        } else null

    }

    private inner class PTypeFilter : Filter() {

        override fun publishResults(prefix: CharSequence, results: Filter.FilterResults) {
            // NOTE: this function is *always* called from the UI thread.

            all = results.values as ArrayList<Spinner>
            if (all != null) {
                notifyDataSetChanged()
            } else {
                all = dataSource
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