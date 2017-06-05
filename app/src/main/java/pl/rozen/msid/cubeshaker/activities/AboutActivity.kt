package pl.rozen.msid.cubeshaker.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import pl.rozen.msid.cubeshaker.R
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.toast
import pl.rozen.msid.cubeshaker.BuildConfig


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        app_version.text = getString(R.string.app_version_about, BuildConfig.VERSION_NAME)

        val onClickListener = View.OnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_site)))
                startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                toast(R.string.share_error)
            }
        }

        about_face.setOnClickListener(onClickListener)
        app_version.setOnClickListener(onClickListener)
        about_app_author.setOnClickListener(onClickListener)
        about_git_site_link.setOnClickListener(onClickListener)
    }


}
