package com.example.a16_1_videoview

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    private fun enableDarkMode() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        if (isDarkMode) {
            setTheme(R.style.Theme_16_1_VideoView)
        } else {
            setTheme(R.style.Theme_16_1_VideoView)
        }
    }

    fun setupToolbar(toolbarId: Int, needBackArrow: Boolean) {
        toolbar = findViewById(toolbarId)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        backButton = toolbar.findViewById(R.id.back_button)

        if (needBackArrow) {
            backButton.visibility = View.VISIBLE
            backButton.setOnClickListener {
                onBackPressed()
            }
        } else {
            backButton.visibility = View.GONE
        }
    }

    protected fun setupWindowInsets(viewId: Int) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(viewId)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Если в back stack есть фрагменты, вернуться к предыдущему
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()  // Закрыть активность, если фрагментов нет
                }
                true
            }

            R.id.action_exit -> {
                finishAffinity()  // Закрыть все активности
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.exit, menu)
        return true
    }
}