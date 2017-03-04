package uk.co.cdevelop.fabvocab.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Chris on 04/03/2017.
 */

public class EditTextWithImeEvents extends EditText {

    EditTextWithImeEventsBackListener mOnImeBackListener = null;
    public EditTextWithImeEvents(Context context) {
        super(context);
    }

    public EditTextWithImeEvents(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextWithImeEvents(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextWithImeEvents(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if(event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mOnImeBackListener != null) {
                mOnImeBackListener.onImeBack();
            }
        }

        return super.onKeyPreIme(keyCode, event);
    }
    
    public void setOnEditTextBackListener(EditTextWithImeEventsBackListener listener) {
        this.mOnImeBackListener = listener;
    }
}

