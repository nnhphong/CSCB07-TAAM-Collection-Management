package com.example.b07demosummer2024;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class PDFGenerator {
    Fragment curFrag;
    private int pageHeight = 400;
    private int pageWidth = 700;
    private int imgHeight = 200;
    private int imgWidth = 180;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Bitmap bmp, scaledbmp;
    StorageReference ref;

    public PDFGenerator(Fragment curFrag) {
        this.curFrag = curFrag;
    }

    public void createReportPDF(List<Item> list, boolean descImgOnly) {
        ref = FirebaseStorage.getInstance("gs://cscb07-taam-management.appspot.com").getReference();
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        drawAllItems(pdfDocument, paint, list).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), getPDFFileName());

                try {
                    pdfDocument.writeTo(new FileOutputStream(file));
                    Toast.makeText(curFrag.getContext(), "PDF file generated successfully.",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(curFrag.getContext(), "Failed to generate PDF file.",
                            Toast.LENGTH_SHORT).show();
                }
                System.out.println("Im here");
                pdfDocument.close();
            }
        });
    }

    private CompletableFuture<Void> drawAllItems(PdfDocument pdfDocument, Paint paint, List<Item> list) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (Item item : list) {
            future = future.thenCompose(avoid -> drawItem(pdfDocument, paint, item));
        }
        return future;
    }

    private CompletableFuture<Void> drawItem(PdfDocument pdfDocument, Paint paint, Item item) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        ref.child(item.getMediaLink()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, 1).create();
                PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
                Canvas canvas = myPage.getCanvas();
                InputStream inputStream = new ByteArrayInputStream(bytes);
                bmp = BitmapFactory.decodeStream(inputStream);
                scaledbmp = Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false);

                if (scaledbmp != null) {
                    canvas.drawBitmap(scaledbmp, 30, 80, paint);
                } else {
                    Log.e("generatePDF", "Bitmap is null. Skipping bitmap drawing.");
                }

                drawText(canvas, item.getName(), pageWidth / 2, 30, 25, true);
                drawText(canvas, "Period: " + item.getPeriod(), pageWidth / 2, 45, 13, true);
                drawText(canvas, "Category: " + item.getCategory(), pageWidth / 2, 60, 13, true);
                drawText(canvas, item.getDescription(), pageWidth / 2 + 2, 90, 15, false);

                pdfDocument.finishPage(myPage);
                future.complete(null);
            }
        });
        return future;
    }

    private void drawText(Canvas canvas, String text, int x, int y, int fontSize, boolean isBold) {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        paint.setColor(ContextCompat.getColor(curFrag.getContext(), R.color.black));
        paint.setTextSize(fontSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(isBold);
        canvas.drawText(text, x, y, paint);
    }

    private String getPDFFileName() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formattedDateTime = sdf.format(now);
        return formattedDateTime + "_report.pdf";
    }

    public boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(curFrag.getContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(curFrag.getContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(curFrag.getActivity(), new String[]{WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
}
