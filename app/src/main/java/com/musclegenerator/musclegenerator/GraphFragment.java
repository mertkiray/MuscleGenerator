package com.musclegenerator.musclegenerator;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends DialogFragment {


    String deneme = "";

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_graph, container, false);


Bundle bundle = this.getArguments();



        @SuppressWarnings("unchecked")
            ArrayList< ArrayList<String> > onedaymap = ( ArrayList< ArrayList<String> > ) bundle.getSerializable("map");

        DataPoint[] values = new DataPoint[onedaymap.size()];
        ArrayList<Date> datelist = new ArrayList<>();

        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");


        Log.d("ONE DAY MAP SIZE ",onedaymap.size()+"");

        for(int i = 0;i<onedaymap.size();i++){

            String time = onedaymap.get(i).get(0);
            String count = onedaymap.get(i).get(1);

            Date date = new Date();
            try {
                date = formatter.parse(time);
                datelist.add(date);
                values[i] = new DataPoint(date,Integer.parseInt(count));
                Log.d("date",date+"");
                Log.d("COUNT: ",Integer.valueOf(count)+"");
            }catch (Exception e){e.printStackTrace();}

        }



        /*
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
*/

        GraphView graph = (GraphView) view.findViewById(R.id.graph);

// you can directly pass Date objects to DataPoint-Constructor
// this will convert the Date to double via Date#getTime()

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(values);




        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });


        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);

        graph.addSeries(series);


// set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
       graph.getViewport().setMinX(datelist.get(0).getTime());
        graph.getViewport().setMaxX(datelist.get(datelist.size()-1).getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setYAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
        // enable scaling and scrolling
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);


        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
    }


    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    GraphFragment.this.dismiss();
                    // handle back button

                    return true;

                }

                return false;
            }
        });
    }




}
