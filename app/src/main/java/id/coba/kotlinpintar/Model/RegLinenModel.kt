package id.coba.kotlinpintar.Model

data class RegLinenModel(val data: List<Data>) {
    data class Data(val serial: String?, val tanggal: String?, val nama_ruangan: String?, val id_jenis: String?)
}