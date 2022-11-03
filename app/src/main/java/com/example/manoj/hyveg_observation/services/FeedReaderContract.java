package com.example.manoj.hyveg_observation.services;

import android.provider.BaseColumns;

public class FeedReaderContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FromEntry implements BaseColumns {
        public static final String TABLE_NAME = "form_db";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    public static class FromDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "form_data_db";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_FORM_ID = "form_id";
        public static final String COLUMN_NAME_FKEY = "fkey";
        public static final String COLUMN_NAME_FNAME = "fname";
        public static final String COLUMN_NAME_REQ = "req";
        public static final String COLUMN_NAME_VALKEY = "valKey";
        public static final String COLUMN_NAME_VALLABEL = "valLabel";
        public static final String COLUMN_NAME_VALVALUE = "valValue";
        public static final String COLUMN_NAME_FTYPE = "f_type";
        public static final String COLUMN_NAME_MSEL = "msel";
        public static final String COLUMN_NAME_DECIMAL = "decimal";
        public static final String COLUMN_NAME_INSTRUCTION = "instruction";
        public static final String COLUMN_NAME_INTYPE = "intType";
        public static final String COLUMN_NAME_MINVAL = "minVal";
        public static final String COLUMN_NAME_MAXVAL = "maxVal";
        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";
//        public static final String COLUMN_NAME_IMAGES = "images";
    }

    public static class ChoiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "choice_db";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_FORM_ID = "form_id";
        public static final String COLUMN_NAME_POSITION = "position_id";
        public static final String COLUMN_NAME_FORM_DATA_ID = "form_data_id";
        public static final String COLUMN_NAME_CHOICE_DESC = "choice_desc";
        public static final String COLUMN_NAME_CHOICE_VAL = "choice_val";
        public static final String COLUMN_NAME_SELECTED_CHOICE = "selectedChoice";
        public static final String COLUMN_NAME_HV_CODES = "hv_codes";
        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";
    }

    public static class DecimalEntry implements BaseColumns {
        public static final String TABLE_NAME = "decimal_db";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_FORM_ID = "form_id";
        public static final String COLUMN_NAME_FORM_DATA_ID = "form_data_id";
        public static final String COLUMN_NAME_DECIMALS = "decimals";
        public static final String COLUMN_NAME_HV_CODES = "hv_codes";
//        public static final String COLUMN_NAME_IS_DELETED = "is_deleted";
        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";
    }

    public static class ImagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "images_db";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_FORM_ID = "form_id";
        public static final String COLUMN_NAME_FORM_DATA_ID = "form_data_id";
//        public static final String COLUMN_NAME_IMAGES = "images";
        public static final String COLUMN_NAME_HV_CODES = "hv_codes";
        //        public static final String COLUMN_NAME_IS_DELETED = "is_deleted";
        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";
    }

}
