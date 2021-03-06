package de.sopro.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.sopro.data.Meter;
import de.sopro.data.MeterType;
import de.sopro.data.Person;
import de.sopro.data.Reading;
import de.sopro.data.ReadingValue;
import de.sopro.data.Role;
import de.sopro.data.User;
import de.sopro.data.UserMeterAssociation;
import de.sopro.dto.MeterDTO;
import de.sopro.dto.ReadingDTO;
import de.sopro.dto.builder.DTOBuilder;
import de.sopro.repository.MeterRepository;
import de.sopro.repository.PersonRepository;
import de.sopro.repository.ReadingRepository;
import de.sopro.repository.ReadingValueRepository;
import de.sopro.repository.UserMeterAssociationRepository;
import de.sopro.repository.UserRepository;
import de.sopro.util.exception.ResourceNotFoundException;

/**
 * The meter controller contains operations to manage all requests belonging to
 * meters.
 *
 */
@RestController
public class MeterController {

	@Autowired
	MeterRepository meterRepository;

	@Autowired
	ReadingRepository readingRepository;

	@Autowired
	PersonRepository personRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ReadingValueRepository rvRepo;

	@Autowired
	UserMeterAssociationRepository userMeterAssociationRepository;

	@Autowired
	DTOBuilder dtoBuilder;

	/**
	 * This method allows an admin to get a list of all meters existing in the
	 * system.
	 * 
	 * @param token The JWT of the admin to authenticate himself.
	 * @return A list of meters.
	 */
	@GetMapping("/api/meters")
	@CrossOrigin
	public Iterable<MeterDTO> getMeters(HttpServletRequest request) {

		Person p = personRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null);
		// Admins can get everyones data
		if (p.getRole().equals(Role.Admin)) {
			return StreamSupport.stream(meterRepository.findAll().spliterator(), false).map(m -> {
				try {
					return dtoBuilder.meterDTO(m);
				} catch (ResourceNotFoundException e) {
					// TODO Auto-generated catch block
					return null;
				}
			}).filter(x -> x != null).collect(Collectors.toList());
		}

		// Normal Users only their stuff
		User u = userRepository.findById(p.getPersonId()).orElse(null);

		Iterable<UserMeterAssociation> umas = userMeterAssociationRepository.findAllByUser(u);

		return StreamSupport.stream(umas.spliterator(), false).map(uma -> {
			try {
				return dtoBuilder.meterDTO(uma.getMeter(), u);
			} catch (ResourceNotFoundException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}).filter(m -> m != null).distinct().collect(Collectors.toList());

	}

	/**
	 * This method allows an admin to create a new meter which is not belonging to
	 * anyone.
	 * 
	 * @param token          The JWT of the admin to authenticate himself.
	 * @param meterNumber    The number of the meter which is given from adesso
	 *                       energy.
	 * @param initialReading The initial reading of the counter. This is not 0 every
	 *                       time because for example the meter could change the
	 *                       provider.
	 * @return The ID of the created meter.
	 * @throws ResourceNotFoundException
	 */

	@PostMapping("/api/meters")
	@CrossOrigin
	public MeterDTO createMeter(HttpServletRequest request, @RequestBody MeterDTO meterDTO)
			throws ResourceNotFoundException {
		Person admin = personRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null);

		MeterType t;
		switch (meterDTO.getType()) {
		case "Gas":
			t = MeterType.Gas;
			break;
		case "Water":
			t = MeterType.Water;
			break;
		case "Electricity":
			t = MeterType.Electricity;
			break;
		default:
			throw new ResourceNotFoundException("Meter Typ not existent");
		}

		Meter m = new Meter(meterDTO.getMeterNumber(), t);
		meterRepository.save(m);

		if (meterDTO.getOwnerId() != null) {
			User u = userRepository.findById(meterDTO.getOwnerId()).orElse(null);
			if (u == null) {
				throw new ResourceNotFoundException("user not existent");
			}
			UserMeterAssociation uma = new UserMeterAssociation(u, m);
			uma.setMeterName(meterDTO.getName());
			userMeterAssociationRepository.save(uma);
		}

		Reading nr = readingRepository.save(new Reading(m));
		rvRepo.save(new ReadingValue(nr, meterDTO.getInitialValue(), admin.getPersonId(), "newly created"));
		return dtoBuilder.meterDTO(m);
	}

	/**
	 * This method allows an user to get one of his meters by its ID or an admin to
	 * get any meter by its ad.
	 * 
	 * @param mid The ID of the meter that should be returned.
	 * @return The object of the meter belonging to the given ID or an error code if
	 *         no meter with the given ID exists.
	 * @throws ResourceNotFoundException
	 */
	@GetMapping("/api/meters/{mid}")
	@CrossOrigin
	public MeterDTO getMeter(HttpServletRequest request, @PathVariable Long mid) throws ResourceNotFoundException {

		Meter m = meterRepository.findById(mid).orElseThrow(() -> new ResourceNotFoundException());

		if (personRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null).getRole()
				.equals(Role.Admin)) {
			return dtoBuilder.meterDTO(m);

		} else {
			return dtoBuilder.meterDTO(m,
					userRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null));
		}

	}

	// Hier unklar welches Attribut, definitiv adden oder Methode löschen.
	/**
	 * This method allows a user to update the reader name.
	 * 
	 * @param token The JWT of the admin to authenticate himself.
	 * @param mid   The ID of the meter which should be updated.
	 * @return A boolean that shows if the update was successful.
	 * @throws ResourceNotFoundException
	 */
	@PutMapping("/api/meters/{mid}")
	@CrossOrigin
	public MeterDTO updateMeter(HttpServletRequest request, @PathVariable Long mid, @RequestBody MeterDTO meterDTO)
			throws ResourceNotFoundException {

		Meter m = meterRepository.findById(mid).orElseThrow(() -> new ResourceNotFoundException());
		Person p = personRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null);
		if (p.getRole().equals(Role.Admin)) {
			Iterable<UserMeterAssociation> umas = userMeterAssociationRepository.findAllByMeter(m);
			for (UserMeterAssociation uma : umas) {
				uma.setMeterName(meterDTO.getName());
				userMeterAssociationRepository.save(uma);
			}
		} else {
			User u = userRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null);
			assert u != null;

			Iterable<UserMeterAssociation> umas = userMeterAssociationRepository.findAllByUserAndMeter(u, m);
			for (UserMeterAssociation uma : umas) {
				uma.setMeterName(meterDTO.getName());
				userMeterAssociationRepository.save(uma);
			}
		}

		return dtoBuilder.meterDTO(m);
	}

	/**
	 * This method allows an admin to delete a meter out of the system.
	 * 
	 * @param token The JWT of the admin to authenticate himself.
	 * @param mid   The ID of the meter which should be deleted.
	 * @return A boolean that shows if the update was successful.
	 * @throws ResourceNotFoundException
	 */
	@DeleteMapping("/api/meters/{mid}")
	@CrossOrigin
	public Boolean deleteMeter(@PathVariable Long mid) throws ResourceNotFoundException {
		Meter m = meterRepository.findById(mid).orElseThrow(() -> new ResourceNotFoundException());
		m.delet();
		meterRepository.save(m);
		return true;
	}

	/**
	 * This method allows an user to look up all readings belonging to one of his
	 * meters or an admin to look up all readings belonging to any meter.
	 * 
	 * @param token The JWT of the user/admin to authenticate himself.
	 * @param mid   The ID of the meter whose readings should be returned.
	 * @return A list of readings and dates they were uploaded.
	 */

	@GetMapping("/api/meters/{mid}/readings")
	@CrossOrigin
	public Iterable<ReadingDTO> lookUpReadings(HttpServletRequest request, @PathVariable Long mid) {

		Person p = personRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null);
		Meter m = meterRepository.findById(mid).orElse(null);

		if (m == null || p == null) {
			return null;
		}

		if (p.getRole().equals(Role.User)) {
			User u = userRepository.findById(p.getPersonId()).orElse(null);
			assert u != null;
			Iterable<UserMeterAssociation> umas = userMeterAssociationRepository.findAllByUserAndMeter(u, m);
			List<Reading> readings = new ArrayList<>();
			StreamSupport.stream(readingRepository.findByMeterOrderByCreatedAtDesc(m).spliterator(), false).map(r -> r)
					.collect(Collectors.toList());
			for (UserMeterAssociation uma : umas) {
				LocalDateTime to = uma.getEndOfAssociation();
				readings.addAll(StreamSupport.stream(
						readingRepository.findByMeterAndCreatedAtBetweenOrderByCreatedAtDesc(m,
								uma.getBeginOfAssociation(), to == null ? LocalDateTime.now() : to).spliterator(),
						false).collect(Collectors.toList()));
			}
			return StreamSupport.stream(readings.spliterator(), false).map(r -> dtoBuilder.readingDTO(r))
					.collect(Collectors.toList());
		}

		return StreamSupport.stream(readingRepository.findByMeterOrderByCreatedAtDesc(m).spliterator(), false)
				.map(r -> dtoBuilder.readingDTO(r)).collect(Collectors.toList());

	}

	/**
	 * This method allows an user to add a new reading to one of his meters or an
	 * admin to add a new reading to any meter. The reading is added as a textual
	 * input here.
	 * 
	 * @param token The JWT of the user/admin to authenticate himself.
	 * @param mid   The ID of the meter the new reading belongs to.
	 * @param value The value of the reading that should be added.
	 * @return A boolean that shows if the adding was successful.
	 * @throws ResourceNotFoundException
	 */
	@PostMapping("/api/meters/{mid}/readings")
	@CrossOrigin
	public ReadingDTO addReading(HttpServletRequest request, @PathVariable Long mid, @RequestBody ReadingDTO readingDTO)
			throws ResourceNotFoundException {
		Meter meter = meterRepository.findById(mid).orElseThrow(() -> new ResourceNotFoundException());
		Person p = personRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null);
		if (p.getRole().equals(Role.User)) {
			User user = userRepository.findByUsername(request.getUserPrincipal().getName()).orElse(null);
			Iterable<UserMeterAssociation> umas = userMeterAssociationRepository.findAllByUserAndMeter(user, meter);

			for (UserMeterAssociation uma : umas) {
				if (uma.getEndOfAssociation() == null) {
					Reading nr = readingRepository.save(new Reading(meter));
					rvRepo.save(new ReadingValue(nr, readingDTO.getValue(), user.getPersonId(), "Number from user"));
					return dtoBuilder.readingDTO(nr);

				}
			}
		}

		Reading r = readingRepository.save(new Reading(meter));
		rvRepo.save(new ReadingValue(r, readingDTO.getValue(), p.getPersonId(), "New reading from admin."));

		return dtoBuilder.readingDTO(r);

	}

}
