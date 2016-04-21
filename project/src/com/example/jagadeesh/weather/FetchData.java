package com.example.jagadeesh.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FetchData {

    public static String json;
    public static String[] dataArray = new String[40];

    public FetchData(String json)
    {
        setJson(json);
    }

    public void setJson(String jsonInput)
    {
        this.json = jsonInput;
    }

    public String getJson()
    {
        return this.json;
    }

    public String[] readData () throws JSONException
    {
        //String[] result = null;
        String jsonData = getJson();
        String location=null;
        JSONObject jsonObj = new JSONObject(jsonData);
        JSONObject channel = jsonObj.getJSONObject("query").getJSONObject("results").getJSONObject("channel");
        //get accurate location of queried city
        if (channel.getJSONObject("location").getString("region").equals(""))
            location = "\n"+channel.getJSONObject("location").getString("city")+", "+channel.getJSONObject("location").getString("country");
        else
            location = "\n"+channel.getJSONObject("location").getString("city")+", "+channel.getJSONObject("location").getString("region")+", "+channel.getJSONObject("location").getString("country");
        //get weather icon
        String icon = convertWeatherCode(channel.getJSONObject("item").getJSONObject("condition").getString("code"));
        String tempUnit = channel.getJSONObject("units").getString("temperature");
        String wind = channel.getJSONObject("wind").getString("speed")+" "+channel.getJSONObject("units").getString("speed");
        String pressure = channel.getJSONObject("atmosphere").getString("pressure");
        String condition = channel.getJSONObject("item").getJSONObject("condition").getString("text")+"\nWind "+ wind +"\nHumidity  "+channel.getJSONObject("atmosphere").getString("humidity")+" %\nCurrent Temperature  "+channel.getJSONObject("item").getJSONObject("condition").getString("temp")+" "+tempUnit+"\n";
        JSONArray forecast = channel.getJSONObject("item").getJSONArray("forecast");
        String forecastToday = "Today\n"+forecast.getJSONObject(0).getString("high")+" \n"+forecast.getJSONObject(0).getString("low");
        String forecastSecond = forecast.getJSONObject(1).getString("day")+"\n"+forecast.getJSONObject(1).getString("high")+" \n"+forecast.getJSONObject(1).getString("low");
        String forecastThird = forecast.getJSONObject(2).getString("day")+"\n"+forecast.getJSONObject(2).getString("high")+" \n"+forecast.getJSONObject(2).getString("low");
        String forecastFourth = forecast.getJSONObject(3).getString("day")+"\n"+forecast.getJSONObject(3).getString("high")+" \n"+forecast.getJSONObject(3).getString("low");
        String forecastFifth = forecast.getJSONObject(4).getString("day")+"\n"+forecast.getJSONObject(4).getString("high")+" \n"+forecast.getJSONObject(4).getString("low");
        String[] sr = channel.getJSONObject("astronomy").getString("sunrise").split(":");
        String sunriseTime = sr[0]+"\t:\t"+sr[1];
        String[] ss = channel.getJSONObject("astronomy").getString("sunset").split(":");
        String sunsetTime = ss[0]+"\t:\t"+ss[1];

        String[] result = {location, icon, condition, forecastToday, forecastSecond, forecastThird, forecastFourth, forecastFifth, sunriseTime, sunsetTime};
        return result;
    }

    public String convertWeatherCode (String code)
    {
        String weather = null;
        switch (code){
            case "0": weather=":"; break;
            case "1": weather="P"; break;
            case "2": weather=":"; break;
            case "3": weather="Q"; break;
            case "4": weather="P"; break;
            case "5": weather="U"; break;
            case "6": weather="U"; break;
            case "7": weather="U"; break;
            case "8": weather="G"; break;
            case "9": weather="F"; break;
            case "10": weather="U"; break;
            case "11": weather="K"; break;
            case "12": weather="K"; break;
            case "13": weather="I"; break;
            case "14": weather="M"; break;
            case "15": weather="W"; break;
            case "16": weather="I"; break;
            case "17": weather="5"; break;
            case "18": weather="U"; break;
            case "19": weather=":"; break;
            case "20": weather="B"; break;
            case "21": weather="C"; break;
            case "22": weather="Z"; break;
            case "23": weather="E"; break;
            case "24": weather=","; break;
            case "25": weather="\""; break;
            case "26": weather="A"; break;
            case "27": weather="3"; break;
            case "28": weather="a"; break;
            case "29": weather="3"; break;
            case "30": weather="A"; break;
            case "31": weather="6"; break;
            case "32": weather="1"; break;
            case "33": weather="6"; break;
            case "34": weather="1"; break;
            case "35": weather="f"; break;
            case "36": weather="\'"; break;
            case "37": weather="Q"; break;
            case "38": weather="Q"; break;
            case "39": weather="Q"; break;
            case "40": weather="K"; break;
            case "41": weather="W"; break;
            case "42": weather="M"; break;
            case "43": weather="W"; break;
            case "44": weather="2"; break;
            case "45": weather="Q"; break;
            case "46": weather="M"; break;
            case "47": weather="S"; break;
        }
        return weather;
    }
}
