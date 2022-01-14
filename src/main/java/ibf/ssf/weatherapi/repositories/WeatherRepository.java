package ibf.ssf.weatherapi.repositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import ibf.ssf.weatherapi.config.Constants;

@Repository
public class WeatherRepository {

  @Autowired
  @Qualifier(Constants.WEATHER_REDIS)
  private RedisTemplate<String, String> template;

  public Optional<String> get(String city) {
    return Optional.ofNullable(template.opsForValue().get(city));
  }

}
