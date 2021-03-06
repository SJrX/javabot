package javabot.dao.impl

import javabot.dao.WeatherHandler
import javabot.model.Weather
import org.xml.sax.InputSource
import org.xml.sax.helpers.XMLReaderFactory
import java.net.URL
import java.net.URLEncoder

/**
 * Implements a weather service Dao using Weather Underground's weather API

 * @see WeatherDao
 */
class WeatherUndergroundHandler : WeatherHandler {

    override fun getWeatherFor(place: String): Weather? {
        val handler = WeatherUndergroundSaxHandler()
        try {
            val reader = XMLReaderFactory.createXMLReader()
            reader.contentHandler = handler
            reader.parse(InputSource(URL(API_URL + URLEncoder.encode(place, "UTF-8")).openStream()))
        } catch (e: Exception) {
            //TODO proper logging/distinction between error vs no-data
            return null
        }

        return handler.getWeather()
    }

    companion object {
        private val API_URL = "http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query="
    }
}
