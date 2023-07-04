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
//
//        //Save The subscription Object into the Db and return the total Amount that user has to pay
//        Subscription subscription=new Subscription();
//        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
//        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
//        int total_amount=0;
//        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
//            total_amount=500 + 200*subscriptionEntryDto.getNoOfScreensRequired();
//        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
//            total_amount=800 + 250*subscriptionEntryDto.getNoOfScreensRequired();
//        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE))
//            total_amount= 1000 + 350*subscriptionEntryDto.getNoOfScreensRequired();
//        subscription.setTotalAmountPaid(total_amount);
//        Optional<User>optionalUser=userRepository.findById(subscriptionEntryDto.getUserId());
//        User user=optionalUser.get();
//        user.setSubscription(subscription);
//        subscriptionRepository.save(subscription);
//        return total_amount;
//    }
//
//    public Integer upgradeSubscription(Integer userId)throws Exception{
//
//        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
//        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
//        //update the subscription in the repository
//         Optional<User>optionalUser=userRepository.findById(userId);
//         User user=optionalUser.get();
//         Subscription subscription=user.getSubscription();
//         if(user.getSubscription().equals(SubscriptionType.ELITE)){
//             throw new Exception("Already the best Subscription");
//         }
//         int fare=0;
//         if(user.getSubscription().equals(SubscriptionType.BASIC)){
//             fare=(800 + 250*subscription.getNoOfScreensSubscribed())-(500 + 200*subscription.getNoOfScreensSubscribed());
//             subscription.setSubscriptionType(SubscriptionType.PRO);
//             subscription.setTotalAmountPaid(500 + 200*subscription.getNoOfScreensSubscribed());
//         }else {
//             fare = (1000 + 350 * subscription.getNoOfScreensSubscribed()) - (800 + 250 * subscription.getNoOfScreensSubscribed());
//             subscription.setSubscriptionType(SubscriptionType.ELITE);
//            subscription.setTotalAmountPaid(800 + 250 * subscription.getNoOfScreensSubscribed());
//         }
//         user.setSubscription(subscription);
//         userRepository.save(user);
//         return fare;
//    }
//
//    public Integer calculateTotalRevenueOfHotstar(){
//
//        //We need to find out total Revenue of hotstar : from all the subscriptions combined
//        //Hint is to use findAll function from the SubscriptionDb
//        List<Subscription>subscriptionList=subscriptionRepository.findAll();
//        Integer total_revenue=0;
//        for(Subscription sub:subscriptionList){
//            total_revenue+=sub.getTotalAmountPaid();
//        }
//
//        return total_revenue;
//    }
    //Save The subscription Object into the Db and return the total Amount that user has to pay

    //code
    //getting user by id
    User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        if (user==null)return 0;

    Subscription userSubscription = new Subscription();
        userSubscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
//        userSubscription.setStartSubscriptionDate(new Date());
        userSubscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

    int amountPaid = 0;

    int screenSubscribed = subscriptionEntryDto.getNoOfScreensRequired();

    //Basic : 500 + 200 * noOfScreensSubscribed
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
    {
        amountPaid = 500 + (200 * screenSubscribed);
    }
    //Pro : 800 + 250*noOfScreensSubscribed
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
    {
        amountPaid = 800 + (250 * screenSubscribed);
    }
    //ELITE Plan : 1000 + 350*noOfScreensSubscribed
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE))
    {
        amountPaid = 1000 + (350 * screenSubscribed);
    }
        else
                return 0;

        userSubscription.setTotalAmountPaid(amountPaid);

    //mapping user->subscription
        user.setSubscription(userSubscription);

        subscriptionRepository.save(userSubscription);

        userRepository.save(user);

        return amountPaid;
}

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent())
            return 0;

        User user = userOptional.get();

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE))
        {
            throw new Exception("Already the best Subscription");
        }

        Subscription subscription = user.getSubscription();

        int screenSubscribed = subscription.getNoOfScreensSubscribed();

        int totalAmountBefore = subscription.getTotalAmountPaid();

        int amountDiff=0;
        int updatedAmount=0;

        if(subscription.getSubscriptionType().equals(SubscriptionType.PRO))
        {
            updatedAmount = 1000 + (350 * screenSubscribed);
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(updatedAmount);
        }
        else if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            updatedAmount = 800 + (250 * screenSubscribed);
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(updatedAmount);
        }

        user.setSubscription(subscription);
        userRepository.save(user);
        subscriptionRepository.save(subscription);

        amountDiff = updatedAmount-totalAmountBefore;
        return amountDiff;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        if(subscriptionList.isEmpty())
            return 0;
        int totalRevenue=0;
        for(Subscription subscription : subscriptionList)
        {
            totalRevenue = totalRevenue + subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
