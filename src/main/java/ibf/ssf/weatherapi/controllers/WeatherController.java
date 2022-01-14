package ibf.ssf.weatherapi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ibf.ssf.weatherapi.models.Weather;
import ibf.ssf.weatherapi.services.OWMService;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

@Controller
@RequestMapping(path = "/weather", produces = MediaType.TEXT_HTML_VALUE)
public class WeatherController {

  private final Logger logger = Logger.getLogger(WeatherController.class.getName());

  @Autowired
  private OWMService weatherService;

  @GetMapping
  public String getCity(@RequestParam(required = true) String city, Model model) {
    String cityForQuery = city.replace(" ", "+");
    List<Weather> weatherList = weatherService.getWeather(cityForQuery);
    model.addAttribute("city", city.toUpperCase());
    model.addAttribute("weatherList", weatherList);
    return "weather";
  }

}
