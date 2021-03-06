package com.noideateam.braincode_noideateam;

import com.noideateam.braincode_noideateam.generategeoindex.GenerateGeoIndex;
import com.noideateam.braincode_noideateam.generategeoindex.opencagedata.ReturnGenerateGeoIndex;
import com.noideateam.braincode_noideateam.restreturn.Location;
import com.noideateam.braincode_noideateam.restreturn.LocationArray;
import javafx.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserRequestController {
    private final UserRepository userRepository;
    CollectionPoints collectionPoints = new CollectionPoints();

    UserRequestController(UserRepository repository) throws IOException {

        this.userRepository = repository;
    }

    /*@CrossOrigin(origins = "*")
    @GetMapping("/requestwithgeo")
    public UserRequestGeo userRequestGeo(
            @RequestParam(value = "login", defaultValue = "User") String login,
            @RequestParam(value = "geoLength", defaultValue = "0.0") float geoLength,
            @RequestParam(value = "geoWidth", defaultValue = "0.0") float geoWidth
    ){
        return new UserRequestGeo(login, geoLength, geoWidth);
    }*/

    @CrossOrigin(origins = "*")
    @GetMapping("/all")
    List<User> all(){
        return userRepository.findAll();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/request")
    LocationArray getWithoutDatabase(
            @RequestParam("chosen_street") String chosen_street,
            @RequestParam("chosen_city") String chosen_city,
            @RequestParam("chosen_zip") String chosen_zip
    )  {
        GenerateGeoIndex ggi = new GenerateGeoIndex(chosen_street, chosen_city, chosen_zip);
        ReturnGenerateGeoIndex tempUserChoice = ggi.generate();

        if (tempUserChoice == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "You must give real address!"
            );
        } else {





            ArrayList<Pair<CollectionPoint, Double>> threeClosest = collectionPoints.getThreeClosest(tempUserChoice.getX(), tempUserChoice.getY());

//            for (Pair<CollectionPoint, Double> entry: threeClosest){
//                System.out.println("entry.getKey() = " + entry.getKey().getName() + "entry.getValue() = " + entry.getValue());
//            }


            //Pair<CollectionPoint, Double> closestPoint = collectionPoints.getOneClosest(tempUserChoice.getX(), tempUserChoice.getY());

            LocationArray result = new LocationArray();

            for (Pair<CollectionPoint, Double> entry: threeClosest){
                System.out.println("Name " + entry.getKey().getName() + "\tDistance " + entry.getValue());

                result.add(
                                entry.getKey().getName(),
                                entry.getValue(),
                                true,
                                entry.getKey().getAddress(),
                                entry.getKey().getCity(),
                                entry.getKey().getZipCode(),
                                entry.getKey().getNotes(),
                                entry.getKey().getType(),
                                entry.getKey().getDeliveryHours()
                    );
            }

            return result;
//            return new Location(
//                    threeClosest.getKey().getName(),
//                    threeClosest.getValue(),
//                    true,
//                    threeClosest.getKey().getAddress(),
//                    threeClosest.getKey().getCity(),
//                    threeClosest.getKey().getZipCode(),
//                    threeClosest.getKey().getNotes(),
//                    threeClosest.getKey().getType(),
//                    threeClosest.getKey().getDeliveryHours()
//            );
        }


    }

    @CrossOrigin(origins = "*")
    @PostMapping("/request")
    User newUser(@RequestBody User newEmployee) {
        return userRepository.save(newEmployee);
    }

    /*@CrossOrigin(origins = "*")
    @GetMapping("/request/{id}/")
    Location one(
            @PathVariable Long id,
            @RequestParam("chosen_street") String chosen_street,
            @RequestParam("chosen_city") String chosen_city,
            @RequestParam("chosen_zip") String chosen_zip
    ) throws IOException {
        User tempUser = new User(
          userRepository.findById(id).get().getLogin(),
          userRepository.findById(id).get().getStreet(),
          userRepository.findById(id).get().getCity(),
          userRepository.findById(id).get().getZip()
        );

        boolean suggest = false;
        System.out.println(tempUser.getId() + ", " +tempUser.getLogin() + ", " + tempUser.getStreet() + ", " + tempUser.getCity() + ", " + tempUser.getZip());

        GenerateGeoIndex ggi = new GenerateGeoIndex(tempUser.getStreet(), tempUser.getCity(), tempUser.getZip());
        ReturnGenerateGeoIndex tempUserGeo = ggi.generate();

        ggi = new GenerateGeoIndex(chosen_street, chosen_city, chosen_zip);
        ReturnGenerateGeoIndex tempUserChoice = ggi.generate();

        CollectionPoints collectionPoints = new CollectionPoints();

        double distanceToChosenPoint = collectionPoints.getDistance(tempUserGeo.getX(), tempUserGeo.getY(), tempUserChoice.getX(), tempUserChoice.getY());

        Optional<Map.Entry<CollectionPoint, Double>> closestPoint = collectionPoints.getClosest(tempUserGeo.getX(), tempUserGeo.getY());

        if (distanceToChosenPoint > closestPoint.get().getValue()){
            suggest = true;
            System.out.println("Suggest is true ");
            return new Location(
                    closestPoint.get().getKey().getName(),
                    closestPoint.get().getValue(),
                    distanceToChosenPoint,
                    chosen_street,
                    chosen_city,
                    chosen_zip,
                    closestPoint.get().getKey().getAddress(),
                    closestPoint.get().getKey().getCity(),
                    closestPoint.get().getKey().getZipCode(),
                    closestPoint.get().getKey().getNotes(),
                    closestPoint.get().getKey().getType(),
                    closestPoint.get().getKey().getDeliveryHours()
            );
        } else{
            return new Location( distanceToChosenPoint, chosen_street, chosen_city, chosen_zip, null, null);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/employees/{id}")
    User replaceEmployee(@RequestBody User newUser, @PathVariable Long id) {

        return userRepository.findById(id)
                .map(user -> {
                    user.setLogin(newUser.getLogin());
                    user.setStreet(newUser.getStreet());
                    user.setCity(newUser.getCity());
                    user.setZip(newUser.getZip());
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return userRepository.save(newUser);
                });
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        userRepository.deleteById(id);
    }*/
}