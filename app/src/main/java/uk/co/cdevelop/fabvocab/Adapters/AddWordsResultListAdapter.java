package uk.co.cdevelop.fabvocab.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

import uk.co.cdevelop.fabvocab.DataModels.WordDefinition;
import uk.co.cdevelop.fabvocab.R;
import uk.co.cdevelop.fabvocab.Views.AddWordsResultsView;

/**
 * Created by Chris on 10/03/2017.
 */

public class AddWordsResultListAdapter  extends ArrayAdapter<WordDefinition> {

    private final AddWordsResultsView parent;

    public AddWordsResultListAdapter(Context context, ArrayList<WordDefinition> items, AddWordsResultsView parent) {
        super(context, -1, items);
        this.parent = parent;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroupParent) {
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflator.inflate(R.layout.addwordsresult_definitionrow, parent, false);

        CheckBox cbDefinition = (CheckBox) rowView.findViewById(R.id.cb_definition);
        final String definition = ((WordDefinition) getItem(position)).getDefinition();
        cbDefinition.setText(definition);

        if(this.parent != null) {
            if(parent.definitionExists(definition)) {
                cbDefinition.setEnabled(false);
                cbDefinition.setChecked(true);
            } else if(parent.isChecked(definition)) {
                cbDefinition.setChecked(true);
            }

            cbDefinition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        parent.addDefinitionForSaving(definition);
                    } else {
                        parent.removeDefinitionForSaving(definition);
                    }

                }
            });
        }

        return rowView;
    }

}
