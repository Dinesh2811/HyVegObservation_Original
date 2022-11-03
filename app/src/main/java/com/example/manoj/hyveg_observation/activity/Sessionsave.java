package com.example.manoj.hyveg_observation.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class Sessionsave {

    private SharedPreferences pref_preview_one ,pref_login ,pref_image,pref_hybrid;
    private SharedPreferences.Editor editor_preview_one ,editor_login,editor_image,editor_hybrid;
    private static final String PREF_NAME_one = "SessionPreviewone";
    private static final String PREF_login = "login";
    private static final String PREF_image = "image";
    private static final String PREF_hybrid = "hybrid";
    String obid = "ob_id";
    String emp_code = "emp_code";
    String Division = "division";
    String Company = "company";
    String DCode = "DCode";
    String CropName = "CropName";
    String CropCode = "CropCode";
    String BreederCode = "BreederCode";
    String HVCode = "HVCode";
    String Variety = "Variety";
    String TrailStages = "TrailStages";
    String StateName = "StateName";
    String StateCode = "StateCode";
    String City = "City";
    String Password = "Password";
    String General_info = "General_Instruction";
    String Master_info = "Master_Instruction";
    String Ob_traits_info = "Ob_traits_Instruction";
    String Usp_info = "Usp_Instruction";
    String check_hybrid = "check_hybrid";

    @SuppressLint("CommitPrefEdits")
    public Sessionsave(Context context) {
        int PRIVATE_MODE = 0;
        pref_preview_one = context.getSharedPreferences(PREF_NAME_one, PRIVATE_MODE);
        pref_login = context.getSharedPreferences(PREF_login, PRIVATE_MODE);
        pref_image = context.getSharedPreferences(PREF_image, PRIVATE_MODE);
        pref_hybrid = context.getSharedPreferences(PREF_hybrid, PRIVATE_MODE);

        editor_preview_one = pref_preview_one.edit();
        editor_login = pref_login.edit();
        editor_image = pref_image.edit();
        editor_hybrid = pref_hybrid.edit();

    }

//    public void create_First_Page_Session(String employee, String Breeder, String hv, String segment, String trialstage,String plotsize,String state,String grower,String location,String sowing,String planting,String firstharvest,String harvestdate,String status,String comments) {
//        // Storing login value as TRUE
//        //   editor_preview.putBoolean(IS_LOGIN, true);
//        editor_preview_one.putString("employee", employee);
//        editor_preview_one.putString("Breeder", Breeder);
//        editor_preview_one.putString("hv", hv);
//        editor_preview_one.putString("segment", segment);
//        editor_preview_one.putString("trialstage", trialstage);
//        editor_preview_one.putString("plotsize", plotsize);
//        editor_preview_one.putString("state", state);
//        editor_preview_one.putString("grower", grower);
//        editor_preview_one.putString("location", location);
//        editor_preview_one.putString("sowing", sowing);
//        editor_preview_one.putString("planting", planting);
//        editor_preview_one.putString("firstharvest", firstharvest);
//        editor_preview_one.putString("harvestdate", harvestdate);
//        editor_preview_one.putString("status", status);
//        editor_preview_one.putString("comments", comments);
//
//        editor_preview_one.apply();
//    }
//

    public void save_one(String field_name, String data) {
        editor_preview_one.putString(field_name, data);
        editor_preview_one.apply();
    }

    public String get_one(String field_name) {
        return pref_preview_one.getString(field_name,"");
    }

    public void save_two(String field_name, String data) {
        editor_preview_one.putString(field_name, data);
        editor_preview_one.apply();
    }

    public String get_two(String field_name) {
        return pref_preview_one.getString(field_name,"");
    }

    public void save_three(String field_name, String data) {
        editor_preview_one.putString(field_name, data);
        editor_preview_one.apply();
    }

    public String get_three(String field_name) {
        return pref_preview_one.getString(field_name,"");
    }

    public void save_four(String field_name, String data) {
        editor_preview_one.putString(field_name, data);
        editor_preview_one.apply();
    }

    public String get_four(String field_name) {
        return pref_preview_one.getString(field_name,"");
    }


    public void session_clear() {
        editor_preview_one.clear();
        editor_preview_one.commit();
    }

    public String get_emp_code() {
        return pref_login.getString(emp_code,"");
    }

    public void save_emp_code(String poCodes) {
        editor_login.putString(emp_code, poCodes);
        editor_login.apply();
    }

    public void save_obid(String field_name) {
        editor_preview_one.putString(obid, field_name);
        editor_preview_one.apply();
    }

    public String get_obid() {
        return pref_preview_one.getString(obid,"");
    }


    public void save_folder(String folder_name) {
        editor_login.putString(Division,folder_name);
        editor_login.apply();
    }

    public String get_folder() {
        return pref_login.getString(Division,"");
    }

    public void save_company(String company) {
        editor_login.putString(Company,company);
        editor_login.apply();
    }

    public String get_company() {
        return pref_login.getString(Company,"");
    }

    public void save_d_code(String dCode) {
        editor_login.putString(DCode,dCode);
        editor_login.apply();
    }

    public String get_d_code() {
        return pref_login.getString(DCode,"");
    }

    public void save_crop_data(String crop_name , String crop_code, String breeder_code, String hv_code, String variety, String trail_stages , String state_name , String state_code, String city) {
        editor_preview_one.putString(CropName, crop_name);
        editor_preview_one.putString(CropCode, crop_code);
        editor_preview_one.putString(BreederCode, breeder_code);
        editor_preview_one.putString(HVCode, hv_code);
        editor_preview_one.putString(Variety, variety);
        editor_preview_one.putString(TrailStages, trail_stages);
        editor_preview_one.putString(StateName, state_name);
        editor_preview_one.putString(StateCode, state_code);
        editor_preview_one.putString(City, city);
        editor_preview_one.apply();
    }

    public String get_crop_code() {
        return pref_preview_one.getString(CropCode,"");
    }

    public String get_crop_name() {
        return pref_preview_one.getString(CropName,"");
    }

    public String get_breeder_code() {
        return pref_preview_one.getString(BreederCode,"");
    }

    public String get_hv_code() {
        return pref_preview_one.getString(HVCode,"");
    }

    public String get_variety() {
        return pref_preview_one.getString(Variety,"");
    }

    public String get_trail_stages() {
        return pref_preview_one.getString(TrailStages,"");
    }

    public String get_states() {
        return pref_preview_one.getString(StateName,"");
    }

    public String get_states_code() {
        return pref_preview_one.getString(StateCode,"");
    }

    public String get_city() {
        return pref_preview_one.getString(City,"");
    }

    public void save_pass(String poCodes_p) {
        editor_login.putString(Password, poCodes_p);
        editor_login.apply();
    }

    public String get_pass() {
        return pref_login.getString(Password,"");
    }

    public void login_session_clear() {
        editor_login.clear();
        editor_login.commit();
    }

    public void save_image(String field_name, String data) {
        editor_image.putString(field_name, data);
        editor_image.apply();
    }

    public String get_image(String field_name) {
        return pref_image.getString(field_name,"");
    }

    public void save_general_instruction(String general_info) {
        editor_preview_one.putString(General_info,general_info);
        editor_preview_one.apply();
    }

    public String get_general_instruction() {
        return pref_preview_one.getString(General_info,"null");
    }

    public void save_master_instruction(String master_info) {
        editor_preview_one.putString(Master_info,master_info);
        editor_preview_one.apply();
    }

    public String get_master_instruction() {
        return pref_preview_one.getString(Master_info,"null");
    }

    public void save_ob_traits_instruction(String ob_traits_info) {
        editor_preview_one.putString(Ob_traits_info,ob_traits_info);
        editor_preview_one.apply();
    }

    public String get_ob_traits_instruction() {
        return pref_preview_one.getString(Ob_traits_info,"null");
    }

    public void save_usp_instruction(String usp_info) {
        editor_preview_one.putString(Usp_info,usp_info);
        editor_preview_one.apply();
    }

    public String get_usp_instruction() {
        return pref_preview_one.getString(Usp_info,"null");
    }


    public void save_hybrid(int size) {
        editor_hybrid.putInt(check_hybrid,size);
        editor_hybrid.apply();
    }

    public int getCheck_hybrid() {
        return pref_hybrid.getInt(check_hybrid,0);
    }

}
