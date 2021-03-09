package com.app.alg.judix.telaMandado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.alg.judix.R;
import com.app.alg.judix.model.DispositivoBluetooth;
import com.app.alg.judix.model.Mandado;

import java.util.List;

/**
 * Created by lucas on 05/02/16.
 */
public class ItemBluetoothAdapter extends BaseAdapter {
    private Context ctx;
    private List<DispositivoBluetooth> dispositvos;


    public ItemBluetoothAdapter(Context ctx, List<DispositivoBluetooth> dispositivos) {
        this.ctx = ctx;
        this.dispositvos = dispositivos;

    }




    @Override
    public int getCount() {
        return this.dispositvos.size();
    }

    @Override
    public Object getItem(int position) {
        return dispositvos.get(position);
    }

    @Override
    public long getItemId(int position) {return Long.parseLong(dispositvos.get(position).getId());}
    /*


    public long getItemId(int position) {
        return Long.parseLong(certidao.get(position).getId());
    }

     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        final DispositivoBluetooth p = dispositvos.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_bluetooth_lista, null);

            holder = new ViewHolder();

            holder.txtNomeDispositivoBluetooth = (TextView) convertView.findViewById(R.id.txtNomeDispositivoBluetooth);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNomeDispositivoBluetooth.setText(p.getNome());


        /*holder.btnAcaoMandado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "ID: "+p.getMAN_ID(), Toast.LENGTH_SHORT).show();
                Toast.makeText(ctx, "NumProce: "+p.getMAN_Numero_Processo(), Toast.LENGTH_SHORT).show();








            }
        });*/


        /*holder.btnMapaMandado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //Intent intent = new Intent(ctx, MapaMandadoActivity.class);
                //ctx.startActivity(intent);

            }
        });*/

        return convertView;


    }


    // usado por conta de performance
    static class ViewHolder {
        TextView txtNomeDispositivoBluetooth;
    }












}
