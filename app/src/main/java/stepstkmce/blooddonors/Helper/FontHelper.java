package stepstkmce.blooddonors.Helper;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

public class FontHelper {
    Context context;
    Typeface PTbold;
    Typeface PTreg;


    public FontHelper(Context context) {
        this.context=context;
        PTbold=Typeface.createFromAsset(context.getAssets(),"fonts/ptbold.ttf");
        PTreg=Typeface.createFromAsset(context.getAssets(),"fonts/ptreg.ttf");
    }

    public void setTypeFaceBtn(Button button,String type) {
        if (type.equals("PTreg")){
            button.setTypeface(PTreg);
        }
        else {
            button.setTypeface(PTbold);
        }
    }

    public void setTypeFaceText(TextView textView, String type) {
        if (type.equals("PTreg")){
            textView.setTypeface(PTreg);
        }
        else {
            textView.setTypeface(PTbold);
        }
    }

}
