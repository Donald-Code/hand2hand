package tshabalala.bongani.courierservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import tshabalala.bongani.courierservice.adapters.LogParcelAdapter;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.FileDialog;
import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.model.Parcel;
import tshabalala.bongani.courierservice.model.User;

import static tshabalala.bongani.courierservice.helper.Common.getMimeType;

public class ReportParcelActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference parcel;
    FirebaseRecyclerAdapter<Parcel, MenuViewHolder> adapterParcel;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView_menu;

    TableLayout checkColumn;

    private static final String logDir = "/LogData";
    private static final String logFile = "/Hand2Hand";

    private static final String FTYPE = ".csv";
    private static final int DIALOG_LOAD_FILE = 1000;
    File sdcFile;
    List<Parcel> timeslotList = new ArrayList<>();
    List<User> userList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_reports);

        firebaseDatabase = FirebaseDatabase.getInstance();
        parcel = firebaseDatabase.getReference("Parcel");
//        recyclerView_menu = findViewById(R.id.recycler_menu);
//
//        swipeRefreshLayout = findViewById(R.id.swipeHome);
//        //Load Menu
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
//
//        recyclerView_menu.setLayoutManager(mLayoutManager);
//        recyclerView_menu.setItemAnimator(new DefaultItemAnimator());
//        recyclerView_menu.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
//
//        BottomNavigationView bottomNavView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavView);
//
//        Menu menu = bottomNavView.getMenu();
//        MenuItem menuItem = menu.getItem(2);
//        menuItem.setChecked(true);
//
//        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                switch (item.getItemId())
//                {
//                    case R.id.ic_customer:
//                        Intent ssearch = new Intent(ReportParcelActivity.this, ReportCustomerActivity.class);
//                        startActivity(ssearch);
//                        break;
//
//                    case R.id.ic_shipper:
//                        Intent s_view = new Intent(ReportParcelActivity.this, ReportShipperActivity.class);
//                        startActivity(s_view);
//                        break;
//
//                    case R.id.ic_parcel:
//
//                        break;
//
//                }
//
//                return false;
//            }
//        });
//
//        loadParcels();

        loadData();
    }

    private void loadData() {


        parcel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Request request = new Request()
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Parcel parcel = snapshot.getValue(Parcel.class);
                    assert parcel != null;
                    Log.e("USer ","adaa "+ parcel + " sasa "+ dataSnapshot.getChildren());
                    timeslotList.add(parcel);
//                        mDatabase.child("User").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                final User user = dataSnapshot.child(request.getFriendPhoneNumber()).getValue(User.class);
//                                assert user != null;
//                                Log.w("TAG"," User "+user.getName()+" phobe number 2"+ user.getPhone());
//                                // String mEmail, String IDNumber, String mMobile, String mAddress, String mImageUrl, String token) {
//                                //
//                                // userDao.
//                                userDao.insertUser(new Users(user.getName(),user.getSurname(),user.getEmail(),user.getIDNumber(),user.getPhone(),user.getImage()));
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });

                }

                displayCustomer(timeslotList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }

    void displayCustomer(List<Parcel> users){
        int fixedRowHeight = 50;
        int fixedHeaderHeight = 60;

        TableRow rowSheet = new TableRow(ReportParcelActivity.this);
        // Log.e("TAG", " NAme " + user.getName() + " Surnssme " + user.getSurname());
        //header (fixed vertically)
        TableRow.LayoutParams wrapWrapTableRowSheetParams = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        int[] fixedColumnWidthsSheet = new int[]{15, 15, 15, 10, 10, 20};
        int[] scrollableColumnWidthsSheet = new int[]{15, 15, 15, 10, 10, 20};

        //header (fixed vertically)
        TableLayout header = findViewById(R.id.table_header);
        header.removeAllViews();
        rowSheet.setLayoutParams(wrapWrapTableRowSheetParams);
        rowSheet.setGravity(Gravity.START);


        rowSheet.addView(makeTableHeader("Name.", fixedColumnWidthsSheet[0], fixedHeaderHeight, users));
        rowSheet.addView(makeTableHeader("Description", fixedColumnWidthsSheet[1], fixedHeaderHeight, users));
        rowSheet.addView(makeTableHeader("From", fixedColumnWidthsSheet[2], fixedHeaderHeight, users));
        rowSheet.addView(makeTableHeader("Destination", fixedColumnWidthsSheet[3], fixedHeaderHeight, users));
        rowSheet.addView(makeTableHeader("Phone", fixedColumnWidthsSheet[4], fixedHeaderHeight, users));
        rowSheet.addView(makeTableHeader("Time and Date", fixedColumnWidthsSheet[5], fixedHeaderHeight, users));

        header.addView(rowSheet);

        //header for checkbox
        checkColumn = findViewById(R.id.check_column);
        //header (fixed horizontally)
        TableLayout fixedColumn = findViewById(R.id.fixed_column);
        fixedColumn.removeAllViews();
        //rest of the table (within a scroll view)
        TableLayout scrollablePart = findViewById(R.id.scrollable_part);
        scrollablePart.removeAllViews();


//                        TextView fixedView = makeTableRowWithText("" + shipper.getName(), scrollableColumnWidthsSheet[0], fixedRowHeight);
//                        fixedView.setBackgroundColor(Color.WHITE);

        // fixedColumn.addView(fixedView);
        for(Parcel user : users) {
            rowSheet = new TableRow(ReportParcelActivity.this);

            rowSheet.setLayoutParams(wrapWrapTableRowSheetParams);
            rowSheet.setGravity(Gravity.CENTER);
            rowSheet.setBackgroundColor(Color.WHITE);



            rowSheet.addView(makeTableRowWithText("" + user.getName(), scrollableColumnWidthsSheet[0], fixedRowHeight));
            rowSheet.addView(makeTableRowWithText("" + user.getDescription(), scrollableColumnWidthsSheet[1], fixedRowHeight));
            rowSheet.addView(makeTableRowWithText("" + user.getPickup(), scrollableColumnWidthsSheet[2], fixedRowHeight));
            rowSheet.addView(makeTableRowWithText("" + user.getDestination(), scrollableColumnWidthsSheet[3], fixedRowHeight));
            rowSheet.addView(makeTableRowWithText("" + user.getPhone(), scrollableColumnWidthsSheet[4], fixedRowHeight));
            rowSheet.addView(makeTableRowWithText("" + user.getTimestamp(), scrollableColumnWidthsSheet[5], fixedRowHeight));

            scrollablePart.addView(rowSheet);

            rowSheet.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // Toast.makeText(ReportCustomerActivity.this, "Clicked "+ user.getName(),Toast.LENGTH_SHORT).show();
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    dialog.dismiss();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                      deleteParcel(user.getUid());
                                      dialog.dismiss();
                                      ReportParcelActivity.this.recreate();
                                    break;
                            }
                        }
                    };

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportParcelActivity.this);
                    builder.setMessage("Delete this parcel?").setPositiveButton("No", dialogClickListener)
                            .setNegativeButton("Yes", dialogClickListener).show();


                    return false;
                }
            });
        }



    }

    private void deleteParcel(String key) {
        Log.e("TAG", " tag "+ key);
         parcel.child(key).removeValue();
        ReportParcelActivity.this.recreate();
    }

    private void changeStatus(String key) {
        parcel.child(key).removeValue();
    }


    @Override
    public void onStart() {
        super.onStart();

        if(adapterParcel != null) {
            adapterParcel.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapterParcel != null) {
            adapterParcel.stopListening();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().goOnline();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().goOffline();
    }


    public TextView makeTableHeader(final String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels,final List<Parcel> parcel) {

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(10);
        textView.setBackgroundColor(getResources().getColor(R.color.color8));
        textView.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        textView.setHeight(fixedHeightInPixels);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text.equals("Name"))
                {
                    ObservableSnapshotArray<Parcel> parcels = adapterParcel.getSnapshots();


//                    Collections.sort(displayListList, new Comparator<Report>() {
//                        public int compare(Report obj1, Report obj2) {
//                            // ## Ascending order
//                            return obj2.getName().compareToIgnoreCase(obj1.getName());
//                            // return Integer.valueOf(obj1.getId).compareTo(obj2.getId); // To compare integer values
//
//                        }
//                    });


                }

            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(text.equals("Name")) {
//                    Collections.sort(displayListList, new Comparator<Report>() {
//                        public int compare(Report obj1, Report obj2) {
//                            // ## Ascending order
//                            return obj1.getName().compareToIgnoreCase(obj2.getName());
//                            // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values
//
//                            // ## Descending order
//                            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
//                            // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
//                        }
//                    });


                }
                return false;

            }
        });
        return textView;
    }

    private TextView textView;

    public TextView makeTableRowWithText(String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        textView.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        textView.setHeight(fixedHeightInPixels);
        return textView;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        List<User> result = new ArrayList<>();
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.itemSave) {
            makeLog();
            // loadReports();
            return true;
        }else if(id == R.id.itemName){


            Collections.sort(timeslotList, new Comparator<Parcel>() {
                public int compare(Parcel obj1, Parcel obj2) {
                    // ## Ascending order
                    return obj2.getName().compareToIgnoreCase(obj1.getName());
                    // return Integer.valueOf(obj1.getId).compareTo(obj2.getId); // To compare integer values

                }
            });
            displayCustomer(timeslotList);
            // sortCustomers("Name");
            return true;
        }else if(id == R.id.itemSurname){


            Collections.sort(timeslotList, new Comparator<Parcel>() {
                public int compare(Parcel obj1, Parcel obj2) {
                    // ## Ascending order
                    return obj2.getSurname().compareToIgnoreCase(obj1.getSurname());
                    // return Integer.valueOf(obj1.getId).compareTo(obj2.getId); // To compare integer values

                }
            });
            displayCustomer(timeslotList);
            // sortCustomers("Name");
            return true;
        }
        else if(id == R.id.itemView){
            if (Common.isExternalStorageAvailable() || !Common.isExternalStorageReadOnly()) {

                sdcFile = new File(Environment.getExternalStorageDirectory() + logDir);
                if (sdcFile.exists()) {
                    FileDialog fileDialog = new FileDialog(ReportParcelActivity.this, sdcFile);
                    fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                        public void fileSelected(File file) {
                            Log.d(getClass().getName(), "selected file " + file.getName());

                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), getMimeType(file.getAbsolutePath()));
                            startActivity(intent);

                        }
                    });
                    fileDialog.showDialog();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



//    private void sortParcels(String sort_item)
//    {
//        ProgressBar progressBar = findViewById(R.id.progress);
//        progressBar.setVisibility(View.GONE);
//
//        FirebaseRecyclerOptions<Parcel> options = new FirebaseRecyclerOptions.Builder<Parcel>()
//                .setQuery(parcel.orderByChild(sort_item), Parcel.class).build();
//
//        adapterParcel = new FirebaseRecyclerAdapter<Parcel, MenuViewHolder>(options) {
//            @NonNull
//            @Override
//            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//            {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
//                return new MenuViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Parcel parcel)
//            {
//                int fixedRowHeight = 50;
//                int fixedHeaderHeight = 60;
//
//                TableRow rowSheet = new TableRow(ReportParcelActivity.this);
//                //header (fixed vertically)
//                TableRow.LayoutParams wrapWrapTableRowSheetParams = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
//                int[] fixedColumnWidthsSheet = new int[]{15, 15, 15, 10, 20, 20};
//                int[] scrollableColumnWidthsSheet = new int[]{15, 15, 15, 10, 20, 20};
//
//                //header (fixed vertically)
//                TableLayout header = holder.itemView.findViewById(R.id.table_header);
//                header.removeAllViews();
//                rowSheet.setLayoutParams(wrapWrapTableRowSheetParams);
//                rowSheet.setGravity(Gravity.START);
//
//
//                rowSheet.addView(makeTableHeader("Name.", fixedColumnWidthsSheet[0], fixedHeaderHeight, parcel.getName()));
//                rowSheet.addView(makeTableHeader("Phone", fixedColumnWidthsSheet[1], fixedHeaderHeight, parcel.getPhone()));
//                rowSheet.addView(makeTableHeader("Departure", fixedColumnWidthsSheet[2], fixedHeaderHeight, parcel.getPickup()));
//                rowSheet.addView(makeTableHeader("Destination", fixedColumnWidthsSheet[3], fixedHeaderHeight, parcel.getDestination()));
//                rowSheet.addView(makeTableHeader("Date", fixedColumnWidthsSheet[4], fixedHeaderHeight, parcel.getTimestamp()));
//                rowSheet.addView(makeTableHeader("Status", fixedColumnWidthsSheet[5], fixedHeaderHeight, parcel.getStatus()));
//
//                header.addView(rowSheet);
//
//                //header for checkbox
//                checkColumn = holder.itemView.findViewById(R.id.check_column);
//                //header (fixed horizontally)
//                TableLayout fixedColumn = holder.itemView.findViewById(R.id.fixed_column);
//                fixedColumn.removeAllViews();
//                //rest of the table (within a scroll view)
//                TableLayout scrollablePart = holder.itemView.findViewById(R.id.scrollable_part);
//                scrollablePart.removeAllViews();
//
//
//
////                        TextView fixedView = makeTableRowWithText("" + shipper.getName(), scrollableColumnWidthsSheet[0], fixedRowHeight);
////                        fixedView.setBackgroundColor(Color.WHITE);
//
//                // fixedColumn.addView(fixedView);
//                rowSheet = new TableRow(ReportParcelActivity.this);
//
//                rowSheet.setLayoutParams(wrapWrapTableRowSheetParams);
//                rowSheet.setGravity(Gravity.CENTER);
//                rowSheet.setBackgroundColor(Color.WHITE);
//                rowSheet.addView(makeTableRowWithText("" + parcel.getName(), scrollableColumnWidthsSheet[0], fixedRowHeight));
//                rowSheet.addView(makeTableRowWithText("" + parcel.getPhone(), scrollableColumnWidthsSheet[1], fixedRowHeight));
//                rowSheet.addView(makeTableRowWithText("" + parcel.getPickup(), scrollableColumnWidthsSheet[2], fixedRowHeight));
//                rowSheet.addView(makeTableRowWithText("" + parcel.getDestination(), scrollableColumnWidthsSheet[3], fixedRowHeight));
//                rowSheet.addView(makeTableRowWithText("" + parcel.getTimestamp(), scrollableColumnWidthsSheet[4], fixedRowHeight));
//                rowSheet.addView(makeTableRowWithText("" + parcel.getStatus(), scrollableColumnWidthsSheet[5], fixedRowHeight));
//
//                scrollablePart.addView(rowSheet);
//
//            }
//        };
//
//        recyclerView_menu.setAdapter(adapterParcel);
//    }


    List<Parcel> rawFrames = new ArrayList();
    public void makeLog() {
        try {

            CSVWriter writer;
            final ListView listView = new ListView(this);
            listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


            // int tot = booksDatabaseManipulation.getContactsCount(subject);
            // Log.w("TSAG","toal "+tot);
            //  logEntries = booksDatabaseManipulation.getContact(subject);
            final LogParcelAdapter logAdapter = new LogParcelAdapter(ReportParcelActivity.this, R.id.textview_book_name, rawFrames);
            listView.setAdapter(logAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    logAdapter.notifyDataSetChanged();
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyWhiteAlertDialogStyle);
            builder.setTitle("Activities Log - ");
            builder.setView(listView)
                    .setNeutralButton("Store Log", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Write Raw Frame Data to File

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        boolean success = false;
                                        File dir = new File(Environment.getExternalStorageDirectory()+ logDir);
                                        dir.setReadable(true,false);

                                        success = (dir.mkdir() || dir.isDirectory());
                                        if (success) {
                                            String timelabel = "_" + Common.getDateTime(new Date()).replace(" ", "_").replace(":", "h").replace("/","-");
                                            CSVWriter writer = new CSVWriter(new FileWriter(dir+ logFile + timelabel+".csv"), ',');
                                            FileOutputStream outputStream = new FileOutputStream(dir + logFile + timelabel + ".csv", false);
                                            // Runtime.getRuntime().exec("chmod 444 " + dir + logFile + timelabel + ".txt");
                                            String logString = "";
                                            // ProgressDialogStart("Application Log", "Storing app log in " + logDir + logFile + timelabel + ".txt...");
                                            for (ListIterator<Parcel> i = rawFrames.listIterator(); i.hasNext(); ) {
                                                Parcel entry = i.next();
                                                String[] entries = (entry.getName()+" : "+ entry.getSurname()+" : "+ entry.getPhone()+" : "+ entry.getPickup() + " : "+ entry.getDestination()+" : "+entry.getTimestamp()).split(":");
                                                writer.writeNext(entries);
                                            }

                                            writer.close();
                                            Toast.makeText(ReportParcelActivity.this,"Successfully created file",Toast.LENGTH_SHORT).show();


                                        } else {
                                            Toast.makeText(ReportParcelActivity.this,"Could not create directory \"/LogData\"",Toast.LENGTH_SHORT).show();
                                        }

                                        //printLog("Log written to file...");
                                    } catch (Exception e) {
                                        e.getStackTrace();
                                    }
                                }
                            }, "writeFileThread").start();

                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            listView.smoothScrollToPosition(logAdapter.getCount()-1);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e){
            e.getStackTrace();
        }
    }
}
