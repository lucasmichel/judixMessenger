package com.app.alg.judix.telaCertidoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.alg.judix.R;
import com.app.alg.judix.model.Certidao;

import java.util.List;

/**
 * Created by lucas on 05/02/16.
 */
public class ItemCertidaoAdapter extends BaseAdapter {
    private Context ctx;
    private List<Certidao> certidao;

    public ItemCertidaoAdapter(Context ctx, List<Certidao> certidao) {
        this.ctx = ctx;
        this.certidao = certidao;
    }

    @Override
    public int getCount() {
        return this.certidao.size();
    }

    @Override
    public Object getItem(int position) {
        return certidao.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(certidao.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        final Certidao p = certidao.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_cetidao_lista, null);

            holder = new ViewHolder();
            holder.txtNomeCertidao = (TextView) convertView.findViewById(R.id.txtNomeCertidao);

            holder.imageViewTipoCertidao = (ImageView) convertView.findViewById(R.id.imageViewTipoCertidao);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtNomeCertidao.setText(p.getNome());

        if(p.getTipo().equals("N") ){
            holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_negativa_1);
            //holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_negativa_2);
        }else{
            holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_positiva_1);
            //holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_positiva_2);
        }

        return convertView;


    }


    // usado por conta de performance
    static class ViewHolder {
        TextView txtNomeCertidao  ;
        ImageView imageViewTipoCertidao;
    }












}
