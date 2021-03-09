package com.app.alg.judix.telaNotificacaoMandado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.alg.judix.R;
import com.app.alg.judix.model.Notificacao;

import java.util.List;

/**
 * Created by lucas on 05/02/16.
 */
public class ItemTodasNotificacaoAdapter extends BaseAdapter {
    private Context ctx;
    private List<Notificacao> notificiacao;

    public ItemTodasNotificacaoAdapter(Context ctx, List<Notificacao> notificiacao) {
        this.ctx = ctx;
        this.notificiacao = notificiacao;
    }

    @Override
    public int getCount() {
        return this.notificiacao.size();
    }

    @Override
    public Object getItem(int position) {
        return notificiacao.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(notificiacao.get(position).getNOT_ID());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        final Notificacao p = notificiacao.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_todas_notificaco_lista, null);

            holder = new ViewHolder();
            holder.txtNumeroMandadoTodasNotificacao = (TextView) convertView.findViewById(R.id.txtNumeroMandadoTodasNotificacao);
            holder.txtTodasNotificacoDataEncontro = (TextView) convertView.findViewById(R.id.txtTodasNotificacoDataEncontro);
            holder.txtTodasNotificacoHoraEncontro = (TextView) convertView.findViewById(R.id.txtTodasNotificacoHoraEncontro);

            holder.imageViewNotificacao = (ImageView) convertView.findViewById(R.id.imageViewNotificacao);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String a = p.getMAN_Numero_Processo();

        holder.txtNumeroMandadoTodasNotificacao.setText(p.getMAN_Numero_Processo().toString());
        holder.txtTodasNotificacoDataEncontro.setText(p.getNOT_DataEncontro().toString());
        holder.txtTodasNotificacoHoraEncontro.setText(p.getNOT_HoraEncontro().toString());

        holder.imageViewNotificacao.setImageResource(R.mipmap.ic_action_shield);

        //if(p.getTipo().equals("N") ){
            //holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_negativa_1);
            //holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_negativa_2);
        //}else{
            //holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_positiva_1);
            //holder.imageViewTipoCertidao.setImageResource(R.mipmap.ic_action_certidao_positiva_2);
        //}

        return convertView;


    }


    // usado por conta de performance
    static class ViewHolder {
        TextView txtNumeroMandadoTodasNotificacao;
        TextView txtTodasNotificacoDataEncontro;
        TextView txtTodasNotificacoHoraEncontro;
        ImageView imageViewNotificacao;
    }












}
