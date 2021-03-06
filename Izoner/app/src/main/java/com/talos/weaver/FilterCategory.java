package com.talos.weaver;

import android.widget.Filter;

import com.talos.weaver.AdaptaterBook.AdaptaterCategory;
import com.talos.weaver.ModelBook.ModelCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {

    ArrayList<ModelCategory> filterList;
    AdaptaterCategory adaptaterCategory;


    public FilterCategory(ArrayList<ModelCategory> filterList, AdaptaterCategory adaptaterCategory) {
        this.filterList = filterList;
        this.adaptaterCategory = adaptaterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels = new ArrayList<>();

            for(int i=0; i<filterList.size(); i++){
                if (filterList.get(i).getCategory().toUpperCase().contains(constraint)){
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adaptaterCategory.categoryArrayList = (ArrayList<ModelCategory>)results.values;

        adaptaterCategory.notifyDataSetChanged();

    }
}
