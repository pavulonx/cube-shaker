package pl.rozen.msid.cubeshaker.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_coin_shaker.*
import kotlinx.android.synthetic.main.fragment_cube_shaker.*
import kotlinx.android.synthetic.main.fragment_history.*
import org.jetbrains.anko.imageResource
import pl.rozen.msid.cubeshaker.R
import pl.rozen.msid.cubeshaker.activities.CubeShakerActivity
import java.util.*


class HistoryFragment : Fragment() {
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var parentActivity: CubeShakerActivity

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentActivity = activity as CubeShakerActivity
        arrayAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, parentActivity.history)
        history_list_view.adapter = arrayAdapter

        fab.setOnClickListener { view ->
            if (parentActivity.history.size > 0) {
                val historyOld: MutableList<String> = ArrayList()
                parentActivity.history.forEach { elem -> historyOld.add(elem) }
                parentActivity.history.clear()
                arrayAdapter.notifyDataSetChanged()
                val sb = Snackbar.make(view, getString(R.string.history_cleared_info), Snackbar.LENGTH_LONG)
                sb.setAction(getString(R.string.history_cleared_undo), {
                    parentActivity.history.addAll(historyOld)
                    arrayAdapter.notifyDataSetChanged()
                })
                sb.show()
            }
        }
    }

    companion object {

        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(sectionNumber: Int): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}

class CubeFragment : Fragment(), RandomMachineFragment {

    val random = Random()
    var sides: Int = 6
    val colors: List<Int> = listOf(Color.BLUE, Color.CYAN, Color.BLACK, Color.DKGRAY, Color.GREEN, Color.MAGENTA, Color.RED)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_cube_shaker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sides = arguments.getInt(ARG_SECTION_NUMBER)
        section_label.text = getString(R.string.section_format, sides)
        random_result.visibility = View.GONE
    }

    private fun toss() = random.nextInt(sides) + 1
    private fun randomColor() = colors[random.nextInt(colors.size)]

    override fun update(): String {
        val nextResult = toss()
        val result = nextResult.toString()
        random_result.text = result
        random_result.setTextColor(randomColor())
        random_result.visibility = View.VISIBLE
        return result
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(sectionNumber: Int): CubeFragment {
            val fragment = CubeFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getDescription(): String {
        return getString(R.string.description_cube, sides)
    }
}

class CoinFragment : Fragment(), RandomMachineFragment {

    val random = Random()
    var sides: Int = 6

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_coin_shaker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sides = arguments.getInt(ARG_SECTION_NUMBER)
        coin_iv.visibility = View.GONE
    }

    private fun toss() = random.nextBoolean()

    override fun update(): String {
        val nextResult = toss()
        val result = if (nextResult) getString(R.string.coin_head_label) else getString(R.string.coin_tail_label)
        val imageRes = if (nextResult) R.drawable.coin_head else R.drawable.coin_tail
        coin_iv.imageResource = imageRes
        coin_iv.visibility = View.VISIBLE
        return result
    }

    override fun getDescription(): String {
        return getString(R.string.coin_title)
    }

    companion object {
        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(sectionNumber: Int): CoinFragment {
            val fragment = CoinFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }

}

interface RandomMachineFragment {
    fun update(): String
    fun getDescription(): String
}
