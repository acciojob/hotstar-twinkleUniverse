package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries webSeries=webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(webSeries==null){
            throw new Exception("Series is already present");
        }
            WebSeries newWebSeries = new WebSeries();
            //webSeriesEntryDto to webseries
            newWebSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
            newWebSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
            newWebSeries.setRating(webSeriesEntryDto.getRating());
            newWebSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
            //get production house passed by user
            Optional<ProductionHouse> optionalProductionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());
            ProductionHouse passProductionHouse=optionalProductionHouse.get();
            //webseries added to listofwebseries in productionhouse
            passProductionHouse.getWebSeriesList().add(newWebSeries);
            double sum=0;
            //Updating the productionHouse rating
            for(WebSeries web: passProductionHouse.getWebSeriesList()){
            sum+=web.getRating();
            }
            double productionRating=sum/(passProductionHouse.getWebSeriesList().size());
            passProductionHouse.setRatings(productionRating);
            //updated productionHouse is added to newWebseries
            newWebSeries.setProductionHouse(passProductionHouse);
            WebSeries web=webSeriesRepository.save(newWebSeries);
            return web.getId();
    }

}
