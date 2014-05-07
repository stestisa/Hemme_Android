package com.povodev.hemme.android.fragment;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.povodev.hemme.android.R;
import com.povodev.hemme.android.activity.Diary;
import com.povodev.hemme.android.activity.Login_Activity;
import com.povodev.hemme.android.activity.New_ClinicaEvent;
import com.povodev.hemme.android.activity.PatientActivity;
import com.povodev.hemme.android.activity.clinicalFolder.ClinicalFolderListActivity;
import com.povodev.hemme.android.activity.memory_results.MemoryResultsListActivity;
import com.povodev.hemme.android.bean.User;
import com.povodev.hemme.android.management.SessionManagement;
import com.povodev.hemme.android.utils.Localization;

import java.util.HashMap;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by Stefano on 27/03/14.
 */
public class Fragment_Home extends RoboFragment implements View.OnClickListener {

    @InjectView(R.id.login_button)                  private Button mLoginButton;
    @InjectView(R.id.newclinicalevent_button)       private Button mNewClinicalEventButton;
    @InjectView(R.id.clinicalfolder_button)         private Button mClinicalFolderButton;
    @InjectView(R.id.test_button)                   private Button mTestButton;
    @InjectView(R.id.visaulizza_diario)             private Button mDiaryButton;
    @InjectView(R.id.user_detail_home)              private TextView mUserDetailTextView;

    private User user;

    /*
     * true if user is logged in, false otherwise
     */
    private boolean isUserLoggedIn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        user = SessionManagement.getUserInSession(getActivity());
        isUserLoggedIn = SessionManagement.isUserLoggedIn(getActivity());

        if (!isUserLoggedIn){
            Intent intent = new Intent(getActivity(),Login_Activity.class);
            startActivity(intent);
            getActivity().finish();
        }




        /*
         * SENSORE DI PROSSIMITA'
         */
        //proximityFunctions();






        /*
         * PAZIENTE
         */
        if (user.getRole()==2){
            Intent intent = new Intent(this.getActivity(),PatientActivity.class);
            redirect(intent);
        }
    }





    private LocationManager lm;
    private void proximityFunctions() {

        HashMap<String,Double> latLong = Localization.getCoordinates(getActivity());
        double myLatitude = latLong.get(Localization.LATITUDE);
        double myLongitude = latLong.get(Localization.LONGITUDE);
        Toast.makeText(getActivity(),Localization.LATITUDE +": "+myLatitude +"  "+Localization.LONGITUDE+": "+myLongitude, Toast.LENGTH_SHORT).show();


        //Intent i = new Intent("com.povodev.hemme.proximity_alert");
        //PendingIntent pi = PendingIntent.getBroadcast(getActivity(), -1, i, 0);

        //lm.addProximityAlert(latitude, longitude, radius, -1, pi);
    }


    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.diary_actionbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new_document:
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();
    }

    /*
     *  Return the type of user
     *  0 TUTOR
     *  1 DOTTORE
     *  2 PAZIENTE
     */
    private int checkUserType(User user) {
        return user.getRole();
    }


    private void initComponents() {
        int userType = checkUserType(user);

        if(userType==0){
            mNewClinicalEventButton.setVisibility(View.GONE);
            mTestButton.setOnClickListener(this);
            mDiaryButton.setOnClickListener(this);
        } else if (userType==1){
            mTestButton.setVisibility(View.GONE);
            mDiaryButton.setVisibility(View.GONE);
            mNewClinicalEventButton.setOnClickListener(this);
        }

        mLoginButton.setOnClickListener(this);
        mClinicalFolderButton.setOnClickListener(this);

        mUserDetailTextView.setText("Benvenuto " + user.getName() + " " + user.getSurname());

        if (isUserLoggedIn){
            mLoginButton.setText("Logout");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id){
            case R.id.login_button:
                if (isUserLoggedIn){
                    SessionManagement.closeSession(getActivity());
                    intent = new Intent(this.getActivity(),Login_Activity.class);
                    redirect(intent);
                    getActivity().finish();
                }
                break;
            case R.id.newclinicalevent_button:
                intent = new Intent(this.getActivity(),New_ClinicaEvent.class);
                redirect(intent);
                break;
            case R.id.clinicalfolder_button:
                intent = new Intent(this.getActivity(),ClinicalFolderListActivity.class);
                redirect(intent);
                break;
            case R.id.test_button:
                intent = new Intent(this.getActivity(),MemoryResultsListActivity.class);
                redirect(intent);
                break;
            case R.id.visaulizza_diario:
                intent = new Intent(this.getActivity(),Diary.class);
                redirect(intent);
                break;
        }
    }

    private void redirect(Intent intent) {
        startActivity(intent);
        //getActivity().finish();
    }
}
