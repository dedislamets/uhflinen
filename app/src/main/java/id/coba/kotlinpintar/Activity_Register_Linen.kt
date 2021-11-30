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
import androidx.appcompat.widget.Toolbar

class Activity_Register_Linen : AppCompatActivity(), View.OnClickListener {

    private lateinit var mFm: FragmentManager
    private lateinit var mFt: FragmentTransaction
    private lateinit var fragment6: Fragment_Register_Linen

    private lateinit var mFragmentCurrent: Fragment

    private lateinit var textView_title: TextView
    private lateinit var mSharedPreferences: SharedPreferences


    private  var mToast: Toast? = null
    private lateinit var toolbar: Toolbar

    companion object {
        lateinit var mUhfrManager: UHFRManager
        lateinit var mSetEpcs : Set<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_linen)

        initView()

        Util.initSoundPool(this)

        mSharedPreferences = getSharedPreferences("UHF", MODE_PRIVATE)

//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle("Register Linen")

//        toolbar.title =  "Register Linen"
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
//
//        toolbar?.setNavigationOnClickListener {
//            startActivity(
//                Intent(
//                    applicationContext,
//                    Activity_Setting::class.java
//                )
//            )
//        }

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

    }

    override fun onClick(v: View) {

    }


    private fun initView() {
        val no_transaksi = getIntent().getStringExtra("no_transaksi")

        fragment6 = Fragment_Register_Linen()
        mFragmentCurrent = fragment6
        val arguments = Bundle()
        arguments.putString( "no_transaksi" , no_transaksi)
        fragment6.setArguments(arguments)

        mFm = supportFragmentManager
        mFt = mFm.beginTransaction()
        mFt.add(R.id.framelayout_main, fragment6)
        mFt.commit()



    }

    override fun onResume() {
        super.onResume()
        try{
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


    //show toast
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

        if (mUhfrManager != null) {//close uhf module
            mUhfrManager.close()
//            mUhfrManager = null
        }
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        //        Log.e("main","destroy");
        if (mUhfrManager != null) {//close uhf module
            mUhfrManager.close()
//            mUhfrManager = null
        }
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
            val intentBiasa = Intent(this, Activity_Setting::class.java)
            startActivity(intentBiasa)
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
//            mUhfrManager = null
            Log.e("eee", "close!")
            //OperRegister();
        } else {
            //ShowDialog("错误信息","初始化签标（RFID）读取器失败。",2);
        }
    }
}
