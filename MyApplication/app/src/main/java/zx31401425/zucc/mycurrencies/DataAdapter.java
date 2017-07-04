package zx31401425.zucc.mycurrencies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 赵轩 on 2017/7/3.
 */

public class DataAdapter extends ArrayAdapter<Data> {
    private int resourceId;

    public DataAdapter (Context context,int textViewResourceId,List<Data> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Data data = getItem(position);
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        TextView foreginname = (TextView) view.findViewById(R.id.data_foregin);
        TextView homename = (TextView) view.findViewById(R.id.data_home);
        TextView num = (TextView) view.findViewById(R.id.data_num);
        TextView forenum = (TextView) view.findViewById(R.id.data_forenum);
        foreginname.setText(data.getForegin());
        homename.setText(data.getHome());
        num.setText(String.valueOf(data.getNum()));
        forenum.setText(String.valueOf(data.getForenum()));
        return view;
    }
}
