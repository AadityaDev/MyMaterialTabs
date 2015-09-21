package com.technawabs.material.mymaterialtabs;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    //Screen Components
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ActionBar actionBar;

    //Contact List adapter and storage
    List<String> nameList=new ArrayList<String>();
    List<String> phoneList=new ArrayList<String>();
    MyAdapter myAdapter;

    //SMS Manager
    SmsManager smsManager;

    //Send Message
    Button sendMess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Set toolbar as action bar
        toolbar=(Toolbar)findViewById(R.id.tabanum_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Choose Contact");
        actionBar=getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);


        //Get the contacts
        getAllContacts(this.getContentResolver());

        //List adapter
        ListView lv=(ListView)findViewById(R.id.lv);
        myAdapter=new MyAdapter();
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);

        //SMS Manager
        smsManager=SmsManager.getDefault();

        //Send messages
        sendMess=(Button)findViewById(R.id.button1);
        sendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check selected Contacts
                StringBuilder checkedcontacts = new StringBuilder();
                for(int i=0;i<nameList.size();i++){
                    if(myAdapter.mCheckStates.get(i)==true){
                        checkedcontacts.append(nameList.get(i).toString());
                        checkedcontacts.append("\t");

                        PackageManager pm=getPackageManager();
                        Intent sendMessage=new Intent();
                        sendMessage.setAction(Intent.ACTION_SEND);
                        sendMessage.putExtra(Intent.EXTRA_SUBJECT, "MyRefers-Earn Good Karma");
                        sendMessage.putExtra(Intent.EXTRA_TEXT,"Your friend has refferd you for a job opportunity on MyRefers");
                        sendMessage.setType("text/plain");
                        sendMessage.putExtra("address", phoneList.get(i).toString());
                        finish();

                        Toast.makeText(getApplicationContext(),"You reffered your friend Successfully",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Please refer again!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        viewPager=(ViewPager)findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);

        tabLayout=(TabLayout)findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new
             TabLayout.OnTabSelectedListener(){
                 @Override
                 public void onTabSelected(TabLayout.Tab tab) {
                     viewPager.setCurrentItem(tab.getPosition());
                     switch (tab.getPosition()){
                         case 0:
                             showToast("One");
                             break;
                         case 1:
                             showToast("Two");
                             break;
                     }
                 }

                 @Override
                 public void onTabReselected(TabLayout.Tab tab) {

                 }

                 @Override
                 public void onTabUnselected(TabLayout.Tab tab) {

                 }
             });
    }

    public void showToast(String s){
        Toast.makeText(getApplicationContext(),"Tab: "+s,Toast.LENGTH_SHORT).show();
    }
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new SMSFragment(),"SMS");
        //adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_dark)),"Email");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        myAdapter.toggle(position);
    }

    public  void getAllContacts(ContentResolver cr){
        Cursor phones=cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (phones.moveToNext()){
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            nameList.add(name);
            phoneList.add(phoneNumber);
        }
        phones.close();
    }


    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{

        private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView tvname,tvphone;
        CheckBox cb;

        MyAdapter(){
            mCheckStates=new SparseBooleanArray(nameList.size());
            //mInflater = (LayoutInflater)SMSFragment.SavedState.;
            mInflater=(LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return nameList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return null;
            View vi=convertView;
            if(convertView==null)
                vi=mInflater.inflate(R.layout.row,null);
            tvname= (TextView) vi.findViewById(R.id.textView1);
            tvphone= (TextView) vi.findViewById(R.id.textView2);
            cb = (CheckBox) vi.findViewById(R.id.checkBox1);
            tvname.setText("Name :"+ nameList.get(position));
            tvphone.setText("Phone No :"+ phoneList.get(position));
            cb.setTag(position);
            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);

            return vi;
        }

        public boolean isChecked(int position){
            return mCheckStates.get(position,false);
        }

        public  void setChecked(int position, boolean isChecked){
            mCheckStates.put(position,isChecked);
        }

        public void toggle(int position){
            setChecked(position,!isChecked(position));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCheckStates.put((Integer)buttonView.getTag(),isChecked);
        }
    }

    //Set SMS tab
    static class ViewPagerAdapter extends FragmentPagerAdapter{

        List<String> nameList=new ArrayList<String>();
        List<String> phoneList=new ArrayList<String>();

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment,String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //Create the different tab fragment
     public static class SMSFragment extends Fragment{
        public SMSFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.sms_fragment,container,false);

            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_bg);


//            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);
//
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
//            recyclerView.setLayoutManager(linearLayoutManager);
//            recyclerView.setHasFixedSize(true);
//
//            List<String> list = new ArrayList<String>();
//            for (int i = 0; i < VersionModel.data.length; i++) {
//                list.add(VersionModel.data[i]);
//            }
//
//            adapter = new SimpleRecyclerAdapter(list);
//            recyclerView.setAdapter(adapter);

            return view;
            //return super.onCreateView(inflater, container, savedInstanceState);
        }
    }



//    static class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFrag(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }

//    public static class DummyFragment extends Fragment {
//        int color;
//        SimpleRecyclerAdapter adapter;
//
//        public DummyFragment() {
//        }
//
//        @SuppressLint("ValidFragment")
//        public DummyFragment(int color) {
//            this.color = color;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            View view = inflater.inflate(R.layout.dummy_fragment, container, false);
//
//            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_bg);
//            frameLayout.setBackgroundColor(color);
//
//            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);
//
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
//            recyclerView.setLayoutManager(linearLayoutManager);
//            recyclerView.setHasFixedSize(true);
//
//            List<String> list = new ArrayList<String>();
//            for (int i = 0; i < VersionModel.data.length; i++) {
//                list.add(VersionModel.data[i]);
//            }
//
//            adapter = new SimpleRecyclerAdapter(list);
//            recyclerView.setAdapter(adapter);
//
//            return view;
//        }
//    }
//
//

}
