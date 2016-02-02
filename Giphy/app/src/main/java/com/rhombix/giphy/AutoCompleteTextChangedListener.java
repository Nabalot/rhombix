package com.rhombix.giphy;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

/**
 * Created by lorddct on 2/2/16.
 */
public class AutoCompleteTextChangedListener implements TextWatcher
{
        Context context;

        public AutoCompleteTextChangedListener(Context context){
            this.context = context;
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence userInput, int start, int before, int count) {

            GiphyActivity mainActivity = ((GiphyActivity) context);

            // query the database based on the user input
            mainActivity.item = mainActivity.getItemsFromDb(userInput.toString());

            // update the adapater
            mainActivity.myAdapter.notifyDataSetChanged();
            mainActivity.myAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
            mainActivity.autoComplete.setAdapter(mainActivity.myAdapter);

        }
}
