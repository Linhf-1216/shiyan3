package com.example.test003

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.app.Notification
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {


    // 实验1：动物数据
    private val animalNames = arrayOf("Lion", "Tiger", "Monkey", "Dog", "Cat", "Elephant")
    private val animalIcons = arrayOf(
        R.drawable.ic_lion, R.drawable.ic_tiger, R.drawable.ic_monkey,
        R.drawable.ic_dog, R.drawable.ic_cat, R.drawable.ic_elephant
    )
    private lateinit var listView: ListView
    private lateinit var tvTest: TextView  // 实验3：测试文本

    // 实验4：ActionMode相关变量
    private var actionMode: ActionMode? = null
    private var selectedPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 实验1：初始化ListView
        initListView()

        // 实验2：绑定对话框触发按钮（后续实验2直接用）
        val btnShowDialog = findViewById<Button>(R.id.btn_show_dialog)
        btnShowDialog.setOnClickListener {
            showCustomSignInDialog()
        }

        // 实验3：初始化测试文本
        tvTest = findViewById(R.id.tv_test)
    }

    // 实验1：初始化ListView
    private fun initListView() {
        listView = findViewById(R.id.lv_animals)
        val dataList = mutableListOf<Map<String, Any>>()
        for (i in animalNames.indices) {
            val map = mutableMapOf<String, Any>()
            map["icon"] = animalIcons[i]
            map["name"] = animalNames[i]
            dataList.add(map)
        }

        // 绑定SimpleAdapter
        val adapter = SimpleAdapter(
            this, dataList, R.layout.list_item,
            arrayOf("icon", "name"), intArrayOf(R.id.iv_animal, R.id.tv_animal)
        )
        listView.adapter = adapter

        // 列表项点击事件（Toast+通知）
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selected = animalNames[position]
            Toast.makeText(this, "选中：$selected", Toast.LENGTH_SHORT).show()
            sendNotification(selected)
        }

        // 实验4：长按事件（触发ActionMode）
        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, view, position, _ ->
                selectedPos = position
                if (actionMode == null) {
                    actionMode = startSupportActionMode(actionModeCallback as androidx.appcompat.view.ActionMode.Callback) as ActionMode?
                }
                view.isSelected = true
                true
            }
    }

    // 实验1：发送通知
    private fun sendNotification(animal: String) {
        val channelId = "animal_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "动物通知", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }.apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("列表选中通知")
            setContentText("你选中了：$animal")
            setAutoCancel(true)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }

    // 实验2：显示自定义对话框
    private fun showCustomSignInDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        val etUsername = dialogView.findViewById<EditText>(R.id.et_username)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_password)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnSignIn = dialogView.findViewById<Button>(R.id.btn_sign_in)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSignIn.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "登录成功！欢迎 $username", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // 实验3：加载菜单
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 实验3：处理菜单点击
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_size_small -> {
                tvTest.textSize = 10f; true
            }

            R.id.menu_size_medium -> {
                tvTest.textSize = 16f; true
            }

            R.id.menu_size_large -> {
                tvTest.textSize = 20f; true
            }

            R.id.menu_normal -> {
                Toast.makeText(this, "点击普通菜单项", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menu_color_red -> {
                tvTest.setTextColor(resources.getColor(R.color.red, theme))
                true
            }

            R.id.menu_color_black -> {
                tvTest.setTextColor(resources.getColor(R.color.black, theme))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }

    // 实验4：ActionMode回调
    private val actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_action_mode, menu)
            mode?.title = "1 selected"
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.menu_delete -> {
                    // 模拟删除逻辑（实际项目需操作数据源）
                    Toast.makeText(
                        this@MainActivity,
                        "删除：${animalNames[selectedPos]}",
                        Toast.LENGTH_SHORT
                    ).show()
                    mode?.finish()
                    true
                }

                else -> false
            }
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            selectedPos = -1
            for (i in 0 until listView.count) {
                listView.getChildAt(i)?.isSelected = false
            }
        }
    }
}