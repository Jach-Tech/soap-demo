package com.hts.demo.soapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class MainActivity extends AppCompatActivity {

    /* 4 parameters are required to call a SOAP web service
    *  1. Web service URL: SOAP web service (A single web service can provide a number of services: methods)
    *  2. SOAP action: particular service provided by the web service
    *  3. Namespace
    *  4. Method name
    * */
    private final String NAMESPACE = "http://www.oorsprong.org/websamples.countryinfo";
    private final String METHOD_NAME = "CountryISOCode";
    private final String METHOD_NAME_INFO = "FullCountryInfo";
    private final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;
    private final String SOAP_ACTION_INFO = NAMESPACE + "/" + METHOD_NAME_INFO;
    private final String URL = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL";

    // Variables to extract data from the query result
    String countryISOCode = "";
    String capitalCity = "";
    String dialCode = "";
    String continentCode = "";
    String currencyIsoCode = "";
    String imageUrl = "";


    Spinner mySpinner;
    TextView textViewCountryIsoCode;
    TextView textViewCapitalCity;
    TextView textViewDialCode;
    TextView textViewContinentCode;
    TextView textViewCurrencyCode;
    ImageView flagView;

    // Variable to hold the query result
    String responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySpinner = findViewById(R.id.my_spinner);
        textViewCountryIsoCode = findViewById(R.id.text_view_iso_code);
        textViewCapitalCity = findViewById(R.id.text_view_capital_city);
        textViewContinentCode = findViewById(R.id.text_view_continent_iso_code);
        textViewDialCode = findViewById(R.id.text_view_dial_code);
        textViewCurrencyCode = findViewById(R.id.text_view_currency_code);
        flagView = findViewById(R.id.flag_view);
    }

    // Asynchronous task is being invoked because of network call
    public void getCountryInfo(View view) {
        LoadDataTask loadDataTask = new LoadDataTask();
        loadDataTask.execute();
    }

    class LoadDataTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            countryInfo(countryIsoCode());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            parseResult();
        }
    }

    // This method returns a country ISO code based on a country name
    public String countryIsoCode() {
        String isoCode = "";

        // Create a SoapObject
        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

        // Because the web service is taking an input, in this case, the country name
        // the method "addProperty" is invoked on the SoapObject
        soapObject.addProperty("sCountryName", mySpinner.getSelectedItem().toString());

        // Specify the SOAP version, 1.2 in this case
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER12);

        // Now we add the SoapObject to the envelope
        envelope.setOutputSoapObject(soapObject);

        // The request has to be transferred on the network
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
        try {
            // This method raises an exception
            httpTransportSE.call(SOAP_ACTION, envelope);

            SoapObject soapObject1= (SoapObject) envelope.bodyIn;

            // Get the response from the server
            isoCode = soapObject1.getProperty(0).toString();

            //System.out.println(isoCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isoCode;
    }

    // This method get info on a country based on the country ISO code
    public void countryInfo(String countryIsoCode) {
        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME_INFO);
        soapObject.addProperty("sCountryISOCode", countryIsoCode);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER12);

        envelope.setOutputSoapObject(soapObject);

        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

        try {
            httpTransportSE.call(SOAP_ACTION_INFO, envelope);

            SoapObject soapObject1= (SoapObject) envelope.bodyIn;

            SoapObject resObject1 = (SoapObject) soapObject1.getProperty(0);
            //int count = resObject1.getPropertyCount();

            responseInfo = soapObject1.getProperty(0).toString();
            //SoapObject soapObject2 = (SoapObject) resObject1.getProperty(0);
            countryISOCode = resObject1.getProperty("sISOCode").toString();
            capitalCity = resObject1.getProperty("sCapitalCity").toString();
            dialCode = resObject1.getProperty("sPhoneCode").toString();
            continentCode = resObject1.getProperty("sContinentCode").toString();
            currencyIsoCode = resObject1.getProperty("sCurrencyISOCode").toString();
            imageUrl = resObject1.getProperty("sCountryFlag").toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseResult() {
        textViewCountryIsoCode.setText("ISO Code: " + countryISOCode);
        textViewCapitalCity.setText("Capital City: " + capitalCity);
        textViewDialCode.setText("Dial Code: " + dialCode);
        textViewContinentCode.setText("Continent Code: " + continentCode);
        textViewCurrencyCode.setText("Currency ISO Code: " + currencyIsoCode);
        Picasso.with(this.getApplicationContext()).load(imageUrl).into(flagView);
    }
}
