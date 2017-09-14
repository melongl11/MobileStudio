package com.example.melon.mobilestudio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.CursorAdapter
import kotlinx.android.synthetic.main.activity_order2.*

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order2)

        finalorder.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("주문하시겠습니까?")
            builder.setPositiveButton("예"){dialog, whichButton ->
                val intent = Intent(this,OrderFinishActivity::class.java)
                intent.putExtra("address",et_useraddress.text.toString())
                intent.putExtra("require",et_require.text.toString())
                startActivity(intent)
            }
            builder.setNegativeButton("아니오"){dialog,whichButton ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
    }
}
