package id.coba.kotlinpintar

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.BRMicro.Tools
import com.handheld.uhfr.UHFRManager
import com.uhf.api.cls.Reader
import id.coba.kotlinpintar.Component.BottomSheetDialog
import id.coba.kotlinpintar.Dto.ListRuangan
import id.coba.kotlinpintar.Rest.ApiClient
import id.coba.kotlinpintar.Rest.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import java.util.Set

class KotorActivity : AppCompatActivity(), BottomSheetDialog.BottomSheetListener, View.OnClickListener {

    private lateinit var mFm: FragmentManager
    private lateinit var mFt: FragmentTransaction
    private lateinit var fragment6: Fragment_Kotor
    private lateinit var fragmentDialog: BottomSheetDialog

    private lateinit var mFragmentCurrent: Fragment

    private lateinit var textView_title: TextView
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mSharedPreferencesMode: SharedPreferences
    private lateinit var spinner_pic: Spinner

    private  var mToast: Toast? = null
    private lateinit var apiInterface: ApiInterface
    var ListRoom: MutableList<String> = ArrayList()

    companion object {
        lateinit var mUhfrManager: UHFRManager
        lateinit var mSetEpcs : Set<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        initView()
        setContentView(R.layout.activity_kotor)

        Util.initSoundPool(this)

        mSharedPreferences = getSharedPreferences("UHF", MODE_PRIVATE)
        mSharedPreferencesMode = getSharedPreferences("MODE", MODE_PRIVATE)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

    }
    override fun onBackPressed() {
        startActivity(Intent(this, ListKotorActivityRecyle::class.java))
        this.finish()
    }

    override fun onClick(v: View) {

    }

    override fun onButtonClicked(text: String?) {
        TODO("Not yet implemented")
    }

    private fun initView() {
        val no_transaksi = getIntent().getStringExtra("no_transaksi")

        fragment6 = Fragment_Kotor()
        mFragmentCurrent = fragment6
        val arguments = Bundle()
        arguments.putString( "no_transaksi" , no_transaksi)
        fragment6.setArguments(arguments)

        mFm = supportFragmentManager
        mFt = mFm.beginTransaction()
        mFt.add(R.id.framelayout_main, fragment6)
        mFt.commit()

    }

    private fun loadSpinner() {
        val db = InputDbHelper(this)
        //var serviceCursor: Cursor = db.serviceCursor

        val call2: Call<ListRuangan> = apiInterface.ruangan
        call2.enqueue(object : Callback<ListRuangan> {
            override fun onResponse(call: Call<ListRuangan>, response: Response<ListRuangan>) {
                val ruanganList: ListRuangan = response.body()
                val datumList: List<ListRuangan.Datum> = ruanganList.data

                for (datum in datumList) {
                    ListRoom.add(datum.ruangan)
                }
                /*var from: Array<String> = arrayOf("nama_ruangan")
                var to: IntArray = intArrayOf(android.R.id.text1)
                val adapterRuangan = SimpleCursorAdapter(
                    baseContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    cursor,
                    from,
                    to,
                    0
                )*/
                val arrayAdapter = ArrayAdapter(this@KotorActivity,android.R.layout.simple_spinner_dropdown_item,ListRoom)

                spinner_pic.setAdapter(arrayAdapter)
            }

            override fun onFailure(call: Call<ListRuangan>, t: Throwable) {
                call.cancel()
            }
        })

    }

    override fun onResume() {
        super.onResume()

        try{
            if(mSharedPreferencesMode.getBoolean("MODE", false)){
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

        try {
            if (mUhfrManager != null) {//close uhf module
                mUhfrManager.close()
            }
        }catch (e: Throwable){ }

    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        //        Log.e("main","destroy");
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
                    ListKotorActivityRecyle::class.java
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
//            mUhfrManager = null
            Log.e("eee", "close!")
            //OperRegister();
        } else {
            //ShowDialog("错误信息","初始化签标（RFID）读取器失败。",2);
        }
    }
}
