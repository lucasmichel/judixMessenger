package com.app.alg.judix.telaArquivosAnexos;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.app.alg.judix.R;
import com.app.alg.judix.telaMandado.MandadoActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;

import java.io.File;

public class viewPdfArquivoAnexo extends AppCompatActivity {


    Integer pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf_arquivo_anexo);

        Bundle bundle = getIntent().getExtras();

        //viewPDF.putExtra("arquivoPDF", nomeArquivo);
        //viewPDF.putExtra("idMandado", mandadoEscolhido.getMAN_ID());

        String nomeArquivoAnexo = bundle.getString("ARQUIVO_PDF");
        String idMandado = bundle.getString("ID_MANDADO");

        setTitle(nomeArquivoAnexo);

        //String pdfile = (File) i.getParcelableExtra("arquivoPDF");

        //Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+mandadoEscolhido.getMAN_ID()+".pdf";

        //String a = i.getParcelableExtra("arquivoPDF");

        File pdfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+idMandado+File.separator+nomeArquivoAnexo);

        if(pdfile.exists()){
            PDFView pdfView = (PDFView) findViewById(R.id.pdfViewAnexo);

            pdfView.fromFile(pdfile)
                    .password(null) // if password protected, then write password
                    .defaultPage(0) // set the default page to open
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageNumber = page;
                        }
                    }).onPageError(
                            new OnPageErrorListener() {
                                @Override
                                public void onPageError(int page, Throwable t) {
                                    Toast.makeText(viewPdfArquivoAnexo.this,"Erro pagina "+page+t.getMessage(), Toast.LENGTH_LONG).show();

                                }
                    })
                    .onError(
                            new OnErrorListener() {
                                public void onError(Throwable t) {
                                    //loadContentListener.onLoadFinished(false);
                                    t.printStackTrace();
                                }
                            }
                    )
                    .load();
        }




    }
}
