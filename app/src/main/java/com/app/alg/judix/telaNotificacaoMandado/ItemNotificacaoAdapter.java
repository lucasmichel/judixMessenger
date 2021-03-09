package com.app.alg.judix.telaNotificacaoMandado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.alg.judix.R;
import com.app.alg.judix.model.Certidao;
import com.app.alg.judix.model.Notificacao;

import java.util.List;

/**
 * Created by lucas on 05/02/16.
 */
public class ItemNotificacaoAdapter extends BaseAdapter {
    private Context ctx;
    private List<Notificacao> notificiacao;

    public ItemNotificacaoAdapter(Context ctx, List<Notificacao> notificiacao) {
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_notificaco_lista_2, null);

            holder = new ViewHolder();
            holder.txtNomeNotificacaooi = (TextView) convertView.findViewById(R.id.txtDescricaoNotificacao);

            holder.imageViewNotificao = (ImageView) convertView.findViewById(R.id.imageViewNotificacao);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String txtNome="";
        if(p.getNOT_Endereco().length()>0){
            txtNome = p.getNOT_Endereco();
            if(p.getNOT_DataEncontro().length()>0){
                txtNome += " - "+p.getNOT_DataEncontro()+" - "+p.getNOT_HoraEncontro();
            }
        }else{
            txtNome = p.getNOT_ID();
        }
        holder.txtNomeNotificacaooi.setText(txtNome);


        holder.imageViewNotificao.setImageResource(R.mipmap.ic_action_mandado_normal);

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
        TextView txtNomeNotificacaooi  ;
        ImageView imageViewNotificao;
    }












}
