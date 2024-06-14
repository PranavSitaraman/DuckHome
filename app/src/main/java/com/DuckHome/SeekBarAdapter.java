package com.DuckHome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class SeekBarAdapter extends ArrayAdapter<Integer> {
    List<Integer> data;
    List<String> titles;
    int resource;
    Controlling inside;

    SeekBarAdapter(Context context, int layoutResource, List<Integer> data, List<String> titles, Controlling inside) {
        super(context, layoutResource, data);
        this.data = data;
        this.titles = titles;
        this.resource = layoutResource;
        this.inside = inside;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Integer value = data.get(position);
        String title = titles.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, null);
        }
        SeekBar sb = convertView.findViewById((R.id.seekBars));
        sb.setProgress(value);
        TextView sensorvalue = convertView.findViewById(R.id.textView);
        TextView titlevalue = convertView.findViewById(R.id.title);
        sensorvalue.setText("" + value);
        titlevalue.setText(title);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean wasInitiatedByUser) {
                sensorvalue.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar

                final String nameString = titlevalue.getText().toString();
                final String messageString = sensorvalue.getText().toString();

                inside.sendBT(nameString, messageString);
            }
        });

        return convertView;
    }
}