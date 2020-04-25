package cu.uci.cmfmovil.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.activeandroid.ActiveAndroid;
import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.modules.data.Comment;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.data.Profile;
import cu.uci.cmfmovil.modules.sync.Utils;

/**
 * Created by tesis on 5/3/16.
 */
public class CmfUtils {

    //public static final String URL_BASE = "http://cmf.uci.cu/";

    private static NumberFormat formatter = new DecimalFormat("#0.00");
    private static Security sec = new Security();
    private static Calendar cal = Calendar.getInstance();
    private static String months[] = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
    public static boolean isEditablePasswordPreference;
    public static SharedPreferences.OnSharedPreferenceChangeListener passListener;
    private static Context context;
    private static ArrayList<MediaPlayer> sounds = new ArrayList<>();

    public static Context getContext() {
        return context;
    }

    public static void init(Context context) {
        CmfUtils.context = context;
        sounds.add(MediaPlayer.create(context, R.raw.ans_right));
        sounds.add(MediaPlayer.create(context, R.raw.ans_right2));
        sounds.add(MediaPlayer.create(context, R.raw.ans_right3));
        sounds.add(MediaPlayer.create(context, R.raw.ans_wrong));
        sounds.add(MediaPlayer.create(context, R.raw.ans_wrong2));
    }

    public static void play(int soundIndex) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext());
        if (prefs.getBoolean("soundOn", true)) {
            sounds.get(soundIndex).start();
        }
    }

    //insert data initialy into database
    public static void setupDemoProblems() {
        InputStream stream = context.getResources().openRawResource(R.raw.demo);
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Problem> demoProblemLst = mapper.readValue(stream, new TypeReference<List<Problem>>() {
            });
            try {
                ActiveAndroid.beginTransaction();
                for (Problem p : demoProblemLst) {
                    Utils.saveProblem(p);
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUpProfile() {
        Profile profile = new Profile();
        profile.neurons = 0;
        profile.ranking = "Oops!, No te has sincronizado";
        profile.score = 0;
        profile.save();
    }

    //security encoding and decoding
    public static String encodeAES(String str) {
        return sec.cipher(str);
    }

    public static String decodeAES(String encoded) {
        return sec.decipher(encoded);
    }
    //end security

    //other utils
    public static String getDate(Date date) {       // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);

        //beware, months start at 0, not 1.
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        String meridian = "AM";
        if (hour == 0)
            hour = 12;
        else if (hour >= 12) {
            meridian = "PM";
            if (hour > 12)
                hour -= 12;
        }

        String minString = (min < 10) ? "0" + min : "" + min;
        return months[month] + " " + day + ", " + year + " " + hour + ":" + minString + " " + meridian;
    }

    public static String format(double value) {
        return formatter.format(value);
    }

    public static byte[] strBase64ToByteArray(String str_base_64) {
        byte[] byteArray = Base64.decode(str_base_64, Base64.DEFAULT);
        return byteArray;
    }

    public static Bitmap ByteArray_To_Bitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    //writing images
    public static void writeImage(int problemId, byte[] img) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("" + problemId, Context.MODE_PRIVATE);
            fileOutputStream.write(img);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Bitmap readImage(int problemId) {
        try {
            byte[] bytes = IOUtils.toByteArray(context.openFileInput("" + problemId));
            return ByteArray_To_Bitmap(bytes);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    //end writing images


}
