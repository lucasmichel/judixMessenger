package com.app.alg.judix.telaAssinaturasMandado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.alg.judix.R;
import com.app.alg.judix.model.AssinaturaMandado;

import java.util.List;

/**
 * Created by lucas on 05/02/16.
 */
public class ItemAssinaturaMandadoAdapter extends BaseAdapter {
    private Context ctx;
    private List<AssinaturaMandado> assinatura;

    public ItemAssinaturaMandadoAdapter(Context ctx, List<AssinaturaMandado> assinatura) {
        this.ctx = ctx;
        this.assinatura = assinatura;
    }




    @Override
    public int getCount() {
        return this.assinatura.size();
    }

    @Override
    public Object getItem(int position) {
        return assinatura.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(assinatura.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        final AssinaturaMandado p = assinatura.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_assinatura_mandado_lista, null);

            holder = new ViewHolder();
            holder.txtNomeAssinaturaMandado = (TextView) convertView.findViewById(R.id.nomeAssinaturaMandado);
            holder.txtTipoAssinaturaMandado = (TextView) convertView.findViewById(R.id.tipoAssinaturaMandado);
            holder.txtTipoDocumentoAssinaturaMandado = (TextView) convertView.findViewById(R.id.tipoDocumentoAssinaturaMandado);
            holder.imageViewAssinaturaMandado = (ImageView) convertView.findViewById(R.id.imageViewTipoCertidao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNomeAssinaturaMandado.setText(p.getNome());

        holder.txtTipoAssinaturaMandado.setText(p.getAgente());

        holder.txtTipoDocumentoAssinaturaMandado.setText(p.getTipoDocumento()+": "+p.getNumeroDocumento());

        if(p.getAgente().equals("Destinatário")){
            holder.imageViewAssinaturaMandado.setImageResource(R.mipmap.ic_action_assinatura_destinatario );
        }else if(p.getAgente().equals("Oficial de Justiça")){
            holder.imageViewAssinaturaMandado.setImageResource(R.mipmap.ic_action_assinatura_oficial_justica);
        }else{
            holder.imageViewAssinaturaMandado.setImageResource(R.mipmap.ic_action_assinatura_parte_relacionada);
        }

        return convertView;


    }


    // usado por conta de performance
    static class ViewHolder {
        TextView txtNomeAssinaturaMandado;
        TextView txtTipoAssinaturaMandado;
        TextView txtTipoDocumentoAssinaturaMandado;
        ImageView imageViewAssinaturaMandado;
    }












}
