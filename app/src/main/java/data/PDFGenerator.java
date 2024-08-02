package data;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.b07demosummer2024.DBOperation;
import com.example.b07demosummer2024.Item;
import com.example.b07demosummer2024.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

// Todo: implement interface for this class to conform OCP
public class PDFGenerator {
    Fragment curFrag;
    private int pageHeight = 792;
    private int pageWidth = 612;
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

        drawAllItems(pdfDocument, list, descImgOnly).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), ".temp.pdf");
                try {
                    pdfDocument.writeTo(new FileOutputStream(file));
                    PrintManager printManager = (PrintManager) curFrag.getContext().getSystemService(Context.PRINT_SERVICE);
                    PrintDocumentAdapter printAdapter = new PdfPrint(curFrag.getContext(),
                            file.getAbsolutePath(), getPDFFileName());
                    printManager.print("Document", printAdapter, null);

                    Toast.makeText(curFrag.getContext(), "PDF file generated successfully.",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(curFrag.getContext(), "Failed to generate PDF file.",
                            Toast.LENGTH_SHORT).show();
                }
                pdfDocument.close();
                curFrag.getParentFragmentManager().popBackStack();
            }
        });
    }

    private CompletableFuture<Void> drawAllItems(PdfDocument pdfDocument,
                                                 List<Item> list, boolean descImgOnly) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (Item item : list) {
            future = future.thenCompose(avoid -> setUpPage(pdfDocument, item, descImgOnly));
        }
        return future;
    }

    // Todo: handling video file type accordingly
    private CompletableFuture<Void> setUpPage(PdfDocument pdfDocument, Item item,
                                             boolean descImgOnly) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        ref.child(item.getMediaLink()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String contentType = storageMetadata.getContentType();
                String fileType = getFileType(contentType);
                try {
                    File tempFile = File.createTempFile("media", fileType);

                    ref.child(item.getMediaLink()).getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth,
                                    pageHeight, 1).create();
                            PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
                            Canvas canvas = myPage.getCanvas();

                            if (contentType.startsWith("image/")) {
                                bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                            } else {
                                bmp = getVideoThumbnail(tempFile);
                            }

                            scaledbmp = Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false);
                            drawPage(canvas, item, descImgOnly);

                            pdfDocument.finishPage(myPage);
                            future.complete(null);

                            tempFile.delete();

                        }
                    });
                } catch (Exception e) {
                    System.out.println(fileType);
                    System.out.println("File not created");
                }
            }
        });

        return future;
    }

    private void drawPage(Canvas canvas, Item item, boolean descImgOnly) {
        int descImgHeight = 60;

        if (!descImgOnly) {
            descImgHeight = 200;

            drawText(canvas, item.getName(), pageWidth / 2, 60, 25, true,
                    true);
            drawText(canvas, "Period: " + item.getPeriod(), pageWidth / 2, 85,
                    13, true, true);
            drawText(canvas, "Category: " + item.getCategory(), pageWidth / 2, 105,
                    13, true, true);
        }

        canvas.drawBitmap(scaledbmp, 30, descImgHeight, new Paint());
        drawText(canvas, item.getDescription(), 250, descImgHeight, 15,
                false, false);
    }

    private void drawText(Canvas canvas, String text, int x, int y, int fontSize, boolean isBold,
                          boolean isCentered) {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        paint.setColor(ContextCompat.getColor(curFrag.getContext(), R.color.black));
        paint.setTextSize(fontSize);
        if (isCentered) {
            paint.setTextAlign(Paint.Align.CENTER);
        }
        paint.setFakeBoldText(isBold);

        List<String> textList = splitTextIntoLines(text,  45);
        for (String txt: textList) {
            canvas.drawText(txt, x, y, paint);
            y += 20;
        }
    }

    private List<String> splitTextIntoLines(String text, int max_width) {
        List<String> strlist = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder curLine = new StringBuilder();
        for (String word : words) {
            if (curLine.length() + word.length() > max_width) {
                strlist.add(curLine.toString());
                curLine = new StringBuilder();
            }
            curLine.append(word).append(" ");
        }
        if (curLine.length() > 0) {
            strlist.add(curLine.toString());
        }
        return strlist;
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

    private String getFileType(String contentType) {
        return contentType.substring(contentType.indexOf("/") + 1);
    }

    private Bitmap getVideoThumbnail(File file) {
        MediaMetadataRetriever retriver = new MediaMetadataRetriever();
        retriver.setDataSource(file.getAbsolutePath());

        return retriver.getFrameAtTime(0);
    }
}
