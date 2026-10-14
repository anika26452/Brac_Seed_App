package com.sysnova.brac_seed_app.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "product";
    private static final String COL1 = "productID";
    private static final String COL2 = "productName";
    private static final String COL3 = "catagoryID";
    private static final String COL4 = "price";
    private static final String COL5 = "unit";
    private static final String COL6 = "availableQty";
    private static final String DATABASE_NAME = "bracseed";
    private static DatabaseHelper sInstance;

    public static DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductTable = "CREATE TABLE " + TABLE_NAME + " (productID TEXT Unique, " +
                COL2 +" TEXT, " +
                COL3 +" INTEGER, " +
                COL4 +" TEXT,"+COL5+" TEXT,"+COL6+" TEXT,onhandQty Text,costcenter Text, c_elementvalue_id TEXT)";

        String createProductCatagoryTable="CREATE TABLE product_catagory(catagoryID INTEGER ," +
                "catagoryName TEXT, c_salesregion_id TEXT)";

        String createProductOrderHeadNewTable="CREATE TABLE ProductOrderHeadNew(prdId INTEGER PRIMARY KEY AUTOINCREMENT,ids TEXT ," +
                "orderdate TEXT,productType Text,activityType Text,District Text,Thana Text,org Text,wh Text,dealer Text,truck TEXT,pricelist TEXT,userID TEXT)";
        String createProductOrderLineTable = "CREATE TABLE productorderline (productId INTEGER PRIMARY KEY, productCode TEXT , " +
                "productName TEXT,productQTY REAL,pricePerUnit REAL, totalCost REAL,docno Text,mproduct_Id text,userID TEXT,Category_Id text,uom Text,editQTY Text, discount REAL, bonus TEXT, discountschemaID TEXT, discountschemabreakID TEXT)";

        String createSalesOrderTable="CREATE TABLE sales_order(docNo TEXT not null unique," +
                "date TEXT,chemist_code TEXT,status TEXT,userid TEXT,delivary_date Text,description Text,promised_amount Text default '0',sales_type Text,billing_location Text,description1 Text)";

        String createSalesOrderLineTable="CREATE TABLE sales_orderline(orderline INTEGER PRIMARY KEY AUTOINCREMENT,docNo TEXT ," +
                "productID TEXT,quantity Text,price Integer,product_name Text,unit_price Text,uom Text,proID Text,searchkey Text)";

        String createChemistProposalTable="CREATE TABLE chemist_proposal(chemistID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "shopName TEXT,shopAddress TEXT,tradeLicenseNo TEXT,drugLicenseNo TEXT,contactPerson TEXT," +
                "mobile TEXT,email TEXT,nearbyHospital TEXT,estimatedDailySales TEXT,ownerName TEXT,ownerAddress TEXT)";

        String createDrTable="CREATE TABLE dr_information(dr_code TEXT ," +
                "drName TEXT,dr_address TEXT)";

        String createEmployeeTable="CREATE TABLE employee(emp_id INTEGER PRIMARY KEY AUTOINCREMENT,ad_user_id TEXT,user_level TEXT,name Text,currentdate Text,feed_org_id Text)";

        String createChemistTable="CREATE TABLE chemist(chemist_code TEXT,chemist_name TEXT,chemist_address TEXT,customer_balance TEXT,phone Text)";

        String createPriceListTable="CREATE TABLE pricelist(ID INTEGER PRIMARY KEY AUTOINCREMENT,pricelist_id TEXT,pricelist_name TEXT,ad_org_id Text)";

        String createTerritoryTable="CREATE TABLE territory(ID INTEGER PRIMARY KEY AUTOINCREMENT,code TEXT,territory_ID TEXT,terrname TEXT)";

        String createCostCenterTable="CREATE TABLE CostCenter(ID INTEGER PRIMARY KEY AUTOINCREMENT, c_elementvalue_id TEXT,name TEXT)";
        String createBankTable="CREATE TABLE AllBank(ID INTEGER PRIMARY KEY AUTOINCREMENT, c_bankaccount_id TEXT, accountno TEXT,"+
                               "bank TEXT, ad_org_id TEXT, bracAC TEXT)";

        String createOrganizationTable = "Create Table organization (id INTEGER PRIMARY KEY AUTOINCREMENT,ad_org_id Text,org_name Text)";
        String createWarehouseTable="Create Table warehouse (id INTEGER PRIMARY KEY AUTOINCREMENT,warehouse_id Text,ad_org_id Text,warehouse_name Text)";

        String createSalesTypeTable = "Create Table salesType(salesType_id INTEGER PRIMARY KEY AUTOINCREMENT,salesType_name Text)";

        String createOrderApprovalTable = "Create Table orderApproval(orderApproval_id INTEGER PRIMARY KEY AUTOINCREMENT,order_doc Text,erp_orderNo Text,grand_total Text,prepared_by text,sales_type Text,sent_date Text,doctype_id Text,orgid Text)";

        String createSalesOrderRecord="CREATE TABLE sales_order_record(docNo TEXT," +
                "date TEXT,customer_name TEXT,grand_total Text,product_name Text,uom Text,ordered_qty Text default '0',pricePerProduct Text default '0',status Text,orgid text,description text)";
        String createSupervisorTable = "Create Table supervisor(userid Text,docid Text,supid Text,supname Text)";
        String createCustomerTable="Create Table customer(id INTEGER PRIMARY KEY AUTOINCREMENT,customer_id Text,customer_name  Text,code Text,customer_location Text,customer_phone Text,territory_ID Text,so_creditlimit Text, c_bp_group_id TEXT," +
                " costcenter_id TEXT, creditlimit TEXT, daysdue TEXT, t_bpcreditlimit_id TEXT, validfrom TEXT, validto TEXT, ad_org_id TEXT)";
        String createCreditLimitTable="Create Table credit_limit(id INTEGER PRIMARY KEY AUTOINCREMENT,customer_id Text,customer_name  Text,code Text,customer_location Text,customer_phone Text,territory_ID Text,so_creditlimit Text, c_bp_group_id TEXT," +
                " costcenter_id TEXT, creditlimit TEXT, daysdue TEXT, t_bpcreditlimit_id TEXT, validfrom TEXT, validto TEXT, ad_org_id TEXT)";
        String createBPGroupTable = "Create Table bpgroup (id INTEGER PRIMARY KEY AUTOINCREMENT,c_bp_group_id Text,name Text)";
        String createApprovalViewLineTable = "Create Table ApprovalViewLine (id INTEGER PRIMARY KEY AUTOINCREMENT,productCode Text,productName Text,uom Text,qty Text,rate Text,total Text,userID Text,discount Text)";

        String createForwardToTable = "Create Table ForwardTo (id INTEGER PRIMARY KEY AUTOINCREMENT,idd Text,name Text,userID Text)";
        String createDBSyncTable="CREATE TABLE DB_Sync(id INTEGER PRIMARY KEY AUTOINCREMENT,ad_user_id TEXT,sync TEXT)";
        String createDiscountSchemaTable="CREATE TABLE discount_schema(discount_schema_id INTEGER PRIMARY KEY AUTOINCREMENT, discount_category TEXT," +
                "m_discountschema_id TEXT, discountschema_name TEXT,validfrom TEXT,validto TEXT, discounttype TEXT, " +
                "isquantitybased TEXT, ismixed TEXT, maxvalue TEXT, minvalue TEXT,ispercentbased TEXT, m_discountschemabreak_id TEXT, m_product_id TEXT," +
                "breakvalue TEXT,breakdiscount TEXT,freeqty TEXT, isproductbased TEXT, m_product_category_id TEXT, IsBPartnerFlatDiscount TEXT)";
        String createPaymentLineTable="Create Table T_App_PaymentLine(id INTEGER PRIMARY KEY AUTOINCREMENT,c_elementvalue_id Text, cost_center TEXT," +
                "selectedBPID Text,bpname Text, paidamt TEXT)";

        String createCollectionTypeTable="CREATE TABLE CollectionType(ID INTEGER PRIMARY KEY AUTOINCREMENT, t_collectiontype_id TEXT,name TEXT)";

        db.execSQL(createProductTable);
        db.execSQL(createProductCatagoryTable);
        db.execSQL(createSalesOrderTable);
        db.execSQL(createSalesOrderLineTable);
        db.execSQL(createChemistProposalTable);
        db.execSQL(createDrTable);
        db.execSQL(createEmployeeTable);
        db.execSQL(createChemistTable);
        db.execSQL(createPriceListTable);
        db.execSQL(createTerritoryTable);
        db.execSQL(createCostCenterTable);
        db.execSQL(createOrganizationTable);
        db.execSQL(createSalesTypeTable);
        db.execSQL(createOrderApprovalTable);
        db.execSQL(createSalesOrderRecord);
        db.execSQL(createSupervisorTable);
        db.execSQL(createWarehouseTable);
        db.execSQL(createCustomerTable);
        db.execSQL(createCreditLimitTable);
        db.execSQL(createBPGroupTable);
        db.execSQL(createProductOrderLineTable);
        db.execSQL(createProductOrderHeadNewTable);
        db.execSQL(createApprovalViewLineTable);
        db.execSQL(createForwardToTable);
        db.execSQL(createDBSyncTable);
        db.execSQL(createDiscountSchemaTable);
        db.execSQL(createBankTable);
        db.execSQL(createPaymentLineTable);
        db.execSQL(createCollectionTypeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP  TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP  TABLE IF EXISTS  product_catagory");
        db.execSQL("DROP  TABLE IF EXISTS sales_order");
        db.execSQL("DROP  TABLE IF EXISTS sales_orderline");
        db.execSQL("DROP  TABLE IF EXISTS chemist_proposal");
        db.execSQL("DROP  TABLE IF EXISTS dr_information");
        db.execSQL("DROP  TABLE IF EXISTS employee");
        db.execSQL("DROP  TABLE IF EXISTS chemist");
        db.execSQL("DROP  TABLE IF EXISTS pricelist");
        db.execSQL("DROP  TABLE IF EXISTS territory");
        db.execSQL("DROP  TABLE IF EXISTS CostCenter");
        db.execSQL("DROP  TABLE IF EXISTS organization");
        db.execSQL("DROP  TABLE IF EXISTS salesType");
        db.execSQL("DROP  TABLE IF EXISTS orderApproval");
        db.execSQL("DROP  TABLE IF EXISTS sales_order_record");
        db.execSQL("DROP  TABLE IF EXISTS supervisor");
        db.execSQL("DROP  TABLE IF EXISTS warehouse");
        db.execSQL("DROP  TABLE IF EXISTS customer");
        db.execSQL("DROP  TABLE IF EXISTS credit_limit");
        db.execSQL("DROP  TABLE IF EXISTS bpgroup");
        db.execSQL("DROP  TABLE IF EXISTS ProductOrderLine");
        db.execSQL("DROP  TABLE IF EXISTS ProductOrderHeadNew");
        db.execSQL("DROP  TABLE IF EXISTS ApprovalViewLine");
        db.execSQL("DROP  TABLE IF EXISTS ForwardTo");
        db.execSQL("DROP  TABLE IF EXISTS DB_Sync");
        db.execSQL("DROP  TABLE IF EXISTS discount_schema");
        db.execSQL("DROP  TABLE IF EXISTS AllBank");
        db.execSQL("DROP  TABLE IF EXISTS T_App_PaymentLine");
        db.execSQL("DROP  TABLE IF EXISTS CollectionType");

        onCreate(db);
    }
    public boolean insertIntoDB_Sync(String Sync,String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Sync", Sync);
        contentValues.put("ad_user_id", userid);

        long result = db.insert("DB_Sync", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoPaymentLine(String c_elementvalue_id ,String cost_center ,String selectedBPID ,String bpname ,String paidamt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("c_elementvalue_id", c_elementvalue_id);
        contentValues.put("cost_center",cost_center);
        contentValues.put("selectedBPID", selectedBPID);
        contentValues.put("bpname", bpname);
        contentValues.put("paidamt", paidamt);

        long result = db.insert("T_App_PaymentLine",null,contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoProductOrderHeadNew(String id,String productType,String activityType,String DistrictName,
                                                 String ThanaName,String org,String wh,String dealer,String truckno,String pricelist,String orderdate,String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ids", id);
        contentValues.put("orderdate",orderdate);
        contentValues.put("productType", productType);
        contentValues.put("activityType", activityType);
        contentValues.put("District", DistrictName);
        contentValues.put("Thana", ThanaName);
        contentValues.put("org", org);
        contentValues.put("wh", wh);
        contentValues.put("dealer",dealer);
        contentValues.put("truck",truckno);
        contentValues.put("pricelist",pricelist);
        contentValues.put("userID",userID);

        long result = db.insert("ProductOrderHeadNew",null,contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoProductOrderLine(int id,String code,String name,double qty,double price,double total,String docno,String mproduct_Id,String userID,String Category_Id,String uom,String editQTY, double discount, String gift
            ,String discountschemaID, String discountschemabreakID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("productId", id);
        contentValues.put("productCode", code);
        contentValues.put("productName", name);
        contentValues.put("productQTY", qty);
        contentValues.put("pricePerUnit", price);
        contentValues.put("totalCost", total);
        contentValues.put("docno",docno);
        contentValues.put("mproduct_Id",mproduct_Id);
        contentValues.put("userID",userID);
        contentValues.put("Category_Id",Category_Id);
        contentValues.put("uom",uom);
        contentValues.put("editQTY", editQTY);
        contentValues.put("discount", discount);
        contentValues.put("bonus", gift);
        contentValues.put("discountschemaID", discountschemaID);
        contentValues.put("discountschemabreakID",discountschemabreakID);

        long result = db.insert("ProductOrderLine",null,contentValues);

        //if data as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoCreditLimit(String customer_id,String customer_name,String code,String customer_location,String customer_phone,String territory_ID,String so_creditlimit, String c_bp_group_id,
                                       String costcenter_id ,String creditlimit,String daysdue, String t_bpcreditlimit_id ,String validfrom,String validto, String ad_org_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_id", customer_id);
        contentValues.put("customer_name", customer_name);
        contentValues.put("code", code);
        contentValues.put("customer_location", customer_location);
        contentValues.put("customer_phone", customer_phone);
        contentValues.put("territory_ID",territory_ID);
        contentValues.put("so_creditlimit",so_creditlimit);
        contentValues.put("c_bp_group_id", c_bp_group_id);
        contentValues.put("costcenter_id", costcenter_id);
        contentValues.put("creditlimit", creditlimit);
        contentValues.put("daysdue", daysdue);
        contentValues.put("t_bpcreditlimit_id", t_bpcreditlimit_id);
        contentValues.put("validfrom", validfrom);
        contentValues.put("validto", validto);
        contentValues.put("ad_org_id", ad_org_id);

        long result = db.insert("credit_limit",null,contentValues);
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoOCustomer(String customer_id,String customer_name,String code,String customer_location,String customer_phone,String territory_ID,String so_creditlimit, String c_bp_group_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("customer_id", customer_id);
        contentValues.put("customer_name", customer_name);
        contentValues.put("code", code);
        contentValues.put("customer_location", customer_location);
        contentValues.put("customer_phone", customer_phone);
        contentValues.put("territory_ID",territory_ID);
        contentValues.put("so_creditlimit",so_creditlimit);
        contentValues.put("c_bp_group_id", c_bp_group_id);

        long result = db.insert("customer",null,contentValues);
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoOrderRecord(String docNo,String date,String customer_name,String grand_total,String product_name,String uom,String ordered_qty,String pricePerProduct,String status,String orgid,String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("docNo", docNo);
        contentValues.put("date", date);
        contentValues.put("customer_name", customer_name);
        contentValues.put("grand_total", grand_total);
        contentValues.put("product_name", product_name);
        contentValues.put("uom", uom);
        contentValues.put("ordered_qty", ordered_qty);
        contentValues.put("pricePerProduct", pricePerProduct);
        contentValues.put("status", status);
        contentValues.put("orgid", orgid);
        contentValues.put("description",description);

        long result = db.insert("sales_order_record",null,contentValues);
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoForwardTo(String idd,String name,String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("idd", idd);
        contentValues.put("name", name);
        contentValues.put("userID",userID);

        long result = db.insert("ForwardTo",null,contentValues);


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoOrderApproval(String order_doc,String erp_orderNo,String grand_total,String prepared_by,String sales_type,String sent_date,String doctype_id,String orgid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("order_doc", order_doc);
        contentValues.put("erp_orderNo", erp_orderNo);
        contentValues.put("grand_total", grand_total);
        contentValues.put("prepared_by", prepared_by);
        contentValues.put("sales_type", sales_type);
        contentValues.put("sent_date", sent_date);
        contentValues.put("doctype_id",doctype_id);
        contentValues.put("orgid",orgid);

        long result = db.insert("orderApproval",null,contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoDiscountSchema(String discount_category ,String m_discountschema_id, String discountschema_name ,String validfrom ,String validto,
                                            String discounttype, String isquantitybased ,String ismixed, String maxvalue,
                                            String minvalue,String ispercentbased, String m_discountschemabreak_id ,String m_product_id,
                                            String breakvalue ,String breakdiscount ,String freeqty, String isproductbased, String m_product_category_id,
                                            String IsBPartnerFlatDiscount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("discount_category", discount_category);
        contentValues.put("m_discountschema_id", m_discountschema_id);
        contentValues.put("discountschema_name", discountschema_name);
        contentValues.put("validfrom", validfrom);
        contentValues.put("validto", validto);
        contentValues.put("discounttype", discounttype);
        contentValues.put("isquantitybased", isquantitybased);
        contentValues.put("ismixed", ismixed);
        contentValues.put("maxvalue", maxvalue);
        contentValues.put("minvalue", minvalue);
        contentValues.put("ispercentbased", ispercentbased);
        contentValues.put("m_discountschemabreak_id", m_discountschemabreak_id);
        contentValues.put("m_product_id", m_product_id);
        contentValues.put("breakvalue", breakvalue);
        contentValues.put("breakdiscount", breakdiscount);
        contentValues.put("freeqty", freeqty);
        contentValues.put("isproductbased", isproductbased);
        contentValues.put("m_product_category_id", m_product_category_id);
        contentValues.put("IsBPartnerFlatDiscount", IsBPartnerFlatDiscount);

        long result = db.insert("discount_schema", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoSalesType(String typename) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("salesType_name", typename);

        long result = db.insert("salesType", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoOrganization(String ad_org_id,String org_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ad_org_id", ad_org_id);
        contentValues.put("org_name", org_name);

        long result = db.insert("organization", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoWarehouse(String warehouse_id,String ad_org_id,String warehouse_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("warehouse_id", warehouse_id);
        contentValues.put("ad_org_id", ad_org_id);
        contentValues.put("warehouse_name", warehouse_name);

        long result = db.insert("warehouse", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoEmployee(String ad_user_id,String user_level,String name,String currentdate,String feed_org_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ad_user_id", ad_user_id);
        contentValues.put("user_level", user_level);
        contentValues.put("name", name);
        contentValues.put("currentdate", currentdate);
        contentValues.put("feed_org_id", feed_org_id);

        long result = db.insert("employee", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean insertIntoProduct(String productID,String productName,int catagoryID,String price,String unit,String avlQty,String onhandQty,String costcenter
                                    ,String c_elementvalue_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("productID", productID);
        contentValues.put("productName", productName);
        contentValues.put("catagoryID", catagoryID);
        contentValues.put("price", price);
        contentValues.put("unit", unit);
        contentValues.put("availableQty", avlQty);
        contentValues.put("onhandQty", onhandQty);
        contentValues.put("costcenter",costcenter);
        contentValues.put("c_elementvalue_id", c_elementvalue_id);
        long result = db.insert("product", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoProductCatagory(int catagoryID,String catagoryName, String c_salesregion_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("catagoryID", catagoryID);
        contentValues.put("catagoryName", catagoryName);
        contentValues.put("c_salesregion_id", c_salesregion_id);

        long result = db.insert("product_catagory", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoALLBank(int c_bankaccount_id, String accountno,String bank,String ad_org_id,String bracAC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("c_bankaccount_id", c_bankaccount_id);
        contentValues.put("accountno", accountno);
        contentValues.put("bank", bank);
        contentValues.put("ad_org_id", ad_org_id);
        contentValues.put("bracAC", bracAC);

        long result = db.insert("AllBank", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<String> getProductCategory(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT catagoryID,catagoryName FROM product_catagory order by catagoryName";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1)+" "+cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public boolean insertIntoTerritory(String code,String territory_Id,String terrname) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("code", code);
        contentValues.put("territory_Id", territory_Id);
        contentValues.put("terrname", terrname);

        long result = db.insert("Territory", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean insertIntoPriceList(String pricelist_id,String pricelist_name,String org_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pricelist_id", pricelist_id);
        contentValues.put("pricelist_name", pricelist_name);
        contentValues.put("ad_org_id", org_id);



        long result = db.insert("pricelist", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean insertIntoChemist(String chemist_code,String chemist_name,String address,String balance,String phoneNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("chemist_code", chemist_code);
        contentValues.put("chemist_name", chemist_name);
        contentValues.put("chemist_address", address);
        contentValues.put("customer_balance", balance);
        contentValues.put("phone", phoneNo);

        long result = db.insert("chemist", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoChemistProposal(String chemistName,String shopAddress,String tradeLicenseNo,String drugLicenseNo,
                                             String contactPerson,String mobile,String email,String nearbyHospital,String estimatedDailySales,String ownerName,String ownerAddress)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("shopName", chemistName);
        contentValues.put("shopAddress", shopAddress);
        contentValues.put("tradeLicenseNo", tradeLicenseNo);
        contentValues.put("drugLicenseNo", drugLicenseNo);
        contentValues.put("contactPerson", contactPerson);
        contentValues.put("mobile", mobile);
        contentValues.put("email", email);
        contentValues.put("nearbyHospital", nearbyHospital);
        contentValues.put("estimatedDailySales", estimatedDailySales);
        contentValues.put("ownerName", ownerName);
        contentValues.put("ownerAddress", ownerAddress);



        long result = db.insert("chemist_proposal", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     * @return
     */


    public List<String> searchProduct(String name){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT productName FROM product inner join product_catagory ON product.catagoryID=product_catagory.catagoryID where upper(product_catagory.catagoryName) = 'FINISHED GOODS' and  product.productName LIKE '%"+name+"%' Order by productName";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> searchProductByCode(String code){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT productName FROM product  inner join product_catagory ON product.catagoryID=product_catagory.catagoryID where upper(product_catagory.catagoryName) = 'FINISHED GOODS' and product.productID LIKE '%"+code+"%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }



    public List<String> searchCustomerByName(String name,String Territory_Id){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT customer_name,code,customer_location,customer_phone FROM customer where customer_name LIKE '"+name+"%' and Territory_Id ='"+Territory_Id+"' Order by customer_name";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }
    public Cursor getCustomerAddress(String name) {
        // Select All Query
        String selectQuery = "SELECT customer_location,customer_phone,so_creditlimit FROM customer where customer_id LIKE '" + name + "' ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        return data;

        // closing connection
    }

    //2021-11-18
    public Cursor getBPGroup() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM bpgroup  order by name";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getProductCategory(String c_salesregion_id) {//

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT catagoryID,catagoryName FROM product_catagory where c_salesregion_id = '"+c_salesregion_id+"' order by catagoryName";

        Cursor data = db.rawQuery(selectQuery, null);
        return data;
    }
    public Cursor getProductCategoryCursor(String catagoryID) {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT catagoryID,catagoryName FROM product_catagory where catagoryID = '"+catagoryID+"' order by catagoryName";

        Cursor data = db.rawQuery(selectQuery, null);
        return data;
    }
    public Cursor getProductCursor(String catID) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM product order by productName";
       Log.d("DEBUG","query"+query);
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getCustomerCreditLimit(String customer_id, String costcenter_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM credit_limit where customer_id = '"+customer_id+"' and costcenter_id = '"+costcenter_id+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getCustomerterrwise(String TerritoryId) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM customer  where Territory_Id = '"+TerritoryId+"' Order by customer_name";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getCostCenter() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM CostCenter Order by name";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getWarehouseByOrg(String ad_org_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM warehouse  where ad_org_id ='"+ad_org_id+"' Order by warehouse_id";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getPriceList(String ad_org_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT PriceList_id,PriceList_name FROM PriceList  where ad_org_id ='"+ad_org_id+"' Order by PriceList_name";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getTerritory(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT territory_id,terrname FROM territory   Order by terrname";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public String getCustomerAddressByCode(String code) {

        SQLiteDatabase db = this.getWritableDatabase();
        String address="";
        String selectQuery = "SELECT chemist_address FROM chemist where chemist_code='"+code+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                address=(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return address;
    }

    public String getCustomerBalanceByCode(String code) {

        SQLiteDatabase db = this.getWritableDatabase();
        String balance="";
        String selectQuery = "SELECT customer_balance FROM chemist where chemist_code='"+code+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                balance=(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return balance;
    }
    public Cursor searchCustomerByCodeNew(String code) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT customer_name FROM " +
                "  where code = '"+code+"' Order by customer_name";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public List<String> searchCustomerByCode(String code){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        //customer_location,customer_phone newly added by shanzida 2021-11-16
        String selectQuery = "SELECT customer_name,code,customer_location,customer_phone FROM customer where code  LIKE '%"+code+"%' Order by customer_name";



        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getAllProduct(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT productName FROM product inner join product_catagory ON product.catagoryID=product_catagory.catagoryID Order by productName";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getAllProductCode(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT productID FROM product Order by productName";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public Cursor getSalesOrderLineData(String docNo){
        SQLiteDatabase db = this.getWritableDatabase();

       // String query = "SELECT product_name,uom,ordered_qty,pricePerProduct,customer_name,orgid FROM sales_order_record where docNo='"+docNo+"'";
        String query = "SELECT * FROM sales_order_record where docNo='"+docNo+"'";

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getSalesOrderLineDataTemp(String docNo){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM sales_orderline where uom='"+docNo+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getSalesOrderListForApproval(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM orderApproval";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getSupervisorData(String userid,String docid){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT supid,supnmae FROM supervisor where userid='"+userid+"' and docid='"+docid+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getSalesType(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT salesType_name from salesType where salesType_name != 'Customer Return Material'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public String getUserIDFromEmployee(){


        String ad_user_id = null;

        // Select All Query
        String selectQuery = "select ad_user_id from employee order by emp_id desc limit 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ad_user_id=(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return ad_user_id;
    }

    public List<String> getUserDetailsFromEmployee(){


        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "select ad_user_id,user_level,name from employee order by emp_id desc limit 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
                labels.add(cursor.getString(2));
                labels.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public String getDrCodeByName(String name){


        String code = null;

        // Select All Query
        String selectQuery = "SELECT dr_code FROM dr_information where drName='"+name+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                code=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return code;
    }

    public String getProductName(String productID){


        String name = null;

        // Select All Query
        String selectQuery = "SELECT productName FROM product where productID='"+productID+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                name=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return name;
    }

    public String getChemistName(String chemId){


        String name = null;

        // Select All Query
        String selectQuery = "SELECT chemist_name FROM chemist where chemist_code='"+chemId+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                name=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return name;
    }

    public String getDrNameByCode(String drID){


        String name = null;

        // Select All Query
        String selectQuery = "SELECT drName FROM dr_information where dr_code='"+drID+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                name=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return name;
    }





    public List<String> getAllDrNames(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT drName FROM dr_information";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }



    public String getChemistID(String docNo){


        String id =null;

        // Select All Query
        String selectQuery = "SELECT chemist_code FROM sales_order where docNo='"+docNo+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                id=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return id;
    }

    public String getTotalPrice(String docNo){


        String total = null;
        //total_price convert to new BigDecimal 2021-11-17 by shanzida
        BigDecimal total_price=new BigDecimal(0);
        // Select All Query
        String selectQuery = "select product_name  from sales_orderline where uom='"+docNo+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String qty = cursor.getString(0);

                total_price = total_price.add(new BigDecimal(qty)); //total_price convert to new BigDecimal 2021-11-17 by shanzida
            } while (cursor.moveToNext());
            total= String.valueOf(total_price);
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return total;
    }

    public String getOrderedProduct(String docNo){


        String name1 = "";

        // Select All Query
        String selectQuery = "select product_name from sales_orderline where docno='"+docNo+"' order by product_name";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String name=(cursor.getString(0));
                name1 += name + "\n";
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return name1;
    }

    public String getOrderedProductQty(String docNo){


        String qty = "";

        // Select All Query
        String selectQuery = "select quantity from sales_orderline where docno='"+docNo+"' order by product_name";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String qty1=(cursor.getString(0));
                qty += qty1 + "\n";
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return qty;
    }

    public Cursor getAllDiscountSchemaforproduct(String m_product_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM discount_schema where m_product_id='"+m_product_id+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getAllDiscountSchemaforproductcat(String m_product_category_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM discount_schema where m_product_category_id='"+m_product_category_id+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public int getCatagoryID(String catagoryName){

        int id = 0;

        String selectQuery = "SELECT catagoryID FROM product_catagory where catagoryName='"+catagoryName+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                id=(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return id;
    }

    public String getProductIDByProductName(String productName){
        String id = null;

        String selectQuery = "SELECT productID FROM product where productName='"+productName+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                id=(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return id;
    }

    public String getDrCodeByNameAddress(String name,String Address){


        String id = null;

        // Select All Query
        String selectQuery = "SELECT dr_code FROM dr_information where drName='"+name+"' and dr_address='"+Address+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                id=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return id;
    }
    public String getChemistCodeByName(String name,String Address){


        String id = null;

        // Select All Query
        String selectQuery = "SELECT chemist_code FROM chemist where chemist_name='"+name+"' and chemist_address='"+Address+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                id=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return id;
    }

    public String getChemistIDByName(String name){


        String id = null;

        // Select All Query
        String selectQuery = "SELECT chemist_code FROM chemist where chemist_name='"+name+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                id=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return id;
    }


    public String getProductQty(String productName){


        String avlQty=null;

        // Select All Query
        String selectQuery = "SELECT availableQty FROM product where productName='"+productName+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                avlQty=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return avlQty;
    }
    public String getonHandProductQty(String productName){


        String avlQty=null;

        // Select All Query
        String selectQuery = "SELECT onhandQty FROM product where productName='"+productName+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                avlQty=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return avlQty;
    }



    public String getProductPrice(String productName){


        String Price=null;

        // Select All Query
        String selectQuery = "SELECT price FROM product where productName='"+productName+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Price=(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return Price;
    }


    public String getProductUnit(String productName){
        String unit=null;
        String selectQuery = "SELECT unit FROM product where productName='"+productName+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                unit=(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return unit;
    }

    public List<String> getAllProductByCatagory(int catagoryID){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT productName FROM product where catagoryID="+catagoryID+" Order by productName";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }






    public List<String> getAllProductCatagory(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT catagoryName FROM product_catagory order by catagoryName";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }


    public boolean getSalesOrderLineByDocAndProduct(String docNo,String productID){

        boolean res=false;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM sales_orderline where docNo='"+docNo+"' and productID='"+productID+"'";
        Cursor data = db.rawQuery(query, null);
        if (data.moveToNext()){

            res=true;
        }
        else{
            res=false;
        }
        return res;

    }

    public Cursor getSalesOrderByDate(String date1,String date2){
        SQLiteDatabase db = this.getWritableDatabase();

        // String query = "SELECT * FROM sales_order where date='"+date+"' and status!='draft' and userid='"+userID+"'";

        // String query = "SELECT DISTINCT docNo,date,customer_name FROM sales_order_record where date between '"+date1+"' and '"+date+"'";
        String query = "SELECT DISTINCT docNo,date,customer_name,status,orgid FROM sales_order_record where date between '"+date1+"' and '"+date2+"' ";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getSalesOrderByDateAndStatus(String date1,String date2,String status){
        SQLiteDatabase db = this.getWritableDatabase();

        // String query = "SELECT * FROM sales_order where date='"+date+"' and status!='draft' and userid='"+userID+"'";

        // String query = "SELECT DISTINCT docNo,date,customer_name FROM sales_order_record where date between '"+date1+"' and '"+date+"'";
        String query = "SELECT DISTINCT docNo,date,customer_name,status,orgid FROM sales_order_record where date between '"+date1+"' and '"+date2+"' and status='"+status+"'";
        Cursor data = db.rawQuery(query, null);
        return data;

    }


    public List<String> getAllChemists(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT chemist_name FROM chemist ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */



    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL3 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    //2021-11-20 shanzida

    public boolean updateOrderDes(String docno,String Des){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put("description",Des);

        int i = db.update("sales_order", args, "docNo = ?", new String[]{docno});
        if( i > 0){
            return true;
        }
        return false;

    }





     //end
    public void updateName(String newProduct,String newItem,String newQuantity, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + " = '" + newProduct + "',"+COL3+ "= '"+newItem+"', "+COL4+" = '"+newQuantity+ "' WHERE " + COL1 + " = " + id+"" ;

        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newItem);
        db.execSQL(query);


    }

    public boolean updateOrderStatus(String docno){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put("status", "complete");

        int i = db.update("sales_order", args, "docNo = ?", new String[]{docno});
        if( i > 0){
            return true;
        }
        return false;

    }

    public void deleteBPGroup(){
        SQLiteDatabase db = this.getWritableDatabase();

        String q="delete from bpgroup";

        db.execSQL(q);
    }

    public void deleteSupervisorData(){
        SQLiteDatabase db = this.getWritableDatabase();

        String q="delete from supervisor";

        Log.d(TAG, "deleteName: query: " + q);
       // Log.d(TAG, "deleteName: Deleting " + item + " from database.");
        db.execSQL(q);
    }

    public void deleteCustomer(){
        SQLiteDatabase db = this.getWritableDatabase();

        String q="delete from customer";

        db.execSQL(q);
    }



    public void deleteName(int id, String item){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = " + id + "";


        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + item + " from database.");
        db.execSQL(query);
    }

    public void deleteSalesOrderLine(String id, String docNo){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  sales_orderline WHERE uom='"+docNo+"' AND proID='"+id+"'";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteProducts(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  product";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteSalesType(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  salesType";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
    public void deleteProductCatagory(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  product_catagory";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
    public Cursor getSalesOrder(String docNo) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT chemist_code,date,delivary_date,description,promised_amount,sales_type,billing_location FROM sales_order where docNo='"+docNo+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getDrDetails() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM dr_information order by drName ";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getChemistDetails() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM chemist order by chemist_name";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getOrganization() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT DISTINCT ad_org_id,org_name FROM organization";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getAllBank(String ad_org_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM AllBank where ad_org_id = '"+ ad_org_id+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getBranch() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT branch_name,branch_id FROM organization where branch_name != 'TRANSIT WH' and branch_name != 'RAW MATERIALS WH'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getSkuWiseStock(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select productName,availableQty,unit from product \n" +
                "inner join product_catagory ON product.catagoryID=product_catagory.catagoryID where upper(product_catagory.catagoryName) = 'FINISHED GOODS'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getTotalOrderPrice(String docNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(price) from sales_orderline where docNo = '" + docNo + "' ", null);
        return res;
    }

    public void deletechemist() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  chemist";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deletedr() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  dr_information";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteOrganization() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  organization";
        db.execSQL(query);
    }
    public void deleteWarehouse() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  warehouse";
        db.execSQL(query);
    }
    public void deletePriceList(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  pricelist";
        db.execSQL(query);
    }

    public void deleteAllBank(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  AllBank";
        db.execSQL(query);
    }

    public void deleteCostcenter(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  CostCenter";
        db.execSQL(query);
    }

    public void deleteEmployee() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  employee";
        db.execSQL(query);
    }

    public void deleteOrderApprovalTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  orderApproval";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteOrderApprovalLine(String erp_doc) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  orderApproval where erp_orderNo = '"+erp_doc+"'  ";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }


    public void deleteSalesOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  sales_order ";
        db.execSQL(query);
    }

    public void deleteSalesOrderLine() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  sales_orderline";
        db.execSQL(query);
    }

    public void deleteSalesOrderRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  sales_order_record";
        db.execSQL(query);
    }
    public void deleteTerritory() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM territory";
        db.execSQL(query);
    }
    public void deleteProductOrderLine()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  productorderline";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
    public Cursor getProductOrderLine(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT * FROM productorderline where userID='"+userID+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getProductOrderLineTemp(String docNo){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM productorderline where docno='"+docNo+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getProductOrderLineCategory(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT Distinct Category_Id FROM productorderline where userID='"+userID+"' limit 1";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public void deleteAllProductOrderLine(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  productorderline";
        // Log.d(TAG, "deleteAllProductOrderLine: query: " + query);
        System.out.println("deleteAllProductOrderLine: query: " +query);
        db.execSQL(query);
    }

    public void deleteOneProductOrderLine(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  productorderline WHERE productId='"+id+"'";
        //  Log.d(TAG, "deleteOneProductOrderLine: query: " + query);
        System.out.println("deleteOneProductOrderLine: query: " +query);
        db.execSQL(query);
    }


    public boolean updateProductOrderLine(int id,double newQTY,double newTotal){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put("productQTY", newQTY);
        args.put("totalCost", newTotal);

        int i = db.update("ProductOrderLine", args, "productId = "+id, null);
        if( i > 0){
            return true;
        }
        return false;

    }
    public void deleteProductOrderHeadNew()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  ProductOrderHeadNew";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
    public Cursor getProductOrderheadNew(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM ProductOrderHeadNew where userID='"+userID+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public void deleteAllProductOrderHeadNew(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  ProductOrderHeadNew";
        // Log.d(TAG, "deleteAllProductOrderLine: query: " + query);
        System.out.println("deleteAllProductOrderHead: query: " +query);
        db.execSQL(query);
    }

    public void deleteOneProductOrderHead(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  ProductOrderHeadNew WHERE prodId='"+id+"'";
        //  Log.d(TAG, "deleteOneProductOrderLine: query: " + query);
        System.out.println("deleteOneProductOrderhead: query: " +query);
        db.execSQL(query);
    }


    public boolean updateProductOrderHeadNew(String id,String orderdate,String productType,String activityType,String DistrictName,String ThanaName,String org,String wh,String dealer,String truckno,String pricelist,String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("ids", id);
        args.put("orderdate",orderdate);
        args.put("productType", productType);
        args.put("activityType", activityType);
        args.put("District", DistrictName);
        args.put("Thana", ThanaName);
        args.put("org", org);
        args.put("wh", wh);
        args.put("dealer",dealer);
        args.put("truck",truckno);
        args.put("pricelist",pricelist);
        args.put("userID",userID);

        int i = db.update("ProductOrderHeadNew", args, "ids = "+id, null);
        if( i > 0){
            return true;
        }
        return false;

    }
    public Cursor getOrganizationSelected(String OName) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT DISTINCT ad_org_id,org_name FROM organization where org_name like '"+OName+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getCustomerdetails(String dealerCode) {
        String selectQuery = "SELECT * FROM customer where code||'-'||customer_name LIKE '%" + dealerCode + "%' limit 1 ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        return data;
        // closing connection
    }

    public Cursor getCustomerdetailsNew() {
        String selectQuery = "SELECT customer_id,code,(case when customer_id='1' then customer_name else code||'-'||customer_name end) CustomerName,customer_location balance,customer_phone creditlimit FROM customer  order by CustomerName";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        return data;
    }

    public Cursor getProductOrderLineProduct(String userID,String selectedProdId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT Distinct mproduct_Id FROM productorderline where mproduct_Id='"+selectedProdId+"' and userID='"+userID+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public void deleteApprovalViewLine()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  ApprovalViewLine";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
    public void deleteForwardTo()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  ForwardTo";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public Cursor getForwardTo(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM ForwardTo ";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getApprovalViewLine(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM ApprovalViewLine where userID='"+userID+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public void deleteDB_Sync()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  DB_Sync";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
    public Cursor getDB_Sync(String userid){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM DB_Sync where ad_user_id like '"+userid+"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteDiscountSchema(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM  discount_schema";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void exec(String s) {

    }

    public boolean insertIntoBPGroup(String c_bp_group_id,String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("c_bp_group_id", c_bp_group_id);
        contentValues.put("name", name);

        long result = db.insert("bpgroup",null,contentValues);


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoApprovalViewLine(String productCode,String productName,String uom,String qty,String rate,String total,String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("productCode", productCode);
        contentValues.put("productName", productName);
        contentValues.put("uom", uom);
        contentValues.put("qty", qty);
        contentValues.put("rate", rate);
        contentValues.put("total", total);
        contentValues.put("userID",userID);
        //contentValues.put("discount",discount);

        long result = db.insert("ApprovalViewLine",null,contentValues);


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntodrInfo(String dr_code,String name,String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("dr_code", dr_code);
        contentValues.put("drName", name);
        contentValues.put("dr_address", address);



        long result = db.insert("dr_information",null,contentValues);


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoCostCenter(String c_elementvalue_id,String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("c_elementvalue_id", c_elementvalue_id);
        contentValues.put("name", name);

        long result = db.insert("CostCenter",null,contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoSupervisor(String userid,String docid,String supid,String supname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("docid", docid);
        contentValues.put("supid", supid);
        contentValues.put("supname", supname);


        long result = db.insert("supervisor",null,contentValues);


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean insertIntoSalesOrderLine(String Product_name,String pro_cat_id,
                                            String unitprice,String qty,String total,String uom,String docno,String proID,String skey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("docNo", Product_name);
        contentValues.put("productID", pro_cat_id);
        contentValues.put("quantity", unitprice);
        contentValues.put("price", qty);
        contentValues.put("product_name",total);
        contentValues.put("unit_price",uom);
        contentValues.put("uom",docno);
        contentValues.put("proID",proID);
        contentValues.put("searchkey",skey);
        long result = db.insert("sales_orderline", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertIntoSalesOrder(String docNo,String date,String chemID,String userID,String delivaryDate,String des,String amount,String salesType,String billingLocation,String description1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("docNo", docNo);
        contentValues.put("date", date);
        contentValues.put("chemist_code", chemID);
        contentValues.put("userid", userID);
        contentValues.put("delivary_date", delivaryDate);
        contentValues.put("description", des);
        contentValues.put("promised_amount", amount);
        contentValues.put("sales_type", salesType);
        contentValues.put("billing_location", billingLocation);
        contentValues.put("description1", description1);

        long result = db.insert("sales_order", null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getcountPaymentline() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT count(c_elementvalue_id) FROM T_App_PaymentLine";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getPaymentline() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT cost_center, bpname , paidamt,id, c_elementvalue_id, selectedBPID FROM T_App_PaymentLine ";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteOneMortality(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM T_App_PaymentLine where id="+id+"";
        db.execSQL(query);
    }

    public Cursor getcountpaidamt() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT paidamt FROM T_App_PaymentLine";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteAllPaymentLine(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM T_App_PaymentLine";
        db.execSQL(query);
    }

    public boolean insertIntoCollectionType(String t_collectiontype_id,String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("t_collectiontype_id", t_collectiontype_id);
        contentValues.put("name", name);

        long result = db.insert("CollectionType",null,contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getCollectionType() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT DISTINCT t_collectiontype_id,name FROM CollectionType";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

}

