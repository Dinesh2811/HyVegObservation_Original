package com.example.manoj.hyveg_observation;

public class Api {

    //--------------------- Live Server ---------------------

//    public static String ipconfig = "http://103.44.97.110:8080/kanagaraj/pd_trail/";

    //-------------------------test----------------------------------------

    public static String ipconfig = "http://103.44.97.110:8080/kanagaraj/Pd_trail_Dev/";


    //--------------------- Local Server ---------------------
//
//    public static String ipconfig = "http://192.168.35.24/hyveg/pd_trail/";

    //--------------------- Version ---------------------

    public static String version() {
        return ipconfig +"Version.php";
    }

    //--------------------- Observation ---------------------

    public static String login(String user_name, String password, String company) {
        return ipconfig +"OBLogin.php?username="+user_name+"&password="+password+"&Company="+company;
    }

    //--------------------- Crop Select ---------------------

    public static String crop_master(String project_folder) {
        return ipconfig + project_folder+ "/ISCropMaster.php";
    }

    //--------------------- Breeder code Select ---------------------

    public static String get_breed(String CropCode ,String company) {
        return ipconfig + "GetBreeder.php?ccode="+CropCode+"&company="+company+"&json=yes";
    }

    public static String get_breed(String company) {
        return ipconfig + "GetBreeder.php?company="+company+"&json=yes";
    }

    //--------------------- HV code Select ---------------------

    public static String get_hv_codes(String CropCode ,String company) {
        return ipconfig + "GetHVCodes.php?ccode="+CropCode+"&company="+company+"&json=yes";
    }

    public static String get_hv_codes(String company) {
        return ipconfig + "GetHVCodes.php?company="+company+"&json=yes";
    }

    //--------------------- HV code Select ---------------------

    public static String get_hv_code(String CropCode ,String company ,String br_code) {
        return ipconfig + "GetHandVDet.php?ccode="+CropCode+"&company="+company+"&brcode="+br_code;
    }

    //--------------------- Select state ---------------------

    public static String get_state(String company) {
        return ipconfig + "StateMaster.php?company="+company;
    }

    //--------------------- Observation ---------------------

    //for online single data
    public static String get_observation(String crop_name, String company) {
//            return ipconfig +"PDTGetObservationFields.php?cropname="+crop_name+"&Company="+company;
            return ipconfig +"PDTGetObservationFields.php?CropCode="+crop_name+"&Company="+company;
    }

    //for offline bulk data
    public static String get_observation(String company) {
//            return ipconfig +"PDTGetObservationFields.php?cropname="+crop_name+"&Company="+company;
        return ipconfig +"PDTGetObservationFields.php?Company="+company;
    }

    //--------------------- Get Observation Data ---------------------

    public static String ob_data(String emp_code, String company) {
        return ipconfig +"OBGetData.php?uid="+emp_code+"&Company="+company;
    }

    //--------------------- Get Observation list ---------------------

    public static String ob_list(String emp_code, String company, String dcode) {
        return ipconfig +"OBGetList.php?EmpCode="+emp_code+"&Company="+company+"&DCode="+dcode;
    }

    //--------------------- Observation record insert ---------------------

//    public final static String obd_insert = ipconfig+"/OBDetailsInsert.php";

    public final static String obd_insert = ipconfig+"/OBDetailsInsert_test.php";

    //--------------------- Image Get ---------------------

    public final static String obd_get_image = ipconfig+"/obimages/";

    //--------------------- Master Data---------------------

    public final static String save_growermaster = ipconfig+"/ManageGrower.php";

    public final static String Pdtrial_formregistration = ipconfig+"/Pd_Trial_Registration.php";

    public final static String Pd_RegistrationNumber = ipconfig+"/Pd_Trial_Registration.php";



    //--------------------- Key_name ---------------------

    public static final String TAG_RESULTS = "result";
    public static final String T1 = "Id";
    public static final String T2 = "A";
    public static final String T3 = "B";
    public static final String T4 = "C";
    public static final String T5 = "D";
    public static final String T6 = "E";
    public static final String T7 = "F";
    public static final String T8 = "G";
    public static final String T9 = "H";
    public static final String T10 = "I";
    public static final String T11 = "J";
    public static final String T12 = "K";
    public static final String T13 = "L";
    public static final String T14 = "M";
    public static final String T15 = "N";
    public static final String T16 = "O";
    public static final String T17 = "P";
    public static final String T18 = "Q";
    public static final String T19 = "R";
    public static final String T20 = "S";
    public static final String T21 = "T";
    public static final String T22 = "U";
    public static final String T23 = "V";
    public static final String T24 = "W";
    public static final String T25 = "X";
    public static final String T26 = "Y";
    public static final String T27 = "Z";
    public static final String T28 = "UserStatus";
    public static final String T2k = "EmpCode";

    //--------------------- Employee list ---------------------

    public static String get_employee_list(String company) {
        return ipconfig +"GetOBPDE.php?Company="+company;
    }
}