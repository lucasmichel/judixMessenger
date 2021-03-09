package com.app.alg.judix.telaMandado;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class PrintRodapeMandadoAdpter extends PrintDocumentAdapter {

    Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    public int totalpages = 0;
    String caminhoArquivoInterno;

    public PrintRodapeMandadoAdpter(Context context, String caminhoArquivo){
        this.context = context;
        this.caminhoArquivoInterno = caminhoArquivo;
    }


    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle bundle) {

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);



        ////totalpages = myPdfDocument.getPages().size();

        pageHeight =
                newAttributes.getMediaSize().getHeightMils()/1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils()/1000 * 72;

        if (cancellationSignal.isCanceled() ) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                .Builder("print_output.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT);

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);


            /*if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }*/

    }

    @Override
    public void onWrite(PageRange[] pageRanges,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {

            //ATENCAO AQI
            //File pdfile = new File(caminhoDGO);
            //String caminhoPrint = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+caminhoDGO;
            String caminhoPrint = caminhoArquivoInterno;
            File pdfile = new File(caminhoPrint );

            input = new FileInputStream(pdfile );
            output = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (FileNotFoundException ee){
            //Catch exception
        } catch (Exception e) {
            //Catch exception
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
