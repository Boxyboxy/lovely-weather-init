package ibf.ssf.weatherapi.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static ibf.ssf.weatherapi.config.Constants.ENV_API_KEY;
import static ibf.ssf.weatherapi.config.Constants.URL_WEATHER;

import ibf.ssf.weatherapi.models.Weather;
import ibf.ssf.weatherapi.repositories.WeatherRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class OWMService {
  private final Logger logger = Logger.getLogger(OWMService.class.getName());

  public final String appId;

  @Autowired
  private WeatherRepository weatherRepo;

  public OWMService() {
    String k = System.getenv(ENV_API_KEY);
    if ((null != k) && (k.trim().length() > 0))
      appId = k;
    else
      appId = "abc123";
  }

  public List<Weather> getWeather(String city) {

    logger.info("API key: " + appId);

    String url = UriComponentsBuilder.fromUriString(URL_WEATHER)
        .queryParam("q", city)
        .queryParam("appid", appId)
        .queryParam("units", "metric")
        .toUriString();

    logger.info("url: " + url);

    final RequestEntity<Void> req = RequestEntity.get(url).build();
    final RestTemplate template = new RestTemplate();
    final ResponseEntity<String> resp = template.exchange(req, String.class);
    logger.info("Status code: " + resp.getStatusCode());
    logger.info("Payload: " + resp.getBody());

    if (resp.getStatusCode() != HttpStatus.OK)
      throw new IllegalArgumentException(
          "Error: status code %s".formatted(resp.getStatusCode().toString()));

    final String body = resp.getBody();

    logger.log(Level.INFO, "payload: %s".formatted(body));

    try (InputStream is = new ByteArrayInputStream(body.getBytes())) {
      final JsonReader reader = Json.createReader(is);
      final JsonObject result = reader.readObject();
      // get weather array
      final JsonArray readings = result.getJsonArray("weather");
      // get city name string
      final String cityName = result.getString("name");
      // get temperature from main object
      final float temperature = (float) result.getJsonObject("main").getJsonNumber("temp").doubleValue();
      return readings.stream()
          .map(v -> (JsonObject) v)
          .map(Weather::create)
          .map(w -> {
            w.setCityName(cityName);
            w.setTemperature(temperature);
            return w;
          })
          .collect(Collectors.toList());
    } catch (Exception e) {
    }

    return Collections.EMPTY_LIST;

  }

  // public boolean hasCity(String city) {
  // Optional<String> opt = weatherRepo.get(city);
  // return opt.isPresent();
  // }
}
