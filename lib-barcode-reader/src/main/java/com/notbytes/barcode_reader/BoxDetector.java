package com.notbytes.barcode_reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.io.ByteArrayOutputStream;

public class BoxDetector extends Detector {
    private static final String TAG = "BoxDetector";
    private Detector mDelegate;
    private Rect croppingRect;

    public BoxDetector(Detector delegate) {
        mDelegate = delegate;
    }

    public void setCroppingRect(Rect rect) {
        this.croppingRect = rect;
    }

    public SparseArray detect(Frame frame) {
        Frame croppedFrame = this.croppingRect == null ? frame
                : new Frame.Builder()
                .setBitmap(crop(frame))
                .setRotation(Frame.ROTATION_90)
                .build();

        return mDelegate.detect(croppedFrame);
    }

    private Bitmap crop(Frame frame) {
        int width = frame.getMetadata().getWidth();
        int height = frame.getMetadata().getHeight();

        Rect rect = croppingRect.right > width || croppingRect.bottom > height ?
                rotateRectClockwise(croppingRect)
                : croppingRect;

        if (rect.width() <= 0 || rect.height() <= 0) {
            return frame.getBitmap();
        }

        try {
            YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(rect, 100, byteArrayOutputStream);
            byte[] jpegArray = byteArrayOutputStream.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);
            return bitmap;
        } catch(Exception e) {
            return frame.getBitmap();
        }
    }

    @NonNull
    private Rect rotateRectClockwise(Rect rect) {
        return new Rect(rect.top, rect.left, rect.bottom, rect.right);
    }


    public boolean isOperational() {
        return mDelegate.isOperational();
    }

    public boolean setFocus(int id) {
        return mDelegate.setFocus(id);
    }
}