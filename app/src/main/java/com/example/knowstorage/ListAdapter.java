/*Esta clase hace una lista de Tests
* donde se ve su password y nombre*/
package com.example.knowstorage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Layer;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Test> {
    private List<Test> listaModelo;
    private Context mContext;
    private int resourceLayout;
    public ListAdapter(@NonNull Context context, int resource, List<Test> objects) {
        super(context, resource, objects);
        this.listaModelo=objects;
        this.mContext=context;
        this.resourceLayout=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        if(view==null)
            view= LayoutInflater.from(mContext).inflate(resourceLayout,null);//elformato de cada item
        Test test=listaModelo.get(position);

        TextView nombreTest=view.findViewById(R.id.textNombreTest);
        nombreTest.setText("Nombre Test:"+test.getNombreTest());
        TextView passwordTest=view.findViewById(R.id.textPasswordTest);
        passwordTest.setText("Password Test"+test.getPasswordTest());

        return view;
    }
}
