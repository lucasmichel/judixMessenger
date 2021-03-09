package com.app.alg.judix.telaMandado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.alg.judix.R;
import com.app.alg.judix.model.Mandado;

import java.util.List;

/**
 * Created by lucas on 05/02/16.
 */
public class ItemMandadoAdapter extends BaseAdapter {
    private Context ctx;
    private List<Mandado> mandado;
    private String listarPor;

    public ItemMandadoAdapter(Context ctx, List<Mandado> mandado, String listarPor) {
        this.ctx = ctx;
        this.mandado = mandado;
        this.listarPor = listarPor;
    }




    @Override
    public int getCount() {
        return this.mandado.size();
    }

    @Override
    public Object getItem(int position) {
        return mandado.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mandado.get(position).getMAN_ID());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        final Mandado p = mandado.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_mandado_lista, null);

            holder = new ViewHolder();
            holder.txtIdMandado = (TextView) convertView.findViewById(R.id.txtIdMandado);
            holder.txtNumeroMandado = (TextView) convertView.findViewById(R.id.txtNumeroMandado);

            holder.txtDestinatario = (TextView) convertView.findViewById(R.id.txtDestinatario);
            holder.txtMedidaJudicial = (TextView) convertView.findViewById(R.id.txtMedidaJudical);
            holder.txtDataMandado = (TextView) convertView.findViewById(R.id.txtDatamandado);
            holder.txtBairroMandado = (TextView) convertView.findViewById(R.id.txtBairroMandado);
            holder.txtPontoReferenciaMandado = (TextView) convertView.findViewById(R.id.txtPontoReferenciaMandado);

            /*holder.btnAcaoMandado = (ImageButton) convertView.findViewById(R.id.btnAcaoMandado);
            holder.btnMapaMandado = (ImageButton) convertView.findViewById(R.id.btnMapaMandado);
            holder.btnAgendamento = (ImageButton) convertView.findViewById(R.id.btnAgendamento);*/


                holder.imageViewPrioridadeMandado = (ImageView) convertView.findViewById(R.id.imageViewPrioridadeMandado);
                holder.imageViewIntimadoSecretaria = (ImageView) convertView.findViewById(R.id.imageViewIntimadoSecretaria);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtIdMandado.setText("Id: "+p.getMAN_ID());
        holder.txtNumeroMandado.setText(p.getMAN_Numero_Processo());
        holder.txtDestinatario.setText("Destinatário: "+p.getMAN_Destinatario());
        holder.txtMedidaJudicial.setText("Medida: "+p.getMAN_MedidaJudicial());
        holder.txtDataMandado.setText("Distribuição: "+p.getMAN_DataHoraCadastro());


        if(p.getMAN_FoneAutor().length()>1){
            holder.txtBairroMandado.setText("Bairro: "+p.getMAN_Bairro()+" Autor: "+p.getMAN_FoneAutor());
        }else{
            holder.txtBairroMandado.setText("Bairro: "+p.getMAN_Bairro());
        }

        String complemento = "";
        if(this.listarPor.equals("concluido")){
            complemento = "\n Certidão: "+p.getHIM_DataHoraCadastro();
        }
        holder.txtPontoReferenciaMandado.setText(" Ref.: "+p.getEND_PontoReferencia()+complemento);

        //holder.imageViewIntimadoSecretaria.setVisibility(View.INVISIBLE);
        if(p.getMAN_intimadoSecretaria().equals("s")){
            holder.imageViewIntimadoSecretaria.setImageResource(R.mipmap.ic_action_assinatura);
        }else{
            holder.imageViewIntimadoSecretaria.setVisibility(View.INVISIBLE);
        }




        if(!this.listarPor.equals("concluido")) {
            holder.imageViewPrioridadeMandado.setVisibility(View.VISIBLE);
            switch (p.getMAN_Prioridade().toString()) {
                case "N":  holder.imageViewPrioridadeMandado.setImageResource(R.mipmap.ic_action_mandado_normal);
                    break;
                case "M":  holder.imageViewPrioridadeMandado.setImageResource(R.mipmap.ic_action_mandado_moderado);
                    break;
                case "U":  holder.imageViewPrioridadeMandado.setImageResource(R.mipmap.ic_action_mandado_urgente);
                    break;
                case "P":  holder.imageViewPrioridadeMandado.setImageResource(R.mipmap.ic_action_mandado_plantao);
                    break;
                default: holder.imageViewPrioridadeMandado.setImageResource(R.mipmap.ic_action_mandado_plantao);
                    break;
            }
        }else{
            holder.imageViewPrioridadeMandado.setVisibility(View.INVISIBLE);
        }

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
        //ImageView imgLogo;
        TextView txtIdMandado, txtNumeroMandado, txtDestinatario, txtMedidaJudicial, txtDataMandado, txtBairroMandado,  txtPontoReferenciaMandado ;
        //ImageButton btnAcaoMandado, btnMapaMandado, btnAgendamento;
        ImageView imageViewPrioridadeMandado, imageViewIntimadoSecretaria;


    }












}
