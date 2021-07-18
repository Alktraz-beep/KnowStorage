package com.example.knowstorage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapterAudios extends ArrayAdapter<Audio> {
    private List<Audio> listaAudios;
    private Context context;
    private int resourceLayout;
    public ListAdapterAudios(@NonNull Context context, int resource, List<Audio> objects) {
        super(context, resource, objects);
        this.listaAudios=objects;
        this.context=context;
        this.resourceLayout=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        if(view==null)
            view= LayoutInflater.from(context).inflate(resourceLayout,null);//elformato de cada item
        Audio audio=listaAudios.get(position);
        /**********los datos a mostrar por row**************/
        TextView nombreAlumno=view.findViewById(R.id.nombreAlumnoAudio);
        nombreAlumno.setText("Nombre alumno: "+audio.getNombreAlumno());
        TextView califAudio=view.findViewById(R.id.calificacionAlumnoAudio);
        califAudio.setText("Calificación: "+audio.getCalificacion());

        return view;
    }
}
