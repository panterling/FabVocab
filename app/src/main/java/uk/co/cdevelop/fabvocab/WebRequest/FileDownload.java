package uk.co.cdevelop.fabvocab.WebRequest;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Chris on 18/03/2017.
 */

public class FileDownload extends AsyncTask<String, Integer, String> {

    private Context mContext;

    public FileDownload(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... fileURL) {
        InputStream is = null;
        OutputStream os = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(fileURL[0]);
            connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();
            is = connection.getInputStream();
            os = mContext.openFileOutput("dbFromServer.db", Context.MODE_PRIVATE);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = is.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    is.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                os.write(data, 0, count);
            }


        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {}

            if(connection != null) {
                connection.disconnect();
            }
        }

        return "Completed - success unknown!";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Toast.makeText(mContext, "Finished downloading the file!", Toast.LENGTH_LONG);

    }
}
