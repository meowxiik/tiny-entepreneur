package com.mewu.plazastar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mewu.plazastar.utils.LoadSave;

public class Story {

    public static int Level;

    private static int counter;

    public static void Update(MainActivity activity){

        switch (Level){
            case 0:
                Level = -1;
                createDialog(activity, "Welcome to Tiny Entrepreneur!", "Ok!", (dialogInterface, i) -> Level = 1);
                LoadSave.SaveStory(1);
                break;
            case 1:
                counter++;
                if (counter == 2) {
                    createDialog(activity, "Tap on your store to earn money!", "Ok!", (dialogInterface, i) -> Level = 2);
                    LoadSave.SaveStory(2);
                    Level = -1;
                    counter = 0;
                }
                break;
            case 2:
                if (GameState.Instance.Money >= 20){
                    createDialog(activity, "Now you have enough money and can expand!", "Ok!", (dialogInterface, i) -> Level = 3);
                    LoadSave.SaveStory(3);
                    Level = -1;
                }
                break;
            case 3:
                counter++;
                if (counter == 2) {
                    createDialog(activity, "Tap on the icon of the bulldozer in the bottom right screen!", "Ok!", (dialogInterface, i) -> Level = 4);
                    LoadSave.SaveStory(4);
                    Level = 4;
                    counter = 0;
                }
                break;
            case 4:
                if (activity.BuildMode){
                    createDialog(activity, "Well done! Now select where you want your new building and which one!", "Ok!", (dialogInterface, i) -> Level = 5);
                    LoadSave.SaveStory(5);
                    Level = -1;
                }
                break;
            case 5:
                if (GameState.Instance.MapSize >= 2){
                    createDialog(activity, "Excellent! Now you make twice the money!", "Ok!", (dialogInterface, i) -> Level = 6);
                    LoadSave.SaveStory(6);
                    Level = -1;
                }
                break;
        }
    }

    private static void createDialog(Context context, String text, String ok, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setNegativeButton(ok, listener);
        builder.create();
        builder.setCancelable(false);
        builder.show();
    }
}
