package com.connectrail.projects.kwiklink.ui.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.connectrail.projects.kwiklink.R;
import com.connectrail.projects.kwiklink.entities.ServiceCategory;

import java.util.List;

/**
 * Created by root on 9/26/17.
 */

public class ServiceCategoryAdapter extends ArrayAdapter<ServiceCategory> {

    private List<ServiceCategory> serviceCategories;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View view;

    private IOnServiceCategoryClick serviceCategoryClick;


    public ServiceCategoryAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public ServiceCategoryAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }
    public ServiceCategoryAdapter(Context context, List<ServiceCategory> serviceCategories, IOnServiceCategoryClick categoryClick) {
        super(context, R.layout.layout_text);

        mLayoutInflater = LayoutInflater.from(context);
        this.serviceCategories = serviceCategories;
        mContext = context;
        serviceCategoryClick = categoryClick;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getCount() {
        return serviceCategories.size();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        if(view == null)
            view = mLayoutInflater.inflate(R.layout.layout_text, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.tv_text);

        final ServiceCategory serviceCategory = serviceCategories.get(position);
        textView.setText(serviceCategory.getName());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serviceCategoryClick != null)
                    serviceCategoryClick.onClick(serviceCategory);
            }
        });

        return view;
    }
    public interface IOnServiceCategoryClick {
        void onClick(ServiceCategory serviceCategory);
    }
}
