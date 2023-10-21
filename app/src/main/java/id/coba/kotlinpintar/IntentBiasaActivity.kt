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

class IntentBiasaActivity : AppCompatActivity(), View.OnClickListener {

     private lateinit var mFm: FragmentManager
     private lateinit var mFt: FragmentTransaction
     private lateinit var fragment1: Fragment1_Inventory
     private lateinit var fragment6: Fragment1_Scanner
     private lateinit var fragment2: Fragment2_ReadAndWrite
     private lateinit var fragment3: Fragment3_Lock
     private lateinit var fragment4: Fragment4_Kill
     private lateinit var fragment5: Fragment5_Settings
     private lateinit var mFragmentCurrent: Fragment

     private lateinit var textView_title: TextView
     private lateinit var textView_f1: TextView
     private lateinit var textView_f2: TextView
     private lateinit var textView_f3: TextView
     private lateinit var textView_f4: TextView
     private lateinit var textView_f5: TextView
     private lateinit var mSharedPreferences: SharedPreferences

     private  var mToast: Toast? = null


     companion object {
         lateinit var mUhfrManager: UHFRManager
         lateinit var mSetEpcs : Set<String>
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent_biasa)

        initView()

        Util.initSoundPool(this)

        mSharedPreferences = getSharedPreferences("UHF", MODE_PRIVATE)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.textView_f1 -> {
                switchContent(fragment6)
                textView_f1.setTextColor(resources.getColor(R.color.blu))
                textView_title.setText(R.string.inventory_epc)
            }
            R.id.textView_f2 -> {
                switchContent(fragment2)
                textView_f2.setTextColor(resources.getColor(R.color.blu))
                textView_title.setText(R.string.read_write_tag)
            }
            R.id.textView_f3 -> {
                switchContent(fragment3)
                textView_f3.setTextColor(resources.getColor(R.color.blu))
                textView_title.setText(R.string.lock)
            }
            R.id.textView_f4 -> {
                switchContent(fragment4)
                textView_f4.setTextColor(resources.getColor(R.color.blu))
                textView_title.setText(R.string.kill)
            }
            R.id.textView_f5 -> {
                switchContent(fragment5)
                textView_f5.setTextColor(resources.getColor(R.color.blu))
                textView_title.setText(R.string.setting_)
            }
        }
    }

     private fun switchContent(to: Fragment) {
         //        Log.e("switch",""+to.getId());
         textView_f1.setTextColor(resources.getColor(R.color.gre))
         textView_f2.setTextColor(resources.getColor(R.color.gre))
         textView_f3.setTextColor(resources.getColor(R.color.gre))
         textView_f4.setTextColor(resources.getColor(R.color.gre))
         textView_f5.setTextColor(resources.getColor(R.color.gre))
         if (mFragmentCurrent !== to) {
             mFt = mFm.beginTransaction()
             if (!to.isAdded) {
                 mFt.hide(mFragmentCurrent).add(R.id.framelayout_main, to).commit() //
             } else {
                 mFt.hide(mFragmentCurrent).show(to).commit() //
             }
             mFragmentCurrent = to
         }
     }

    private fun initView() {
        //        Log.e("main","init view" );
        fragment1 = Fragment1_Inventory()
        fragment6 = Fragment1_Scanner()
        mFragmentCurrent = fragment6
        fragment2 = Fragment2_ReadAndWrite()
        fragment3 = Fragment3_Lock()
        fragment4 = Fragment4_Kill()
        fragment5 = Fragment5_Settings()

        //        fragments = new ArrayList<Fragment>();
        //        fragments.add(fragment1);
        //        fragments.add(fragment2);
        //        fragments.add(fragment3);
        //        fragments.add(fragment4);
        //        fragments.add(fragment5);

        mFm = supportFragmentManager
        mFt = mFm.beginTransaction()
        mFt.add(R.id.framelayout_main, fragment6)
        mFt.commit()

        textView_title = findViewById(R.id.title) as TextView
        textView_f1 = findViewById(R.id.textView_f1) as TextView
        textView_f2 = findViewById(R.id.textView_f2) as TextView
        textView_f3 = findViewById(R.id.textView_f3) as TextView
        textView_f4 = findViewById(R.id.textView_f4) as TextView
        textView_f5 = findViewById(R.id.textView_f5) as TextView
        textView_f1.setOnClickListener(this)
        textView_f2.setOnClickListener(this)
        textView_f3.setOnClickListener(this)
        textView_f4.setOnClickListener(this)
        textView_f5.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
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
