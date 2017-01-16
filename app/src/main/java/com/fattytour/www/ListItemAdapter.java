package com.fattytour.www;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Junjie on 10/01/2017.
 */

public class ListItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListItem> item;

    public ListItemAdapter() {
        super();
    }

    public ListItemAdapter(Context context, ArrayList<ListItem> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return this.item.size();
    }

    @Override
    public Object getItem(int position) {
        return this.item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(item.get(position).getType() == "section"){
            convertView = inflater.inflate(R.layout.list_section, parent, false);
            ListSection listSection = (ListSection) item.get(position);
            TextView sectionTitle = (TextView) convertView.findViewById(R.id.sectionTitle);
            sectionTitle.setText(listSection.getTitle());
        }else if(item.get(position).getType() == "item"){
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            final ListNormalItem listItem = (ListNormalItem) item.get(position);
            TextView listItemTitle = (TextView) convertView.findViewById(R.id.listItemTitle);
            listItemTitle.setMinWidth(parent.getWidth() * 20 / 100);
            TextView listItemDetail = (TextView) convertView.findViewById(R.id.listItemDetail);
            listItemDetail.setMaxWidth(parent.getWidth() * 80 / 100);
            listItemTitle.setText(listItem.getTitle());
            listItemDetail.setText(listItem.getDetail());
        }
        return convertView;
    }
}

interface ListItem{
    public String getType();
}

class ListSection implements ListItem {
    private final String type = "section";
    private String title = "";

    public ListSection(){}

    public ListSection(String title){
        this.title = title;
    }

    public String getType(){
        return this.type;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }
}

class ListButton implements ListItem{
    private final String type = "button";
    private String[] btnTitles;
    private ArrayList<Button> buttons;

    public String getType(){
        return this.type;
    }

    public String[] getBtnTitle(){
        return this.btnTitles;
    }

    public ListButton(Context context, String btnTitle){
        this(context, new String[]{btnTitle});
    }

    public ListButton(Context context, String[] btnTitles){
        this.buttons = new ArrayList<>();
        this.btnTitles = btnTitles;
        for (String btnTitle: btnTitles) {
            Button button = new Button(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight = 1/btnTitles.length;
            button.setText(btnTitle);
            button.setLayoutParams(lp);
            this.buttons.add(button);
        }
    }
}

class ListNormalItem implements ListItem {
    private final String type = "item";
    private String id;
    private String title = "";
    private String detail = "";

    public ListNormalItem(String id){
        this.id = id;
    }

    public ListNormalItem(String id, String title){
        this.title = title;
        this.id = id;
    }

    public ListNormalItem(String id, String title, String detail){
        this.id = id;
        this.title = title;
        this.detail = detail;
    }

    @Override
    public String getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail){
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
