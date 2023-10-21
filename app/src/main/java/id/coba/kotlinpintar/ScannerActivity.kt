package id.coba.kotlinpintar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.scanner_linen.*

class ScannerActivity : AppCompatActivity() {

    private var products : ArrayList<Product> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_linen)

        rvProduct.layoutManager = GridLayoutManager(this, 2)

        var price = 12000
        for (i in 1..8) {
            price += i * 8
            val product = Product("Nama Produk $i", price, i)
            products.add(product)
        }

        rvProduct.adapter = ProductAdapter(products)
    }

}