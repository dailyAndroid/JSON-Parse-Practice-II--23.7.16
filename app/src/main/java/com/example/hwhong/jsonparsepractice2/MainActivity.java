package com.example.hwhong.jsonparsepractice2;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hwhong.jsonparsepractice2.Model.CountryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listView);

        new JSONTask().execute("http://www.androidbegin.com/tutorial/jsonparsetutorial.txt");

    }

    public class JSONTask extends AsyncTask<String, String, List<CountryModel>> {

        @Override
        protected List<CountryModel> doInBackground(String... urls) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = bufferedReader.readLine()) !=  null) {
                    buffer.append(line);
                }

                String json = buffer.toString();

                JSONObject jsonObject = new JSONObject(json);
                JSONArray array = jsonObject.getJSONArray("worldpopulation");

                List<CountryModel> countryModelList = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    CountryModel countryModel = new CountryModel();

                    JSONObject childJson = array.getJSONObject(i);

                    countryModel.setRank(childJson.getInt("rank"));
                    countryModel.setCountry(childJson.getString("country"));
                    countryModel.setPopulation(childJson.getString("population"));

                    countryModelList.add(countryModel);
                }

                return countryModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CountryModel> countryModels) {
            //handling the UI Works i.e updating the row xml
            super.onPostExecute(countryModels);

            CountryAdapter adapter = new CountryAdapter(getApplicationContext(),
                    R.layout.row, countryModels );
            listview.setAdapter(adapter);
        }

        public class CountryAdapter extends ArrayAdapter {

            private List<CountryModel> list;
            private int resource;
            private LayoutInflater inflater;

            public CountryAdapter(Context context, int resource, List<CountryModel> objects) {
                super(context, resource, objects);
                this.list = objects;
                this.resource = resource;
                this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;

                if (convertView == null ){
                    holder = new ViewHolder();
                    convertView = inflater.inflate(resource, null);
                    holder.rank = (TextView) convertView.findViewById(R.id.rank);
                    holder.country = (TextView) convertView.findViewById(R.id.country);
                    holder.population = (TextView) convertView.findViewById(R.id.population);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.rank.setText("Rank: " + list.get(position).getRank());
                holder.country.setText("Country: " + list.get(position).getCountry());
                holder.population.setText("Population: " + list.get(position).getPopulation());

                return convertView;
            }

            public class ViewHolder {
                private TextView rank;
                private TextView country;
                private TextView population;
            }
        }
    }
}

