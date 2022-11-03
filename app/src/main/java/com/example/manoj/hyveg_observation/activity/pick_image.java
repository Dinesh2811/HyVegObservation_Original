package com.example.manoj.hyveg_observation.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.system.ErrnoException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manoj.hyveg_observation.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class pick_image extends AppCompatActivity implements View.OnClickListener {

    private Uri mCropImageUri;
    private ImageView imageView, img_add_1, img_add_2;
    //            ,img_add1,img_add2,img_add3,img_add4,img_add5;
    LinearLayout l_save_image;
    String img_name, position, from, key;
    Sessionsave sessionsave;
    TextView text_img_empty;
    int pick_no = 1;
    RecyclerView recycle_img;
    ArrayList<Uri> img_list;
    List<String> st_img_list;
    private ProgressDialog dialog;

    private File Oroot = Environment.getExternalStorageDirectory() ,GetMasterPath=null;
    private SQLiteDatabase dbGetMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image);

        sessionsave = new Sessionsave(this);
        imageView = findViewById(R.id.CropImageView);
        img_add_1 = findViewById(R.id.imageView1);
        img_add_2 = findViewById(R.id.imageView2);
//        img_add2 = findViewById(R.id.imageView2);
//        img_add3 = findViewById(R.id.imageView3);
//        img_add4 = findViewById(R.id.imageView4);
//        img_add5 = findViewById(R.id.imageView5);
        l_save_image = findViewById(R.id.l_save_image);
        text_img_empty = findViewById(R.id.text_img_empty);
//        recycle_img = findViewById(R.id.recycle_img);
        img_list = new ArrayList<>();
        st_img_list = new ArrayList<>();
        dialog = new ProgressDialog(this);

        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();

            GetMasterPath = new File(dir, "GetMasterDB.db");

        }

        dbGetMaster = openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);

        recycle_img.setHasFixedSize(true);
        recycle_img.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        img_name = getIntent().getStringExtra("image_name");
        position = getIntent().getStringExtra("position");
        from = getIntent().getStringExtra("from");
        key = getIntent().getStringExtra("key");

        switch (from) {
            case "m_list_edit":
                int m_len = Integer.parseInt(sessionsave.get_image("master_len"));
                for (int i = 1 ; i < (m_len+1) ; i++){
                    st_img_list.add(sessionsave.get_image("master_" + i));
                }
                break;
            case "g_list_edit":
                int g_len = Integer.parseInt(sessionsave.get_image("general_len"));
                for (int i = 1 ; i < (g_len+1) ; i++){
                    st_img_list.add(sessionsave.get_image("general_" + i));
                }
                break;
            case "o_list_edit":
                int o_len = Integer.parseInt(sessionsave.get_image("observe_len"));
                for (int i = 1 ; i < (o_len+1) ; i++){
                    st_img_list.add(sessionsave.get_image("observe_" + i));
                }
                break;
            case "u_list_edit":
                int u_len = Integer.parseInt(sessionsave.get_image("usp_len"));
                for (int i = 1 ; i < (u_len+1) ; i++){
                    st_img_list.add(sessionsave.get_image("usp_" + i));
                }
                break;
        }

        imageView.setOnClickListener(this);
        img_add_2.setOnClickListener(this);
//       img_add1.setOnClickListener(this);
//       img_add2.setOnClickListener(this);
//       img_add3.setOnClickListener(this);
//       img_add4.setOnClickListener(this);
//       img_add5.setOnClickListener(this);
        l_save_image.setOnClickListener(this);

        RecyclerView.Adapter adapter = new img_adapter(this, st_img_list);
        recycle_img.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        }, 200);
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), img_name+pick_no+ ".jpeg"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri = null;
        if (resultCode == Activity.RESULT_OK) {
            try {
                imageUri = getPickImageResultUri(data);
            }catch (Exception e){
                e.printStackTrace();
            }

            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;

                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                text_img_empty.setVisibility(View.GONE);
                imageView.setImageURI(imageUri);

                try {
                    if (data.getClipData() != null) {
                        Log.e("pick","already picked");
                    }
                }catch (Exception e) {
                    BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                    File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation/Images/");
                    if (dir.exists ()) {
                        dir.delete();
                    }else {
                        dir.mkdirs();
                    }
                    File file = new File(dir, img_name+pick_no+ ".jpeg");
                    if (file.exists ()) {
                        file.delete();
                    }

                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (st_img_list.size() != 0){
                        st_img_list.add(getStringImage(bitmap));
                    }else {
                        img_list.add(Uri.fromFile(file));
                    }
                }

//                if (pick_no == 1){
//                    img_add1.setImageURI(imageUri);
//                }else if (pick_no == 2){
//                    img_add2.setImageURI(imageUri);
//                }else if (pick_no == 3){
//                    img_add3.setImageURI(imageUri);
//                }else if (pick_no == 4){
//                    img_add4.setImageURI(imageUri);
//                }else if (pick_no == 5){
//                    img_add5.setImageURI(imageUri);
//                }
                if (st_img_list.size() != 0) {
                    RecyclerView.Adapter adapter = new img_adapter(this, st_img_list);
                    recycle_img.setAdapter(adapter);
                }else {
                    RecyclerView.Adapter adapter = new img_adapter(this, img_list);
                    recycle_img.setAdapter(adapter);
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            text_img_empty.setVisibility(View.GONE);
            imageView.setImageURI(mCropImageUri);
            if (st_img_list.size() != 0) {
                BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) imageView.getDrawable());
                Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                st_img_list.add(getStringImage(bitmap1));
                RecyclerView.Adapter adapter = new img_adapter(this, st_img_list);
                recycle_img.setAdapter(adapter);
            }else {
                img_list.add(mCropImageUri);
                RecyclerView.Adapter adapter = new img_adapter(this, img_list);
                recycle_img.setAdapter(adapter);
            }
//            if (pick_no == 1){
//                img_add1.setImageURI(mCropImageUri);
//            }else if (pick_no == 2){
//                img_add2.setImageURI(mCropImageUri);
//            }else if (pick_no == 3){
//                img_add3.setImageURI(mCropImageUri);
//            }else if (pick_no == 4){
//                img_add4.setImageURI(mCropImageUri);
//            }else if (pick_no == 5){
//                img_add5.setImageURI(mCropImageUri);
//            }
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        try {
            assert data != null;
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    if (st_img_list.size() != 0) {
                        img_add_1.setImageURI(uri);
                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_1.getDrawable());
                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        st_img_list.add(getStringImage(bitmap1));
                    }else {
                        img_list.add(uri);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.CropImageView) {
            pick_no++;
            startActivityForResult(getPickImageChooserIntent(), 200);
        }
//        else if (view.getId() == R.id.imageView1){
//            pick_no = 1;
//            startActivityForResult(getPickImageChooserIntent(), 200);
//        }
        else if (view.getId() == R.id.imageView2) {
            pick_no++;
            startActivityForResult(getPickImageChooserIntent(), 200);
        }
// else if (view.getId() == R.id.imageView3){
//            pick_no = 3;
//            startActivityForResult(getPickImageChooserIntent(), 200);
//        }else if (view.getId() == R.id.imageView4){
//            pick_no = 4;
//            startActivityForResult(getPickImageChooserIntent(), 200);
//        }else if (view.getId() == R.id.imageView5){
//            pick_no = 5;
//            startActivityForResult(getPickImageChooserIntent(), 200);
//        }
        else if (view.getId() == R.id.l_save_image) {
            dialog.setMessage("Saving data.....");
            dialog.show();
            Bitmap bitmap = null;
            try {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
                bitmap = bitmapDrawable.getBitmap();
            }catch (Exception e){
                e.printStackTrace();
            }
            switch (from) {
                case "m_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
                        img_add_1.setImageURI(img_list.get(m_s));
                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_1.getDrawable());
                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("master_" + (m_s + 1), getStringImage(bitmap1));
//                        insert_image("Master",getStringImage(bitmap1),sessionsave.get_crop_code());
//                        sessionsave.save_image("master_" + (m_s + 1), img_list.get(m_s)+"");
                    }
//                    if (pick_no == 1){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("master_" + pick_no, getStringImage(bitmap1));
//                    }else if (pick_no == 2){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("master_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("master_" + 2, getStringImage(bitmap2));
//                    }else if (pick_no == 3){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("master_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("master_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("master_" + 3, getStringImage(bitmap3));
//                    }else if (pick_no == 4){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("master_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("master_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("master_" + 3, getStringImage(bitmap3));
//                        BitmapDrawable bitmapDrawable4 = ((BitmapDrawable) img_add4.getDrawable());
//                        Bitmap bitmap4 = bitmapDrawable4.getBitmap();
//                        sessionsave.save_image("master_" + 4, getStringImage(bitmap4));
//                    }
                    sessionsave.save_image("master_len", img_list.size() + "");
                    sessionsave.save_one("master_" + position, getStringImage(bitmap));
                    sessionsave.save_one("master_key_" + position, "img_" + key);
                    break;
                case "g_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
                        img_add_1.setImageURI(img_list.get(m_s));
                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_1.getDrawable());
                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("general_" + (m_s + 1), getStringImage(bitmap1));
//                        insert_image("General",getStringImage(bitmap1),sessionsave.get_crop_code());
//                        sessionsave.save_image("general_" + (m_s + 1), img_list.get(m_s)+"");
                    }
//                    if (pick_no == 1){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("general_" + pick_no, getStringImage(bitmap1));
//                    }else if (pick_no == 2){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("general_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("general_" + 2, getStringImage(bitmap2));
//                    }else if (pick_no == 3){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("general_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("general_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("general_" + 3, getStringImage(bitmap3));
//                    }else if (pick_no == 4){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("general_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("general_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("general_" + 3, getStringImage(bitmap3));
//                        BitmapDrawable bitmapDrawable4 = ((BitmapDrawable) img_add4.getDrawable());
//                        Bitmap bitmap4 = bitmapDrawable4.getBitmap();
//                        sessionsave.save_image("general_" + 4, getStringImage(bitmap4));
//                    }
                    sessionsave.save_image("general_len", img_list.size() + "");
                    sessionsave.save_two("general_" + position, getStringImage(bitmap));
                    sessionsave.save_two("general_key_" + position, "img_" + key);
                    break;
                case "o_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
                        img_add_1.setImageURI(img_list.get(m_s));
                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_1.getDrawable());
                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("observe_" + (m_s + 1), getStringImage(bitmap1));
//                        insert_image("Observation",getStringImage(bitmap1),sessionsave.get_crop_code());
//                        sessionsave.save_image("observe_" + (m_s + 1), img_list.get(m_s)+"");
                    }
//                    if (pick_no == 1){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("observe_" + pick_no, getStringImage(bitmap1));
//                    }else if (pick_no == 2){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("observe_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("observe_" + 2, getStringImage(bitmap2));
//                    }else if (pick_no == 3){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("observe_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("observe_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("observe_" + 3, getStringImage(bitmap3));
//                    }else if (pick_no == 4){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("observe_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("observe_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("observe_" + 3, getStringImage(bitmap3));
//                        BitmapDrawable bitmapDrawable4 = ((BitmapDrawable) img_add4.getDrawable());
//                        Bitmap bitmap4 = bitmapDrawable4.getBitmap();
//                        sessionsave.save_image("observe_" + 4, getStringImage(bitmap4));
//                    }
                    sessionsave.save_image("observe_len", img_list.size() + "");
                    sessionsave.save_three("observe_" + position, getStringImage(bitmap));
                    sessionsave.save_three("observe_key_" + position, "img_" + key);
                    break;
                case "u_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
                        img_add_1.setImageURI(img_list.get(m_s));
                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_1.getDrawable());
                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("usp_" + (m_s + 1), getStringImage(bitmap1));
//                        insert_image("Usp",getStringImage(bitmap1),sessionsave.get_crop_code());
//                        sessionsave.save_image("usp_" + (m_s + 1), img_list.get(m_s)+"");
                    }
//                    if (pick_no == 1){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("usp_" + pick_no, getStringImage(bitmap1));
//                    }else if (pick_no == 2){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("usp_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("usp_" + 2, getStringImage(bitmap2));
//                    }else if (pick_no == 3){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("usp_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("usp_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("usp_" + 3, getStringImage(bitmap3));
//                    }else if (pick_no == 4){
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add1.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        sessionsave.save_image("usp_" + 1, getStringImage(bitmap1));
//                        BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) img_add2.getDrawable());
//                        Bitmap bitmap2 = bitmapDrawable2.getBitmap();
//                        sessionsave.save_image("usp_" + 2, getStringImage(bitmap2));
//                        BitmapDrawable bitmapDrawable3 = ((BitmapDrawable) img_add3.getDrawable());
//                        Bitmap bitmap3 = bitmapDrawable3.getBitmap();
//                        sessionsave.save_image("usp_" + 3, getStringImage(bitmap3));
//                        BitmapDrawable bitmapDrawable4 = ((BitmapDrawable) img_add4.getDrawable());
//                        Bitmap bitmap4 = bitmapDrawable4.getBitmap();
//                        sessionsave.save_image("usp_" + 4, getStringImage(bitmap4));
//                    }
                    sessionsave.save_image("usp_len", img_list.size() + "");
                    sessionsave.save_four("usp_" + position, getStringImage(bitmap));
                    sessionsave.save_four("usp_key_" + position, "img_" + key);
                    break;
                case "m_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("master_" + (m_s + 1), st_img_list.get(m_s));
                    }
                    sessionsave.save_image("master_len", (st_img_list.size()) + "");
                    sessionsave.save_four("master_" + position, getStringImage(bitmap));
                    sessionsave.save_four("master_key_" + position, "img_" + key);
                    break;
                case "g_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("general_" + (m_s + 1), st_img_list.get(m_s));
                    }
                    sessionsave.save_image("general_len", st_img_list.size() + "");
                    sessionsave.save_two("general_" + position, getStringImage(bitmap));
                    sessionsave.save_two("general_key_" + position, "img_" + key);
                    break;
                case "o_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("observe_" + (m_s + 1), st_img_list.get(m_s));
                    }
                    sessionsave.save_image("observe_len", st_img_list.size() + "");
                    sessionsave.save_three("observe_" + position, getStringImage(bitmap));
                    sessionsave.save_three("observe_key_" + position, "img_" + key);
                    break;
                case "u_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("usp_" + (m_s + 1),st_img_list.get(m_s));
                    }
                    sessionsave.save_image("usp_len", st_img_list.size() + "");
                    sessionsave.save_four("usp_" + position, getStringImage(bitmap));
                    sessionsave.save_four("usp_key_" + position, "img_" + key);
                    break;
            }
            dialog.cancel();
            finish();
        }
    }

//    private void insert_image(String field_name,String img, String crop_code) {
//        String query = "INSERT INTO ObservationImage("+field_name+",CropCode) VALUES('" + img + "','" + crop_code +"');";
//        dbGetMaster.execSQL(query);
//    }

    private String getStringImage(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            return "not null";
        }
    }

    private class img_adapter extends RecyclerView.Adapter<img_adapter.ViewHolder> {

        ArrayList<Uri> img_list;
        List<String> img_st_list;
        Context context;

        img_adapter(pick_image pick_image, ArrayList<Uri> img_uri_list) {
            context = pick_image;
            this.img_list = img_uri_list;
        }

        img_adapter(pick_image pick_image, List<String> img_st_list) {
            context = pick_image;
            this.img_st_list = img_st_list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_img, parent, false);
            ViewHolder viewHolder = new img_adapter.ViewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            try {
                if (img_list.size() != 0) {
                    try {
                        Log.e("pick",img_list.get(position)+"");

                        holder.img_add.setImageURI(img_list.get(position));

                        holder.img_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                imageView.setImageURI(img_list.get(position));
                            }
                        });

                        holder.img_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                img_list.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, img_list.size());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                try {
                    if (img_st_list.size() != 0) {
                        try {
                            Log.e("pick",img_st_list.get(position)+"");

                            holder.img_add.setImageBitmap(St_to_img(img_st_list.get(position)));

                            holder.img_add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(context, "Deleting option only available", Toast.LENGTH_SHORT).show();
                                }
                            });

                            holder.img_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    img_st_list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, img_list.size());
                                    switch (from) {
                                        case "m_list_edit":
                                            sessionsave.save_image("master_" + position,"");
                                            break;
                                        case "g_list_edit":
                                            sessionsave.save_image("general_" + position,"");
                                            break;
                                        case "o_list_edit":
                                            sessionsave.save_image("observe_" + position ,"");
                                            break;
                                        case "u_list_edit":
                                            sessionsave.save_image("usp_" + position ,"");
                                            break;
                                    }
                                }
                            });
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }catch (Exception e1){
                    Log.e("err",e1.toString());
                }
            }
        }

        @Override
        public int getItemCount() {

            try {
                return img_list.size();
            }catch (Exception e){
                try {
                    return img_st_list.size();
                }catch (Exception e1){
                    return 0;
                }
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView img_add ,img_close;

            public ViewHolder(View v) {
                super(v);

                img_add = v.findViewById(R.id.image_add);
                img_close = v.findViewById(R.id.image_close);
            }
        }
    }

    private Bitmap St_to_img(String s) {
        byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
