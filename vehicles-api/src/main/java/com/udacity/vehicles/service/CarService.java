package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;

    private WebClient clientMaps;
    private WebClient clientPricing;
    private ModelMapper modelMapper;

    public CarService(CarRepository repository, @Qualifier("maps") WebClient clientMaps,
                      @Qualifier("pricing") WebClient clientPricing, ModelMapper modelMapper) {
        this.repository = repository;
        this.clientMaps = clientMaps;
        this.clientPricing = clientPricing;
        this.modelMapper = modelMapper;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Optional<Car> optionalCar = repository.findById(id);

        if(optionalCar.isPresent()) {
            Car car = optionalCar.get();
        /**
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
            PriceClient priceClient = new PriceClient(clientPricing);
            String[] currencyAndPrice = priceClient.getPrice(id).split(" ");
            car.setPrice(currencyAndPrice[1]);
            /**
             * Note: The Location class file also uses @transient for the address,
             * meaning the Maps service needs to be called each time for the address.
             */
            MapsClient mapsClient = new MapsClient(clientMaps, modelMapper);
            Location location = car.getLocation();
            Location locationWithAddress = mapsClient.getAddress(location);
            car.setLocation(locationWithAddress);
        } else {
            throw new CarNotFoundException();
        }
        return optionalCar.get();
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if(car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }
        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        Optional<Car> car = repository.findById(id);

        if(car.isPresent()) {
            repository.delete(car.get());
        } else {
            throw new CarNotFoundException();
        }
    }
}
