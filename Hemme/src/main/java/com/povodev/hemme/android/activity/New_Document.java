package com.povodev.hemme.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.povodev.hemme.android.Configurator;
import com.povodev.hemme.android.R;
import com.povodev.hemme.android.bean.Document;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class New_Document extends RoboActivity implements View.OnClickListener{

    private final String TAG = "NewDocument_Activity";
    final int ACTIVITY_CHOOSE_FILE = 1;


    @InjectView(R.id.file_edittext)                         private EditText mFileEditText;
    @InjectView(R.id.insert_new_document_button)            private Button mInserNewDocumentButton;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__document);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        this.context = this;

        mFileEditText.setText("Path del documento da caricare");
        setComponentsListener();
    }

    private void setComponentsListener() {
        mInserNewDocumentButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new__document, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.insert_new_document_button:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,ACTIVITY_CHOOSE_FILE);
                break;
        }

    }

    String filePath;
    String file;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case ACTIVITY_CHOOSE_FILE:
                if(resultCode==RESULT_OK){
                    filePath = data.getData().getPath();
                    mFileEditText.setText(filePath);
                    new NewDocument_HttpRequest(context,getDocument()).execute();
                }
            break;
        }
    }

    private Document getDocument() {
        return getDocumentValues();
    }
    private Document getDocumentValues() {
        file = mFileEditText.getText().toString();
        return setUserValues(file);
    }

    private Document setUserValues(String file) {
        Document document =  new Document();
        document.setFile(file);
        return document;
    }



    private class NewDocument_HttpRequest extends AsyncTask<Void, Void, Boolean> {

        private Document document;

        public NewDocument_HttpRequest(Context context, Document document){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Caricamento file in corso");
            this.document = document;
        }

        ProgressDialog progressDialog;
        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                final String url = "http://"+ Configurator.ip+"/"+Configurator.project_name+"/uploadDocument";

                MultiValueMap<String, Object> para = new LinkedMultiValueMap<String, Object>();
                para.add("file",new FileSystemResource(filePath));

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type","multipart/form-data");
                headers.set("enctype","multipart/form-data");
                headers.set("method","post");

                HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(para,headers);

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                return restTemplate.postForObject(url,requestEntity,boolean.class);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return false;
        }











































        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean created) {
            if (progressDialog.isShowing()) progressDialog.dismiss();

            if (created) Log.d(TAG, "Inserito file correttamente");
            else Log.d(TAG,"Failed to insert new document");
        }
    }

























}
