package asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nw on 17/4/26.
 */

public class AsyncTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute("xx");
        myAsyncTask.cancel(true);
    }


    class MyAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("MyAsyncTask + onPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            System.out.println("MyAsyncTask + doInBackground");
            publishProgress(2);
            return "34";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            System.out.println("MyAsyncTask + onProgressUpdate");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("MyAsyncTask + onPostExecute");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            System.out.println("MyAsyncTask + onCancelled");
        }
    }


}
