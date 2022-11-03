package com.example.manoj.hyveg_observation.services;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


import com.example.manoj.hyveg_observation.list.FormModel;
import com.example.manoj.hyveg_observation.list.ListModel;

import java.util.ArrayList;

public class SQLHelper {
    public static final String SQL_CREATE_FROM_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FromEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FromEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FromEntry.COLUMN_NAME_SERVER_ID + " TEXT," +
                    FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED + " TEXT," +
                    FeedReaderContract.FromEntry.COLUMN_NAME_TYPE + " TEXT)";

    public static final String SQL_DELETE_FROM_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FromEntry.TABLE_NAME;

    public static final String SQL_CREATE_FROM_DATA_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FromDataEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FromDataEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_FORM_ID + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_FKEY + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_FNAME + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_REQ + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_VALKEY + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_VALLABEL + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_VALVALUE + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_FTYPE + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_MSEL + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_DECIMAL+ " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_INSTRUCTION + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_INTYPE + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_MAXVAL + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_MINVAL + " TEXT," +
                    FeedReaderContract.FromDataEntry.COLUMN_NAME_IS_SYNCED + " TEXT)";

    public static final String SQL_DELETE_FROM_DATA_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FromDataEntry.TABLE_NAME;

    public static final String SQL_CREATE_CHOICE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.ChoiceEntry.TABLE_NAME + " (" +
                    FeedReaderContract.ChoiceEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_SERVER_ID + " TEXT," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_ID + " TEXT," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_POSITION + " INTEGER," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID + " TEXT," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_DESC + " TEXT," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_VAL + " TEXT," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_SELECTED_CHOICE + " TEXT," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_HV_CODES + " TEXT," +
                    FeedReaderContract.ChoiceEntry.COLUMN_NAME_IS_SYNCED + " TEXT)";

    public static final String SQL_DELETE_CHOICE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.ChoiceEntry.TABLE_NAME;

    public static final String SQL_CREATE_DECIMAL_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.DecimalEntry.TABLE_NAME + " (" +
                    FeedReaderContract.DecimalEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.DecimalEntry.COLUMN_NAME_SERVER_ID + " TEXT," +
                    FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_ID + " TEXT," +
                    FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID + " TEXT," +
                    FeedReaderContract.DecimalEntry.COLUMN_NAME_DECIMALS + " TEXT," +
                    FeedReaderContract.DecimalEntry.COLUMN_NAME_HV_CODES + " TEXT," +
                    FeedReaderContract.DecimalEntry.COLUMN_NAME_IS_SYNCED + " TEXT)";

    public static final String SQL_DELETE_DECIMAL_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.DecimalEntry.TABLE_NAME;

    public static long addFormToDB(ArrayList<ListModel> listModel, SQLiteDatabase paramSQLiteDatabase, String isOnline,String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReaderContract.FromEntry.COLUMN_NAME_SERVER_ID, -1);
        contentValues.put(FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED, isOnline);
        contentValues.put(FeedReaderContract.FromEntry.COLUMN_NAME_TYPE,type);
        long id = paramSQLiteDatabase.insert(FeedReaderContract.FromEntry.TABLE_NAME, null, contentValues);
        addAllFormDataToDB(listModel, paramSQLiteDatabase, (int) id, isOnline);
        return id;
    }

    public static void addAllFormDataToDB(ArrayList<ListModel> listModels, SQLiteDatabase db, int formId, String isOnline) {
        ContentValues contentValues = new ContentValues();
        for (int b = 0; b < listModels.size(); b++) {
            ListModel listModel = (ListModel) listModels.get(b);
//            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID, listModel.getId());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_FORM_ID, formId);
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_FKEY, listModel.getFkey());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_FNAME, listModel.getFname());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_REQ, listModel.getReq());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALKEY, listModel.getValKey());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALLABEL, listModel.getValLabel());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALVALUE, listModel.getValValue());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_FTYPE, listModel.getF_type());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_MSEL, listModel.getMsel());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_DECIMAL, listModel.getDecimal());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_INSTRUCTION, listModel.getInstruction());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_INTYPE, listModel.getIntType());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_MAXVAL, listModel.getMaxVal());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_MINVAL, listModel.getMinVal());
            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_IS_SYNCED, isOnline);
            long id = db.insert(FeedReaderContract.FromDataEntry.TABLE_NAME, null, contentValues);
            if (listModel.getChoice_desc() != null && listModel.getChoice_desc().length > 0) {
                if (listModel.getIntType() == 4){
                    addAllChoicesToDB(listModel.getChoice_desc(), listModel.getHv_codes(), listModel.getSelectedChoiceList(), db,
                            formId, (int) id, isOnline, true);
                } else {
                    addAllChoicesToDB(listModel.getSelected_choice_desc(), listModel.getHv_codes(), listModel.getSelectedChoiceList(), db,
                            formId, (int) id, isOnline, false);
                }

            }
            if (listModel.getDecimals() != null && listModel.getDecimals().length > 0) {
                addAllDecimalsToDB(listModel.getDecimals(), listModel.getHv_codes(), db, formId, (int) id, isOnline);
            }
        }
    }

    public static void addAllChoicesToDB(String[] choice_des, String[] hv_code, int[] selected, SQLiteDatabase db, int formId, int dataId,
                                         String isOnline, boolean isImg) {
        ContentValues contentValues = new ContentValues();
//        for (int b = 0; b < hv_code.length; b++) {
        int hv_pos = 0;
        if (isImg){
            for (int i = 0; i < choice_des.length; i++) {
//            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID, listModel.getId());
//                if (i == selected[b]) {
                if (i != 0) {
                    if (i % 2 == 0) {
                        hv_pos++;
                    }
                }
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_ID, formId);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID, dataId);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_POSITION, i);
//                    contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_VAL, choice_val[i]);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_DESC, choice_des[i]);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_SELECTED_CHOICE, "Y");
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_HV_CODES, hv_code[hv_pos]);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_IS_SYNCED, isOnline);
                db.insert(FeedReaderContract.ChoiceEntry.TABLE_NAME, null, contentValues);
//                }
//            }
            }
        } else {
            for (int i = 0; i < choice_des.length; i++) {
//            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID, listModel.getId());
//                if (i == selected[b]) {
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_ID, formId);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID, dataId);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_POSITION, i);
//                    contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_VAL, choice_val[i]);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_DESC, choice_des[i]);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_SELECTED_CHOICE, "Y");
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_HV_CODES, hv_code[i]);
                contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_IS_SYNCED, isOnline);
                db.insert(FeedReaderContract.ChoiceEntry.TABLE_NAME, null, contentValues);
//                }
//            }
            }
        }

    }

    public static String isChoiceSelected(int[] selected, int pos){
        for (int i = 0; i < selected.length; i++){
            if (selected[i] == pos + 1){
                return "Y";
            }
        }
        return "N";
    }

    public static void addAllDecimalsToDB(String[] decimals, String[] hv_code, SQLiteDatabase db, int formId, int dataId, String isOnline) {
        ContentValues contentValues = new ContentValues();
        for (int b = 0; b < decimals.length; b++) {
//            contentValues.put(FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID, listModel.getId());
            contentValues.put(FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_ID, formId);
//            contentValues.put(FeedReaderContract.DecimalEntry.COLUMN_NAME_SERVER_ID, choice_val[b]);
            contentValues.put(FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID, dataId);
            contentValues.put(FeedReaderContract.DecimalEntry.COLUMN_NAME_DECIMALS, decimals[b]);
            contentValues.put(FeedReaderContract.ChoiceEntry.COLUMN_NAME_HV_CODES, hv_code[b]);
            contentValues.put(FeedReaderContract.DecimalEntry.COLUMN_NAME_IS_SYNCED, isOnline);
            db.insert(FeedReaderContract.DecimalEntry.TABLE_NAME, null, contentValues);
        }
    }

    public static ArrayList<FormModel> getAllUnSyncedFormFromDB(SQLiteDatabase db, String hv_code,String type,Activity mActivity) {
        ArrayList<FormModel> mList = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FromEntry.COLUMN_NAME_SERVER_ID,
                FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED,
                FeedReaderContract.FromEntry.COLUMN_NAME_TYPE,
        };

//        String selection = FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED + " = ?";
        String selection = FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED + " = ? AND " +
                FeedReaderContract.FromEntry.COLUMN_NAME_TYPE + " = ?";
        String[] selectionArgs = { "N" , type};
        Cursor cursor = db.query(
                FeedReaderContract.FromEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null        // The sort order
        );

        while (cursor.moveToNext()) {
            FormModel formModel;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FromEntry._ID));
            String server_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromEntry.COLUMN_NAME_SERVER_ID));
            String is_synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED));
            String ob_type = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromEntry.COLUMN_NAME_TYPE));

            ArrayList<ListModel> listModels = getAllFormDataByHvCodeFromDB(db, id, hv_code,mActivity);
            formModel  = new FormModel(server_id, id, listModels,ob_type);
            formModel.setIs_synced(is_synced);
            mList.add(formModel);
//                String tag = cursor.getString(cursor.getColumnIndexOrThrow("tag"));
//                String tag_exit = cursor.getString(cursor.getColumnIndexOrThrow("tag_exit"));
        }
        if(cursor != null)
            cursor.close();
        return mList;
    }

    public static ArrayList<ListModel> getAllFormDataFromDB(SQLiteDatabase db, int form_id, Activity mActivity) {
        ArrayList<ListModel> mList = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_FKEY,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_FNAME,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_REQ,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_VALKEY,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_VALLABEL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_VALVALUE,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_FTYPE,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_MSEL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_DECIMAL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_INSTRUCTION,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_INTYPE,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_MINVAL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_MAXVAL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_IS_SYNCED,
        };

        String selection = FeedReaderContract.FromDataEntry.COLUMN_NAME_FORM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(form_id)};
        Cursor cursor = db.query(
                FeedReaderContract.FromDataEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null        // The sort order
        );

        while (cursor.moveToNext()) {
            ListModel listModel;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry._ID));
            String server_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID));
            String f_key = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_FKEY));
            String f_name = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_FNAME));
            String req = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_REQ));
            String valkey = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALKEY));
            String vallabel = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALLABEL));
            String valvalue = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALVALUE));
            String ftype = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_FTYPE));
            String msel = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_MSEL));
            String decimal = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_DECIMAL));
            String instruction = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_INSTRUCTION));
            String inttype = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_INTYPE));
            String minval = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_MINVAL));
            String maxval = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_MAXVAL));
            String is_synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_IS_SYNCED));

            listModel = new ListModel(id, server_id, f_key, f_name, req, valkey, vallabel, valvalue, ftype, msel, decimal, instruction, Integer.parseInt(inttype)
                    , Integer.parseInt(minval), Integer.parseInt(maxval), is_synced,mActivity);
            getAllChoiceFromDB(listModel, db, form_id, id);
            getAllDecimalsFromDB(listModel, db, form_id, id);
            mList.add(listModel);
        }
        if(cursor != null)
            cursor.close();
        return mList;
    }

    public static ListModel getAllChoiceFromDB(ListModel listModel, SQLiteDatabase db, int formId, int dataId) {
        String[] choice_val;
        String[] choice_desc;
        int[] selected_choice;
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_SERVER_ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_VAL,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_DESC,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_SELECTED_CHOICE,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_IS_SYNCED,
        };

        String selection = FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID + " = ?";
        String[] selectionArgs = { String.valueOf(dataId) };
        Cursor cursor = db.query(
                FeedReaderContract.ChoiceEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null        // The sort order
        );
        int pos = 0;
        ArrayList<String> val = new ArrayList<>();
        ArrayList<String> desc = new ArrayList<>();
        ArrayList<Integer> sel = new ArrayList<>();
        int b = 0;
        while (cursor.moveToNext()) {
            FormModel formModel;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry._ID));
            String server_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_SERVER_ID));
            String form_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_ID));
            String form_data_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID));
            String choice_va = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_VAL));
            String choice_dec = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_DESC));
            String selected_choic = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_SELECTED_CHOICE));
            String is_synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_IS_SYNCED));
//                String tag = cursor.getString(cursor.getColumnIndexOrThrow("tag"));
//                String tag_exit = cursor.getString(cursor.getColumnIndexOrThrow("tag_exit"));
            val.add(choice_va);
            desc.add(choice_dec);
            listModel.setSelected_choice_desc(b, choice_dec);
            if (selected_choic.equalsIgnoreCase("Y")) {
                sel.add(id);
            }
            b++;
        }
        if(cursor != null)
            cursor.close();

        choice_desc = new String[desc.size()];
        choice_val = new String[val.size()];
        selected_choice = new int[desc.size()];

        for(int i = 0; i < desc.size(); i++){
            choice_desc[i] = desc.get(i);
            choice_val[i] = val.get(i);
        }

        for(int i = 0; i < sel.size(); i++){
            selected_choice[i] = sel.get(i);
        }

        listModel.setChoice_val(choice_val);
        listModel.setChoice_desc(choice_desc);
        listModel.setSelectedChoiceList(selected_choice);

        return listModel;
    }

    public static ListModel getAllDecimalsFromDB(ListModel listModel, SQLiteDatabase db, int formId, int dataId) {
        String[] decimals;
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_SERVER_ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_DECIMALS,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_IS_SYNCED,
        };

        String selection = FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID + " = ?";
        String[] selectionArgs = { String.valueOf(dataId) };
        Cursor cursor = db.query(
                FeedReaderContract.ChoiceEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null        // The sort order
        );
        int pos = 0;
        ArrayList<String> dec = new ArrayList<>();
        while (cursor.moveToNext()) {
            FormModel formModel;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry._ID));
            String server_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_SERVER_ID));
            String form_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_ID));
            String form_data_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID));
            String decimal = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_DECIMALS));
            String is_synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_IS_SYNCED));
//                String tag = cursor.getString(cursor.getColumnIndexOrThrow("tag"));
//                String tag_exit = cursor.getString(cursor.getColumnIndexOrThrow("tag_exit"));
//                listModel.setEntered_decimal(decimal);
            dec.add(decimal);
        }
        if(cursor != null)
            cursor.close();

        decimals = new String[dec.size()];
        for(int i = 0; i < dec.size(); i++){
            decimals[i] = dec.get(i);
        }

        listModel.setDecimals(decimals);

        return listModel;
    }

    /************************** Update Form Data************************/
    public static void updateForm(ArrayList<FormModel> formList, SQLiteDatabase db, String is_synced) {
        for(int i = 0; i < formList.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED, is_synced);

            // Which row to update, based on the title
            String selection = FeedReaderContract.FromEntry._ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(formList.get(i).getDb_id())};

            int count = db.update(FeedReaderContract.FromEntry.TABLE_NAME,
                    contentValues,
                    selection,
                    selectionArgs);
//                return count;
        }
    }

    public static void updateFormData(ArrayList<ListModel> listModels, SQLiteDatabase db, String is_synced) {
        for(int i = 0; i < listModels.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED, is_synced);

            // Which row to update, based on the title
            String selection = FeedReaderContract.FromEntry._ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(listModels.get(i).getDb_id())};

            int count = db.update(FeedReaderContract.FromEntry.TABLE_NAME,
                    contentValues,
                    selection,
                    selectionArgs);
//                return count;
        }
    }

  /*  public static void updateChoiceData(String[] choice, SQLiteDatabase db, String is_synced) {
        for(int i = 0; i < choice.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FeedReaderContract.FromEntry.COLUMN_NAME_IS_SYNCED, is_synced);

            // Which row to update, based on the title
            String selection = FeedReaderContract.FromEntry._ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(listModels.get(i).getDb_id())};

            int count = db.update(FeedReaderContract.FromEntry.TABLE_NAME,
                    contentValues,
                    selection,
                    selectionArgs);
//                return count;
        }
    }*/


    /********************* Get data with hv codes ********************/
    public static ArrayList<ListModel> getAllFormDataByHvCodeFromDB(SQLiteDatabase db, int form_id, String hv_code,Activity mActivity) {
        ArrayList<ListModel> mList = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_FKEY,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_FNAME,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_REQ,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_VALKEY,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_VALLABEL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_VALVALUE,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_FTYPE,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_MSEL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_DECIMAL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_INSTRUCTION,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_INTYPE,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_MINVAL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_MAXVAL,
                FeedReaderContract.FromDataEntry.COLUMN_NAME_IS_SYNCED,
        };

        String selection = FeedReaderContract.FromDataEntry.COLUMN_NAME_FORM_ID + " = ? AND " +
                FeedReaderContract.FromDataEntry.COLUMN_NAME_IS_SYNCED + " = ?";
        String[] selectionArgs = {String.valueOf(form_id), "N"};
        Cursor cursor = db.query(
                FeedReaderContract.FromDataEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null        // The sort order
        );

        while (cursor.moveToNext()) {
            ListModel listModel = null;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry._ID));
            String server_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_SERVER_ID));
            String f_key = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_FKEY));
            String f_name = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_FNAME));
            String req = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_REQ));
            String valkey = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALKEY));
            String vallabel = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALLABEL));
            String valvalue = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_VALVALUE));
            String ftype = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_FTYPE));
            String msel = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_MSEL));
            String decimal = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_DECIMAL));
            String instruction = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_INSTRUCTION));
            String inttype = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_INTYPE));
            String minval = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_MINVAL));
            String maxval = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_MAXVAL));
            String is_synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FromDataEntry.COLUMN_NAME_IS_SYNCED));
            try {
                listModel = new ListModel(id, server_id, f_key, f_name, req, valkey, vallabel, valvalue, ftype, msel, decimal, instruction, Integer.parseInt(inttype)
                        , Double.parseDouble(minval), Double.parseDouble(maxval), is_synced, mActivity);
            }catch (NumberFormatException e){
                listModel = new ListModel(id, server_id, f_key, f_name, req, valkey, vallabel, valvalue, ftype, msel, decimal, instruction, (int) Double.parseDouble(inttype)
                        , Double.parseDouble(minval), Double.parseDouble(maxval), is_synced, mActivity);
                e.getMessage();
            }
            getAllChoiceByHvCodeFromDB(listModel, db, form_id, id ,hv_code);
            getAllDecimalsByHvCodeFromDB(listModel, db, form_id, id, hv_code);
            mList.add(listModel);
        }
        if(cursor != null)
            cursor.close();
        return mList;
    }

    public static ListModel getAllChoiceByHvCodeFromDB(ListModel listModel, SQLiteDatabase db, int formId, int dataId, String hv_code) {
        String[] choice_val;
        String[] choice_desc;
        int[] selected_choice;
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_SERVER_ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_POSITION,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_VAL,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_DESC,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_SELECTED_CHOICE,
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_IS_SYNCED,
        };

        String selection = FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID +
                " = ? AND " +
                FeedReaderContract.ChoiceEntry.COLUMN_NAME_HV_CODES + " = ?";
        String[] selectionArgs = { String.valueOf(dataId) , hv_code};
        Cursor cursor = db.query(
                FeedReaderContract.ChoiceEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null        // The sort order
        );
        int pos = 0;
        ArrayList<String> ch = new ArrayList<>();
        while (cursor.moveToNext()) {
            FormModel formModel;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry._ID));
            String server_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_SERVER_ID));
            int position = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_POSITION));
            String form_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_ID));
            String form_data_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_FORM_DATA_ID));
            String choice_va = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_VAL));
            String choice_dec = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_CHOICE_DESC));
            String selected_choic = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_SELECTED_CHOICE));
            String is_synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.ChoiceEntry.COLUMN_NAME_IS_SYNCED));
//            if (selected_choic.equalsIgnoreCase("Y")) {
            if (listModel.getIntType() == 4){
                ch.add(choice_dec);
//                listModel.setOneChoice(position, choice_dec);
            } else {
                ch.add(choice_dec);
//                listModel.setSelected_choice_desc(position, choice_dec);
            }

//                break;
//            }
        }
        String[] ch_des = new String[ch.size()];
        for(int i = 0 ; i < ch.size(); i++){
            ch_des[i] = ch.get(i);
        }
        if (listModel.getIntType() == 4) {
            listModel.setChoice_desc(0, ch_des);
        } else {
            listModel.setSelected_choice_(ch_des);
        }
        if(cursor != null)
            cursor.close();

        return listModel;
    }

    public static ListModel getAllDecimalsByHvCodeFromDB(ListModel listModel, SQLiteDatabase db, int formId, int dataId, String hv_code) {
        String[] decimals;
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_SERVER_ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_DECIMALS,
                FeedReaderContract.DecimalEntry.COLUMN_NAME_IS_SYNCED,
        };

        String selection = FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID + " = ? AND " +
                FeedReaderContract.DecimalEntry.COLUMN_NAME_HV_CODES + " = ?";
        String[] selectionArgs = { String.valueOf(dataId), hv_code };
        Cursor cursor = db.query(
                FeedReaderContract.DecimalEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null        // The sort order
        );
        int pos = 0;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry._ID));
            String server_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_SERVER_ID));
            String form_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_ID));
            String form_data_id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_FORM_DATA_ID));
            String decimal = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_DECIMALS));
            String is_synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.DecimalEntry.COLUMN_NAME_IS_SYNCED));
            listModel.setSelectedDecimal(decimal, 0);
        }
        if(cursor != null)
            cursor.close();
        return listModel;
    }
}
