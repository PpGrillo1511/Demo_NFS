package com.example.demonfs;

import android.content.Context;
import android.text.Layout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private ArrayList<Device> devices;
    private Context context;
    private static LayoutInflater inflater;
    public ListAdapter(Context context,ArrayList<Device>devices){
        this.context=context;
        this.devices=devices;
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View fila;
        fila = inflater.inflate(R.layout.view_item, null);
        holder.tvName=(TextView) fila.findViewById(R.id.tv_name);
        holder.tvAddress=(TextView)fila.findViewById(R.id.tv_address);
        holder.ivDevice=(ImageView) fila.findViewById(R.id.img_device);
        holder.tvAddress.setText(devices.get(position).getAddress());
        holder.ivDevice.setImageResource(devices.get(position).getImage());
        holder.tvName.setText(devices.get(position).getName());
        return fila;
    }
}
