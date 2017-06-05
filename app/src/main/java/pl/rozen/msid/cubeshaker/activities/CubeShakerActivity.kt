package pl.rozen.msid.cubeshaker.activities

import android.content.Context
import android.graphics.Color
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.os.Vibrator
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import kotlinx.android.synthetic.main.fragment_coin_shaker.*
import kotlinx.android.synthetic.main.fragment_cube_shaker.*
import kotlinx.android.synthetic.main.fragment_history.*

import pl.rozen.msid.cubeshaker.R
import pl.rozen.msid.cubeshaker.listeners.ShakeEventManager
import java.util.*
import kotlin.collections.ArrayList
import org.jetbrains.anko.*

class CubeShakerActivity : AppCompatActivity(), ShakeEventManager.ShakeListener {

    private lateinit var sd: ShakeEventManager
    private lateinit var vibrator: Vibrator
    private var history: MutableList<String> = ArrayList()


    override fun onResume() {
        super.onResume()
        sd.register()
    }

    override fun onPause() {
        super.onPause()
        sd.deregister()
    }

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    private lateinit var mViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube_shaker)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)


        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        sd = ShakeEventManager()
        sd.setListener(this)
        sd.init(this)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onShake() {
        val currentItemPosition = mViewPager.currentItem
        val currentFragment: Fragment = mSectionsPagerAdapter.getRegisteredFragment(currentItemPosition)
        val historyFragmentFragment: HistoryFragment = mSectionsPagerAdapter.getRegisteredFragment(0) as HistoryFragment
        if (currentFragment is RandomMachineFragment) {
            val currentRandomFragment: RandomMachineFragment = currentFragment as RandomMachineFragment
            var result = currentRandomFragment.update()
            result += (" ~ " + currentRandomFragment.getDescription())
            history.add(0, result)
            historyFragmentFragment.arrayAdapter.notifyDataSetChanged()
            vibrator.vibrate(100)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cube_shaker, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_history -> {
                mViewPager.setCurrentItem(HISTORY.first, true)
                return true
            }
            R.id.action_about -> {
                startActivity<AboutActivity>()
                return true
            }
            R.id.action_settings -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


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


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val registeredFragments: SparseArray<Fragment> = SparseArray<Fragment>()

        override fun getItem(position: Int): Fragment {
            val toReturn: Fragment = when (position) {
                0 -> HistoryFragment.newInstance(position + 1)
                1 -> CubeFragment.newInstance(CUBE_SIX.first)
                2 -> CoinFragment.newInstance(COIN.first)
                3 -> CubeFragment.newInstance(CUBE_TEN.first)
                4 -> CubeFragment.newInstance(CUBE_TWELVE.first)
                5 -> CubeFragment.newInstance(CUBE_SIXTEEN.first)
                6 -> CubeFragment.newInstance(CUBE_TWENTY.first)
                else -> CubeFragment.newInstance(CUBE_SIX.first)
            }
            return toReturn
        }

        override fun getCount(): Int {
            return ITEM_COUNT
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val strId = when (position) {
                0 -> HISTORY.second
                1 -> CUBE_SIX.second
                2 -> COIN.second
                3 -> CUBE_TEN.second
                4 -> CUBE_TWELVE.second
                5 -> CUBE_SIXTEEN.second
                6 -> CUBE_TWENTY.second
                else -> CUBE_SIX.second
            }
            return resources.getString(strId)
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val fragment: Fragment = super.instantiateItem(container, position) as Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }

        fun getRegisteredFragment(position: Int): Fragment {
            return registeredFragments.get(position)
        }
    }

    companion object {
        val HISTORY = Pair(0, R.string.history_title)
        val CUBE_SIX = Pair(6, R.string.cube_6_title)
        val COIN = Pair(2, R.string.coin_title)
        val CUBE_TEN = Pair(10, R.string.cube_10_title)
        val CUBE_TWELVE = Pair(12, R.string.cube_12_title)
        val CUBE_SIXTEEN = Pair(16, R.string.cube_16_title)
        val CUBE_TWENTY = Pair(20, R.string.cube_20_title)

        val ITEM_COUNT = 7
    }
}

