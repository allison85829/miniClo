package com.example.miniclo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;

public class PopUpClass extends AppCompatActivity {

    //PopupWindow display method
    private Button buttonAction;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);
    }

    public void showPopupWindow(final View view, String item_key) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        message = popupView.findViewById(R.id.titleText);
        message.setText("Do you want to delete the item?");
        buttonAction = popupView.findViewById(R.id.action_btn);
        buttonAction.setText("Delete");
        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //As an example, display the message
                Toast.makeText(view.getContext(), item_key, Toast.LENGTH_SHORT).show();
                // Delete Item here - Need key
                // close the window
                popupWindow.dismiss();

                // go back to laundry or closet page
                Navigation.createNavigateOnClickListener(R.id.layout_laundry, null);
//                Navigation.findNavController(view).navigate(R.id.layout_laundry);
//                Intent intent = new Intent(PopUpClass.this, BottomNavFragmentLaundry.class);
//                startActivity(intent);
            }
        });

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void setButtonText(String btn_txt) {
        buttonAction.setText(btn_txt);
    }

    public void setMessageText(String msg_txt) {
        message.setText(msg_txt);
    }
}
