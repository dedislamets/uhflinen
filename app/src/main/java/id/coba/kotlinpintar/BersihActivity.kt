package id.coba.kotlinpintar

import java.util.ArrayList
import java.util.Set

import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.BRMicro.Tools
import com.handheld.uhfr.UHFRManager
import com.uhf.api.cls.Reader
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Intent
import android.database.Cursor
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar

class BersihActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mFm: FragmentManager
    private lateinit var mFt: FragmentTransaction
    private lateinit var fragment6: Fragment_Bersih

    private lateinit var mFragmentCurrent: Fragment

    private lateinit var textView_title: TextView
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var spinner_pic: Spinner

    private  var mToast: Toast? = null


    companion object {
        lateinit var mUhfrManager: UHFRManager
        lateinit var mSetEpcs : Set<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotor)

        initView()

        Util.initSoundPool(this)

        mSharedPreferences = getSharedPreferences("UHF", MODE_PRIVATE)


    }

    override fun onBackPressed() {
        startActivity(Intent(this, ListBersihActivity::class.java))
        this.finish()
    }

    override fun onClick(v: View) {

    }


    private fun initView() {
        val no_transaksi = getIntent().getStringExtra("no_transaksi")
        val hist = getIntent().getStringExtra("history")

        fragment6 = Fragment_Bersih()
        mFragmentCurrent = fragment6
        val arguments = Bundle()
        arguments.putString( "no_transaksi" , no_transaksi)
        arguments.putString( "history" , hist)
        fragment6.setArguments(arguments)

        mFm = supportFragmentManager
        mFt = mFm.beginTransaction()
        mFt.add(R.id.framelayout_main, fragment6)
        mFt.commit()
    }

    private fun loadSpinner() {

        val db = InputDbHelper(this)
        var serviceCursor: Cursor = db.serviceCursor
        var from: Array<String> = arrayOf("nama_ruangan")
        var to: IntArray = intArrayOf(android.R.id.text1)

        val adapterRuangan = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            serviceCursor,
            from,
            to,
            0
        )
        spinner_pic.setAdapter(adapterRuangan)
    }

    override fun onResume() {
        super.onResume()
        try {
            mUhfrManager = UHFRManager.getIntance()// Init Uhf module
            if (mUhfrManager != null) {
                mUhfrManager.setPower(
                    mSharedPreferences.getInt("readPower", 30),
                    mSharedPreferences.getInt("writePower", 30)
                )//set uhf module power
                mUhfrManager.region =
                    Reader.Region_Conf.valueOf(mSharedPreferences.getInt("freRegion", 1))
                Toast.makeText(
                    applicationContext,
                    "FreRegion:" + Reader.Region_Conf.valueOf(
                        mSharedPreferences.getInt(
                            "freRegion",
                            1
                        )
                    ) +
                            "\n" + "Read Power:" + mSharedPreferences.getInt("readPower", 30) +
                            "\n" + "Write Power:" + mSharedPreferences.getInt("writePower", 30),
                    Toast.LENGTH_LONG
                ).show()
                showToast(getString(R.string.inituhfsuccess))
            } else {
                showToast(getString(R.string.inituhffail))
            }
        }catch (e: Throwable) {
            showToast("Device ini tidak mendukung..")
        }

    }

    private fun showToast(info: String) {
        if (mToast == null)
            mToast = Toast.makeText(this, info, Toast.LENGTH_SHORT)
        else
            mToast!!.setText(info)
        mToast!!.show()
    }

    override fun onPause() {
        super.onPause()
        //        Log.e("main","pause");
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        try {
            if (mUhfrManager != null) {//close uhf module
                mUhfrManager.close()
//            mUhfrManager = null
            }
        }catch (e: Throwable){

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mUhfrManager != null) {//close uhf module
                mUhfrManager.close()
            }
        }catch (e: Throwable){ }
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {

        val id = item.itemId
        if (id == R.id.action_about)
        {
            val packageManager = packageManager
            var packInfo: PackageInfo? = null
            try
            {
                packInfo = packageManager.getPackageInfo(packageName, 0)
                val version = packInfo!!.versionName//get this version
                showToast(
                    "Version:" + version
                            + "\nDate:" + "2017-05-20" + "\nType:" + mUhfrManager.hardware
                )
            }
            catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return true
        }else{
            startActivity(
                Intent(
                    applicationContext,
                    ListBersihActivity::class.java
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getRfidTest() {
        mSharedPreferences = getSharedPreferences("UHF", MODE_PRIVATE)
        Log.e("eee", "init !")
        if (mUhfrManager == null) {
            mUhfrManager = UHFRManager.getIntance()
        }
        if (mUhfrManager != null) {
            Log.e("eee", "init success!")
            mUhfrManager.setPower(
                mSharedPreferences.getInt("readPower", 20),
                mSharedPreferences.getInt("writePower", 20)
            )//t uhf module power
            mUhfrManager.region =
                Reader.Region_Conf.valueOf(mSharedPreferences.getInt("freRegion", 1))
            mUhfrManager.setCancleFastMode()
            var iCount = 0
            var m_Rfid = ""
            var epcList: List<Reader.TAGINFO>?
            while (iCount < 200) {
                Log.e("eee", "running!$iCount")

                try {
                    Thread.sleep(250)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                epcList = mUhfrManager.tagInventoryByTimer(50.toShort())
                if (epcList != null && epcList.size > 0) {
                    for (tfs in epcList) {
                        val epcdata = tfs.EpcId
                        val epc = Tools.Bytes2HexString(epcdata, epcdata.size)
                        val rssi = tfs.RSSI
                        m_Rfid = epc
                        Util.play(1, 0)
                        iCount = 200
                        break
                    }
                }
                iCount = iCount + 1
            }
            mUhfrManager.stopTagInventory()
            mUhfrManager.close()
            Log.e("eee", "close!")
            //OperRegister();
        } else {
            //ShowDialog("错误信息","初始化签标（RFID）读取器失败。",2);
        }
    }
}
