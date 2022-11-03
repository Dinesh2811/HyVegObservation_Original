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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manoj.hyveg_observation.BuildConfig;
import com.example.manoj.hyveg_observation.R;
import com.example.manoj.hyveg_observation.services.alertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class pick_image_2 extends AppCompatActivity implements View.OnClickListener {

    private Uri mCropImageUri;
    private ImageView imageView, img_add_1, img_add_2, img_add_3, img_close_1, img_close_2;
    LinearLayout l_save_image;
    String img_name, position, from, key;
    Sessionsave sessionsave;
    TextView text_img_empty;
    int pick_no = 1 ,pick_edit = 0 ,sl_no = 1;
    ArrayList<Uri> img_list;
    List<String> st_img_list;
    private ProgressDialog dialog;
    alertDialog alert;
    private File Oroot = Environment.getExternalStorageDirectory(), GetMasterPath = null;
    private SQLiteDatabase dbGetMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image);

        sessionsave = new Sessionsave(this);
        imageView = findViewById(R.id.CropImageView);
        img_add_1 = findViewById(R.id.imageView1);
        img_add_2 = findViewById(R.id.imageView2);
        img_add_3 = findViewById(R.id.imageView3);
        img_close_1 = findViewById(R.id.image_close_1);
        img_close_2 = findViewById(R.id.image_close_2);
        l_save_image = findViewById(R.id.l_save_image);
        text_img_empty = findViewById(R.id.text_img_empty);
        img_list = new ArrayList<>();
        st_img_list = new ArrayList<>();
        dialog = new ProgressDialog(this);
        alert = new alertDialog(this);

        if (Oroot.canWrite()) {
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation");
            dir.mkdirs();
            GetMasterPath = new File(dir, "GetMasterDB.db");
        }

        dbGetMaster = openOrCreateDatabase(GetMasterPath.getPath(), Context.MODE_PRIVATE, null);

        img_name = getIntent().getStringExtra("image_name");
        position = getIntent().getStringExtra("position");
        from = getIntent().getStringExtra("from");
        key = getIntent().getStringExtra("key");
        if (alert.isNetworkAvailable()){
            sl_no = 1;
        }else {
            sl_no = get_sl_no_db()+1;
        }

        switch (from) {
            case "m_list_edit":
                int m_len = Integer.parseInt(sessionsave.get_image("master_len"));
                for (int i = 1; i < (m_len + 1); i++) {
                    st_img_list.add(sessionsave.get_image("master_" + i));
                }
                break;
            case "g_list_edit":
                int g_len = Integer.parseInt(sessionsave.get_image("general_len"));
                for (int i = 1; i < (g_len + 1); i++) {
                    st_img_list.add(sessionsave.get_image("general_" + i));
                }
                break;
            case "o_list_edit":
                int o_len = Integer.parseInt(sessionsave.get_image("observe_len"));
                for (int i = 1; i < (o_len + 1); i++) {
                    st_img_list.add(sessionsave.get_image("observe_" + i));
                }
                break;
            case "u_list_edit":
                int u_len = Integer.parseInt(sessionsave.get_image("usp_len"));
                for (int i = 1; i < (u_len + 1); i++) {
                    st_img_list.add(sessionsave.get_image("usp_" + i));
                }
                break;
            default:
                img_add_2.setVisibility(View.GONE);
                break;
        }

        img_add_2.setOnClickListener(this);
        img_add_1.setOnClickListener(this);
        l_save_image.setOnClickListener(this);
        img_close_1.setOnClickListener(this);
        img_close_2.setOnClickListener(this);

        if (from.equals("m_list_edit") || from.equals("g_list_edit") || from.equals("o_list_edit") || from.equals("u_list_edit")) {
            for (int i = 0; i < st_img_list.size(); i++) {
                if (i == 0) {
                    if (st_img_list.get(i) != null) {
//                        img_add_1.setImageBitmap(St_to_img(st_img_list.get(i)));
                        img_add_1.setImageURI(Uri.parse(st_img_list.get(i)));
                        img_close_1.setVisibility(View.VISIBLE);
                        Log.e("pick_2_1",st_img_list.get(i));
                        Log.e("pick_2_1",Uri.parse(st_img_list.get(i))+"");
                    }
                } else if (i == 1) {
                    if (st_img_list.get(i) != null) {
//                        img_add_2.setImageBitmap(St_to_img(st_img_list.get(i)));
                        Log.e("pick_2",st_img_list.get(i));
                        Log.e("pick_2",Uri.parse(st_img_list.get(i))+"");
                        img_add_2.setImageURI(Uri.parse(st_img_list.get(i)));
                        img_close_2.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private int get_sl_no_db() {
        String countQuery = "SELECT  * FROM ObservationData";
        Cursor cursor = dbGetMaster.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
        File Oroot = Environment.getExternalStorageDirectory();
        File getImage = new File(Oroot.getAbsolutePath() + "/Android/Observation/Images");
        getImage.mkdirs();
        if (getImage != null) {
            outputFileUri = FileProvider.getUriForFile(pick_image_2.this, BuildConfig.APPLICATION_ID + ".provider", new File(getImage.getPath(), sl_no + "_" + img_name + pick_no + ".jpeg"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri = null;
        if (resultCode == Activity.RESULT_OK) {
            try {
                Log.e("pick_2","capture_2_1");
                imageUri = getPickImageResultUri(data);
            } catch (Exception e) {
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
                Log.e("pick_2","capture_2_2 imageuri "+imageUri);
                imageView.setImageURI(imageUri);

                try {
                    if (data.getClipData() != null) {
                        Log.e("pick", "already picked");
                    }
                } catch (Exception e) {
//                    BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
//                    Bitmap bitmap = bitmapDrawable.getBitmap();
//
//                    File Oroot = Environment.getExternalStorageDirectory();
//                    File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation/Images");
//                    if (dir.exists()) {
//                        dir.delete();
//                    } else {
//                        dir.mkdirs();
//                    }
//                    File file = new File(dir, img_name + pick_no + ".jpeg");
//                    if (file.exists()) {
//                        file.delete();
//                    }
//
//                    try {
//                        FileOutputStream out = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                        out.flush();
//                        out.close();
//
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                    if (st_img_list.size() != 0) {
//                        st_img_list.add(imageUri+"");
//                    } else {
//                        img_list.add(imageUri);
//                    }
                    e.printStackTrace();
                }

                if (pick_no == 1) {
                    img_add_1.setImageURI(imageUri);
                    img_add_2.setVisibility(View.VISIBLE);
                    img_add_1.setEnabled(false);
                } else if (pick_no == 2) {
                    img_add_2.setImageURI(imageUri);
                    img_add_2.setEnabled(false);
                }
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            text_img_empty.setVisibility(View.GONE);
//            imageView.setImageURI(mCropImageUri);
//            if (st_img_list.size() != 0) {
//                BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) imageView.getDrawable());
//                Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                st_img_list.add(mCropImageUri+"");
//
//                if (pick_no == 1) {
//                    img_add_1.setImageBitmap(bitmap1);
//                } else if (pick_no == 2) {
//                    img_add_2.setImageBitmap(bitmap1);
//                }
//            } else {
//                img_list.add(mCropImageUri);
//
//                if (pick_no == 1) {
//                    img_add_1.setImageURI(mCropImageUri);
//                } else if (pick_no == 2) {
//                    img_add_2.setImageURI(mCropImageUri);
//                }
//            }
//        } else {
//            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
//        }
//    }

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
//                    if (st_img_list.size() != 0) {
////                        img_add_3.setImageURI(uri);
////                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_3.getDrawable());
////                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
//                        st_img_list.add(uri+"");
//                    } else {
//                        img_list.add(uri);
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("pick_2","capture_2 "+isCamera);
        if (!isCamera){
            File Oroot = Environment.getExternalStorageDirectory();
            File dir = new File(Oroot.getAbsolutePath() + "/Android/Observation/Images");

            try {
                copyFile(new File(getPath(data.getData())), new File(dir, sl_no + "_" + img_name + pick_no + ".jpeg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (st_img_list.size() != 0) {
                Log.e("pick_2",st_img_list.size()+" size");
                if (pick_edit == 1) {
                    st_img_list.set(0,FileProvider.getUriForFile(pick_image_2.this, BuildConfig.APPLICATION_ID + ".provider", new File(dir.getPath(), sl_no + "_" + img_name + pick_no + ".jpeg")) + "");
                }else if (pick_edit == 2){
                    st_img_list.set(1,FileProvider.getUriForFile(pick_image_2.this, BuildConfig.APPLICATION_ID + ".provider", new File(dir.getPath(), sl_no + "_" + img_name + pick_no + ".jpeg")) + "");
                }else {
                    st_img_list.add(FileProvider.getUriForFile(pick_image_2.this, BuildConfig.APPLICATION_ID + ".provider", new File(dir.getPath(), sl_no + "_" + img_name + pick_no + ".jpeg")) + "");
                }
            } else {
                img_list.add(FileProvider.getUriForFile(pick_image_2.this, BuildConfig.APPLICATION_ID + ".provider", new File(dir.getPath(), sl_no + "_" + img_name + pick_no + ".jpeg")));
            }
        }else {
            if (st_img_list.size() != 0) {
//                        img_add_3.setImageURI(uri);
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_3.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                Log.e("pick_2",st_img_list.size()+" size");
                if (pick_edit == 1) {
                    st_img_list.set(0,getCaptureImageOutputUri() + "");
                }else if (pick_edit == 2){
                    st_img_list.set(1,getCaptureImageOutputUri() + "");
                }else {
                    st_img_list.add(getCaptureImageOutputUri() + "");
                }
            } else {
                Log.e("pick_2",img_list.size()+" size");
                img_list.add(getCaptureImageOutputUri());
            }
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
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
        } else if (view.getId() == R.id.imageView1) {
            pick_no = 1;
            startActivityForResult(getPickImageChooserIntent(), 200);
        } else if (view.getId() == R.id.imageView2) {
            pick_no = 2;
            startActivityForResult(getPickImageChooserIntent(), 200);
        } else if (view.getId() == R.id.image_close_1) {
            img_add_1.setImageResource(R.drawable.add);
            img_close_1.setVisibility(View.GONE);
            pick_no = 1;
            pick_edit = 1;
            startActivityForResult(getPickImageChooserIntent(), 200);
//            if (from.equals("m_list") || from.equals("g_list") || from.equals("o_list") || from.equals("u_list")) {
//                img_list.remove(0);
//            } else if (from.equals("m_list_edit") || from.equals("g_list_edit") || from.equals("o_list_edit") || from.equals("u_list_edit")) {
//                st_img_list.remove(0);
//                Log.e("pick_2",st_img_list.size()+" size at 0");
//            }
        } else if (view.getId() == R.id.image_close_2) {
            img_add_2.setImageResource(R.drawable.add);
            img_close_2.setVisibility(View.GONE);
            pick_no = 2;
            pick_edit = 2;
            startActivityForResult(getPickImageChooserIntent(), 200);
//            if (from.equals("m_list") || from.equals("g_list") || from.equals("o_list") || from.equals("u_list")) {
//                img_list.remove(1);
//            } else if (from.equals("m_list_edit") || from.equals("g_list_edit") || from.equals("o_list_edit") || from.equals("u_list_edit")) {
//                st_img_list.remove(1);
//                Log.e("pick_2",st_img_list.size()+" size at 1");
//            }
        } else if (view.getId() == R.id.l_save_image) {
            dialog.setMessage("Saving data.....");
            dialog.show();
            Bitmap bitmap = null;
            try {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
                bitmap = bitmapDrawable.getBitmap();
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (from) {
                case "m_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
//                        img_add_3.setImageURI(img_list.get(m_s));
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_3.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("master_" + (m_s + 1), img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("master_len", img_list.size() + "");
                    sessionsave.save_one("master_" + position, img_list.size()+"");
                    sessionsave.save_one("master_key_" + position, "img_" + key);
                    break;
                case "g_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
//                        img_add_3.setImageURI(img_list.get(m_s));
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_3.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("general_" + (m_s + 1), img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("general_len", img_list.size() + "");
                    sessionsave.save_two("general_" + position, img_list.size()+"");
                    sessionsave.save_two("general_key_" + position, "img_" + key);
                    break;
                case "o_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
//                        img_add_3.setImageURI(img_list.get(m_s));
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_3.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("observe_" + (m_s + 1), img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("observe_len", img_list.size() + "");
                    sessionsave.save_three("observe_" + position, img_list.size()+"");
                    sessionsave.save_three("observe_key_" + position, "img_" + key);
                    break;
                case "u_list":
                    for (int m_s = 0; m_s < img_list.size(); m_s++) {
//                        img_add_3.setImageURI(img_list.get(m_s));
//                        BitmapDrawable bitmapDrawable1 = ((BitmapDrawable) img_add_3.getDrawable());
//                        Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                        sessionsave.save_image("usp_" + (m_s + 1), img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("usp_len", img_list.size() + "");
                    sessionsave.save_four("usp_" + position, img_list.size()+"");
                    sessionsave.save_four("usp_key_" + position, "img_" + key);
                    break;
                case "m_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("master_" + (m_s + 1), st_img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("master_len", (st_img_list.size()) + "");
                    sessionsave.save_four("master_" + position, st_img_list.size()+"");
                    sessionsave.save_four("master_key_" + position, "img_" + key);
                    break;
                case "g_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("general_" + (m_s + 1), st_img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("general_len", st_img_list.size() + "");
                    sessionsave.save_two("general_" + position, st_img_list.size() + "");
                    sessionsave.save_two("general_key_" + position, "img_" + key);
                    break;
                case "o_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("observe_" + (m_s + 1), st_img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("observe_len", st_img_list.size() + "");
                    sessionsave.save_three("observe_" + position, st_img_list.size() + "");
                    sessionsave.save_three("observe_key_" + position, "img_" + key);
                    break;
                case "u_list_edit":
                    for (int m_s = 0; m_s < st_img_list.size(); m_s++) {
                        sessionsave.save_image("usp_" + (m_s + 1), st_img_list.get(m_s)+"");
                    }
                    sessionsave.save_image("usp_len", st_img_list.size() + "");
                    sessionsave.save_four("usp_" + position, st_img_list.size() + "");
                    sessionsave.save_four("usp_key_" + position, "img_" + key);
                    break;
            }
            dialog.cancel();
            finish();
        }
    }

//    private String getStringImage(Bitmap bitmap) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] imageBytes = baos.toByteArray();
//            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "not null";
//        }
//    }
//
//    private Bitmap St_to_img(String s) {
//        byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        return decodedByte;
//    }
}
