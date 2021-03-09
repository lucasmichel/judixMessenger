package com.app.alg.judix.telaArquivosAnexos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.alg.judix.R;
import com.app.alg.judix.model.ArquivoAnexoMandado;
import com.app.alg.judix.model.AssinaturaMandado;

import java.util.List;

/**
 * Created by lucas on 05/02/16.
 */
public class ItemArquivoAnexoMandadoAdapter extends BaseAdapter {
    private Context ctx;
    private List<ArquivoAnexoMandado> arquivo;

    public ItemArquivoAnexoMandadoAdapter(Context ctx, List<ArquivoAnexoMandado> arquivo) {
        this.ctx = ctx;
        this.arquivo = arquivo;
    }




    @Override
    public int getCount() {
        return this.arquivo.size();
    }

    @Override
    public Object getItem(int position) {
        return arquivo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(arquivo.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        final ArquivoAnexoMandado p = arquivo.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_arq_anexos_mandado_lista, null);

            holder = new ViewHolder();
            holder.txtNomeArquivo = (TextView) convertView.findViewById(R.id.nomeArquivoAnexo);
            holder.imageViewArquivoAnexoMandado = (ImageView) convertView.findViewById(R.id.imageViewArquivoAnexoMandado);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String[] lista = p.getEndereco().split("/");
        holder.txtNomeArquivo.setText(lista[lista.length-1]);
        holder.imageViewArquivoAnexoMandado.setImageResource(R.mipmap.ic_action_document);


        /*if(p.getAgente().equals("Destinatário")){
            holder.imageViewAssinaturaMandado.setImageResource(R.mipmap.ic_action_certidao_negativa_1);
        }else if(p.getAgente().equals("Oficial de Justiça")){
            holder.imageViewAssinaturaMandado.setImageResource(R.mipmap.ic_action_requisitar);
        }else{
            holder.imageViewAssinaturaMandado.setImageResource(R.mipmap.ic_action_assinatura);
        }*/

        return convertView;


    }


    // usado por conta de performance
    static class ViewHolder {
        /*TextView txtEnderecoArquivo;*/
        TextView txtNomeArquivo;
        ImageView imageViewArquivoAnexoMandado;
    }












}
