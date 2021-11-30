package id.coba.kotlinpintar

import android.content.Context

fun sportsList(context: Context): List<Sports> {

    return listOf(
        Sports(
            R.drawable.ic_rugby,
            context.getString(R.string.title_rugby),
            context.getString(R.string.subtitle_rugby),
            context.getString(R.string.about_rugby)
        ),
        Sports(
            R.drawable.ic_fitness,
            context.getString(R.string.title_cricket),
            context.getString(R.string.subtitle_cricket),
            context.getString(R.string.about_cricket)
        ),
        Sports(
            R.drawable.ic_settings,
            context.getString(R.string.title_hockey),
            context.getString(R.string.subtitle_hockey),
            context.getString(R.string.about_hockey)
        ),
        Sports(
            R.drawable.ic_basketball,
            context.getString(R.string.title_basketball),
            context.getString(R.string.subtitle_basketball),
            context.getString(R.string.about_basketball)
        ),
        Sports(
            R.drawable.ic_clear,
            context.getString(R.string.title_volleyball),
            context.getString(R.string.subtitle_volleyball),
            context.getString(R.string.about_volleyball)
        ),
        Sports(
            R.drawable.ic_account_circle,
            context.getString(R.string.title_esports),
            context.getString(R.string.subtitle_esports),
            context.getString(R.string.about_esports)
        ),
        Sports(
            R.drawable.ic_favorite,
            context.getString(R.string.title_kabbadi),
            context.getString(R.string.subtitle_kabbadi),
            context.getString(R.string.about_kabbadi)
        ),
        Sports(
            R.drawable.ic_home,
            context.getString(R.string.title_baseball),
            context.getString(R.string.subtitle_baseball),
            context.getString(R.string.about_baseball)
        )

    )
}
