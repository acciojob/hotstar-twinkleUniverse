package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        int total_amount=0;
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
            total_amount=500 + 200*subscriptionEntryDto.getNoOfScreensRequired();
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
            total_amount=800 + 250*subscriptionEntryDto.getNoOfScreensRequired();
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE))
            total_amount= 1000 + 350*subscriptionEntryDto.getNoOfScreensRequired();
        subscription.setTotalAmountPaid(total_amount);
        Optional<User>optionalUser=userRepository.findById(subscriptionEntryDto.getUserId());
        User user=optionalUser.get();
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);
        return total_amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
         Optional<User>optionalUser=userRepository.findById(userId);
         User user=optionalUser.get();
         Subscription subscription=user.getSubscription();
         if(user.getSubscription().equals(SubscriptionType.ELITE)){
             throw new Exception("Already the best Subscription");
         }
         int fare=0;
         if(user.getSubscription().equals(SubscriptionType.BASIC)){
             fare=(800 + 250*subscription.getNoOfScreensSubscribed())-(500 + 200*subscription.getNoOfScreensSubscribed());
             subscription.setSubscriptionType(SubscriptionType.PRO);
             subscription.setTotalAmountPaid(500 + 200*subscription.getNoOfScreensSubscribed());
         }else {
             fare = (1000 + 350 * subscription.getNoOfScreensSubscribed()) - (800 + 250 * subscription.getNoOfScreensSubscribed());
             subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(800 + 250 * subscription.getNoOfScreensSubscribed());
         }
         user.setSubscription(subscription);
         userRepository.save(user);
         return fare;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription>subscriptionList=subscriptionRepository.findAll();
        Integer total_revenue=0;
        for(Subscription sub:subscriptionList){
            total_revenue+=sub.getTotalAmountPaid();
        }

        return total_revenue;
    }

}
