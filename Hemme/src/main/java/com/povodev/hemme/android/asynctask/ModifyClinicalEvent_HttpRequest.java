package com.povodev.hemme.android.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.povodev.hemme.android.Configurator;
import com.povodev.hemme.android.activity.New_ClinicaEvent;
import com.povodev.hemme.android.activity.clinicalFolder.ClinicalFolderListActivity;
import com.povodev.hemme.android.bean.ClinicalEvent;
import com.povodev.hemme.android.dialog.CustomProgressDialog;
import com.povodev.hemme.android.utils.Header_Creator;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Stefano on 07/05/14.
 */
public class ModifyClinicalEvent_HttpRequest extends AsyncTask<Void, Void, Boolean> {

    private final String TAG = "NewClinicalEvent_AsyncTask";
    /*
     * Loading dialog message
     */
    private final String mDialogLoadingMessage = "Inserimento evento clinico in corso...";

    private ClinicalEvent clinicalEvent;
    private ProgressDialog progressDialog;
    private Context context;

    public ModifyClinicalEvent_HttpRequest(Context context, ClinicalEvent clinicalEvent) {
        progressDialog = new CustomProgressDialog(context, mDialogLoadingMessage);

        this.context = context;
        this.clinicalEvent = clinicalEvent;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            final String url = "http://"+ Configurator.ip+"/"+Configurator.project_name+"/modifyClinicalEvent?clinicalEvent_id=" + clinicalEvent.getId();

            HttpHeaders headers = Header_Creator.create();

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            //TODO usato per risolvere bug http://sapandiwakar.in/eofexception-with-spring-rest-template-android/
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());


            HttpEntity entity = new HttpEntity(clinicalEvent, headers);
            return restTemplate.postForObject(url, entity, Boolean.class);


            //return restTemplate.postForObject(url, clinicalEvent, Boolean.class);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean created) {
        if (progressDialog.isShowing()) progressDialog.dismiss();

        if (created) {
            Log.d(TAG, "Evento clinico inserito.");
            startActivity(context);
        } else Log.d(TAG, "Failed to insert new clinical event");
    }

    private void startActivity(Context context) {
        Intent intent = new Intent(context, ClinicalFolderListActivity.class);
        context.startActivity(intent);
        ((New_ClinicaEvent) context).finish();
    }
}