package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.domain.Hotel;
import com.datalex.eventia.domain.PullSessionResponse;
import com.datalex.eventia.dto.predictHQ.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@Service
public class HotelService {


    @Autowired
    RestTemplate restTemplate;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private HttpHeaders headers;

    private static final String QUERY = "?";

    @Autowired
    ApplicationProperties applicationProperties;


    @PostConstruct
    private void init() {
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
    }


    public List<Hotel> findHotels(Event event){
        ZoneId zondIdDestinaion = ZoneId.of(event.getTimezone());
        LocalDate eventStartLocalDate = event.getStart().toInstant().atZone(zondIdDestinaion).toLocalDate();
        LocalDate eventEndLocalDate = event.getEnd().toInstant().atZone(zondIdDestinaion).toLocalDate();
        String startDate = eventStartLocalDate.minusDays(1).format(formatter);
        String endDate = eventEndLocalDate.plusDays(1).format(formatter);

        String createSessionrequest = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getSkyscannerUrlBase()
                        + applicationProperties.getSkyscannerUrlCreateSession()
                            .replace(applicationProperties.getSkyscannerStartDatePlaceHolder(), startDate)
                            .replace(applicationProperties.getSkyscannerEndDatePlaceHolder(), endDate))
                .build()
                .encode()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        HttpEntity<String> createSessionResponseEntity = restTemplate.exchange(createSessionrequest, HttpMethod.GET, entity, String.class);
        HttpHeaders responseEntityHeaders = createSessionResponseEntity.getHeaders();
        URI location = responseEntityHeaders.getLocation();

        String pullSessionRequest = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getSkyscannerUrlBase()
                        + location.getPath()
                        + QUERY
                        + location.getQuery())
                .build()
                .encode()
                .toUriString();
        System.out.println(pullSessionRequest);
        restTemplate.getForEntity(pullSessionRequest, String.class);

//        HttpEntity<String> pullSessionResponseEntity = restTemplate.exchange(pullSessionRequest, HttpMethod.GET, entity, String.class);

//        ResponseEntity<PullSessionResponse> pullSessionResponseEntity = restTemplate.exchange(pullSessionRequest, HttpMethod.GET, entity, PullSessionResponse.class);

//        pullSessionResponseEntity.getBody().getHotelsPrices()
//                                            .stream()
//                                            .forEach(hotelPriceInfo -> System.out.println(hotelPriceInfo.getId()));

        return null;
    }

    public static void main(String... args){

//        HttpClient client = HttpClientBuilder.create().build();
//        HttpGet request = new HttpGet(url);
//
//        // add request header
//        request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//        try {
//            HttpResponse response = client.execute(request);
//            System.out.println(response.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            URL url = new URL("http://partners.api.skyscanner.net/apiservices/hotels/liveprices/v2/H4sIAAAAAAAEAKtWyleyilYKDVbSUUrN0wXTocEuQNLI3NTY3NTECMQ0MDTXNQAikxADAyswQhI1NEASNQRCJUNjHSMjHUNTQyWdvNKcnFgdpTwlK11DY6COWgCuSmd2cQAAAA2?apikey=_HvyLQUibSmiFspnQ-Mf1E2v6Ok9z7p9tJrTM628vwS1p8K4kmwbazKVmHjFe4-rCFxn_oEsTRfR0Tz-5DJGXAA%3D%3D");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



}
