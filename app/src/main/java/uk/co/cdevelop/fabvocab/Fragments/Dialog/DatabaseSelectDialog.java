package uk.co.cdevelop.fabvocab.Fragments.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.SQL.FabVocabSQLHelper;
import uk.co.cdevelop.fabvocab.WebRequest.FileDownload;

/**
 * Created by Chris on 11/03/2017.
 */

public class DatabaseSelectDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LinearLayout llDbSelectView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_databaseselect, null);


        String[] filesList = getActivity().fileList();
        final ArrayList<String> filteredFileList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, filteredFileList);

        RequestQueue q = Volley.newRequestQueue(getContext());
        q.add(new StringRequest("http://54.165.239.42/listing.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] filenames = response.split("##");
                for(String filename : filenames) {
                    filteredFileList.add(filename);
                    adapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                filteredFileList.add("No Files Found!");
            }
        }));


        ListView lvDatabaseFiles = (ListView) llDbSelectView.findViewById(R.id.lv_databasefiles);
        lvDatabaseFiles.setAdapter(adapter);
        lvDatabaseFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileDownload fd = new FileDownload(getContext());
                fd.execute("http://54.165.239.42/uploads/" + adapter.getItem(position));
            }
        });

        builder.setView(llDbSelectView);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*try {
                    // CREATE Backup of Current DB
                    FileInputStream fis = new FileInputStream(new File("/data/data/" + getContext().getPackageName() + "/databases/" + FabVocabSQLHelper.getDatabaseFilename()));
                    FileOutputStream db = getActivity().openFileOutput("dbBackup.db", Context.MODE_PRIVATE);

                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while((length = fis.read(buffer)) > 0) {
                        db.write(buffer, 0, length);
                    }
                    db.flush();
                    db.close();
                    fis.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/



                /*try {
                    File toFile = new File("/data/data/" + getContext().getPackageName() + "/databases/" + FabVocabSQLHelper.getDatabaseFilename());

                    FileInputStream fis = getActivity().openFileInput("dbBackup.db");
                    FileOutputStream fos = new FileOutputStream(toFile);

                    FileChannel fromChannel = fis.getChannel();
                    FileChannel toChannel = fos.getChannel();

                    fromChannel.transferTo(0, fromChannel.size(), toChannel);

                    fromChannel.close();
                    toChannel.close();
                    fis.close();
                    fos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String urlServer = "http://54.165.239.42/upload.php";
                            String lineEnd = "\r\n";
                            String twoHyphens = "--";
                            String boundary =  "*****";
                            int maxBufferSize = 1*1024*1024;

                            FileInputStream fileInputStream = new FileInputStream(new File("/data/data/" + getContext().getPackageName() + "/databases/" + FabVocabSQLHelper.getDatabaseFilename()));

                            URL url = new URL(urlServer);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                            // Allow Inputs &amp; Outputs.
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setUseCaches(false);

                            // Set HTTP method to POST.
                            connection.setRequestMethod("POST");

                            connection.setRequestProperty("Connection", "Keep-Alive");
                            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + FabVocabSQLHelper.getDatabaseFilename() +"-" + Long.toString(System.currentTimeMillis()) + "\"" + lineEnd);
                            outputStream.writeBytes(lineEnd);

                            int bytesAvailable = fileInputStream.available();
                            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            byte[] buffer = new byte[bufferSize];

                            // Read file
                            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            while (bytesRead > 0)
                            {
                                outputStream.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            outputStream.writeBytes(lineEnd);
                            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            // Responses from the server (code and message)
                            int serverResponseCode = connection.getResponseCode();
                            String info = connection.getHeaderField("info");
                            String serverResponseMessage = connection.getResponseMessage();

                            fileInputStream.close();
                            outputStream.flush();
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();



            }
        });
        builder.setTitle("Select Database");

        return builder.create();

    }
}
