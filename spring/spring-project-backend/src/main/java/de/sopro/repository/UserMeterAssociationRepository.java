package de.sopro.repository;

import org.springframework.data.repository.CrudRepository;

import de.sopro.data.Meter;
import de.sopro.data.User;
import de.sopro.data.UserMeterAssociation;

public interface UserMeterAssociationRepository extends CrudRepository<UserMeterAssociation, Long> {

	Iterable<UserMeterAssociation> findAllByMeter(Meter meter);
	
	Iterable<UserMeterAssociation> findAllByUserAndMeter(User user, Meter meter);

	// TODO create soft delte for associations
	// void deleteByMeter(Meter meter);

}
