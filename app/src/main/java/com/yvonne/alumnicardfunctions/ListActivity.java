package com.yvonne.alumnicardfunctions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ListActivity extends AppCompatActivity {

    private TableLayout tblLayout;
    public static final String NAME = "EVENT_NAME";
    public static final String LOCATION = "EVENT_LOCATION";
    public static final String RADIUS = "EVENT_RADIUS";
    public static final String SHARE_PREFERENCE = "SHARE_PREFERENCE";
    public static final String EVENT_NUMBER = "EVENT_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        tblLayout = findViewById(R.id.Table_Layout);


//        TableRow row= new TableRow(this);
//        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//        row.setLayoutParams(lp);
//        checkBox = new CheckBox(this);
//        tv = new TextView(this);
//        addBtn = new ImageButton(this);
//        addBtn.setImageResource(R.drawable.add);
//        minusBtn = new ImageButton(this);
//        minusBtn.setImageResource(R.drawable.minus);
//        qty = new TextView(this);
//        checkBox.setText("hello");
//        qty.setText("10");
//        row.addView(checkBox);
//        row.addView(minusBtn);
//        row.addView(qty);
//        row.addView(addBtn);
//        ll.addView(row,i);
//







        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE);
        int eventNumber = sharedPref.getInt(EVENT_NUMBER,0) + 1;


    }

    public void checkLocation(View view){
        Intent intent = new Intent(this, UserLocationActivity.class);
        startActivity(intent);
    }

}
