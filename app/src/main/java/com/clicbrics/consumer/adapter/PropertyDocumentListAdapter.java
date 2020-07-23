package com.clicbrics.consumer.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buy.housing.backend.userEndPoint.model.Document;
import com.clicbrics.consumer.R;
import com.clicbrics.consumer.activities.WebviewActivity;
import com.clicbrics.consumer.utils.BuildConfigConstants;
import com.clicbrics.consumer.utils.PermissionManager;
import com.clicbrics.consumer.utils.UtilityMethods;

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
 * Created by Alok on 04-10-2016.
 */

@SuppressWarnings("ResourceType")
public class PropertyDocumentListAdapter extends RecyclerView.Adapter<PropertyDocumentListAdapter.ViewHolder>{

    private static final String TAG =  PropertyDocumentListAdapter.class.getSimpleName();
    private static final String savedFileName = "Redbrics_";
    Context mContext;
    Activity mActivity;
    private List<Document> mDocumentList;

    public DownloadDoc downloadDocTask;
    private PermissionCheckListener listener;
    public interface PermissionCheckListener{
        void checkStoragePermission(String url);
    }
    public PropertyDocumentListAdapter(Activity mActivity, Context mContext, List<Document> mDocumentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mDocumentList = mDocumentList;
        this.listener = (PermissionCheckListener) mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView documentName,documentType;
        ImageView docTypeImage;
        ImageButton downloadLinkImage, downloadSuccessImage;
        ProgressBar downloadingPBar;
        public ViewHolder(View view) {
            super(view);
            documentName = (TextView) view.findViewById(R.id.id_document_name);
            documentType = (TextView) view.findViewById(R.id.id_document_type);
            docTypeImage = (ImageView) view.findViewById(R.id.id_doc_type_icon);
            downloadLinkImage = (ImageButton) view.findViewById(R.id.id_download_link_img);
            downloadSuccessImage =(ImageButton) view.findViewById(R.id.id_download_success_img);
            downloadingPBar = (ProgressBar) view.findViewById(R.id.id_downloading_pbar);
        }
    }


    @Override
    public PropertyDocumentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_document_list_adapter_layout, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.i(TAG, "onBindViewHolder: Position " + position);
        if(mDocumentList != null && mDocumentList.size() > 0){
            holder.itemView.setVisibility(View.VISIBLE);
            final Document document = mDocumentList.get(position);
            String documentName = document.getName();
            if(!TextUtils.isEmpty(documentName)){
                holder.documentName.setText(documentName);
            }else{
                documentName = "";
            }
            String documentType = document.getRemark();
            if(!TextUtils.isEmpty(documentType)){
                holder.documentType.setVisibility(View.VISIBLE);
                holder.documentType.setText(documentType);
            }else{
                holder.documentType.setVisibility(View.GONE);
            }
            final String url = document.getUrl();
            if(!TextUtils.isEmpty(url)){
                String extension = "";
                if(!TextUtils.isEmpty(url)){
                    extension = url.substring(url.lastIndexOf('.'),url.length());
                }
                if(extension.equalsIgnoreCase(".PDF")){
                    holder.docTypeImage.setImageResource(R.drawable.doc_type_pdf_icon);
                }else if(extension.equalsIgnoreCase(".PPT")){
                    holder.docTypeImage.setImageResource(R.drawable.doc_type_ppt_icon);
                }else if(extension.equalsIgnoreCase(".DOC")){
                    holder.docTypeImage.setImageResource(R.drawable.doc_type_doc_icon);
                }else if(extension.equalsIgnoreCase(".XLSX")){
                    holder.docTypeImage.setImageResource(R.drawable.doc_type_xls_icon);
                }else if(extension.equalsIgnoreCase(".ODT")){
                    holder.docTypeImage.setImageResource(R.drawable.doc_type_doc_icon);
                }else if(extension.equalsIgnoreCase(".PNG")){
                    holder.docTypeImage.setImageResource(R.drawable.doc_type_png_icon);
                }else{
                    holder.docTypeImage.setImageResource(R.drawable.doc_type_jpg_icon);
                }
                final String appendName = savedFileName + documentName + holder.getAdapterPosition()+extension;
                holder.downloadLinkImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG, "onClick: Storage Path : " + BuildConfigConstants.storage_path);
                        downloadDocTask = new DownloadDoc(mContext,holder,appendName);
                        if(PermissionManager.checkIfAlreadyhasPermission(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})){
                            downloadDocTask.execute(BuildConfigConstants.storage_path+url);
                        }else {
                            listener.checkStoragePermission(BuildConfigConstants.storage_path + url);
                        }
                    }
                });

                holder.downloadSuccessImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = null;
                        try {
                            String docURL  = "";
                            if(!url.contains("https")){
                                docURL = BuildConfigConstants.storage_path + url;
                            }else{
                                docURL = url;
                            }
                            URL objectURL = new URL(docURL);
                            URI objectURI = new URI(objectURL.getProtocol(), objectURL.getUserInfo(), objectURL.getHost(), objectURL.getPort(), objectURL.getPath(), objectURL.getQuery(), objectURL.getRef());
                            uri = Uri.parse(objectURI.toString());
                            Intent intent = new Intent(mContext, WebviewActivity.class);
                            String extension = "";
                            if(!TextUtils.isEmpty(url)){
                                extension = url.substring(url.lastIndexOf('.'),url.length());
                            }
                            if(extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpg")
                                    || extension.equalsIgnoreCase(".jpeg")
                                    || extension.equalsIgnoreCase(".bmp")){
                                intent.putExtra("URL", uri.toString());
                            }else {
                                intent.putExtra("URL", "http://docs.google.com/gview?embedded=true&url=" + uri.toString());
                            }
                            Document doc = mDocumentList.get(position);
                            if(doc != null){
                                if(!TextUtils.isEmpty(doc.getRemark())){
                                    intent.putExtra("Title",doc.getRemark());
                                }else if(!TextUtils.isEmpty(doc.getName())){
                                    intent.putExtra("Title",doc.getName());
                                }else{
                                    intent.putExtra("Title","DOCUMENT DETAIL");
                                }
                            }
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
            if(TextUtils.isEmpty(documentName) && TextUtils.isEmpty(url)){
                holder.itemView.setVisibility(View.GONE);
            }else if(TextUtils.isEmpty(documentName) && TextUtils.isEmpty(documentType)){
                holder.documentName.setVisibility(View.VISIBLE);
                holder.documentName.setText("Document");
            }

        }
    }


    @Override
    public int getItemCount() {
        if(mDocumentList != null && mDocumentList.size() > 0) {
            return mDocumentList.size();
        }else{
            return 0;
        }
    }

    public class DownloadDoc extends AsyncTask<String, Integer, Boolean>{
        Context context;
        ViewHolder viewHolder;
        String fileName;
        String errorMsg;
        String fileDownloadURL;
        DownloadDoc(Context context, ViewHolder holder, String fileName){
            this.context = context;
            this.viewHolder = holder;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewHolder.downloadLinkImage.setVisibility(View.GONE);
            viewHolder.downloadingPBar.setVisibility(View.VISIBLE);
            viewHolder.downloadSuccessImage.setVisibility(View.GONE);
            viewHolder.downloadingPBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            viewHolder.downloadingPBar.setProgress(values[0]);
            Log.i(TAG, "onProgressUpdate: Download progress : " + values[0]);
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
                    publishProgress((int)((total*100)/lenghtOfFile));
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
                if(result) {
                    viewHolder.downloadLinkImage.setVisibility(View.VISIBLE);
                    viewHolder.downloadingPBar.setVisibility(View.GONE);
                    //viewHolder.downloadSuccessImage.setVisibility(View.VISIBLE);
                    String extension = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (extension != null) {
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        Log.i(TAG, "onClick: Filename -> " + fileName);
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
                        intent.setDataAndType(Uri.fromFile(file),mimeType);
                    }
                    List<ResolveInfo> activities = mContext.getPackageManager().queryIntentActivities(intent,0);
                    if(activities != null && activities.size()>0) {
                        mContext.startActivity(Intent.createChooser(intent, "Open"));
                    }else{
                        Uri uriUrl = Uri.parse("http://docs.google.com/gview?embedded=true&url=" + fileDownloadURL);
                        intent.setData(uriUrl);
                        mContext.startActivity(Intent.createChooser(intent, "Open"));
                        //UtilityMethods.showSnackBar(mActivity.findViewById(R.id.id_document_list_adapter),"File Downloaded Successfully!", Snackbar.LENGTH_SHORT);
                    }
                }else{
                    viewHolder.downloadLinkImage.setVisibility(View.VISIBLE);
                    viewHolder.downloadingPBar.setVisibility(View.GONE);
                    //viewHolder.downloadSuccessImage.setVisibility(View.GONE);
                    UtilityMethods.showErrorSnackBar(mActivity.findViewById(R.id.id_document_list_adapter),errorMsg, Snackbar.LENGTH_SHORT);
                }
                this.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onPostExecute: Result " + result);
        }
    }


}
