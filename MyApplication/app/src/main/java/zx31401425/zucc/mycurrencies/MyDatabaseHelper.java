package zx31401425.zucc.mycurrencies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by 赵轩 on 2017/7/2.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_DATEBASE =
            "create table datebase ("
                    + "id integer primary key autoincrement,"
                    + "foregin text,"
                    + "home text,"
                    + "forenum double,"
                    + "num double)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATEBASE);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
