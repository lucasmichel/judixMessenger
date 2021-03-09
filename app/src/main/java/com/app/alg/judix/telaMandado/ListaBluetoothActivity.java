package com.app.alg.judix.telaMandado;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.alg.judix.R;
import com.app.alg.judix.model.Certidao;
import com.app.alg.judix.model.DispositivoBluetooth;
import com.app.alg.judix.model.Mandado;
import com.app.alg.judix.telaCertidoes.CertidaoDoMandado;
import com.app.alg.judix.telaCertidoes.ListaCertidoes;
import com.google.gson.Gson;

//import org.vudroid.core.DecodeServiceBase;
//import org.vudroid.pdfdroid.codec.PdfContext;
//import org.vudroid.pdfdroid.codec.PdfPage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ListaBluetoothActivity extends AppCompatActivity {


    private Mandado mandadoEscolhido;


    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;


    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private boolean stopWorker;
    private int readBufferPosition;
    private byte[] readBuffer;
    private Thread  workerThread;
    private Set<BluetoothDevice> pairedDevices;

    ListView mainListViewDispositivos = null;

    private ArrayList<DispositivoBluetooth> listaDispositivosBluetooth = new ArrayList<DispositivoBluetooth>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bluetooth);


        //identifica o mandado
        SharedPreferences pref1 = getSharedPreferences("PreferenciaJudix", 0);
        Gson gsonMandados = new Gson();
        String jsonMandado = pref1.getString("objectMandado", "");
        String assinaturaOficial = pref1.getString("USU_Assinatura", "");
        String nomeOficial = pref1.getString("USU_Nome", "");
        String CPFOficial = pref1.getString("USU_CPF", "");


        this.mandadoEscolhido = gsonMandados.fromJson(jsonMandado, Mandado.class);


        this.findBT();


    }















    private void findBT() {

        try {

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


            if (mBluetoothAdapter == null) {
                //myLabel.setText("No bluetooth adapter available");
                Toast.makeText(ListaBluetoothActivity.this, "Bluetooth não encontrado", Toast.LENGTH_LONG).show();
                // startActivity(new Intent(MainActivity.this,NewAct.class));
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
                Toast.makeText(ListaBluetoothActivity.this, "Ativando o bluetooth.", Toast.LENGTH_LONG).show();



                //startActivity(new Intent(MainActivity.this,NewAct.class));
            }




            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();


            if (pairedDevices.size() > 0) {
                listdata();







                for (BluetoothDevice device : pairedDevices) {
















                    // MP300 is the name of the bluetooth printer device
                    //L52 nome
                    //if (device.getName().equals(NewAct.printer)) {
                    if (device.getName().equals("L52 BT Printer")) {
                        //openBT();
                        mmDevice = device; //ATENÇÂO TIRA DAQUI E COLOCAR NO CLICK DO ITEM EXCOLHIDO
                        break;
                    }
                    else {

                    }
                }
            }


            //myLabel.setText("Bluetooth Device Found");
            try {
                // Standard SerialPortService ID
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();

                beginListenForData();

                //myLabel.setText("Bluetooth Opened");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            this.openBT();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            //myLabel.setText("Bluetooth Opened");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * After opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                //myLabel.setText(data);
                                                String a = data.toString();
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();











        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //public ArrayList<String> listdata(ListView lv){
    public void listdata(){
        try{
            pairedDevices = mBluetoothAdapter.getBondedDevices();

            int contador=0;
            for(BluetoothDevice bt : pairedDevices) {
                contador++;

                DispositivoBluetooth obj = new DispositivoBluetooth( String.valueOf(contador) , bt.getName());

                this.listaDispositivosBluetooth.add(obj);

            }


            mainListViewDispositivos = (ListView) findViewById(R.id.listViewDispositivosBluetooth);
            final ItemBluetoothAdapter adapterMandado = new ItemBluetoothAdapter(ListaBluetoothActivity.this, listaDispositivosBluetooth);
            mainListViewDispositivos.setAdapter(adapterMandado);
            adapterMandado.notifyDataSetChanged();


            mainListViewDispositivos.setClickable(true);
            mainListViewDispositivos.setLongClickable(true);


            mainListViewDispositivos.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                    // Get the info on which item was selected
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                    int posicao = info.position;

                    DispositivoBluetooth dispositivo = new DispositivoBluetooth();
                    dispositivo = listaDispositivosBluetooth.get(posicao);
                    //nomeDispositivoBluetooth = listaDispositivosBluetooth.contains(info.position);

                    //com o nome do dispositivo escolhido vrifica se é uma impressora , se for manda gerar as imagens e imprimir
                    //se não for retorna uma msgm informando que o dispositivo escolhido nao é uma impressora..

                    Toast.makeText(ListaBluetoothActivity.this, "Impressora escolhida: "+dispositivo.getNome(), Toast.LENGTH_LONG).show();

                }





            });

            mainListViewDispositivos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    return false;
                }
            });

            //return list;
            /*Toast.makeText(getApplicationContext(),"Showing Paired Devices",
                    Toast.LENGTH_SHORT).show();
            @SuppressWarnings("unchecked")
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this,android.R.layout.simple_list_item_1, list);
            lv.setAdapter(adapter);*/
        }catch(Exception e)
        {
            Toast.makeText(this, "error"+e, Toast.LENGTH_LONG).show();
        }

        //return null;
    }

    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            //myLabel.setText("Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }








    /*private void criarImagemPDF(){

        DecodeServiceBase decodeService = new DecodeServiceBase(new PdfContext());
        decodeService.setContentResolver(this.getContentResolver());




        //Create Folder
        //File folder = new File(Environment.getExternalStorageDirectory().toString()+"/ImagesPDF");
        //folder.mkdirs();

        //Save the path as a string value
        //String extStorageDirectory = folder.toString();

        //Create New file and name it Image2.PNG
        //File file = new File(extStorageDirectory, "Image2.PNG");

        //File pdf = new File(String.valueOf(Environment.getExternalStorageDirectory())+"/filename.pdf" );

        //File pdf = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID()+File.separator+mandadoEscolhido.getMAN_ID()+".pdf");
        //File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Judix"+File.separator+mandadoEscolhido.getMAN_ID());

// a bit long running
        decodeService.open(Uri.fromFile(pdf));

        int pageCount = decodeService.getPageCount();
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = (PdfPage) decodeService.getPage(i);
            RectF rectF = new RectF(0, 0, 1, 1);

            int PHOTO_WIDTH_PIXELS=2480;
            int PHOTO_HEIGHT_PIXELS=3508;


            // do a fit center to 1920x1080
            double scaleBy = Math.min(PHOTO_WIDTH_PIXELS / (double) page.getWidth(), //
                    PHOTO_HEIGHT_PIXELS / (double) page.getHeight());
            int with = (int) (page.getWidth() * scaleBy);
            int height = (int) (page.getHeight() * scaleBy);

            // you can change these values as you to zoom in/out
            // and even distort (scale without maintaining the aspect ratio)
            // the resulting images

            // Long running
            Bitmap bitmap = page.renderBitmap(with, height, rectF);

            try {

                File outputFile = new File(folder, System.currentTimeMillis() + "IIMAGEM.jpg");
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                // a bit long running
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                outputStream.close();
            } catch (IOException e) {
                Toast.makeText(ListaBluetoothActivity.this,e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                //LogWrapper.fatalError(e);
            }
        }

    }*/

}
