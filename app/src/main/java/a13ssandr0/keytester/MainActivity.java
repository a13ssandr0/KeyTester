package a13ssandr0.keytester;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ArrayList <String> listp = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    ListView mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mylist = findViewById(R.id.pressedBtn);
        String savedList = getPreferences(Context.MODE_PRIVATE).getString("KeyList", "");
        if (savedList!=null && !savedList.equals("")) {
            Collections.addAll(listp, savedList.split(","));
            listUpdate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
        The core of the application
        Every time a key is pressed and triggers KeyEvent.ACTION_DOWN,
        we get the KeyCode and add it to a list
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int keyCode = event.getKeyCode();
        if (event.getAction()==KeyEvent.ACTION_DOWN){
            // keyCodeToString was addded in API level 12
            // in all the previous APIs we can only get an integer representing the keyCode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) listp.add(KeyEvent.keyCodeToString(keyCode));
            else listp.add(String.valueOf(keyCode));
            listUpdate();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) return super.dispatchKeyEvent(event);
        return true;
    }

    private void listUpdate(){
        final KeyArrayAdapter adapter = new KeyArrayAdapter(this, listp);
        mylist.setAdapter(adapter);
        mylist.setSelection(adapter.getCount() - 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*case R.id.settings: // not implemented yet
                break;
            */
            case R.id.clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.confirmClear)
                        .setPositiveButton(R.string.save, dialogClickListener)
                        .setNegativeButton(R.string.clear, dialogClickListener)
                        .setNeutralButton(R.string.cancel, dialogClickListener).show();
                break;
            case R.id.export:
                saveFile();
                break;
            case R.id.share:
                startActivity(Intent.createChooser(new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, stringArrayListToString(listp,System.getProperty("line.separator")))
                        .setType("text/plain"), null));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveFile(){
        if (listp.isEmpty()) { Toast.makeText(this, R.string.emptyFile, Toast.LENGTH_LONG).show(); }
        else {
            //In order to give the file a unique, but yet understandable, name, we will use the scheme
            // "KeyTester_YYYY_MM_DD_hh_mm_ss.txt"
            Calendar dt = Calendar.getInstance();
            String filename = "KeyTester_" + dt.get(Calendar.YEAR) + "_" + dt.get(Calendar.MONTH) + "_" + dt.get(Calendar.DAY_OF_MONTH) + "_" + dt.get(Calendar.HOUR_OF_DAY) + "_" + dt.get(Calendar.MINUTE) + "_" + dt.get(Calendar.SECOND) + ".txt";

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                //this method was deprecated in API level 29, however the new method below works only from API level 19
                //we will use this method for devices down to APi 1, saving the file in a predefined directory
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File path = new File(Environment.getExternalStorageDirectory() + File.separator + "KeyTester");
                    final File file = new File(path, filename);
                    if (!path.exists()) path.mkdir(); //we don't need its result, so we suppress IDE warnings
                    try {
                        FileOutputStream fos = new FileOutputStream(file, true);
                        for (int i = 0; i < listp.size(); i++) {
                            fos.write(listp.get(i).getBytes());
                            fos.write(System.getProperty("line.separator").getBytes());
                        }
                        fos.close();
                        //A snackbar will tell us where the file was saved
                        Snackbar.make(findViewById(R.id.myCoordinatorLayout), getString(R.string.savedIn) + file.toString(), Snackbar.LENGTH_LONG)
                                .setAction(R.string.open, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_EDIT);
                                        Uri fileURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                                        intent.setDataAndType(fileURI, "text/plain").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        startActivity(intent);
                                    }
                                }).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.fileNotFound, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.savingError, Toast.LENGTH_LONG).show();
                    }
                } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(this, R.string.fsMountedReadOnly, Toast.LENGTH_LONG).show();
                } else if (Environment.MEDIA_UNKNOWN.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(this, R.string.fsUnknown, Toast.LENGTH_LONG).show();
                } else if (Environment.MEDIA_UNMOUNTABLE.equals(Environment.getExternalStorageState()) || Environment.MEDIA_UNMOUNTED.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(this, R.string.fsNotMounted, Toast.LENGTH_LONG).show();
                }
            } else { //this new method allows the user to select where to save the file
                startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                .addCategory(Intent.CATEGORY_OPENABLE)
                                .setType("text/plain")
                                .putExtra(Intent.EXTRA_TITLE, filename)
                        /*.putExtra(DocumentsContract.EXTRA_INITIAL_URI,uri)*/
                        , 1);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                FileOutputStream fos = (FileOutputStream) getContentResolver().openOutputStream(data.getData());
                for (int i = 0; i < listp.size(); i++) {
                    fos.write(listp.get(i).getBytes());
                    fos.write(System.getProperty("line.separator").getBytes());
                }
                fos.close();
                //A snackbar will tell us where the file was saved
                Snackbar.make(findViewById(R.id.myCoordinatorLayout), getString(R.string.savedIn) + data.getData().toString(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.open, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_EDIT);
                                intent.setDataAndType(data.getData(), "text/plain").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);
                            }
                        }).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.fileNotFound, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.savingError, Toast.LENGTH_LONG).show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    //respond to clear button
    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    saveFile();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    listp.clear();
                    listUpdate();
                    Toast.makeText(getBaseContext(), R.string.cleared, Toast.LENGTH_SHORT).show();
                    break;
                case DialogInterface.BUTTON_NEUTRAL: //Do nothing
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        //Don't exit if the button is pressed once, so it can be captured and displayed;
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.pressBackAgain, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { doubleBackToExitPressedOnce=false; }
        }, 2000);
    }

    public String stringArrayListToString(ArrayList<String> arrayList, String separator){
        StringBuilder outputString = new StringBuilder();
        for (String item : arrayList) {
            if (!outputString.toString().equals("")) outputString.append(separator);
            outputString.append(item);
        }
        return outputString.toString();
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("KeyList", stringArrayListToString(listp, ","));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) editor.apply();
        else editor.commit();
        super.onStop();
    }
}