package com.notbytes.barcode_reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class BarcodeReaderActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {
    public static String KEY_CAPTURED_BARCODE = "key_captured_barcode";
    public static String KEY_CAPTURED_RAW_BARCODE = "key_captured_raw_barcode";
    private static final String KEY_AUTO_FOCUS = "key_auto_focus";
    private static final String KEY_USE_FLASH = "key_use_flash";
    protected static final String KEY_USE_CROPPED_FRAME = "key_use_cropped_frame";
    protected static final String KEY_USE_EVENT_BUS = "key_use_event_bus";
    private boolean autoFocus = false;
    private boolean useFlash = false;
    protected boolean useCroppedFrame = false;
    protected boolean useEventBus = false;
    private BarcodeReaderFragment mBarcodeReaderFragment;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        floatingActionButton = findViewById(R.id.fab_change_flash_state);
        final Intent intent = getIntent();
        if (intent != null) {
            autoFocus = intent.getBooleanExtra(KEY_AUTO_FOCUS, false);
            useFlash = intent.getBooleanExtra(KEY_USE_FLASH, false);
            useCroppedFrame = intent.getBooleanExtra(KEY_USE_CROPPED_FRAME, false);
            useEventBus = intent.getBooleanExtra(KEY_USE_EVENT_BUS, false);
        }
        mBarcodeReaderFragment = attachBarcodeReaderFragment();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useFlash = !useFlash;
                toggleUseFlash();
            }
        });
    }

    private void toggleUseFlash() {
        if (useFlash) {
            mBarcodeReaderFragment.setUseFlash(useFlash);
            floatingActionButton.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.ic_flash_state_on));
        } else {
            mBarcodeReaderFragment.setUseFlash(useFlash);
            floatingActionButton.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.ic_flash_state_off));
        }
    }

    private BarcodeReaderFragment attachBarcodeReaderFragment() {
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        BarcodeReaderFragment fragment = BarcodeReaderFragment.newInstance(autoFocus, useFlash, useCroppedFrame);
        fragment.setListener(this);
        fragmentTransaction.replace(R.id.fm_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        return fragment;
    }

    public static Intent getLaunchIntent(Context context, boolean autoFocus, boolean useFlash, boolean useCroppedFrame, boolean useEventBus) {
        Intent intent = new Intent(context, BarcodeReaderActivity.class);
        intent.putExtra(KEY_AUTO_FOCUS, autoFocus);
        intent.putExtra(KEY_USE_FLASH, useFlash);
        intent.putExtra(KEY_USE_CROPPED_FRAME, useCroppedFrame);
        intent.putExtra(KEY_USE_CROPPED_FRAME, useEventBus);
        return intent;
    }

    @Override
    public void onScanned(Barcode barcode) {
        if (mBarcodeReaderFragment != null) {
            mBarcodeReaderFragment.pauseScanning();
        }
        if (barcode != null) {
            if (useEventBus) {
                EventBus.getDefault().postSticky(barcode.rawValue);
            } else {
                Intent intent = new Intent();
                intent.putExtra(KEY_CAPTURED_BARCODE, barcode);
                intent.putExtra(KEY_CAPTURED_RAW_BARCODE, barcode.rawValue);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }
}
