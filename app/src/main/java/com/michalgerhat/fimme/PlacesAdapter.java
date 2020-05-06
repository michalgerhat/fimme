package com.michalgerhat.fimme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

// https://www.codeproject.com/Tips/1204611/How-To-Make-The-ListView-Aware-Of-Data-Changes

public class PlacesAdapter extends BaseAdapter
{
    public interface IDeleteButtonListener
    {
        void OnButtonClickListener(int position, LocationObject place);
    }

    private ArrayList<LocationObject> data;
    private static LayoutInflater inflater = null;
    private IDeleteButtonListener deleteButtonListener;

    PlacesAdapter (Context context, ArrayList<LocationObject> data)
    {
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
            v = inflater.inflate(R.layout.place, null);

        TextView name = v.findViewById(R.id.place_name);
        name.setText(data.get(position).displayName);

        ImageView deleteButton = (ImageView)v.findViewById(R.id.place_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtonListener.OnButtonClickListener(position, data.get(position));
            }
        });

        return v;
    }

    void setDeleteButtonListener(IDeleteButtonListener listener)
    {
        this.deleteButtonListener = listener;
    }
}
