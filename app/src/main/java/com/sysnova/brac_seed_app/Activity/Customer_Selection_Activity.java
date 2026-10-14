package com.sysnova.brac_seed_app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sysnova.brac_seed_app.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.ParseException;
import java.util.Locale;

import static com.sysnova.brac_seed_app.Activity.LoginActivity.*;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

public class Customer_Selection_Activity extends AppCompatActivity {
    String documentNo = "", pricelistname="";
    DatabaseHelper mDatabaseHelper;
    SearchableSpinner orgSpinner,branchSpinner,CustomerSpinner,Location_Spinner;
    EditText search_editText,customer_PhoneNo_editText,customer_Address_editText;
    Button showdate;
    private DatePickerDialog.OnDateSetListener dateSetListener1;
    public static String selectedOrgId="",selectedWHId="",selectedOrgName="",selectedWHName="",selectedTerritoryId="",selectedTerritoryName="";
    public static String selectedBPCustomerID = "", selectedBPTerrID = "", selectedPriceListName="",selectedPriceListId="",showdateNew="",
            selectedBPCustomerName="",iscash="", cashpos = "" ;
    HashMap<String, String> hashBPGourpID = new HashMap<>();
    HashMap<String, String> hashBPID = new HashMap<>();
    HashMap<String, String> hashBPTerrID = new HashMap<>();
    ArrayList<String> customerArrayList = new ArrayList<>();
    ArrayList<String> BPGroupArayList = new ArrayList<>();
    ArrayList<String> salesTypeArrayList = new ArrayList<>();
    HashMap<String, String> hashPriceListID = new HashMap<>();
    ArrayList<String> PriceListArrayList = new ArrayList<>();
    List<String> orgNameArrayList = new ArrayList<String>();
    List<String> branchNameArrayList = new ArrayList<String>();
    List<String> TerritoryNameArrayList = new ArrayList<String>();
    HashMap<String,String> hashOrgId  = new HashMap<>();
    HashMap<String,String> hashBranchId  = new HashMap<>();
    HashMap<String,String> hashTerritoryId  = new HashMap<>();
    CheckBox codeCheckbox;
    String cdate,cdate1, dateorder;
    CheckBox cash_checkbox;
    String dateStr;
    String odate = "",orderdate = "",actType = "",DistrictName = "",ThanaName = "",org = "",wh = "";
    String dealer = "",cashcheck = "",pricelist ="";

    Spinner salesTypeSpinner, customerGroupSpinner, CustomerSpinner1,pricelist_spinner, ProductCategorySpinner, ProductSpinner;
    String date = "", selectedBPGroupID = "", c_bp_group_id = "";
    public static String selectedSalesType = "";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
    Date date1 = new Date();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutomer_selection);
        long now = System.currentTimeMillis();
        String strLong = Long.toString(now);
        documentNo = employeeid + strLong;
        /**designing code**/
        getSupportActionBar().setTitle("Choose Customer");
        //getSupportActionBar().setSubtitle("Brac Fisheries App");
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#97c740")));
        boolean isDarkThemeOn = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)  == Configuration.UI_MODE_NIGHT_YES;

        mDatabaseHelper=DatabaseHelper.getInstance(Customer_Selection_Activity.this);

        codeCheckbox = findViewById(R.id.search_code_checkbox);
        salesTypeSpinner = findViewById(R.id.salesType_spinner);
        customerGroupSpinner = findViewById(R.id.customer_group_spinner);
        CustomerSpinner = findViewById(R.id.customer_spinner);
        search_editText = findViewById(R.id.search_customer_editText);
        orgSpinner = (SearchableSpinner) findViewById(R.id.org_spinner);
        Location_Spinner=(SearchableSpinner) findViewById(R.id.location_spinner);
        pricelist_spinner=(SearchableSpinner) findViewById(R.id.pricelist_spinner);
        branchSpinner=(SearchableSpinner) findViewById(R.id.branch_spinner);
        customer_Address_editText = findViewById(R.id.search_customerAddress_editText);
        customer_PhoneNo_editText = findViewById(R.id.search_customerPhoneNo_editText);
        customer_Address_editText.setEnabled(false);
        customer_PhoneNo_editText.setEnabled(false);

        codeCheckbox = findViewById(R.id.search_code_checkbox);
        cash_checkbox=findViewById(R.id.cash_checkbox);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy"); //dd-MM-yyyy changed by Shanzida 2021-11-15
        dateStr = df.format(cal.getTime());

        showdate = (Button) findViewById(R.id.date);


        Date dateSql=new Date();

        String strDate = currentdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateSql = dateFormat.parse(strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        cdate =currentdate;// new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        cdate1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateSql);
        dateorder = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        System.out.println(new Date());
        showdate.setText(""+cdate1+"");
        Date finalDateSql = dateSql;
        showdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer_Selection_Activity obj = new Customer_Selection_Activity();
                Calendar calendar;
                calendar = obj.dateToCalendar(finalDateSql);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //calendar.set(Calendar.DATE, calendar.getActualMaximum(dateSql));

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Customer_Selection_Activity.this,
                        dateSetListener1,
                        year, month, day);
                datePickerDialog.show();
            }
        });

        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String sdate = dayOfMonth+"/"+month+"/"+year;
                date1 = new Date(year-1900, month-1,dayOfMonth);
                cdate = formatter.format(date1);
                cdate1 = formatter1.format(date1);
                showdate.setText(cdate1);
                dateorder = formatter.format(date1);
            }
        };
        //getBPGroup();
        getSalesType();
        getOrg();
        getTerritory();
        searchCustomer();
        getCustomer();
        getPriceList();
        getlist();
    }
    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private void loadSalesTypeSpinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, salesTypeArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        salesTypeSpinner.setAdapter(dataAdapter);
        //anika 2023-06-26
        int spinnerPosition = dataAdapter.getPosition("Standard Sales Order");
        salesTypeSpinner.setSelection(spinnerPosition);
        //anika 2023-06-26
        dataAdapter.notifyDataSetChanged();
        selectSalesTypeSpinner();
    }

    private void loadBPGroupSpinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BPGroupArayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        customerGroupSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectBPGroupSpinner();
    }

    private void getBPGroup() {
        if (!hashBPGourpID.isEmpty()) {
            hashBPGourpID.clear();
        }
        Cursor cursor = mDatabaseHelper.getBPGroup();
        if (cursor.moveToFirst()) {
            do {
                String bpID = cursor.getString(1);
                String bpName = cursor.getString(2);
                BPGroupArayList.add(bpName);
                hashBPGourpID.put(bpName, bpID);

            } while (cursor.moveToNext());
        }
        loadBPGroupSpinner();
    }

    private void getSalesType() {
        if(!salesTypeArrayList.isEmpty())
        {
            salesTypeArrayList.clear();
        }
        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getSalesType();

            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(0);

                    salesTypeArrayList.add(name);

                } while (cursor.moveToNext());
            }
        }
        finally {
                if (cursor != null)
                    cursor.close();  // RIGHT: ensure resource is always recovered
            }
        loadSalesTypeSpinner();

    }

    private void getCustomer() {
        if (!customerArrayList.isEmpty()) {
            customerArrayList.clear();
            customer_Address_editText.getText().clear();
            customer_PhoneNo_editText.getText().clear();
        }
        if (!hashBPID.isEmpty()) {
            hashBPID.clear();
            customer_Address_editText.getText().clear();
            customer_PhoneNo_editText.getText().clear();
        }
            Cursor cursor = null;
            try {
                cursor = mDatabaseHelper.getCustomerterrwise(selectedTerritoryId);           //.getCustomer()

                if (cursor.moveToFirst()) {
                    do {
                        String orgId = cursor.getString(1);
                        String orgName = cursor.getString(2);
                        String bpaddress = cursor.getString(4);
                        String bpphone = cursor.getString(5);
                        String territory_ID = cursor.getString(6);
                        String so_creditlimit = cursor.getString(7);
                        c_bp_group_id = cursor.getString(8);
                        //check
                        if (!bpaddress.equals("null") && bpaddress != null && !bpaddress.isEmpty()) {
                            customer_Address_editText.setText(bpaddress);
                        } else {
                            customer_Address_editText.getText().clear();
                        }
                        if (!bpphone.equals("null") && bpphone != null && !bpphone.isEmpty()) {
                            customer_PhoneNo_editText.setText(bpphone);
                        } else {
                            customer_PhoneNo_editText.getText().clear();
                        }
                        //2023-01-29
                        if (so_creditlimit.equals("0")){
                            cash_checkbox.setChecked(true);
                            cash_checkbox.setEnabled(false);
                            cash_checkbox.setClickable(false);
                        }
                        else{
                            cash_checkbox.setChecked(false);
                            cash_checkbox.setEnabled(true);
                            cash_checkbox.setClickable(true);
                        }
                        //2023-01-29
                        customerArrayList.add(orgName);
                        //hashBPID.put(orgName,bpaddress,bpphone,orgId);
                        hashBPID.put(orgName, orgId);
                        hashBPTerrID.put(orgName, territory_ID);

                    } while (cursor.moveToNext());
                }
            }
            finally {
                if (cursor != null)
                    cursor.close();  // RIGHT: ensure resource is always recovered
            }
        loadCustomerSpinner();
    }

    private void loadCustomerSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, customerArrayList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        CustomerSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectBPCustomerSpinner();
    }
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void ShowAlert(String title, String msg) {

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Customer_Selection_Activity.this);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setIcon(R.drawable.logo);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void selectBPGroupSpinner() {
        customerGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String bpgrpd = parent.getSelectedItem().toString();
                    selectedBPGroupID = hashBPGourpID.get(bpgrpd);

                } catch (NullPointerException ex) {

                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectSalesTypeSpinner() {
        salesTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedSalesType = parent.getSelectedItem().toString().trim();
                } catch (NullPointerException ex) {
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectBPCustomerSpinner() {
        CustomerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String bpcustomer = parent.getSelectedItem().toString();
                    //String bpcustomer1=bpcustomer.split(" - ")[0];
                    selectedBPCustomerName=bpcustomer;
                    selectedBPCustomerID = hashBPID.get(bpcustomer);
                    selectedBPTerrID = hashBPTerrID.get(bpcustomer);

                    Cursor cursor=null;
                    try{
                        cursor = mDatabaseHelper.getCustomerAddress(selectedBPCustomerID);

                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                String bpaddress = cursor.getString(0);
                                String bpphone = cursor.getString(1);
                                String so_creditlimit = cursor.getString(2);
                                //check
                                if (!bpaddress.equals("null") && bpaddress != null && !bpaddress.isEmpty()) {
                                    customer_Address_editText.setText(bpaddress);
                                } else {
                                    customer_Address_editText.getText().clear();
                                }
                                if (!bpphone.equals("null") && bpphone != null && !bpphone.isEmpty()) {
                                    customer_PhoneNo_editText.setText(bpphone);
                                } else {
                                    customer_PhoneNo_editText.getText().clear();
                                }
                                //2023-01-29
                                if (so_creditlimit.equals("0")){
                                    cash_checkbox.setChecked(true);
                                    cash_checkbox.setEnabled(false);
                                    cash_checkbox.setClickable(false);
                                }
                                else{
                                    cash_checkbox.setChecked(false);
                                    cash_checkbox.setEnabled(true);
                                    cash_checkbox.setClickable(true);
                                }
                                //2023-01-29
                            } while (cursor.moveToNext());
                        }
                    }
                    finally {
                        if (cursor != null)
                            cursor.close();  // RIGHT: ensure resource is always recovered
                    }

                    if (dealer !="") {
                        CustomerSpinner.setSelection(((ArrayAdapter) CustomerSpinner.getAdapter()).getPosition(dealer));
                    }


                } catch (NullPointerException ex) {

                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getPriceList() {
        hashPriceListID.clear();
        if(!PriceListArrayList.isEmpty())
        {
            PriceListArrayList.clear();
        }
        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getPriceList(selectedOrgId);

            if (cursor.moveToFirst()) {
                do {
                    String PriceListID = cursor.getString(0);
                    String PriceListname = cursor.getString(1);
                    PriceListArrayList.add(PriceListname);
                    hashPriceListID.put(PriceListname, PriceListID);

                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }
        loadPriceListSpinner();

    }


    private void loadPriceListSpinner()
    {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, PriceListArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        pricelist_spinner.setAdapter(dataAdapter);

        //anika 2023-10-09
        if(selectedOrgName.equals("Rangpur Seed Processing Center")){
            pricelistname = "Dealer Price List SPC Rangpur";
        }else if(selectedOrgName.equals("Sherpur Seed Processing Center")){
            pricelistname = "Dealer Price List  SPC Sherpur";
        }else if(selectedOrgName.equals("Sreepur Seed Processing Center")){
            pricelistname = "Dealer Price List SPC Sreepur";
        }
        int spinnerPosition = dataAdapter.getPosition(pricelistname);
        pricelist_spinner.setSelection(spinnerPosition);
        //anika 2023-10-09

        dataAdapter.notifyDataSetChanged();
        selectPriceListSpinner();
    }
    private void selectPriceListSpinner(){
        pricelist_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedPriceListName=selectedOrg;
                    selectedPriceListId = hashPriceListID.get(selectedOrg);

                    if(!pricelist.equals("")) {
                        pricelist_spinner.setSelection(((ArrayAdapter) pricelist_spinner.getAdapter()).getPosition(pricelist));//2023-01-26
                    }

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void loadOrganizationSpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, orgNameArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        orgSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectOrgSpinner();

    }
    private void loadWarehouseSpinner(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, branchNameArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        branchSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectWHSpinner();

    }
    private void getOrg() {
        hashOrgId.clear();
        if(!orgNameArrayList.isEmpty())
        {
            orgNameArrayList.clear();
        }
        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getOrganization();

            if (cursor.moveToFirst()) {
                do {
                    String orgId = cursor.getString(0);
                    String orgName = cursor.getString(1);
                    orgNameArrayList.add(orgName);
                    hashOrgId.put(orgName, orgId);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }
        loadOrganizationSpinner();

    }
    private void getWH()
    {
        if(!branchNameArrayList.isEmpty())
        {
            branchNameArrayList.clear();
        }
        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getWarehouseByOrg(selectedOrgId);


            if (cursor.moveToFirst()) {
                do {
                    String warehouseID = cursor.getString(1);
                    String warehouseName = cursor.getString(3);
                    branchNameArrayList.add(warehouseName);
                    hashBranchId.put(warehouseName, warehouseID);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }
        loadWarehouseSpinner();
    }
    private void getTerritory() {
        hashTerritoryId.clear();
        if(!TerritoryNameArrayList.isEmpty())
        {
            TerritoryNameArrayList.clear();
        }
        Cursor cursor = null;
        try {
            cursor = mDatabaseHelper.getTerritory();

            if (cursor.moveToFirst()) {
                do {
                    String orgId = cursor.getString(0);
                    String orgName = cursor.getString(1);
                    TerritoryNameArrayList.add(orgName);
                    hashTerritoryId.put(orgName, orgId);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null)
                cursor.close();  // RIGHT: ensure resource is always recovered
        }
        loadTerritorySpinner();

    }
    private void loadTerritorySpinner(){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, TerritoryNameArrayList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        Location_Spinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        selectTerritorySpinner();

    }
    private void selectTerritorySpinner(){
        Location_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedTerritoryName=selectedOrg;
                    selectedTerritoryId = hashTerritoryId.get(selectedOrg);
                    getCustomer();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectOrgSpinner(){
        orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedOrgName=selectedOrg;
                    selectedOrgId = hashOrgId.get(selectedOrg);

                    getWH();
                    getPriceList();

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void selectWHSpinner(){
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String selectedOrg = parent.getSelectedItem().toString();
                    selectedWHName=selectedOrg;
                    selectedWHId= hashBranchId.get(selectedWHName);

                    if(!wh.equals("")) {
                        branchSpinner.setSelection(((ArrayAdapter) branchSpinner.getAdapter()).getPosition(wh));//2023-01-26
                    }

                }catch (NullPointerException ex){

                }catch (ArrayIndexOutOfBoundsException ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void getlist() {
        Cursor cursor = mDatabaseHelper.getProductOrderheadNew(LoginActivity.ad_user_id);
        if (cursor.moveToFirst()) {
            do {
                odate = cursor.getString(2);
                //productType = cursor.getString(3);
                actType = cursor.getString(4);
                //DistrictName = cursor.getString(5);
                ThanaName = cursor.getString(6);
                org = cursor.getString(7);
                wh = cursor.getString(8);
                dealer = cursor.getString(9);
                cashcheck= cursor.getString(10);
                pricelist = cursor.getString(11);

                showdate.setText(odate);
                //prodTypeSpinner.setSelection(((ArrayAdapter) prodTypeSpinner.getAdapter()).getPosition(productType));
                salesTypeSpinner.setSelection(((ArrayAdapter) salesTypeSpinner.getAdapter()).getPosition(actType));
                Cursor cursor3 = mDatabaseHelper.getOrganizationSelected(org);
                if (cursor3.moveToFirst()) {
                    String orgId = cursor3.getString(0);
                    getWHSelected(orgId);
                }
                Location_Spinner.setSelection(((ArrayAdapter) Location_Spinner.getAdapter()).getPosition(ThanaName));
                branchSpinner.setSelection(((ArrayAdapter) branchSpinner.getAdapter()).getPosition(wh));
                orgSpinner.setSelection(((ArrayAdapter) orgSpinner.getAdapter()).getPosition(org));
                Cursor cursor4 = mDatabaseHelper.getCustomerdetails(dealer);
                if (cursor4.moveToFirst()) {
                    CustomerSpinner.setSelection(((ArrayAdapter) CustomerSpinner.getAdapter()).getPosition(dealer));
                }
                else{
                    CustomerSpinner.setSelection(((ArrayAdapter) CustomerSpinner.getAdapter()).getPosition(""));
                }
                // CustomerSpinner.setSelection(((ArrayAdapter) CustomerSpinner.getAdapter()).getPosition(dealer));
                pricelist_spinner.setSelection(((ArrayAdapter) pricelist_spinner.getAdapter()).getPosition(pricelist));
                if(cashcheck.equals("Y")){
                    cash_checkbox.setChecked(true);
                }else {
                    cash_checkbox.setChecked(false);
                }
            } while (cursor.moveToNext());
        }
    }
    public void gotoProductActivity(View view) throws ParseException {
        boolean result=false;
        int id1=1;

        showdateNew=showdate.getText().toString();
        String ids=String.valueOf(id1);
        if (pricelist_spinner.getCount()==0){
                toastMessage("No PriceList Item Selected !!");
                return;
            }
        if (selectedOrgId.equals("1")) { //prodTypeSpinner.getCount()==0){
            toastMessage("No District is Selected !!");
            return;
        }
        if (selectedWHId.equals("")) { //actTypeSpinner.getCount()==0){
            toastMessage("No Warehouse is Selected !!");
            return;
        }
        if (selectedBPCustomerID.equals("1")){ //prodTypeSpinner.getCount()==0){
            toastMessage("No Dealer is Selected !!");
            return;
        }
        if(cash_checkbox.isChecked()) {
            iscash="Y";
            cashpos = "cash_discount='Y'";  // Anika 2023-10-10
        }else{ iscash="N";
            cashpos = "credit_discount='Y'"; // Anika 2023-10-10
        }


        Cursor cursor = mDatabaseHelper.getProductOrderheadNew(LoginActivity.ad_user_id);

        if (cursor.moveToFirst()){
            result = mDatabaseHelper.updateProductOrderHeadNew("1",showdateNew,"", selectedSalesType,"", selectedTerritoryName,selectedOrgName,selectedWHName, selectedBPCustomerName,iscash,selectedPriceListName, LoginActivity.ad_user_id);
            //anika 2023-10-08
            Date fg = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(formatter1.parse(showdateNew));
            Timestamp dateTstmp = new Timestamp(c.getTimeInMillis());
            Date thedate= new Date(dateTstmp.getTime());
            orderdate = formatter.format(thedate);
            //anika 2023-10-08
            if (result) {
                toast("Updated");


                String customerName_Code = selectedBPCustomerName;
                Intent i = new Intent(Customer_Selection_Activity.this, Sales_Order_Activity1.class);
                //i.putExtra("DOCNO", documentNo);
                i.putExtra("CUSTOMERNAMECODE", customerName_Code);
                i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
                i.putExtra("selectedBPTerrID", selectedBPTerrID);    //anika 2026-04-22
                i.putExtra("date_ordered", orderdate);  //anika 2023-10-08
                i.putExtra("c_bp_group_id", c_bp_group_id);
                i.putExtra("iscash", iscash);
                i.putExtra("selectedSalesType", selectedSalesType);   //anika 2023-08-23
                i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
                i.putExtra("ad_org_id", selectedOrgId);  // Anika 2023-10-10
                startActivity(i);

            } else {
                toast("Could not Updated into internal database");
            }
        }
        else {
            result = mDatabaseHelper.insertIntoProductOrderHeadNew(ids, "", selectedSalesType, "", selectedTerritoryName, selectedOrgName, selectedWHName, selectedBPCustomerName, iscash, selectedPriceListName, showdateNew, LoginActivity.ad_user_id);
            //result=mDatabaseHelper.insertIntoProductOrderHead(ids,selectedProdActivityName,selectedactivityName,selectedDistrictName,selectedLocationName,selectedOrgName,selectedWHName,selectedBPCustomerName,truckno);
            //anika 2023-10-08
            Date fg = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(formatter1.parse(showdateNew));
            Timestamp dateTstmp = new Timestamp(c.getTimeInMillis());
            Date thedate= new Date(dateTstmp.getTime());
            orderdate = formatter.format(thedate);
            //anika 2023-10-08
            if (result) {
                toast("ProductOrder Inserted");
                String customerName_Code = selectedBPCustomerName;
                Intent i = new Intent(Customer_Selection_Activity.this, Sales_Order_Activity1.class);
                i.putExtra("CUSTOMERNAMECODE", customerName_Code);
                i.putExtra("selectedBPCustomerID", selectedBPCustomerID);
                i.putExtra("selectedBPTerrID", selectedBPTerrID);   //anika 2026-04-22
                i.putExtra("date_ordered", orderdate);  //anika 2023-10-08
                i.putExtra("c_bp_group_id", c_bp_group_id);
                i.putExtra("iscash", iscash);
                i.putExtra("selectedSalesType", selectedSalesType);  //anika 2023-08-23
                i.putExtra("cashpos", cashpos);  // Anika 2023-10-10
                i.putExtra("ad_org_id", selectedOrgId);  // Anika 2023-10-10
                startActivity(i);

            } else {
                toast("Could not insert into internal database");
            }
        }

    }
    public void toast(String msg)
    {
        Toast.makeText(Customer_Selection_Activity.this,msg,Toast.LENGTH_SHORT).show();
    }

    public void savealert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you want to save this order?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean result=false;
                        int id1=1;
                        //String iscash="";
                        showdateNew=showdate.getText().toString();
                        String ids=String.valueOf(id1);
                        if (selectedSalesType.equals("")){ //actTypeSpinner.getCount()==0){
                            toastMessage("No Sales Type Selected !!");
                            return;
                        }
                        if (pricelist_spinner.getCount()==0){
                            toastMessage("No PriceList Item Selected !!");
                            return;
                        }

                        if (selectedOrgId.equals("1")) { //prodTypeSpinner.getCount()==0){
                            toastMessage("No Organization is Selected !!");
                            return;
                        }
                        if (selectedWHId.equals("")) { //actTypeSpinner.getCount()==0){
                            toastMessage("No Warehouse is Selected !!");
                            return;
                        }
                        if(cash_checkbox.isChecked()) {
                            iscash="Y";
                        }else{ iscash="N"; }

                        Cursor cursor = mDatabaseHelper.getProductOrderheadNew(LoginActivity.ad_user_id);

                        if (cursor.moveToFirst()){
                            result = mDatabaseHelper.updateProductOrderHeadNew("1",showdateNew,"", selectedSalesType,"", selectedTerritoryName,selectedOrgName,selectedWHName, selectedBPCustomerName,iscash,selectedPriceListName, LoginActivity.ad_user_id);
                            if (result) {
                                Intent i=new Intent(Customer_Selection_Activity.this,Home.class);
                                startActivity(i);

                            } else {
                                toast("Could not Updated into internal database");
                            }

                        }
                        else {
                            result = mDatabaseHelper.insertIntoProductOrderHeadNew(ids, "", selectedSalesType, "", selectedTerritoryName, selectedOrgName, selectedWHName, selectedBPCustomerName, iscash, selectedPriceListName, showdateNew, LoginActivity.ad_user_id);
                            //result=mDatabaseHelper.insertIntoProductOrderHead(ids,selectedProdActivityName,selectedactivityName,selectedDistrictName,selectedLocationName,selectedOrgName,selectedWHName,selectedBPCustomerName,truckno);
                            if (result) {
                                toast("ProductOrder Inserted");
                                String customerName_Code = selectedBPCustomerName;
                                Intent i = new Intent(Customer_Selection_Activity.this, Home.class);
                                //i.putExtra("CUSTOMERNAMECODE", customerName_Code);
                                startActivity(i);

                            } else {
                                toast("Could not insert into internal database");
                            }
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Customer_Selection_Activity.this, Home.class);
                        startActivity(i);
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        //alert11.show();
        alert11.getWindow().setType(WindowManager.LayoutParams.LAST_APPLICATION_WINDOW);
        alert11.show();
        alert11.getWindow().setGravity(Gravity.BOTTOM);
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Home.class);

        startActivity(intent);
    }
    private void searchCustomer(){
        search_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(codeCheckbox.isChecked()) {
                    String searchKey = search_editText.getText().toString();

                    List<String> customerList = (List<String>) mDatabaseHelper.searchCustomerByCode(searchKey);

                    if (!customerList.isEmpty()) {
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Customer_Selection_Activity.this, android.R.layout.simple_spinner_item, customerList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        CustomerSpinner.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
                        // setCustomerDetailsByCode(searchKey);
                    } else {
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Customer_Selection_Activity.this, android.R.layout.simple_spinner_item, customerArrayList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        CustomerSpinner.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        // setCustomerDetails();
                    }
                }else{
                    String searchKey = search_editText.getText().toString();

                    List<String> customerList = (List<String>) mDatabaseHelper.searchCustomerByName(searchKey,selectedTerritoryId);

                    if(!customerList.isEmpty()){
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Customer_Selection_Activity.this, android.R.layout.simple_spinner_item, customerList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        CustomerSpinner.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        //  setCustomerDetailsByName(searchKey);

                    }else{
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Customer_Selection_Activity.this, android.R.layout.simple_spinner_item, customerArrayList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        CustomerSpinner.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        //  setCustomerDetails();
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getWHSelected(String OrgId)
    {   Cursor cursor ;
        hashBranchId.clear();
        if(!branchNameArrayList.isEmpty())
        {
            branchNameArrayList.clear();
        }
        cursor = mDatabaseHelper.getWarehouseByOrg(OrgId);
        if(cursor.moveToFirst()){
            do{
                String   warehouseID   = cursor.getString(1);
                String warehouseName          = cursor.getString(3);
                branchNameArrayList.add(warehouseName);
                hashBranchId.put(warehouseName,warehouseID);
            }while(cursor.moveToNext());
        }
        loadWarehouseSpinner();
    }

    public void callCancel(View view) {
        Cursor cursor = mDatabaseHelper.getProductOrderheadNew(LoginActivity.ad_user_id);
        if (cursor.moveToFirst()){
            Intent i=new Intent(Customer_Selection_Activity.this,Home.class);
            startActivity(i);
        }
        else{
            if (selectedOrgId.equals("1")){
                Intent i=new Intent(Customer_Selection_Activity.this,Home.class);
                startActivity(i);
            }
            else {
                savealert();
            }
        }
    }
    public void callCancelLogOut(View view) {
        cancelalert();
    }

    public void callCancelDeleteOrder(View view) {
        cancelOrderalert();
    }

    public void cancelalert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Customer_Selection_Activity.this);
        builder1.setMessage("Are you sure you want to LogOut?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i=new Intent(Customer_Selection_Activity.this,LoginActivity.class);
                        startActivity(i);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public void cancelOrderalert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Customer_Selection_Activity.this);
        builder1.setMessage("Are you sure you want to Delete this Order?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mDatabaseHelper.deleteProductOrderHeadNew();
                        mDatabaseHelper.deleteAllProductOrderLine();
                        Intent i=new Intent(Customer_Selection_Activity.this,Customer_Selection_Activity.class);
                        startActivity(i);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
//                        Intent intent = new Intent(CreatePaymentActivity.this, SelectionAcitvity.class);
//                        startActivity(intent);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}