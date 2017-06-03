package pl.rozen.msid.cubeshaker.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.design.widget.TabLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_cube_shaker.*
import org.jetbrains.anko.textResource

import pl.rozen.msid.cubeshaker.R
import pl.rozen.msid.cubeshaker.listeners.ShakeEventListener
import java.util.*

class CubeShakerActivity : AppCompatActivity() {

    lateinit private var mShakeDetector: ShakeEventListener
    lateinit private var mSensorManager: SensorManager
    lateinit private var mAccelerometer: Sensor

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        mSensorManager.unregisterListener(mShakeDetector)
        super.onPause()
    }

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    /**
     * The [ViewPager] that will host the section contents.
     */
    private lateinit var mViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube_shaker)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager?.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


        // ShakeDetector initialization
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeEventListener(ShakeEventListener.OnShakeListener {

            val currentItemPosition = mViewPager.currentItem
            if (currentItemPosition > 0) {
                val currentFragment: RandomFragment = mSectionsPagerAdapter.getItem(currentItemPosition) as RandomFragment
                currentFragment.update()
                // TODO FIX THIS
                TODO("FIX THIS SHIT")

            }
//            Toast.makeText(applicationContext, "SHAAAAAKE!!!!", Toast.LENGTH_SHORT).show()
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_cube_shaker, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            R.id.action_history -> {
                mViewPager?.setCurrentItem(HISTORY.first, true)
                return true
            }
            R.id.action_about -> {
                return true
            }
            R.id.action_settings -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)

        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class HistoryFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_cube_shaker, container, false)
            val textView = rootView.findViewById(R.id.section_label) as TextView
//            textView.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): HistoryFragment {
                val fragment = HistoryFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    class RandomFragment : Fragment() {

        val random = Random()
        var sides: Int = 6

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_cube_shaker, container, false)
            val textView = rootView.findViewById(R.id.section_label) as TextView
            sides = arguments.getInt(ARG_SECTION_NUMBER)
            textView.text = getString(R.string.section_format, sides)
//            random_result.visibility = View.GONE
            return rootView
        }

        private fun toss() = random.nextInt(sides)

        fun update() {
            val nextResult = toss()
            random_result.text = nextResult.toString()
            random_result.visibility = View.VISIBLE
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): RandomFragment {
                val fragment = RandomFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a RandomFragment (defined as a static inner class below).
            val toReturn: Fragment = when (position) {
                0 -> HistoryFragment.newInstance(position + 1)
                1 -> RandomFragment.newInstance(CUBE_SIX.first)
                2 -> RandomFragment.newInstance(COIN.first)
                3 -> RandomFragment.newInstance(CUBE_TEN.first)
                4 -> RandomFragment.newInstance(CUBE_TWELVE.first)
                5 -> RandomFragment.newInstance(CUBE_SIXTEEN.first)
                6 -> RandomFragment.newInstance(CUBE_TWENTY.first)
                else -> RandomFragment.newInstance(CUBE_SIX.first)
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
