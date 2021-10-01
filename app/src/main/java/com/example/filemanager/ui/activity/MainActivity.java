package com.example.filemanager.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.filemanager.BuildConfig;
import com.example.filemanager.R;
import com.example.filemanager.databinding.ActivityMainBinding;
import com.example.filemanager.ui.data.FileData;
import com.example.filemanager.ui.adapter.FileItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public interface OnClickListener {
        void onClick(String directory, int position);

        void onClick(String fileName);
    }

    //권한 요청 코드
    private final int PERMISSION_REQUEST_CODE = 9999;
    // 권한 리스트
    private final String[] mPermissionList = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private String mRootPath = "sdcard";
    private ArrayList<FileData> mCurrentFileList;
    private ActivityMainBinding mBinding;
    private FileItemAdapter fileItemAdapter;
    private int oldPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ActivityCompat.requestPermissions(this, mPermissionList, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // 다이얼로그로 바꾸는게 좋을 것 같음
                Toast.makeText(this, "파일 및 미디어 권한을 허용해주세요", Toast.LENGTH_LONG).show();
                goSettingPermission();
                finish();
            } else {
                init();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mRootPath.length() > "sdcard".length()) {
            mRootPath = mRootPath.substring(0, mRootPath.lastIndexOf("/"));
            Log.e("after back", mRootPath);
            setFileList();
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        initData();
    }

    /**
     * 초기 데이터 세팅
     */
    private void initData() {
        mCurrentFileList = new ArrayList<>();
        fileItemAdapter = new FileItemAdapter(mCurrentFileList);
        mBinding.recyclerview.setAdapter(fileItemAdapter);
        fileItemAdapter.onClickListener = new OnClickListener() {
            @Override
            public void onClick(String directory, int position) {
                mRootPath = mRootPath + "/" + directory;
                setFileList();
                oldPosition = position;
            }

            @Override
            public void onClick(String fileName) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(mRootPath + "/" + fileName);

                Uri uri= FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".provider",file);
                String fileExtend = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
                if (fileExtend.equalsIgnoreCase("txt")||fileExtend.equalsIgnoreCase("html")) {
                    intent.setDataAndType(uri, "text/*");
                } else if (fileExtend.equalsIgnoreCase("mp3")) {
                    intent.setDataAndType(uri, "audio/*");
                } else if (fileExtend.equalsIgnoreCase("mp4")) {
                    intent.setDataAndType(uri, "video/*");
                } else if (fileExtend.equalsIgnoreCase("jpg") || fileExtend.equalsIgnoreCase("jpeg") ||
                        fileExtend.equalsIgnoreCase("gif") || fileExtend.equalsIgnoreCase("png") ||
                        fileExtend.equalsIgnoreCase("bmp")) {
                    intent.setDataAndType(uri, "image/*");
                }else if (fileExtend.equalsIgnoreCase("doc")||fileExtend.equalsIgnoreCase("docx")) {
                    intent.setDataAndType(uri, "application/msword");
                }else if (fileExtend.equalsIgnoreCase("xls")||fileExtend.equalsIgnoreCase("xlsx")) {
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                }else if (fileExtend.equalsIgnoreCase("ppt")||fileExtend.equalsIgnoreCase("pptx")) {
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                }else if(fileExtend.equalsIgnoreCase("pdf")){
                    intent.setDataAndType(uri, "application/pdf");
                }else if(fileExtend.equalsIgnoreCase("hwp")){
                    intent.setDataAndType(uri, "application/hwp");
                }else if(fileExtend.equalsIgnoreCase("zip")){
                    intent.setDataAndType(uri,"application/zip");
                }
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        };
        setFileList();
    }

    /**
     * set File List
     */
    private void setFileList() {
        File file = new File(mRootPath);
        if (file.listFiles() == null) {
            Toast.makeText(this, "can't open folder", Toast.LENGTH_SHORT).show();
            mRootPath = mRootPath.substring(0, mRootPath.lastIndexOf("/"));
            return;
        }
        mCurrentFileList.clear();
        fileItemAdapter.notifyDataSetChanged();
        if (file.listFiles().length == 0) {
            mCurrentFileList.add(new FileData(0, "", true));
        } else {
            for (File files : file.listFiles()) {
                if (files.isDirectory()) {
                    if (!files.getName().contains(".")) {
                        mCurrentFileList.add(setFileIcon(files.getName()));
                    }
                }
            }
            Collections.sort(mCurrentFileList, new Comparator<FileData>() {
                @Override
                public int compare(FileData fileData, FileData t1) {

                    return fileData.getFileName().compareTo(t1.getFileName());
                }
            });
            ArrayList<FileData> tmpList = new ArrayList<>();
            for (File files : Objects.requireNonNull(file.listFiles())) {
                if (files.isFile()) {
                    tmpList.add(setFileIcon(files.getName()));
                }
            }
            Collections.sort(tmpList, new Comparator<FileData>() {
                @Override
                public int compare(FileData fileData, FileData t1) {

                    return fileData.getFileName().compareTo(t1.getFileName());
                }
            });
            for (FileData data : tmpList) {
                mCurrentFileList.add(data);
            }

            fileItemAdapter.notifyDataSetChanged();
            ((LinearLayoutManager) mBinding.recyclerview.getLayoutManager()).scrollToPositionWithOffset(oldPosition, mBinding.recyclerview.getWidth() / 2 + mBinding.recyclerview.getLayoutManager().getWidth() / 2);
//            mBinding.recyclerview.scrollToPosition(oldPosition);
        }
    }

    /**
     * set file icon & fileData
     *
     * @param name   
     * @return
     */
    private FileData setFileIcon(String name) {
        if (name.contains(".txt") || name.contains(".pdf") || name.contains(".xlsx") || name.contains(".html")) {
            return new FileData(R.drawable.documents_24dp, name, false);
        } else if (name.contains(".jpg") || name.contains(".jpeg") || name.contains(".png") || name.contains(".gif")) {
            return new FileData(R.drawable.image_24dp, name, false);
        } else {
            if (name.contains(".")) {
                return new FileData(R.drawable.file_24dp, name, false);
            } else {
                //폴더
                return new FileData(R.drawable.folder_24dp, name, false);
            }
        }
    }

    /**
     * 앱 설정 창으로 이동
     */
    private void goSettingPermission() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}