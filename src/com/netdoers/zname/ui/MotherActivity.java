/* HISTORY
 * CATEGORY			 :- BASE ACTIVITY
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- ACTIVITY FOR VIEW PAGER + FRAGMENTS
 * NOTE: ROOT OF THE CONTACTS SCREEN. [ALL, FRIENDS, FAMILY, WORK, CALL LOG] 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     16/05/2014                       CREATED
 * ZM002      VIKALP PATEL     03/06/2014                       SUPPRESSED DRAWER OVER OVERFLOW MENU
 * ZM003      VIKALP PATEL     01/07/2014                       ADDED SYNCPHONEBOOK SERVICE
 * --------------------------------------------------------------------------------------------------------------------
 */

package com.netdoers.zname.ui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.BuildConfig;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.service.RequestBuilder;
import com.netdoers.zname.service.RestClient;
import com.netdoers.zname.service.SyncPhoneBookService;
import com.netdoers.zname.utils.PagerSlidingTabStrip;


/**
 * @author Vikalp Patel(vikalppatelce@yahoo.com)
 *
 */
public class MotherActivity extends SherlockFragmentActivity {

	// Declare Variables
	ActionBar mActionBar;
	ViewPager mPager;
	PagerSlidingTabStrip pagerSlidingTabStrp; 
	Tab tab;
	
	//TYPEFACE
	Typeface styleFont;
	
	//CONSTANTS
	public static final String TAG = MotherActivity.class.getSimpleName();
	
	//SA GCM
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
//	String SENDER_ID = "777045980104";
    String SENDER_ID = AppConstants.GCM_SENDER_ID;
	    
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
	//EA GCM
	
//	SU ZM002
//	private DrawerLayout mDrawerLayout;
//	private ListView mDrawerList;
//	private ActionBarDrawerToggle mDrawerToggle;
//
//	private CharSequence mDrawerTitle;
//	private CharSequence mTitle;
//	private String[] mPlanetTitles;
//	EU ZM002

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from activity_main.xml
		setContentView(R.layout.activity_mother);
		
		styleFont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		// Activate Navigation Mode Tabs
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Locate ViewPager in activity_main.xml
		mPager = (ViewPager) findViewById(R.id.pager);
		
		// Activate Fragment Manager
		FragmentManager fm = getSupportFragmentManager();

		// Capture ViewPager page swipes
		ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				mActionBar.setSelectedNavigationItem(position);
				switch(position)
				{
				case 0:
					setMotherActionBarTitle(getString(R.string.str_all_contacts_fragment));
					break;
				case 1:
					setMotherActionBarTitle(getString(R.string.str_group_contacts_fragment));
					break;
				case 2:
					setMotherActionBarTitle(getString(R.string.str_call_logs_fragment));
					break;
				/*case 1:
					setMotherActionBarTitle(getString(R.string.str_friends_contacts_fragment));
					break;
				case 2:
					setMotherActionBarTitle(getString(R.string.str_family_contacts_fragment));
					break;
				case 3:
					setMotherActionBarTitle(getString(R.string.str_work_contacts_fragment));
					break;
				case 4:
					setMotherActionBarTitle(getString(R.string.str_call_logs_fragment));
					break;*/
				}
			}
		};

		mPager.setOnPageChangeListener(ViewPagerListener);
		// Locate the adapter class called ViewPagerAdapter.java
		ViewPagerAdapter viewpageradapter = new ViewPagerAdapter(fm);
		// Set the View Pager Adapter into ViewPager
		mPager.setAdapter(viewpageradapter);
		//PAGER SLIDING TAB STRIP
//		pagerSlidingTabStrp = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);
//		pagerSlidingTabStrp.setViewPager(mPager);
//		pagerSlidingTabStrp.setOnPageChangeListener(ViewPagerListener);
		
		// Capture tab button clicks
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// Pass the position on tab click to ViewPager
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
		};

		// Create first Tab
//		tab = mActionBar.newTab().setText("Tab1").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_contact_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_all_contacts_fragment));

		// Create second Tab
//		tab = mActionBar.newTab().setText("Tab2").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_friends_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_friends_contacts_fragment));
		
		/*// Create second Tab
//		tab = mActionBar.newTab().setText("Tab2").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_friends_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_friends_contacts_fragment));
		
		// Create third Tab
//		tab = mActionBar.newTab().setText("Tab3").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_family_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_family_contacts_fragment));
		
//		tab = mActionBar.newTab().setText("Tab4").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_work_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_work_contacts_fragment));*/
		
//		tab = mActionBar.newTab().setText("Tab4").setTabListener(tabListener);
		tab = mActionBar.newTab().setIcon(R.drawable.tab_icon_zname_call_log_selector).setTabListener(tabListener);
		mActionBar.addTab(tab);
		setMotherActionBarTitle(getString(R.string.str_call_logs_fragment));
		
//		SA ZM003
		Zname.getPreferences().setLastSyncPhoneBook(String.valueOf(System.currentTimeMillis()));
		Intent syncPhoneBookIntent =  new Intent(Zname.getApplication().getApplicationContext(), SyncPhoneBookService.class);
		startService(syncPhoneBookIntent);
//		EA ZM003
		
		// SA GCM
		context = Zname.getApplication().getApplicationContext();
		
		if(isNetworkAvailable()){
			if (BuildConfig.DEBUG) {
				Log.i("REG_ID",Zname.getSharedPreferences().getString("registration_id","Not yet Registered"));
				Log.i("VERSION", String.valueOf(getAppVersion(context)));
				Log.i("SENDER_ID", SENDER_ID);
			}

			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regid = getRegistrationId(context);
				Log.i("REG_ID", regid);
				if (TextUtils.isEmpty(regid)) {
					registerInBackground();
//					CA.getPreferences().setFirstTime(false);
					Toast.makeText(Zname.getApplication().getApplicationContext(), "Not yet registered to GCM", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(Zname.getApplication().getApplicationContext(), "Registered to GCM", Toast.LENGTH_SHORT).show();
					if(!Zname.getSharedPreferences().getBoolean("isRegisteredToServer", false)){
						sendRegistrationIdToBackend();
						Toast.makeText(Zname.getApplication().getApplicationContext(), "Not yet registered to Server", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Log.i(TAG, "No valid Google Play Services APK found.");
			}
		}
		// EA GCM
		
// 		SU ZM002
//		mTitle = mDrawerTitle = getTitle();
//		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
//		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//		mDrawerList = (ListView) findViewById(R.id.left_drawer);
//
//		// set a custom shadow that overlays the main content when the drawer
//		// opens
//		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);
//		// set up the drawer's list view with items and click listener
//		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, mPlanetTitles);
//		mDrawerList.setAdapter(adapter);
////		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
////				R.layout.drawer_list_item, mPlanetTitles));
//		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


//		 enable ActionBar app icon to behave as action to toggle nav drawer

//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
//		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
//		mDrawerLayout, /* DrawerLayout object */
//		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
//		R.string.drawer_open, /* "open drawer" description for accessibility */
//		R.string.drawer_close /* "close drawer" description for accessibility */
//		) {
//			@Override
//			public void onDrawerClosed(View view) {
//				getSupportActionBar().setTitle(mTitle);
//				supportInvalidateOptionsMenu();
//				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//				// invalidateOptionsMenu(); // creates call to
//				// onPrepareOptionsMenu()
//			}
//
//			@Override
//			public void onDrawerOpened(View drawerView) {
//				getSupportActionBar().setTitle(mDrawerTitle);
//				supportInvalidateOptionsMenu();
//				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//				// invalidateOptionsMenu(); // creates call to
//				// onPrepareOptionsMenu()
//			}
//		};
//		mDrawerLayout.setDrawerListener(mDrawerToggle);
//		EU ZM002
		
//		ContactsContentObserver contactsContentObserver = new ContactsContentObserver();
//		getContentResolver().registerContentObserver(android.provider.ContactsContract.Data.CONTENT_URI, false, contactsContentObserver);
//		
//		CallLogsContentObserver callLogsContentObserver = new CallLogsContentObserver();
//		getContentResolver().registerContentObserver(CallLog.CONTENT_URI, false, callLogsContentObserver);
	}
	
//	SA GCM
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
//	D: CHECK PLAY SERVICES ON RESUME [PLAYSERVICES GCM]
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
//	D: CHECK PLAY SERVICES EXIST ON THE DEVICE OR NOT [PLAYSERVICES GCM]	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        /*if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }*/
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (TextUtils.isEmpty(registrationId)) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
//	    return getSharedPreferences(NewHomeActivity.class.getSimpleName(),Context.MODE_PRIVATE);
		return Zname.getSharedPreferences();
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	public void registerInBackground()
	{
		new GCMRegisterTask().execute();
	}
	
	private class GCMRegisterTask extends AsyncTask<Void, Void, String>
	{
		String msg = "";
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;

                

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
                
                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                if(!Zname.getSharedPreferences().getBoolean("isRegisteredToServer", false))
                {
                sendRegistrationIdToBackend();
                }
                else
                {
                	Log.i("AndroidToServer", "Already registered to server");
                }
            } 
            catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("GCM", msg);
		}
	}
	
	private class SendToServerTask extends AsyncTask<JSONObject, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = params[0];
			boolean status = false;
			try {
				String jsonStr = RestClient.postData(AppConstants.URLS.GCM_URL+Zname.getPreferences().getApiKey()+"/appregistraion", dataToSend);
				
				if(jsonStr != null){
					JSONObject jsonObject = new JSONObject(new String(jsonStr));
					status = jsonObject.getBoolean("status");
					if(status)
					{
						try {
		                    // Getting JSON Array node
							//SERVERDEMO
							final SharedPreferences prefs = getGCMPreferences(context);
						    Log.i(TAG, "Saving regId on server ");
						    SharedPreferences.Editor editor = prefs.edit();
						    editor.putBoolean("isRegisteredToServer", true);
						    editor.commit();
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
					}
					}
				Log.e("PushServer",jsonStr.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}


	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
	    // Your implementation here.
		
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String currentSIMImsi = mTelephonyMgr.getDeviceId();
		
		JSONObject jsonObject = RequestBuilder.getPushNotificationData(currentSIMImsi);
		Log.e("PUSH REGID SERVER---->>>>>>>>>>", jsonObject.toString());
		SendToServerTask sendTask = new SendToServerTask();
		sendTask.execute(new JSONObject[]{jsonObject});
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
//	EA GCM
	/*
	 * DEPRECEATED MOVES INTO IMPORTCONTACTSERVICE 
	 */
	
	//////////////////////////////////////////////////////////////////////
	// CONTENT OBSERVER
	//////////////////////////////////////////////////////////////////////
//	private class ContactsContentObserver extends ContentObserver {
//        public ContactsContentObserver() {
//            super(null);
//        }
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            Zname.getPreferences().setRefreshContact(true);
//        }
//    }
//	
//	private class CallLogsContentObserver extends ContentObserver {
//        public CallLogsContentObserver() {
//            super(null);
//        }
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            Zname.getPreferences().setRefreshCallLogs(true);
//        }
//    }
	
	public void setMotherActionBarTitle(String s)
	{
//		mActionBar.setTitle(s);
		fontActionBar(s);
	}
	
	public void fontActionBar(String str)
	{
		try {
			int titleId;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				titleId = getResources().getIdentifier("action_bar_title","id", "android");
			} else {
				titleId = R.id.abs__action_bar_title;
			}
			TextView yourTextView = (TextView) findViewById(titleId);
			yourTextView.setText(str);
			yourTextView.setTypeface(styleFont);
		} catch (Exception e) {
			Log.e("ActionBar Style", e.toString());
		}
	}
	
	public String getActionBarTitle(){
		try {
			int titleId;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				titleId = getResources().getIdentifier("action_bar_title","id", "android");
			} else {
				titleId = R.id.abs__action_bar_title;
			}
			TextView yourTextView = (TextView) findViewById(titleId);
			return yourTextView.getText().toString();
		} catch (Exception e) {
			Log.e("ActionBar Style", e.toString());
			return null;
		}
	}
	
	/////////////////////////////////////////////
	// ARRAY ADAPTER FOR DRAWER
	/////////////////////////////////////////////
	
	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		  private final Context context;
		  private final String[] values;

		  public MySimpleArrayAdapter(Context context, String[] values) {
		    super(context, R.layout.item_list_drawer, values);
		    this.context = context;
		    this.values = values;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.item_list_drawer, parent, false);
		    TextView textView = (TextView) rowView.findViewById(R.id.text1);
		    ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);
		    textView.setText(values[position]);
		    
		    textView.setTypeface(styleFont);
		    // Change the icon for Windows and iPhone
		    String s = values[position];
			switch (position) {
			case 0:
				imageView.setImageResource(R.drawable.ic_drawer_edit);
				break;
			case 1:
				imageView.setImageResource(R.drawable.ic_drawer_gear);
				break;
			default:
				break;
			}
		    return rowView;
		  }
		} 
	
	
	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		
//		PlanetFragment fragment = new PlanetFragment();
		Bundle args = new Bundle();
//		args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//		fragment.setArguments(args);

		Intent drawerIntent = null;
		
      switch (position) {
      case 0:
//          fragment = new AllContactsFragment();
          break;
      case 1:
//          fragment = new AllContactsFragment();
    	  drawerIntent = new Intent(this, SettingsActivity.class);
          break;
      case 2:
          fragment = new ContactsFragment();
          break;
      case 3:
          fragment = new ContactsFragment();
          break;
      case 4:
          fragment = new ContactsFragment();
          break;
      case 5:
          fragment = new ContactsFragment();
          break;
      case 6:
          fragment = new ContactsFragment();
          break;
      case 7:
//          fragment = new SendFeedbackFragment();
//          showFeedbackActivity();
          break;
      default:
          break;
      }
//      fragment.setArguments(args);
//		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//		// update selected item and title, then close the drawer
//		mDrawerList.setItemChecked(position, true);
//		setTitle(mPlanetTitles[position]);
//		mDrawerLayout.closeDrawer(mDrawerList); COMMENTED ZM002
		
		if(drawerIntent!=null)
			startActivity(drawerIntent);
	}
	
	////////////////////////////////////////////////
	// ACTIVITY CREATED : OPTIONS INSIDE ABS
	///////////////////////////////////////////////
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_friends_contacts, menu);
//		MenuItem overFlowMenu = menu.findItem(R.id.action_more);
//		MenuItem notificationMenu = menu.findItem(R.id.action_notification);
		return true;
	}
	
	////////////////////////////////////////////////
	// OPTIONS IN ABS : SELECTED
	///////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
//			SU ZM002
//			try {
//				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//					mDrawerLayout.closeDrawer(mDrawerList);
//				} else {
//					mDrawerLayout.openDrawer(mDrawerList);
//				}
//			} catch (Exception e) {
//				Log.e(TAG, e.toString());
//			}
//			EU ZM002
			return true;
		case R.id.action_add:
			Intent addZnameIntent = new Intent(this, AddZnameActivity.class);
			startActivity(addZnameIntent);
			return true;
		case R.id.action_edit:
			Intent profileIntent = new Intent(this, ProfileActivity.class);
			startActivity(profileIntent);
			return true;
		case R.id.action_search:
			Intent searchIntent = new Intent(this,SearchActivity.class);
			String _title = getActionBarTitle(); 
			
			searchIntent.putExtra(SearchActivity.SEARCH_TYPE, _title);
			String value = "5";
			
			if(_title.equalsIgnoreCase(getString(R.string.str_all_contacts_fragment)) || _title.equalsIgnoreCase(getString(R.string.app_name))){
			}else{
				if(_title.equalsIgnoreCase(getString(R.string.str_friends_contacts_fragment))){
					value = "0";
			    }else {
			    	if(_title.equalsIgnoreCase(getString(R.string.str_family_contacts_fragment))){
			    		  value = "1";
			        }else{
				    value = "2";
			        }
			    }
			}
			
			searchIntent.putExtra(SearchActivity.GROUP_TYPE, value);
			startActivity(searchIntent);
			return true;
		case R.id.action_settings:
			Intent settingIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingIntent);
			return true;
		case R.id.action_notification:
			Intent notificationIntent = new Intent(this, NotificationActivity.class);
			startActivity(notificationIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	try {
//	    		SU ZM002
//				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//					mDrawerLayout.closeDrawer(mDrawerList);
//				} else {
//					mDrawerLayout.openDrawer(mDrawerList);
//				}
//	    		EU ZM002
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
	        return true;
	    } else {
	        return super.onKeyUp(keyCode, event);
	    }
	}
	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
//		mDrawerToggle.syncState();   COMMENTED ZM002
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
//		mDrawerToggle.onConfigurationChanged(newConfig); COMMENTED ZM002
	}
	
}
