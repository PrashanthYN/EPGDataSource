package receivers

import android.content.*
import android.media.tv.TvContract
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.tvprovider.media.tv.Program
import androidx.tvprovider.media.tv.TvContractCompat

class RunOnInstallReceiver : BroadcastReceiver() {
    var context:Context?=null
    override fun onReceive(context: Context, intent: Intent) {
        this.context=context
        Log.d(this.javaClass.simpleName,"Intialise programs broadcast received")
        ContentTask().execute("")
    }

    private fun createChannelPrograms(uri: Uri) {
        val channelId= ContentUris.parseId(uri)

        val programBuilder1= Program.Builder().setChannelId(channelId)
            .setDescription("Demo program 1").setTitle("Program 1").build()

        var programUri1 = context?.contentResolver!!.insert(
            TvContractCompat.Programs.CONTENT_URI,
            programBuilder1.toContentValues())

        val programBuilder2= Program.Builder().setChannelId(channelId)
            .setDescription("Demo program 2").setTitle("Program 2").build()

        var programUri2 = context?.contentResolver!!.insert(
            TvContractCompat.Programs.CONTENT_URI,
            programBuilder2.toContentValues())

        TvContractCompat.requestChannelBrowsable(context, channelId)

    }

    private fun createChannels(): Uri {
        val values = ContentValues().apply {
            put(TvContractCompat.Channels.COLUMN_DISPLAY_NAME, "LTTS-OTT Promotions")
        }
        return context?.contentResolver!!.insert(TvContract.Channels.CONTENT_URI, values)!!
    }


    inner class ContentTask: AsyncTask<String, String, String>(){
        override fun doInBackground(vararg params: String?): String {
            createChannelPrograms(createChannels())
            return "completed"
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(context,"Channels and programs created", Toast.LENGTH_SHORT).show()
        }
    }
}
