package com.clicbrics.consumer.adapter;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.buy.housing.backend.propertyEndPoint.model.Document;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.WebviewActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.Constants;
import com.clicbrics.consumer.utils.NotificationUtils;
import com.clicbrics.consumer.utils.PermissionManager;
import com.clicbrics.consumer.utils.UtilityMethods;
import com.clicbrics.consumer.view.activity.ProjectDetailsScreen;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


/**
 * Created by Alok on 21-04-2017.
 */

public class ProjectDocumentsAdapter extends RecyclerView.Adapter<ProjectDocumentsAdapter.ProjectDocumentViewHolder> {

    private static final String TAG = ProjectDocumentsAdapter.class.getSimpleName();
    private final int STORAGE_PERMISSION = 105;
    private Context mContext;
    private List<Document> documentList;
    private static final String savedFileName = "Clicbrics_";
    private NotificationUtils notificationUtils;
    //private List<String> documentListSize;

    public ProjectDocumentsAdapter(Context context, final List<Document> documentList) {
        mContext = context;
        this.documentList = documentList;

        //this.documentListSize = documentListSize;

    }

    public class ProjectDocumentViewHolder extends RecyclerView.ViewHolder {
        private ImageView documentLogo, projectImage, downloadIcon;
        private TextView documentName;

        //private TextView documentSize;
        public ProjectDocumentViewHolder(View itemView) {
            super(itemView);
            documentLogo = (ImageView) itemView.findViewById(R.id.document_logo);
            documentName = (TextView) itemView.findViewById(R.id.document_name);
            projectImage = itemView.findViewById(R.id.id_project_image);
            downloadIcon = itemView.findViewById(R.id.download_icon);
            //documentSize = (TextView) itemView.findViewById(R.id.document_size);
        }
    }

    @Override
    public ProjectDocumentsAdapter.ProjectDocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_documents, parent, false);
        return new ProjectDocumentsAdapter.ProjectDocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProjectDocumentsAdapter.ProjectDocumentViewHolder holder, final int position) {
        if (position == 0) {
            holder.itemView.setPadding(UtilityMethods.dpToPx(16), 0, 0, 0);
        } else if (position == documentList.size() - 1) {
            holder.itemView.setPadding(0, 0, UtilityMethods.dpToPx(16), 0);
        } else {
            holder.itemView.setPadding(0, 0, 0, 0);
        }
        if (documentList != null && !documentList.isEmpty()) {

            if (documentList.get(position).getName() != null && !documentList.get(position).getName().isEmpty()) {
                String docName = documentList.get(position).getName();
                Log.i(TAG, "Document Name -> " + docName);
                holder.documentName.setText(docName);
            }
            if (documentList.get(0).getSecUrl() != null && !documentList.get(0).getSecUrl().isEmpty()) {
                Picasso.get().load(documentList.get(0).getSecUrl() + "=h400")
                        .placeholder(R.drawable.placeholder)
                        .into(holder.projectImage);
            }
            if (documentList.get(position).getUrl() != null && !documentList.get(position).getUrl().isEmpty()) {
                String docURL = documentList.get(position).getUrl();
                Log.i(TAG, "Document URL -> " + docURL);
                if (docURL.contains(".pdf") || docURL.contains(".PDF")) {
                    holder.documentLogo.setImageResource(R.drawable.doc_type_pdf_icon);
                } else if (docURL.contains(".ppt") || docURL.contains(".PPT")) {
                    holder.documentLogo.setImageResource(R.drawable.doc_type_ppt_icon);
                } else if (docURL.contains(".doc") || docURL.contains(".DOC")) {
                    holder.documentLogo.setImageResource(R.drawable.doc_type_doc_icon);
                } else if (docURL.contains(".xlsx") || docURL.contains(".XLSX")) {
                    holder.documentLogo.setImageResource(R.drawable.doc_type_xls_icon);
                } else if (docURL.contains(".odt") || docURL.contains(".ODT")) {
                    holder.documentLogo.setImageResource(R.drawable.doc_type_doc_icon);
                } else if (docURL.contains(".png") || docURL.contains(".PNG")) {
                    holder.documentLogo.setImageResource(R.drawable.doc_type_png_icon);
                } else {
                    holder.documentLogo.setImageResource(R.drawable.doc_type_jpg_icon);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if(documentList.get(holder.getAdapterPosition()).getUrl() != null
                                && !documentList.get(holder.getAdapterPosition()).getUrl().isEmpty()){
                            String actualURL = documentList.get(holder.getAdapterPosition()).getUrl();
                            String finalURL = BuildConfigConstants.storage_path + actualURL;
                            Log.i(TAG, "onClick: actualURL->" + actualURL);
                            Log.i(TAG, "onClick: finalURL->" + finalURL);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            String extension = finalURL.substring(finalURL.lastIndexOf(".")+1,finalURL.length());
                            if (extension != null) {
                                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                                Log.i(TAG, "onClick: Extension & Mimetype -> " + extension + " & " + mimeType);
                                String fileName = finalURL.substring(finalURL.lastIndexOf("/")+1,finalURL.length());
                                Log.i(TAG, "onClick: Filename -> " + fileName);
                                File file = new File(Environment.getExternalStorageDirectory()+
                                        File.separator + "clicbrics" +File.separator+ fileName);
                                intent.setDataAndType(Uri.fromFile(file),mimeType);
                            }
                            mContext.startActivity(Intent.createChooser(intent,"Open"));
                        }*/
                        /*String actualURL = documentList.get(holder.getAdapterPosition()).getUrl();
                        String finalURL = BuildConfigConstants.storage_path + actualURL;
                        Intent intent = new Intent(mContext, DocumentWebview.class);
                        intent.putExtra("DocumentURL",finalURL);
                        mContext.startActivity(intent);*/

                        Uri uriUrl = null;
                        try {
                            String actualURL = documentList.get(holder.getAdapterPosition()).getUrl();
                            String finalURL = BuildConfigConstants.storage_path + actualURL;
                            Intent intent = new Intent(mContext, WebviewActivity.class);
                            //finalURL = URLEncoder.encode(finalURL,"UTF-8");
                            if (finalURL.contains(".jpg") || finalURL.contains(".JPG")
                                    || finalURL.contains(".jpeg") || finalURL.contains(".JPEG")
                                    || finalURL.contains(".png") || finalURL.contains(".PNG")
                                    || finalURL.contains(".odt") || finalURL.contains(".ODT")) {
                                //String doc="<iframe src='http://docs.google.com/viewer?url=" +finalURL+ "&embedded=true' width='100%' height='100%'  style='border: none;'></iframe>";
                                URL url = new URL(finalURL);
                                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                                uriUrl = Uri.parse(uri.toString());
                                intent.putExtra("URL", uri.toString());
                            } else {
                                uriUrl = Uri.parse("http://docs.google.com/gview?embedded=true&url=" + finalURL);
                                intent.putExtra("URL", "http://docs.google.com/gview?embedded=true&url=" + finalURL);
                            }
                            if (!TextUtils.isEmpty(holder.documentName.getText().toString())) {
                                intent.putExtra("Title", holder.documentName.getText().toString());
                            }
                            mContext.startActivity(intent);
                            /*Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            mContext.startActivity(Intent.createChooser(launchBrowser,"Open"));*/
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (uriUrl != null && !uriUrl.toString().trim().isEmpty()) {
                                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                                mContext.startActivity(Intent.createChooser(launchBrowser, "Open"));
                            }
                        }

                    }
                });

                holder.downloadIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = "", documentName = "", extension = "";
                        if (documentList.get(holder.getAdapterPosition()) != null) {
                            if (!TextUtils.isEmpty(documentList.get(holder.getAdapterPosition()).getName())) {
                                documentName = documentList.get(holder.getAdapterPosition()).getName();
                            }
                            if (!TextUtils.isEmpty(documentList.get(holder.getAdapterPosition()).getUrl())) {
                                url = documentList.get(holder.getAdapterPosition()).getUrl();
//                                extension = url.substring(url.lastIndexOf('.'), url.length());
                                extension = MimeTypeMap.getFileExtensionFromUrl(url);
                            }
                        }
                        final String appendName = (savedFileName + documentName + holder.getAdapterPosition() + "."+extension);
                        Log.i(TAG, "onClick: Storage Path : " + BuildConfigConstants.storage_path);
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + appendName);

                        DownloadDoc downloadDocTask = new DownloadDoc(mContext, holder, appendName);


                        if (PermissionManager.checkIfAlreadyhasPermission(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                            if (file.exists()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                if (extension != null) {
                                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                                    Log.i(TAG, "onClick: Filename -> " + appendName);
                                    File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + appendName);
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Uri uri = FileProvider.getUriForFile(mContext, "com.clicbrics.consumer.fileprovider001",file);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(uri, mimeType);
                                    }else {
                                        intent.setDataAndType(Uri.fromFile(file1),mimeType);
                                    }
                                }
                                List<ResolveInfo> activities = mContext.getPackageManager().queryIntentActivities(intent, 0);
                                if (activities != null && activities.size() > 0) {
                                    mContext.startActivity(Intent.createChooser(intent, "Open"));
                                } else {
                                    Uri uriUrl = Uri.parse("http://docs.google.com/gview?embedded=true&url=" + url);
                                    intent.setData(uriUrl);
                                    mContext.startActivity(Intent.createChooser(intent, "Open"));
                                }
                            } else {
                                downloadDocTask.execute(BuildConfigConstants.storage_path + url);
                            }

                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!UtilityMethods.selfPermissionGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, mContext)) {
                                    ActivityCompat.requestPermissions((ProjectDetailsScreen) mContext,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSION);
                                }
                            }
                        }
                    }
                });
            }


            /*if(documentListSize != null && !documentListSize.isEmpty()){
                holder.documentSize.setText(documentListSize.get(position));
            }*/


        }
    }

    @Override
    public int getItemCount() {
        if (documentList != null) {
            return documentList.size();
        } else {
            return 0;
        }
    }

    public class DownloadDoc extends AsyncTask<String, Integer, Boolean> {
        Context context;
        ProjectDocumentViewHolder viewHolder;
        String fileName;
        String errorMsg;
        String fileDownloadURL;
        Integer notificationID = 100;
        Integer notificationID2 = 101;
        Notification.Builder notificationBuilder;
        Notification.Builder notificationBuilder1;
        NotificationManager notificationManager;
        Notification notification;
        Uri defaultSoundUri;

        DownloadDoc(Context context, ProjectDocumentViewHolder holder, String fileName) {
            this.context = context;
            this.viewHolder = holder;
            this.fileName = fileName;
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //Set notification information:
            notificationBuilder = new Notification.Builder(context);
            notificationBuilder1 = new Notification.Builder(context);
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showNotification("Downloading file","Progress 0%",0);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i(TAG, "onProgressUpdate: Download progress : " + values[0]);
            showNotification("Downloading file","Progress " + values[0] + "%",values[0]);
        }

        @Override
        protected Boolean doInBackground(String... objects) {
            Log.i(TAG, "doInBackground: File is Downloading");
            boolean isDownloaded;
            int count;
            try {
                URL url = new URL(objects[0]);
                //URL url = new URL("https://storage.googleapis.com/housingtestserver.appspot.com/bookedunits/5981219506356224/1496225491034_swiggy-order-1076117364%20(1).pdf");
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = null;
                try {
                    output = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + fileName);
                } catch (Exception e) {
                    errorMsg = "No storage permission!";
                    e.printStackTrace();
                    return false;
                }
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                isDownloaded = true;
                fileDownloadURL = url.toString();
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                errorMsg = "Error downloading file. Please try again!";
                isDownloaded = false;
            }
            return isDownloaded;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            try {
                if (result) {

                    notificationManager.cancel(notificationID);
//                    String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                    String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (extension != null) {
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        Log.i(TAG, "onClick: Filename -> " + fileName);
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            Uri uri = FileProvider.getUriForFile(mContext, "com.clicbrics.consumer.fileprovider001",file);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(uri, mimeType);
                        }else {
                            intent.setDataAndType(Uri.fromFile(file), mimeType);
                        }

                    }
                    List<ResolveInfo> activities = mContext.getPackageManager().queryIntentActivities(intent, 0);
                    if (activities != null && activities.size() > 0) {
                        mContext.startActivity(Intent.createChooser(intent, "Open File"));
                    } else {
                        Uri uriUrl = Uri.parse("http://docs.google.com/gview?embedded=true&url=" + fileDownloadURL);
                        intent.setData(uriUrl);
                        mContext.startActivity(Intent.createChooser(intent, "Open File"));
                        //UtilityMethods.showSnackBar(mActivity.findViewById(R.id.id_document_list_adapter),"File Downloaded Successfully!", Snackbar.LENGTH_SHORT);
                    }
                } else {
                    //viewHolder.downloadSuccessImage.setVisibility(View.GONE);
                    notificationManager.cancel(notificationID);
                    UtilityMethods.showSnackbarOnTop((ProjectDetailsScreen) mContext, "Error", errorMsg, true, Constants.AppConstants.ALERTOR_LENGTH_LONG);
                }
                this.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onPostExecute: Result " + result);
        }

        private void showNotification(String title, String message,int progress) {
            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationUtils = new NotificationUtils(mContext);
                Notification.Builder nb = notificationUtils.
                        getNotification(title, message);
                    nb.setProgress(100, progress, false);
                notificationUtils.notify(notificationID, nb);


            } else {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_app_logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true);

                    notificationBuilder.setProgress(100, progress, false);

                notificationManager.notify(notificationID, notificationBuilder.build());
            }
        }
    }


}
