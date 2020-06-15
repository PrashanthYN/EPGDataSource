package ott.epg

import android.annotation.SuppressLint
import android.content.ContentUris
import android.media.tv.TvContract
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.Program
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import kotlinx.android.synthetic.main.activity_setup.*
import kotlin.random.Random

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        syncButton.setOnClickListener {
            ContentTask().execute("")
        }
    }

    @SuppressLint("RestrictedApi")
    private fun createOnNextPrograms(uri: Uri) {


        val programBuilder1 = WatchNextProgram.Builder()

        for (i in 1..4) {
            val startTime: Long = 10
            val endTime: Long = 150
            programBuilder1.setAuthor("orlando " + i)
                .setLastEngagementTimeUtcMillis(835858848549)
                .setDescription("Description " + i)
                .setEpisodeTitle("Title" + i)
                .setType(TvContractCompat.WatchNextPrograms.TYPE_TV_EPISODE)
                .setLogoUri(Uri.parse("https://images-eu.ssl-images-amazon.com/images/I/51CNksbNcfL.png"))
                .setBrowsable(true)
                .setLastPlaybackPositionMillis((startTime..endTime).random().toInt())
                .setStartTimeUtcMillis(startTime)
                .setEndTimeUtcMillis(endTime)
            if (i % 2 == 0) {
                programBuilder1.setPosterArtAspectRatio(TvContractCompat.PreviewProgramColumns.ASPECT_RATIO_4_3)
                    .setPosterArtUri(Uri.parse("https://picsum.photos/200/300?random=" +  Random.nextInt(1,100)))

            } else {
                programBuilder1.setPosterArtAspectRatio(TvContractCompat.PreviewProgramColumns.ASPECT_RATIO_2_3)
                    .setPosterArtUri(Uri.parse("https://picsum.photos/300/200?random=" +  Random.nextInt(1,100)))
            }
            contentResolver?.insert(
                TvContractCompat.WatchNextPrograms.CONTENT_URI,
                programBuilder1.build().toContentValues()
            )
        }

    }

    private fun createChannelPrograms(uri: Uri) {

        val channelId = ContentUris.parseId(uri)

        for (i in 0..2) {
            val program = Program.Builder()
                .setChannelId(channelId)
                .setTitle("GOT Episode {$i}")
                .setStartTimeUtcMillis(System.currentTimeMillis())
                .setEndTimeUtcMillis(System.currentTimeMillis() + 3600000)
                .setDescription("Program Description")
                .setThumbnailUri(Uri.parse("https://i.picsum.photos/id/" + Random.nextInt(1,100) + "/300/200.jpg")) // Set more attributes...
                .setPosterArtUri(Uri.parse("https://i.picsum.photos/id/" + Random.nextInt(1,100) + "/300/200.jpg"))
                .build()
            val programUri =
                contentResolver.insert(TvContract.Programs.CONTENT_URI, program.toContentValues())
        }

        TvContractCompat.requestChannelBrowsable(applicationContext, channelId)
    }

    private fun createChannels(number: Int): Uri {
        val builder = Channel.Builder()
// Every channel you create must have the type TYPE_PREVIEW
        builder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
            .setDisplayName("MY HBO  " + number)
            .setInputId(1.toString())
        return contentResolver.insert(
            TvContract.Channels.CONTENT_URI,
            builder.build().toContentValues()
        )!!
    }

    inner class ContentTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            for (i in 1..4) {
                createChannelPrograms(createChannels(i))
            }
            for (i in 1..4) {
                createOnNextPrograms(createChannels(i))
            }
            return "completed"
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(applicationContext, "Channels and programs created", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
