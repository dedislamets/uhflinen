package id.coba.kotlinpintar.Model

data class NoteModel(val data: List<Data>) {
    data class Data(val id: String?, val ruangan: String?)
}